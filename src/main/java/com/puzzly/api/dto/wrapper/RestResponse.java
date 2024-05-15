package com.puzzly.api.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestResponse {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @JsonInclude
    private int status;
    @JsonInclude
    private String message;
    @JsonInclude
    private String timestamp = sdf.format(new Date());

/*    private List fields;
    private List results;*/
    private Object result;
    public RestResponse() {
        this.status = HttpStatus.OK.value();
        this.message = "SUCCESS";
    }

    public RestResponse(Object o){
        this.result = o;
    }

    public RestResponse(Exception e) {
        this.status = HttpStatus.BAD_REQUEST.value();
        this.message = e.getMessage();
    }

    public RestResponse(int status, Exception e) {
        this.status = status;
        this.message = e.getMessage();
    }
}
