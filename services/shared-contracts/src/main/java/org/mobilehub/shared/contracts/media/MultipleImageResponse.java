package org.mobilehub.shared.contracts.media;

import java.util.List;


public record MultipleImageResponse (List<ImageUploadedEvent> uploadedEvents) {}
