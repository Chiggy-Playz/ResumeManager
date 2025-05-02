package com.chiggy.resumeviewer;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


@SpringBootApplication
public class ResumeViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResumeViewerApplication.class, args);
	}
}

@RestController
@Configuration
class PdfControllerButBetter {

	@Value("${UPLOAD_DIR:uploads}")
	private String uploadDir;

	@Value("${UPLOAD_AUTH_USERNAME}")
	private String authUsername;

	@Value("${UPLOAD_AUTH_PASSWORD}")
	private String authPassword;

	private Path fileStorageLocation;

	@PostConstruct
	public void init() throws IOException {
		fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(fileStorageLocation);
	}

	@GetMapping("/")
	public ResponseEntity<Resource> viewPdf(HttpServletRequest request) throws IOException {
		File folder = fileStorageLocation.toFile();
		File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

		if (files == null || files.length == 0) {
			return ResponseEntity.notFound().build();
		}

		Path filePath = files[0].toPath();
		Resource resource = new UrlResource(filePath.toUri());

		String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		if (contentType == null) {
			contentType = "application/pdf";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/upload")
	public ResponseEntity<String> uploadUi(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (isNotAuthorized(authHeader)) {
			return ResponseEntity.status(401).header("WWW-Authenticate", "Basic realm=\"Upload UI\"").build();
		}

		String html = """
        <form method="post" enctype="multipart/form-data" action="/upload">
          <input type="file" name="file" accept="application/pdf" required>
          <input type="submit" value="Upload PDF">
        </form>
    	""";

		return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
	}

	@PostMapping("/api/upload")
	public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file,
											@RequestHeader(value = "Authorization", required = false) String authHeader) throws IOException {
		if (isNotAuthorized(authHeader)) {
			return ResponseEntity.status(401).header("WWW-Authenticate", "Basic realm=\"Upload Area\"").build();
		}

		String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		if (!fileName.endsWith(".pdf")) {
			return ResponseEntity.badRequest().body("Only PDF files are allowed");
		}

		Path targetLocation = fileStorageLocation.resolve(fileName);
		Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

		return ResponseEntity.ok("File uploaded successfully");
	}

	private boolean isNotAuthorized(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Basic ")) {
			return true;
		}
		String base64Credentials = authHeader.substring("Basic ".length()).trim();
		String credentials = new String(java.util.Base64.getDecoder().decode(base64Credentials));
		String[] values = credentials.split(":", 2);
		return values.length != 2 || !values[0].equals(authUsername) || !values[1].equals(authPassword);
	}
}
