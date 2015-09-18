package com.humanify.localization.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.humanify.localization.exception.BadRequestException;
import com.humanify.localization.exception.InconsistentDataException;
import com.humanify.localization.model.ErrorDetail;
import com.humanify.localization.model.ErrorResponse;
import com.humanify.localization.model.ServerResponse;

@EnableWebMvc
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler
{
	
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request)
	{
		ErrorDetail err = new ErrorDetail(status, ex);
		err.setRequestInfo(request.getDescription(false));
		return new ResponseEntity<Object>(new ErrorResponse(err), err.getHttpStatus());
	}
	
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ServerResponse> badRequestHandler(HttpServletRequest req, BadRequestException ex)
	{
		ErrorDetail err = new ErrorDetail(HttpStatus.BAD_REQUEST, ex);
		err.setRequestInfo(req.getRequestURL().toString());
		return ErrorResponse.create(err);
	}
	
	@ExceptionHandler(InconsistentDataException.class)
	public ResponseEntity<ServerResponse> inconsistentDataHandler(HttpServletRequest req, InconsistentDataException ex)
	{
		ErrorDetail err = new ErrorDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex);
		err.setRequestInfo(req.getRequestURL().toString());
		return ErrorResponse.create(err);
	}
	
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ServerResponse> defaultHandler(HttpServletRequest req, Throwable ex)
	{
		ErrorDetail err = new ErrorDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex);
		err.setRequestInfo(req.getRequestURL().toString());
		return ErrorResponse.create(err);
	}
	
}
