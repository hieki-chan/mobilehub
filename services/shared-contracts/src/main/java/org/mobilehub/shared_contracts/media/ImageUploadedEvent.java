package org.mobilehub.shared_contracts.media;

import lombok.Data;


@Data
public class ImageUploadedEvent {
    private Long productId;
    private String publicId;
    private String url;
}
