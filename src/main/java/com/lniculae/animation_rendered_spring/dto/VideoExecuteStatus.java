package com.lniculae.animation_rendered_spring.dto;

import com.lniculae.animation_rendered_spring.executors.TaskStatus.StatusKind;

public class VideoExecuteStatus {
    StatusKind videoRenderStaus;
    String resourceUrl;

    public VideoExecuteStatus(StatusKind videoRenderStaus, String resourceUrl) {
        this.videoRenderStaus = videoRenderStaus;
        this.resourceUrl = resourceUrl;
    }

}
