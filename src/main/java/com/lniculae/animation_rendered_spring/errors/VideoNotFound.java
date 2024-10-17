package com.lniculae.animation_rendered_spring.errors;

import com.lniculae.animation_rendered_spring.executors.TaskStatus.StatusKind;

public class VideoNotFound extends RuntimeException {
    StatusKind videoStatus;
    String message;

    public VideoNotFound(String message, StatusKind videoStatus) {
        this.message = message;
        this.videoStatus = videoStatus;
    }

    @Override
    public String toString() {
        return "VideoNotFound [videoStatus=" + videoStatus + ", message=" + message + "]";
    }

}

