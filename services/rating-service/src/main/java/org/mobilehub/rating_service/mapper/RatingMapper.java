package org.mobilehub.rating_service.mapper;

import org.mapstruct.*;
import org.mobilehub.rating_service.dto.request.RatingCreateRequest;
import org.mobilehub.rating_service.dto.response.RatingResponse;
import org.mobilehub.rating_service.entity.Rating;
import org.mobilehub.rating_service.entity.RatingReply;


@Mapper(componentModel = "spring")
public interface RatingMapper {
    Rating toEntity(RatingCreateRequest req);

    @Mappings({
            @Mapping(target = "id", source = "rating.id"),
            @Mapping(target = "productId", source = "rating.productId"),
            @Mapping(target = "userId", source = "rating.userId"),
            @Mapping(target = "username", source = "rating.username"),
            @Mapping(target = "stars", source = "rating.stars"),
            @Mapping(target = "comment", source = "rating.comment"),
            @Mapping(target = "createdAt", source = "rating.createdAt"),
            @Mapping(target = "updatedAt", source = "rating.updatedAt"),
            @Mapping(target = "reply", expression = "java(toReplyDto(reply))")
    })
    RatingResponse toDto(Rating rating, RatingReply reply);

    default RatingResponse.ReplyDto toReplyDto(RatingReply r) {
        if (r == null) return null;
        return new RatingResponse.ReplyDto(
                r.getId(),
                r.getContent(),
                r.getAdminName(),
                r.getAdminId(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
