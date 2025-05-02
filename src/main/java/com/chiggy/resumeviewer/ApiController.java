package com.chiggy.resumeviewer;

import com.chiggy.resumeviewer.exception.UnauthorizedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller // <- important
public class ApiController {

    final AuthService authService;
    final FileService fileService;

    public ApiController(AuthService authService, FileService fileService) {
        this.authService = authService;
        this.fileService = fileService;
    }

    @PostMapping("/api/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Authorization", required = false) String authHeader) throws IOException {
        if (authService.isNotAuthorized(authHeader)) {
            throw new UnauthorizedException();
        }

        fileService.uploadFile(file);
        return "redirect:/upload";
    }
}