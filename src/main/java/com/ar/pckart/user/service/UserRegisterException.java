package com.ar.pckart.user.service;

public class UserRegisterException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private ErrorResponse errorResponse;
	
	public UserRegisterException(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}
}
