package com.zarnab.panel.common.file.service;

import com.zarnab.panel.common.file.config.MinioConfig;
import com.zarnab.panel.common.file.util.MinioUtils;
import io.minio.ObjectWriteResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.zarnab.panel.common.file.constants.FileConstants.*;

@Component
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final MinioUtils minioUtils;
    private final MinioConfig minioConfig;

    @Override
    public ObjectWriteResponse uploadFile(MultipartFile file) {
        return uploadFile(file, EMPTY);
    }

    @Override
    public ObjectWriteResponse uploadFile(MultipartFile file, String folderPath) {
        String newFileName = generateFileName(file.getOriginalFilename(), folderPath);
        return minioUtils.uploadFile(minioConfig.getBucketName(), file, newFileName, file.getContentType());
    }

    @Override
    public String uploadFileAndGetFileName(MultipartFile file) {
        ObjectWriteResponse objectWriteResponse = uploadFile(file, EMPTY);
        return StringUtils.substringAfterLast(objectWriteResponse.object(), SLASH);
    }

    @Override
    public String uploadFileAndGetFileName(MultipartFile file, String folderPath) {
        ObjectWriteResponse objectWriteResponse = uploadFile(file, folderPath);
        return StringUtils.substringAfterLast(objectWriteResponse.object(), SLASH);
    }

    @Override
    public void removeFile(String objectName) {
        minioUtils.removeFile(minioConfig.getBucketName(), objectName);
    }

    @Override
    public void removeFiles(List<String> objectNames) {
        minioUtils.removeFiles(minioConfig.getBucketName(), objectNames);
    }

    @Override
    public List<ObjectWriteResponse> uploadFile(List<MultipartFile> files) {
        return uploadFile(files, EMPTY);
    }

    @Override
    public List<ObjectWriteResponse> uploadFile(List<MultipartFile> files, String folderPath) {
        return files.stream()
                .map(file -> uploadFile(file, folderPath))
                .toList();
    }

    @Override
    public InputStream getFile(String objectName) throws FileNotFoundException {
        boolean isObjectExist = minioUtils.isObjectExist(minioConfig.getBucketName(), objectName);
        if (!isObjectExist) {
            throw new FileNotFoundException(
                    StringUtils.substringAfterLast(objectName, SLASH)
            );
        }

        return minioUtils.getObject(minioConfig.getBucketName(), objectName);
    }

    @Override
    public String generateFolderPath(List<String> folders) {
        return folders.stream()
                .map(folderName -> {
                    String sanitizedFolderName = DISALLOWED_CHARACTERS.matcher(folderName).replaceAll(DASH);
                    return sanitizedFolderName.replace(SPACE, DASH);
                })
                .collect(Collectors.joining(SLASH));
    }

    @Override
    public String getFileUrl(String objectName, boolean download) {
        return download
                ? getDownloadOnlyUrl(objectName)
                : getPresignedObjectUrl(objectName);
    }

    @Override
    public List<String> getFileUrl(List<String> objectNames, boolean download) {
        return objectNames.stream().map(name -> getFileUrl(name, download)).toList();
    }

    @Override
    public String getDownloadOnlyUrl(String objectName) {
        return minioUtils.getDownloadOnlyUrl(minioConfig.getBucketName(), objectName, minioConfig.getDefaultExpire());
    }

    @Override
    public String getDownloadOnlyUrl(String objectName, int expire) {
        return minioUtils.getDownloadOnlyUrl(minioConfig.getBucketName(), objectName, expire);
    }

    @Override
    public String getPresignedVideoUrl(String objectName) {
        return minioUtils.getPresignedVideoUrl(minioConfig.getBucketName(), objectName);
    }

    @Override
    public String getPresignedVideoUrl(String objectName, Integer expiresInSeconds) {
        expiresInSeconds = expiresInSeconds == null ? minioConfig.getDefaultExpire() : expiresInSeconds;
        return minioUtils.getPresignedVideoUrl(minioConfig.getBucketName(), objectName, expiresInSeconds);
    }

    @Override
    public String getPresignedObjectUrl(String objectName) {
        return minioUtils.getPresignedObjectUrl(minioConfig.getBucketName(), objectName);
    }

    @Override
    public String getPresignedObjectUrl(String objectName, int expiresInSeconds) {
        return minioUtils.getPresignedObjectUrl(minioConfig.getBucketName(), objectName, expiresInSeconds);
    }

    @Override
    public String getPresignedObjectUrl(String bucketName, String objectName, int expiresInSeconds) {
        return minioUtils.getPresignedObjectUrl(bucketName, objectName, expiresInSeconds);
    }

    private String generateFileName(String originalFileName, String folderPath) {
//        String fileExtension = StringUtils.substringAfterLast(originalFileName, DOT);
//        String timestamp = String.valueOf(System.currentTimeMillis());
        String sanitizedFolderPath = Optional.ofNullable(folderPath)
                .filter(path -> !path.isEmpty()).orElse(EMPTY);

        return sanitizedFolderPath.isEmpty()
                ? String.format("%s", originalFileName)
                : String.format("%s/%s", sanitizedFolderPath, originalFileName);
    }
}