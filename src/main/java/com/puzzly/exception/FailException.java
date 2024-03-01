package com.puzzly.exception;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;

public class FailException extends RuntimeException{
    // See https://congsong.tistory.com/53   for Sending Custom FailException through RestfulAPI
    private static final long serialVersionUID = 16804106399509039L;

    public FailException(final String message, final int code) {
        super(convertJsonString(message, code));
    }

    public FailException(final String message, final HttpStatus httpStatus) {
        super(convertJsonString(message, httpStatus.value()));
    }

    public static String convertJsonString(String message, int code) {
        JSONObject object = new JSONObject();
        try {
            object.put("message", message);
            object.put("code", code);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
