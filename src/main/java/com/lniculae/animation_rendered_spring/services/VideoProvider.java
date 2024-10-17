package com.lniculae.animation_rendered_spring.services;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lniculae.animation_rendered_spring.dto.VideoRenderingStatus;
import com.lniculae.animation_rendered_spring.executors.TaskStatus;
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

        Resource videoResource;

        try {
            videoResource = storageService.loadAsResource(taskId);
        } catch (StorageFileNotFoundException ex) {
            videoResource = renderedVideoProvider.getRenderedVideo(taskId, script);
        }
        
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
			"attachment; filename=\"" + videoResource.getFilename()+".mp4" + "\"").body(videoResource);
    }
    
    public ResponseEntity<VideoRenderingStatus> executeVideoRender(String script) {
        String taskId = hashingService.sha256Hash(script);
        if (taskId == "") {
            LOGGER.error("could not get script digest");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "oopsie doopsy from developer, sha algorithm is bad");
        }

        TaskStatus status = renderedVideoProvider.executeRenderVideo(taskId, script);        

        URI uri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/" + taskId)
                    .build()
                    .toUri();

        if (uri.getHost().equals("127.0.0.1")) {
            uri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .host("localhost")
                .path("/files/" + taskId)
                .build()
                .toUri();
        }

        return ResponseEntity.ok().body(
            new VideoRenderingStatus(status.getStatus(), uri.toString()));
    }
}
