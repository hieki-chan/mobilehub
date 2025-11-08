package org.mobilehub.shared.contracts.media;

import lombok.Data;

import java.util.Map;


@Data
public class ImageUploadedEvent {
    private String publicId;
    private String imageUrl;
    private Map<String, Object> properties;

    public <T> T getProperty(String id, Class<T> type) {
        Object v = properties.get(id);
        return type.isInstance(v) ? type.cast(v) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String id) {
        return (T) properties.get(id);
    }
}
