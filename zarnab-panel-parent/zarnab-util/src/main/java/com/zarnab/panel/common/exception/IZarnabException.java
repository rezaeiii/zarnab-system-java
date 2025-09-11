package com.zarnab.panel.common.exception;

import org.springframework.http.HttpStatus;

public interface IZarnabException {

    HttpStatus getStatus();

    int getCode();

    String getMessageKey();
}
