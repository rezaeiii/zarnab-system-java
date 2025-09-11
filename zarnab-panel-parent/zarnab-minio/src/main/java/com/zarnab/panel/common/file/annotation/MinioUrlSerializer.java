package com.zarnab.panel.common.file.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zarnab.panel.common.file.config.MinioConfig;
import com.zarnab.panel.common.file.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

@Component
public class MinioUrlSerializer extends JsonSerializer<Object> {
    private final MinioConfig minioConfig;
    private final FileStorageService fileStorageService;

    @Autowired
    public MinioUrlSerializer(MinioConfig minioConfig, FileStorageService fileStorageService) {
        this.minioConfig = minioConfig;
        this.fileStorageService = fileStorageService;
    }

    public MinioUrlSerializer() {
        this.minioConfig = null;
        this.fileStorageService = null;
    }

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        if (value instanceof String objectName) {
            jsonGenerator.writeString(generatePresignedUrl(objectName, jsonGenerator));
        } else if (value instanceof Collection<?> listOfObjectNames) {
            jsonGenerator.writeStartArray();
            for (Object item : listOfObjectNames) {
                if (item instanceof String objectName) {
                    jsonGenerator.writeString(generatePresignedUrl(objectName, jsonGenerator));
                }
            }
            jsonGenerator.writeEndArray();
        } else {
            jsonGenerator.writeNull();
        }
    }

    private String generatePresignedUrl(String objectName, JsonGenerator jsonGenerator) {
        Field field = getCurrentField(jsonGenerator);

        if (field == null) {
            return null;
        }

        MinioUrl annotation = field.getAnnotation(MinioUrl.class);
        String folderPath = annotation.folderPath();
        int expire = getExpire(annotation);
        boolean onlyDownload = annotation.onlyDownload();
        boolean videoStream = annotation.videoStream();

        if (videoStream) {
            return fileStorageService.getPresignedVideoUrl(folderPath + objectName, expire);
        }
        if (onlyDownload) {
            return fileStorageService.getDownloadOnlyUrl(folderPath + objectName, expire);
        }

        return fileStorageService.getPresignedObjectUrl(folderPath + objectName, expire);
    }

    private Field getCurrentField(JsonGenerator jsonGenerator) {
        // Get the current serialization context
        JsonWriteContext context = (JsonWriteContext) jsonGenerator.getOutputContext();

        // Traverse up the context to find the parent object and field name
        while (context != null) {
            if (context.inObject()) { // Check if we're inside an object
                String fieldName = context.getCurrentName();
                Object currentValue = context.getCurrentValue();

                if (fieldName != null && currentValue != null) {
                    try {
                        // Use reflection to get the field from the current object
                        return currentValue.getClass().getDeclaredField(fieldName);
                    } catch (NoSuchFieldException e) {
                        // Field not found, continue searching up the context
                    }
                }
            }
            // Move up to the parent context
            context = context.getParent();
        }

        // Field not found
        return null;
    }

    private int getExpire(MinioUrl annotation) {
        int expire = (annotation != null) ? annotation.expire() : 0;
        expire = expire == 0 ? minioConfig.getDefaultExpire() : expire;

        return expire;
    }
}