package com.feedback.app.mapper;

import com.feedback.app.dto.FeedbackRequest;
import com.feedback.app.dto.FeedbackResponse;
import com.feedback.app.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.feedback.app.dto.FeedbackUpdateRequest;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FeedbackMapper {

    Feedback toEntity(FeedbackRequest request);

    FeedbackResponse toResponse(Feedback feedback);

    void updateEntityFromDto(FeedbackUpdateRequest request, @MappingTarget Feedback feedback);
}
