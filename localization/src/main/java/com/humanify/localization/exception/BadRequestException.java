package com.humanify.localization.exception;

public class BadRequestException extends LocalizationServerException
{
	private static final long serialVersionUID = -393892053143636322L;

	public BadRequestException(String message)
	{
		super(message);
	}
}

