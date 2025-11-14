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
            writeUrl(objectName, jsonGenerator);
        } else if (value instanceof Collection<?> listOfObjectNames) {
            jsonGenerator.writeStartArray();
            for (Object item : listOfObjectNames) {
                if (item instanceof String objectName) {
                    writeUrl(objectName, jsonGenerator);
                }
            }
            jsonGenerator.writeEndArray();
        } else {
            jsonGenerator.writeNull();
        }
    }

    private void writeUrl(String objectName, JsonGenerator jsonGenerator) throws IOException {
        Field field = getCurrentField(jsonGenerator);
        if (field == null) {
            jsonGenerator.writeNull();
            return;
        }

        MinioUrl annotation = field.getAnnotation(MinioUrl.class);
        if (annotation == null) {
            jsonGenerator.writeNull();
            return;
        }

        String folderPath = annotation.folderPath();
        int expire = getExpire(annotation);
        MinioUrlMode mode = annotation.mode();
        String fullPath = folderPath + objectName;

        switch (mode) {
            case STREAM_VIDEO -> jsonGenerator.writeString(fileStorageService.getPresignedVideoUrl(fullPath, expire));
            case VIDEO -> jsonGenerator.writeString(fileStorageService.getPresignedObjectUrl(fullPath, expire));
            case DOWNLOAD -> jsonGenerator.writeString(fileStorageService.getDownloadOnlyUrl(fullPath, expire));
            case PREVIEW -> jsonGenerator.writeString(fileStorageService.getPresignedObjectUrl(fullPath, expire));
            case BOTH -> {
                // Write as object with two URLs
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("preview", fileStorageService.getPresignedObjectUrl(fullPath, expire));
                jsonGenerator.writeStringField("download", fileStorageService.getDownloadOnlyUrl(fullPath, expire));
                jsonGenerator.writeEndObject();
            }
        }
    }

    private Field getCurrentField(JsonGenerator jsonGenerator) {
        JsonWriteContext context = (JsonWriteContext) jsonGenerator.getOutputContext();
        while (context != null) {
            if (context.inObject()) {
                String fieldName = context.getCurrentName();
                Object currentValue = context.getCurrentValue();

                if (fieldName != null && currentValue != null) {
                    try {
                        return currentValue.getClass().getDeclaredField(fieldName);
                    } catch (NoSuchFieldException ignored) {
                    }
                }
            }
            context = context.getParent();
        }
        return null;
    }

    private int getExpire(MinioUrl annotation) {
        int expire = annotation.expire();
        return expire == 0 ? minioConfig.getDefaultExpire() : expire;
    }


}
