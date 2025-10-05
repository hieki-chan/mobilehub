package org.mobilehub.cloud_media_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private String publicId;
    private String url;
    private String secureUrl;
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private String format;
    private Long size;
}