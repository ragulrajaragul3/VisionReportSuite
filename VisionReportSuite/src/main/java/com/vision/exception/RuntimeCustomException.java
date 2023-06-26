/**
 * 
 */
package com.vision.exception;

/**
 * @author kiran-kumar.karra
 *
 */
public class RuntimeCustomException extends RuntimeException {

	private static final long serialVersionUID = -829778053262168020L;
	private ExceptionCode code;
	
	public RuntimeCustomException() {}

	public RuntimeCustomException(String message) {
		super(message);
		this.code = new ExceptionCode();
		this.code.setErrorMsg(message);
	}

	public RuntimeCustomException(Throwable cause) {
		super(cause);
	}

	public RuntimeCustomException(String message, Throwable cause) {
		super(message, cause);
		this.code = new ExceptionCode();
		this.code.setErrorMsg(message);
	}
	public RuntimeCustomException(ExceptionCode code) {
		super(code.getErrorMsg());
		this.code = code;
	}
	public ExceptionCode getCode() {
		return code;
	}

	public void setCode(ExceptionCode code) {
		this.code = code;
	}

}
