package com.lniculae.animation_rendered_spring.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lniculae.animation_rendered_spring.storage.StorageFileNotFoundException;
import com.lniculae.animation_rendered_spring.storage.StorageService;

@Service
public class VideoProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoProvider.class);
    
    private HashingService hashingService;
    private StorageService storageService;
    private RenderedVideoProvider renderedVideoProvider;

    public VideoProvider(
        HashingService hashingService,
        StorageService storageService,
        RenderedVideoProvider renderedVideoProvider
    ) {
        this.hashingService = hashingService;
        this.storageService = storageService;
        this.renderedVideoProvider = renderedVideoProvider;
    }

    public ResponseEntity<Resource> getVideoResource(String script) {
        String taskId = hashingService.sha256Hash(script);
        if (taskId == "") {
            LOGGER.error("could not get script digest");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "oopsie doopsy from developer, sha algorithm is bad");
        }

        String videoId = taskId+".mp4";

        Resource videoResource;

        try {
            videoResource = storageService.loadAsResource(videoId);
        } catch (StorageFileNotFoundException ex) {
            videoResource = renderedVideoProvider.getRenderedVideo(taskId, script);
        }
        
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=\"" + videoResource.getFilename() + "\"").body(videoResource);
    }
}
