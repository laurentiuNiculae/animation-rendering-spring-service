package com.lniculae.animation_rendered_spring.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lniculae.AnimationOutput.AnimationFileRenderer;
import com.lniculae.AnimationOutput.JavaAWTRenderer;
import com.lniculae.AnimationParser.*;
import com.lniculae.animation_rendered_spring.dto.InitializeRenderingResponse;
import com.lniculae.animation_rendered_spring.executors.AnimationRendererExecutor;
import com.lniculae.animation_rendered_spring.storage.StorageService;

@RestController
public class ImageRenderingController {
    private AnimationRendererExecutor renderExecutor;
    private final StorageService storageService;

    public ImageRenderingController(
        AnimationRendererExecutor renderExecutor,
        StorageService storageService
    ) {
        this.storageService = storageService;
        this.renderExecutor = renderExecutor;
    }

    @PostMapping("/script")
    public ResponseEntity<?> getRequestedVideo(@RequestBody String script) throws InterruptedException {
        var parser = new AnimationScriptParser();

        var taskResult = parser.ParseString(script);
        if (!taskResult.Ok()) {
            return ResponseEntity.badRequest()
                .body(new InitializeRenderingResponse(false, taskResult.Err().getMessage()));
        }

        var task = taskResult.Some();
        AnimationFileRenderer animationRenderer = new JavaAWTRenderer(task, 600, 600, 60);

        String taskId = getDigestString(script);
        if (taskId == "") {
            return ResponseEntity.internalServerError()
                .body(new InitializeRenderingResponse(false, "Error while calculating file digest"));
        }

        try {
            var file = storageService.loadAsResource(taskId+".mp4");
            if (file != null) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
            }
        } catch (Exception e) {}


        var result = renderExecutor.submitRender(animationRenderer, "upload-dir", taskId);
        if (!result.Ok()) {
            return ResponseEntity.internalServerError()
                .body(new InitializeRenderingResponse(false, result.Err().getMessage()));
        }

        var file = storageService.loadAsResource(taskId+".mp4");

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    private String getDigestString(String script) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(script.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

}
