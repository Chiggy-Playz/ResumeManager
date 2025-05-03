package com.chiggy.resumeviewer;

import com.chiggy.resumeviewer.exception.UnauthorizedException;
import com.chiggy.resumeviewer.models.UploadedFileModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@Configuration
public class PdfController {

    final AuthService authService;
    final FileService fileService;

    public PdfController(AuthService authService, FileService fileService) {
        this.authService = authService;
        this.fileService = fileService;
    }

    @GetMapping("/")
    public ResponseEntity<Resource> viewPdf(HttpServletRequest request) throws IOException {
        File file;
        try {
            file = fileService.getCurrentFile();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

        Resource resource = new UrlResource(file.toPath().toUri());
        String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        if (contentType == null) {
            contentType = "application/pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Chirag Garg - Resume\"")
                .body(resource);
    }

    @GetMapping("/upload")
    public String uploadUi(@RequestHeader(value = "Authorization", required = false) String authHeader, Model model) {
        if (authService.isNotAuthorized(authHeader)) {
            throw new UnauthorizedException();
        }

        List<UploadedFileModel> files = fileService.getFiles();

        model.addAttribute("files", files);

        return "dashboard";
    }
}