package com.chiggy.resumeviewer;

import com.chiggy.resumeviewer.exception.UnauthorizedException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;

@Controller // <- important
public class ApiController {

  final AuthService authService;
  final FileService fileService;

  public ApiController(AuthService authService, FileService fileService) {
    this.authService = authService;
    this.fileService = fileService;
  }

  @PostMapping("/api/upload")
  public String uploadPdf(
      @RequestParam("file") MultipartFile file,
      @RequestHeader(value = "Authorization", required = false) String authHeader)
      throws IOException {
    if (authService.isNotAuthorized(authHeader)) {
      throw new UnauthorizedException();
    }

    fileService.uploadFile(file);
    return "redirect:/upload";
  }

  @GetMapping("/api/download/{timestamp}")
  public ResponseEntity<Resource> downloadPdf(
      @RequestHeader(value = "Authorization") String authHeader, @PathVariable String timestamp)
      throws IOException {
    if (authService.isNotAuthorized(authHeader)) {
      throw new UnauthorizedException();
    }

    File file = fileService.getFileByTimestamp(timestamp);

    if (file == null) {
      throw new FileNotFoundException();
    }

    Resource resource = new UrlResource(file.toURI());

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getName().split("_", 2)[1] + "\"")
        .body(resource);
  }

  @PostMapping("/api/set-current")
  public String setCurrent(
      @RequestParam("filename") String fileTimestamp,
      @RequestHeader(value = "Authorization", required = false) String authHeader) throws IOException {
    if (authService.isNotAuthorized(authHeader)) {
      throw new UnauthorizedException();
    }

    File file = fileService.getFileByTimestamp(fileTimestamp);
    if (file == null) {
      throw new FileNotFoundException();
    }

    Instant now = Instant.now();
    String fileName = file.getName().split("_", 2)[1];
    String newName = now.toEpochMilli() + "_" + fileName;
    // Rename file
    if(!file.renameTo(new File(file.getParent(), newName))) {
      return "redirect:/error-page?msg=Unable+to+rename+file";
    }

    return "redirect:/upload";
  }
}
