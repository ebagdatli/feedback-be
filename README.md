# Feedback BE

Kullanıcı geri bildirimlerini merkezi olarak toplayan ve **Google Gemini AI** ile otomatik analiz eden bir Spring Boot backend uygulamasıdır.

Sistem, gelen feedbackleri `screenName` ve `issueType` bazında gruplayarak ortak sorunları tespit eder. Minimum 3 referans ticket'a sahip gruplar analiz sonucu olarak kaydedilir ve her analiz sonucuna severity (önem derecesi) ile tag (etiket) atanır.

---

## Teknoloji Stack'i

| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| **Java** | 21 | Virtual Threads desteği aktif |
| **Spring Boot** | 3.4.0 | Web, Data JPA, Validation starter'ları |
| **PostgreSQL** | 15+ | İlişkisel veritabanı |
| **Hibernate** | Spring Boot BOM | ORM - `ddl-auto: update` ile otomatik şema yönetimi |
| **MapStruct** | 1.5.5.Final | DTO-Entity mapping |
| **Lombok** | Spring Boot BOM | Boilerplate kod azaltma |
| **Google Gemini API** | gemini-2.5-flash | AI destekli feedback analizi |
| **Maven** | 3.9+ | Build ve dependency yönetimi |

---

## Proje Yapısı

```
feedback-be/
├── pom.xml
├── Feedback_Hub.postman_collection.json
├── README.md
└── src/main/
    ├── java/com/feedback/app/
    │   ├── FeedbackHubApplication.java
    │   ├── config/
    │   │   ├── CorsConfig.java
    │   │   └── JpaAuditingConfig.java
    │   ├── controller/
    │   │   ├── FeedbackController.java
    │   │   └── FeedbackAnalysisController.java
    │   ├── dto/
    │   │   ├── FeedbackRequest.java
    │   │   ├── FeedbackResponse.java
    │   │   ├── FeedbackUpdateRequest.java
    │   │   ├── FeedbackAnalysisResponse.java
    │   │   └── GeminiAnalysisResult.java
    │   ├── entity/
    │   │   ├── BaseEntity.java
    │   │   ├── Feedback.java
    │   │   └── FeedbackAnalysis.java
    │   ├── exception/
    │   │   ├── AnalysisNotFoundException.java
    │   │   ├── FeedbackNotFoundException.java
    │   │   ├── ErrorResponse.java
    │   │   └── GlobalExceptionHandler.java
    │   ├── mapper/
    │   │   ├── FeedbackMapper.java
    │   │   └── FeedbackAnalysisMapper.java
    │   ├── repository/
    │   │   ├── FeedbackRepository.java
    │   │   └── FeedbackAnalysisRepository.java
    │   └── service/
    │       ├── FeedbackService.java
    │       ├── FeedbackAnalysisService.java
    │       └── GeminiService.java
    └── resources/
        └── application.yml
```

---

## Gereksinimler

Projeyi çalıştırmak için aşağıdaki yazılımların bilgisayarınızda kurulu olması gerekmektedir:

