package com.chiggy.resumeviewer.models;

public record UploadedFileModel(String filename, String uploadedAt, boolean current) {
}