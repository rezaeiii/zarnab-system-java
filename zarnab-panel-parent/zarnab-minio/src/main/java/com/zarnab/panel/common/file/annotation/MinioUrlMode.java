package com.zarnab.panel.common.file.annotation;

public enum MinioUrlMode {
        DOWNLOAD,       // Only downloadable link
        PREVIEW,        // Only preview (inline)
        BOTH,           // JSON object with both preview + download
        VIDEO,          // Normal video URL
        STREAM_VIDEO    // Chunked/streaming URL
    }