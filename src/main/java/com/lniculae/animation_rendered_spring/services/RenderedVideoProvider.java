package com.lniculae.animation_rendered_spring.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.lniculae.Animation.Task;
import com.lniculae.AnimationOutput.AnimationFileRenderer;
import com.lniculae.AnimationOutput.JavaAWTRenderer;
import com.lniculae.AnimationParser.AnimationScriptParser;
import com.lniculae.animation_rendered_spring.executors.AnimationRendererExecutor;
import com.lniculae.animation_rendered_spring.executors.TaskStatus;
import com.lniculae.animation_rendered_spring.executors.TaskStatus.StatusKind;
import com.lniculae.animation_rendered_spring.storage.StorageService;

@Service
public class RenderedVideoProvider {
    private static Logger LOGGER = LoggerFactory.getLogger(RenderedVideoProvider.class);
    
    private AnimationScriptParser parser;
    private final AnimationRendererExecutor renderExecutor;
    private HashingService hashingService;
    private StorageService storageService;

    public RenderedVideoProvider(
        AnimationScriptParser parser,
        HashingService hashingService,
        StorageService storageService,
        AnimationRendererExecutor renderExecutor
    ) {
        this.parser = parser;
        this.hashingService = hashingService;
        this.renderExecutor = renderExecutor;
        this.storageService = storageService;
    }

    public Resource getRenderedVideo(String script) {
        String taskId = hashingService.sha256Hash(script);
        if (taskId == "") {
            LOGGER.error("could not get script digest");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "oopsie doopsy from developer, sha algorithm is bad");
        }

        return getRenderedVideo(taskId, script);
    }

    public Resource getRenderedVideo(String taskId, String script) {
        var taskResult = parser.ParseString(script);
        if (!taskResult.Ok()) {
            LOGGER.error("failed to parse the script");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, taskResult.Err().getMessage());
        }

        Task task = taskResult.Some();

        AnimationFileRenderer animationRenderer = new JavaAWTRenderer(task, 600, 600, 60);

        var result = renderExecutor.submitRender(animationRenderer, "upload-dir", taskId);
        if (!result.Ok()) {
            LOGGER.error(result.Err().getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while submiting the render task.");
        }

        try {
            Resource file = storageService.loadAsResource(taskId);

            return file;
        } catch (Exception ex) {   
            LOGGER.error("Error loading rendered video: " + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving resource " + taskId);
        }
    }
    
    public TaskStatus executeRenderVideo(String taskId, String script) {
        TaskStatus videoStatus = renderExecutor.getVideoRenderStatus(taskId);
        if (videoStatus.getStatus() != StatusKind.Unknown) {
            return videoStatus;
        }

        var taskResult = parser.ParseString(script);
        if (!taskResult.Ok()) {
            LOGGER.error("failed to parse the script");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, taskResult.Err().getMessage());
        }
        
        Task task = taskResult.Some();

        AnimationFileRenderer animationRenderer = new JavaAWTRenderer(task, 600, 600, 60);

        renderExecutor.executeRender(animationRenderer, "upload-dir", taskId);

        return new TaskStatus(StatusKind.Started);
    }
}
