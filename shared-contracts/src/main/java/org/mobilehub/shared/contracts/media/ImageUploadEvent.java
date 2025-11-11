package org.mobilehub.shared.contracts.media;

import java.util.List;
import java.util.Map;


public record ImageUploadEvent (
        List<String> files, // Base64 encoded files
        String folder,
        Map<String, Object>[] propertiesMap
) {
}