package org.mobilehub.cloud_media_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    private String publicId;
    private String url;
    private String secureUrl;
    private Integer width;
    private Integer height;
    private String format;
    private ImageVersions versions;
}