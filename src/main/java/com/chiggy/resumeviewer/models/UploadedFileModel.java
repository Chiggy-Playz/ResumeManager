package com.chiggy.resumeviewer.models;

import java.time.Instant;
import java.time.ZoneOffset;

public record UploadedFileModel(String filename, Instant uploadedAt, boolean current) {

    public String uploadedAtFormatted() {
        return uploadedAt.atOffset(ZoneOffset.ofHoursMinutes(5, 30)).toString();
    }

}