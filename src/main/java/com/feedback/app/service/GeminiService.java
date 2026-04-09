package com.feedback.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.app.dto.GeminiAnalysisResult;
import com.feedback.app.entity.Feedback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final String apiKey;
    private final int maxRetries;
    private final int retryDelaySeconds;

    public GeminiService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model:gemini-2.0-flash-lite}") String model,
            @Value("${gemini.api-url:https://generativelanguage.googleapis.com/v1beta}") String apiUrl,
            @Value("${gemini.max-retries:3}") int maxRetries,
            @Value("${gemini.retry-delay-seconds:35}") int retryDelaySeconds,
            ObjectMapper objectMapper) {
        this.model = model;
        this.apiKey = apiKey;
        this.maxRetries = maxRetries;
        this.retryDelaySeconds = retryDelaySeconds;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .build();
    }

    public List<GeminiAnalysisResult> analyzeFeedbacks(List<Feedback> feedbacks) {
        String prompt = buildPrompt(feedbacks);
        String jsonResponse = callGeminiWithRetry(prompt);
        return parseResponse(jsonResponse);
    }

    private String buildPrompt(List<Feedback> feedbacks) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                Sen bir yazılım hata analiz uzmanısın. Aşağıdaki kullanıcı feedbacklerini analiz et.
                
                ## GÖREV
                Feedbackleri screenName ve issueType'a göre gruplayarak, birden fazla kullanıcının bahsettiği ORTAK sorunları tespit et.
                
                ## KRİTİK KURALLAR
                1. SADECE en az 3 farklı ticket tarafından bildirilen ortak sorunları raporla.
                2. Tek bir kullanıcının bildirdiği tekil sorunları KESINLIKLE dahil etme.
                3. Feedbacklerin içeriklerini anlamsal olarak karşılaştır - aynı sorunu farklı kelimelerle anlatan feedbackleri de grupla.
                4. Eğer hiçbir ortak sorun tespit edemezsen boş array [] döndür.
                
                ## ÇIKTI FORMATI
                Her tespit ettiğin ortak sorun için:
                - title: Sorunun kısa ve açıklayıcı başlığı
                - description: Sorunun detaylı açıklaması (hangi kullanıcı davranışları etkileniyor, olası kök neden)
                - screenName: İlgili ekran adı
                - issueType: İlgili sorun tipi
                - referenceTicketIds: Bu sorunu bildiren feedbacklerin ticket ID listesi (EN AZ 3 olmalı)
                - referenceFeedbackIds: Bu sorunu bildiren feedbacklerin ID listesi (EN AZ 3 olmalı)
                - tag: Tahmini hata kaynağı ("BACKEND" veya "FRONTEND")
                - severity: Sorunun ciddiyeti ("LOW", "MEDIUM", "HIGH" veya "CRITICAL")
                
                ## FEEDBACKLER
                """);

        for (Feedback f : feedbacks) {
            sb.append(String.format(
                    "- TicketID: %s | FeedbackID: %d | ScreenName: %s | IssueType: %s | Text: \"%s\"%n",
                    f.getTicketId(), f.getId(), f.getScreenName(), f.getIssueType(), f.getFeedbackText()));
        }

        sb.append("""
                
                SADECE JSON array formatında yanıt ver, başka hiçbir açıklama ekleme.
                Hatırla: Her sonuçta referenceTicketIds ve referenceFeedbackIds EN AZ 3 eleman içermeli. 3'ten az referansı olan sorunları DAHIL ETME.
                Eğer minimum 3 referanslı ortak sorun yoksa boş array [] döndür.
                
                Örnek format:
                [
                  {
                    "title": "Login Sayfası Validasyon Hatası",
                    "description": "Birden fazla kullanıcı login sayfasında...",
                    "screenName": "Login Page",
                    "issueType": "BUG",
                    "referenceTicketIds": ["TK-XXXXXXXX", "TK-YYYYYYYY", "TK-ZZZZZZZZ"],
                    "referenceFeedbackIds": [1, 2, 5],
                    "tag": "FRONTEND",
                    "severity": "HIGH"
                  }
                ]
                """);

        return sb.toString();
    }

    private String callGeminiWithRetry(String prompt) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return callGemini(prompt);
            } catch (RestClientResponseException e) {
                if (e.getStatusCode() == HttpStatusCode.valueOf(429) && attempt < maxRetries) {
                    log.warn("Gemini API 429 rate limit. Deneme {}/{}. {} saniye bekleniyor...",
                            attempt, maxRetries, retryDelaySeconds);
                    sleep(retryDelaySeconds);
                } else {
                    log.error("Gemini API hata döndü. Status: {}, Body: {}",
                            e.getStatusCode(), e.getResponseBodyAsString());
                    throw new RuntimeException("Gemini API hatası: " + e.getStatusCode(), e);
                }
            } catch (Exception e) {
                log.error("Gemini API isteği başarısız (deneme {}/{}): {}",
                        attempt, maxRetries, e.getMessage());
                if (attempt == maxRetries) {
                    throw new RuntimeException("Gemini API bağlantı hatası: " + e.getMessage(), e);
                }
                sleep(retryDelaySeconds);
            }
        }
        throw new RuntimeException("Gemini API maksimum deneme sayısına ulaşıldı");
    }

    private String callGemini(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.2
                )
        );

        log.info("Gemini API isteği gönderiliyor. Model: {}", model);

        String responseBody = restClient.post()
                .uri("/models/{model}:generateContent?key={apiKey}", model, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        JsonNode root;
        try {
            root = objectMapper.readTree(responseBody);
        } catch (Exception e) {
            log.error("Gemini yanıtı JSON parse edilemedi: {}", responseBody, e);
            throw new RuntimeException("Gemini API yanıtı işlenemedi", e);
        }

        String text = root.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text")
                .asText();

        if (text == null || text.isBlank()) {
            log.error("Gemini yanıtında text bulunamadı. Full response: {}", responseBody);
            throw new RuntimeException("Gemini API boş yanıt döndü");
        }

        log.info("Gemini API yanıtı başarıyla alındı.");
        return text;
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry beklemesi kesildi", e);
        }
    }

    private List<GeminiAnalysisResult> parseResponse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Gemini JSON yanıtı parse edilemedi: {}", json, e);
            throw new RuntimeException("Gemini analiz sonucu parse edilemedi", e);
        }
    }
}
