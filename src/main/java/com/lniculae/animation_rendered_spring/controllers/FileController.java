package com.lniculae.animation_rendered_spring.controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lniculae.animation_rendered_spring.dto.VideoFilesList;
import com.lniculae.animation_rendered_spring.errors.VideoNotFound;
import com.lniculae.animation_rendered_spring.executors.TaskStatus;
import com.lniculae.animation_rendered_spring.executors.TaskStatusManager;
import com.lniculae.animation_rendered_spring.executors.TaskStatus.StatusKind;
import com.lniculae.animation_rendered_spring.storage.StorageFileNotFoundException;
import com.lniculae.animation_rendered_spring.storage.StorageService;

@Controller
public class FileController {

	private final StorageService storageService;
	private final TaskStatusManager taskStatusManager;

	@Autowired
	public FileController(
		StorageService storageService,
		TaskStatusManager taskStatusManager
	) {
		this.storageService = storageService;
		this.taskStatusManager = taskStatusManager;
	}

	@GetMapping("/")
	public ResponseEntity<VideoFilesList> listUploadedFiles(Model model) throws IOException {

		List<String> videoList = storageService.loadAll()
			.map(path -> "\"" + path.getFileName().toString() + "\"" )
			.collect(Collectors.toList());

		return ResponseEntity.ok().body(new VideoFilesList(videoList));
	}

	@GetMapping("/files/{taskId:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String taskId) {
		TaskStatus status = taskStatusManager.getStatus(taskId);
		switch (status.getStatus()) {
			case Started:
				throw new VideoNotFound("Video processing is in progress", StatusKind.Started);
			case Unsuccessful: 
				throw new VideoNotFound("Video render failed: " + status.getError(), StatusKind.Unsuccessful);	
			case Unknown:
				throw new VideoNotFound("Video doesn't exist", StatusKind.Unknown);	
			case Successful:
				break;
		}

		Resource file;

		try {
			file = storageService.loadAsResource(taskId);
			if (file == null) {
				throw new VideoNotFound("", null);
			}
		} catch (StorageFileNotFoundException ex) {
			throw new VideoNotFound("video file is missing, but it shoudn't. might have been removed", 
				StatusKind.Unknown);
		}

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename()+".mp4" + "\"").body(file);
	}

	// @PostMapping("/")
	// public ResponseEntity<VideoUploadStatus> handleFileUpload(@RequestParam("file") MultipartFile file) {
	// 	storageService.store(file);
	// 	return ResponseEntity.ok().body(new VideoUploadStatus(true));
	// }

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
