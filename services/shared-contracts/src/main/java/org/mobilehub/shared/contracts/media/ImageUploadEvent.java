package org.mobilehub.shared.contracts.media;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ImageUploadEvent {
    private List<String> files; // Base64 encoded files
    private String folder;
    private Map<String, Object>[] propertiesMap;
}