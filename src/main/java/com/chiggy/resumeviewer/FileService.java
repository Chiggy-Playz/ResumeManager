package com.chiggy.resumeviewer;

import com.chiggy.resumeviewer.exception.InvalidFileType;
import com.chiggy.resumeviewer.models.UploadedFileModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class FileService {

    @Value("${UPLOAD_DIR:uploads}")
    private String uploadDir;

    private File uploadFolder;


    @PostConstruct
    public void init() throws IOException {
        Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(fileStorageLocation);
        uploadFolder = new File(uploadDir);
    }

    public File getCurrentFile() throws FileNotFoundException {
        File[] files = uploadFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            throw new FileNotFoundException("Could not find any pdf files in " + uploadFolder.getAbsolutePath());
        }

        // To find the latest file, just sort the files by name in descending order
        Arrays.sort(files, (o1, o2) -> o2.getName().compareTo(o1.getName()));
        return files[0];
    }

    public void uploadFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if (!fileName.endsWith(".pdf")) {
            throw new InvalidFileType();
        }

        // Write file as current time
        Instant now = Instant.now();
        Path targetLocation = uploadFolder.toPath().resolve(now.toEpochMilli() + "_" + fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    }

    public List<UploadedFileModel> getFiles() {
        File[] files = uploadFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        Arrays.sort(files, (o1, o2) -> o2.getName().compareTo(o1.getName()));
        List<UploadedFileModel> fileModels = new ArrayList<>();
        boolean current = true;
        for (File file : files) {
            Instant uploadedAt = Instant.ofEpochMilli(Long.parseLong(file.getName().split("_", 2)[0]));
            fileModels.add(
                    new UploadedFileModel(
                            file.getName().split("_", 2)[1],
                            uploadedAt,
                            current
                    )
            );

            if (current) current = false;
        }

        return fileModels;
    }

    public File getFileByTimestamp(String timestamp) {
        File[] files = uploadFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf") && name.startsWith(timestamp));
        if (files == null || files.length == 0) {
            return null;
        }

        return files[0];
    }
}
