package com.konsol.beatstream.service.bucket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import org.springframework.stereotype.Component;

@Component
public class BucketManager {

    public static final Path rootPath = Paths.get("rootFolder");

    public BucketManager() {
        try {
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create root directory: ");
        }
    }

    public BucketManager(String rootFolder) {
        try {
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to create root directory: " + rootFolder, e);
        }
    }

    // Create a bucket (folder)
    public void createBucket(String bucketName) throws IOException {
        Path bucketPath = rootPath.resolve(bucketName);
        if (!Files.exists(bucketPath)) {
            Files.createDirectories(bucketPath);
            System.out.println("Bucket created: " + bucketPath.toAbsolutePath());
        } else {
            System.out.println("Bucket already exists: " + bucketPath.toAbsolutePath());
        }
    }

    // Delete a bucket (folder) and all its contents
    public void deleteBucket(String bucketName) throws IOException {
        Path bucketPath = rootPath.resolve(bucketName);
        if (Files.exists(bucketPath)) {
            Files.walkFileTree(
                bucketPath,
                new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                }
            );
            System.out.println("Bucket deleted: " + bucketPath.toAbsolutePath());
        } else {
            System.out.println("Bucket does not exist: " + bucketPath.toAbsolutePath());
        }
    }

    // Check if bucket exists
    public boolean bucketExists(String bucketName) {
        Path bucketPath = rootPath.resolve(bucketName);
        return Files.exists(bucketPath);
    }

    // Upload a file to the bucket
    public void uploadFile(String fileName, String bucketName, InputStream inputStream) throws IOException {
        Path bucketPath = rootPath.resolve(bucketName);
        File file = new File(bucketPath.toAbsolutePath().toFile(), fileName);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            System.out.println("File uploaded: " + file.getAbsolutePath());
        }
    }
}
