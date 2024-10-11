package com.lniculae.animation_rendered_spring.dto;

import java.util.List;

public class VideoFilesList {
    List<String> videoList;

    public VideoFilesList(List<String> videoList) {
        this.videoList = videoList;
    }

    public List<String> getVideoList() {
        return videoList;
    }
}