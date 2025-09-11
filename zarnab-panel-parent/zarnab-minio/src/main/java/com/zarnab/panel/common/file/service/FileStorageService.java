package com.zarnab.panel.common.file.service;

import io.minio.ObjectWriteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public interface FileStorageService {

    ObjectWriteResponse uploadFile(MultipartFile file);

    ObjectWriteResponse uploadFile(MultipartFile file, String folderPath);

    String uploadFileAndGetFileName(MultipartFile file);

    String uploadFileAndGetFileName(MultipartFile file, String folderPath);

    void removeFile(String objectName);

    void removeFiles(List<String> objectNames);

    List<ObjectWriteResponse> uploadFile(List<MultipartFile> files);

    List<ObjectWriteResponse> uploadFile(List<MultipartFile> files, String folderPath);

    InputStream getFile(String objectName) throws FileNotFoundException;

    String generateFolderPath(List<String> folderNames);

    String getFileUrl(String objectName, boolean download);

    List<String> getFileUrl(List<String> objectName, boolean download);

    String getDownloadOnlyUrl(String objectName);

    String getDownloadOnlyUrl(String objectName, int expiresInSeconds);

    String getPresignedVideoUrl(String objectName);

    String getPresignedVideoUrl(String objectName, Integer expiresInSeconds);

    String getPresignedObjectUrl(String objectName);

    String getPresignedObjectUrl(String objectName, int expiresInSeconds);

    String getPresignedObjectUrl(String bucketName, String objectName, int expiresInSeconds);

}