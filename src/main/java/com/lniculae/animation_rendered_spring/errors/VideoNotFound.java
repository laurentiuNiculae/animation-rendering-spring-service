package com.lniculae.animation_rendered_spring.errors;

import com.lniculae.animation_rendered_spring.executors.TaskStatus.StatusKind;

public class VideoNotFound extends RuntimeException {
    StatusKind videoStatus;

    public VideoNotFound(String error, StatusKind videoStatus) {
        super(error);
        this.videoStatus = videoStatus;
    }
    
}

