package com.lniculae.animation_rendered_spring.dto;

import com.lniculae.animation_rendered_spring.executors.TaskStatus.StatusKind;

public class VideoRenderingStatus {
    StatusKind videoRenderStaus;
    String resourceUrl;

    public VideoRenderingStatus(StatusKind videoRenderStaus, String resourceUrl) {
        this.videoRenderStaus = videoRenderStaus;
        this.resourceUrl = resourceUrl;
    }

    public StatusKind getVideoRenderStaus() {
        return videoRenderStaus;
    }

    public void setVideoRenderStaus(StatusKind videoRenderStaus) {
        this.videoRenderStaus = videoRenderStaus;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

}
