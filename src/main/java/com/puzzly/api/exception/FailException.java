package com.puzzly.api.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;

import java.util.Date;

@JsonIgnoreProperties({"stackTrace", "cause", "localizedMessage", "suppressed", "suppressedExceptions"})
public class FailException extends RuntimeException {
	// See https://congsong.tistory.com/53   for Sending Custom FailException through RestfulAPI 
	//private static final long serialVersionUID = 16804106399509039L;

	private int status = 400;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String timestamp = sdf.format(new Date());
	private Object message = this.getMessage();

	public FailException(Exception e) {
		super(e.getMessage());
		this.message = e.getMessage();
	}

	public FailException(int status) {
		this("", status);
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status){
		this.status = status;
	}
	public FailException(String message, int status) {
		super(message);
		this.status = status;
	}
}
