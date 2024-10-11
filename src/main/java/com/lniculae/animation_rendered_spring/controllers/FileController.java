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
import com.lniculae.animation_rendered_spring.storage.StorageFileNotFoundException;
import com.lniculae.animation_rendered_spring.storage.StorageService;

@Controller
public class FileController {

	private final StorageService storageService;

	@Autowired
	public FileController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/")
	public ResponseEntity<VideoFilesList> listUploadedFiles(Model model) throws IOException {

		List<String> videoList = storageService.loadAll()
			.map(path -> "\"" + path.getFileName().toString() + "\"" )
			.collect(Collectors.toList());

		return ResponseEntity.ok().body(new VideoFilesList(videoList));
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);

		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
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
