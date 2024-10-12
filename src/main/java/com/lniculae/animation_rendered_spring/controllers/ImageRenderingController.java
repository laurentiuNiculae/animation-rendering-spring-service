package com.lniculae.animation_rendered_spring.controllers;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.lniculae.animation_rendered_spring.executors.AnimationRendererExecutor;
import com.lniculae.animation_rendered_spring.services.VideoProvider;
import com.lniculae.animation_rendered_spring.storage.StorageService;

@RestController
public class ImageRenderingController {
    private final VideoProvider videoProvider;

    public ImageRenderingController(
        AnimationRendererExecutor renderExecutor,
        StorageService storageService,
        VideoProvider videoProvider
    ) {
        this.videoProvider = videoProvider;
    }

    @PostMapping("/script")
    public ResponseEntity<Resource> getRequestedVideo(@RequestBody String script) throws InterruptedException {
        return videoProvider.getVideoResource(script);
    }

}
