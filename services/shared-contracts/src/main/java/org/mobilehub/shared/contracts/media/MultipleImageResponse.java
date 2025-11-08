package org.mobilehub.shared.contracts.media;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MultipleImageResponse {
    List<ImageUploadedEvent> uploadedEvents;
}