| Yazılım | Minimum Versiyon | İndirme Linki |
|---------|-----------------|---------------|
| **Java JDK** | 21 | [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) veya [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21) |
| **Apache Maven** | 3.9+ | [Maven İndir](https://maven.apache.org/download.cgi) |
| **PostgreSQL** | 15+ | [PostgreSQL İndir](https://www.postgresql.org/download/) |
| **Git** | 2.x | [Git İndir](https://git-scm.com/downloads) |
| **Postman** (opsiyonel) | Son sürüm | [Postman İndir](https://www.postman.com/downloads/) |

---

## Kurulum

### 1. Veritabanı Kurulumu

PostgreSQL'de `feedback_hub` adında bir veritabanı oluşturun:

```sql
CREATE DATABASE feedback_hub;
```

Varsayılan bağlantı bilgileri (`application.yml`):

| Parametre | Değer |
|-----------|-------|
| Host | `localhost` |
| Port | `5432` |
| Database | `feedback_hub` |
| Username | `postgres` |
| Password | `postgres` |

### 2. Gemini API Key

Google Gemini API kullanmak için bir API key gereklidir. API key'i ortam değişkeni olarak tanımlayın:

```bash
# Linux / macOS
export GEMINI_API_KEY=your_api_key_here

# Windows (PowerShell)
$env:GEMINI_API_KEY="your_api_key_here"

# Windows (CMD)
set GEMINI_API_KEY=your_api_key_here
```

### 3. Projeyi Çalıştırma

```bash
# Projeyi derleyin
mvn clean install

# Uygulamayı başlatın
mvn spring-boot:run
```

Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışır.

---

## API Endpoints

### Feedback İşlemleri — `/api/v1/feedbacks`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| `POST` | `/api/v1/feedbacks` | Yeni feedback oluştur |
| `GET` | `/api/v1/feedbacks` | Tüm feedbackleri listele |
| `GET` | `/api/v1/feedbacks/{id}` | ID ile feedback getir |
| `PUT` | `/api/v1/feedbacks/{id}` | Feedback güncelle |
| `DELETE` | `/api/v1/feedbacks/{id}` | Feedback sil |

### Analiz İşlemleri — `/api/v1/analyses`

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| `POST` | `/api/v1/analyses/trigger` | Gemini analizi tetikle |
| `GET` | `/api/v1/analyses` | Tüm analiz sonuçlarını listele |
| `GET` | `/api/v1/analyses/{id}` | ID ile analiz getir |
| `GET` | `/api/v1/analyses/tag/{tag}` | Etikete göre filtrele (FRONTEND, BACKEND, vb.) |
| `GET` | `/api/v1/analyses/severity/{severity}` | Önem derecesine göre filtrele (CRITICAL, HIGH, vb.) |
| `GET` | `/api/v1/analyses/unanalyzed-count` | Analiz edilmemiş feedback sayısı |

---

## Postman Collection

Proje kök dizininde `Feedback_Hub.postman_collection.json` dosyası bulunmaktadır. Bu koleksiyonu Postman'e import ederek tüm API endpoint'lerini hızlıca test edebilirsiniz.

### Import Adımları

1. Postman uygulamasını açın
2. **Import** butonuna tıklayın
3. `Feedback_Hub.postman_collection.json` dosyasını sürükleyin veya seçin
4. Collection otomatik olarak yüklenecektir

### Collection Değişkenleri

| Değişken | Varsayılan Değer | Açıklama |
|----------|-----------------|----------|
| `baseUrl` | `http://localhost:8080` | API base URL |
| `feedbackId` | `1` | Test için kullanılan feedback ID |
| `analysisId` | `1` | Test için kullanılan analiz ID |

### Collection İçeriği

**Feedbacks Klasörü**
- `Create Feedback - BUG` — Login Page bug feedback'i oluşturma
- `Create Feedback - BUG (Login 2)` — Login Page ikinci bug feedback'i
- `Create Feedback - BUG (Login 3)` — Login Page üçüncü bug feedback'i
- `Create Feedback - PERFORMANCE (Dashboard)` — Dashboard performans feedback'i
- `Create Feedback - PERFORMANCE (Dashboard 2)` — Dashboard ikinci performans feedback'i
- `Create Feedback - BUG (Reports)` — Reports Page bug feedback'i
- `Get All Feedbacks` — Tüm feedbackleri listeleme
- `Get Feedback By ID` — Tek feedback getirme
- `Update Feedback` — Feedback güncelleme (status, priority, isAnalysis)
- `Delete Feedback` — Feedback silme

**Analysis Klasörü**
- `Get Unanalyzed Count` — Analiz bekleyen feedback sayısını kontrol etme
- `Trigger Gemini Analysis` — Analiz edilmemiş feedbackleri Gemini'ye gönderme
- `Get All Analyses` — Tüm analiz sonuçlarını listeleme
- `Get Analysis By ID` — Tek analiz sonucu getirme
- `Get Analyses By Tag - FRONTEND` — FRONTEND etiketli analizler
- `Get Analyses By Tag - BACKEND` — BACKEND etiketli analizler
- `Get Analyses By Severity - CRITICAL` — CRITICAL seviyeli analizler
- `Get Analyses By Severity - HIGH` — HIGH seviyeli analizler

### Hızlı Test Senaryosu

1. Sırasıyla `Create Feedback` requestlerini çalıştırarak örnek veriler oluşturun
2. `Get Unanalyzed Count` ile analiz bekleyen feedback sayısını kontrol edin
3. `Trigger Gemini Analysis` ile Gemini analizini tetikleyin
4. `Get All Analyses` ile sonuçları görüntüleyin
5. Tag ve severity filtrelerini kullanarak sonuçları inceleyin

---

## Konfigürasyon

Temel konfigürasyon `src/main/resources/application.yml` dosyasında yer alır. Ortam değişkenleri ile override edilebilir:

| Parametre | Ortam Değişkeni | Varsayılan |
|-----------|----------------|------------|
| Veritabanı URL | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/feedback_hub` |
| Veritabanı Kullanıcı | `SPRING_DATASOURCE_USERNAME` | `postgres` |
| Veritabanı Şifre | `SPRING_DATASOURCE_PASSWORD` | `postgres` |
| Server Port | `SERVER_PORT` | `8080` |
| Gemini API Key | `GEMINI_API_KEY` | - |
| Gemini Model | `GEMINI_MODEL` | `gemini-2.5-flash` |

---

## CORS Ayarları

Uygulama aşağıdaki originlere izin verir:

- `http://localhost:3000`
- `http://localhost:5173`

Path pattern: `/api/**`
