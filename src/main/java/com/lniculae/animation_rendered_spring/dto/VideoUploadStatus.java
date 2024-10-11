package com.lniculae.animation_rendered_spring.dto;

public class VideoUploadStatus {
    boolean successful;


    public VideoUploadStatus(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    public String toString() {
        return "VideoUploadStatus [successful=" + successful + "]";
    }
    
}
