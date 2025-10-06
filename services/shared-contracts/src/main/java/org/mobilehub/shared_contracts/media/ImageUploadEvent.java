package org.mobilehub.shared_contracts.media;

import lombok.Data;

import java.util.List;

@Data
public class ImageUploadEvent {
    private Long productId;
    private List<String> files; // Base64 encoded files
}