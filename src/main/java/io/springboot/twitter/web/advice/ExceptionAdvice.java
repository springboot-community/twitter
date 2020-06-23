package io.springboot.twitter.web.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.springboot.twitter.common.Message;
import io.springboot.twitter.exception.ServiceException;
import io.springboot.twitter.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 
 * 
 * @author Administrator
 *
 */
@ControllerAdvice
public class ExceptionAdvice {

	static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

	static final String ERROR_VIEW = "error/error";

	// 请求路径未找到
	@ExceptionHandler(NoHandlerFoundException.class)
	public Object noHandlerFoundException(HttpServletRequest request, HttpServletResponse response,
			NoHandlerFoundException exception) throws IOException {
		return this.errorHandler(request, response, Message.fail(Message.Code.NOT_FOUND, HttpStatus.NOT_FOUND),
				exception);
	}

	// 上传文件过大
	@ExceptionHandler(value = { MaxUploadSizeExceededException.class })
	public Object maxUploadSizeExceededException(HttpServletRequest request, HttpServletResponse response,
			MaxUploadSizeExceededException exception) throws IOException {
		return this.errorHandler(request, response,
				Message.fail(Message.Code.BAD_REQUEST, "文件过大，不能超过：" + exception.getMaxUploadSize() + " 字节"), exception);
	}

	// 请求方式不支持
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Object httpRequestMethodNotSupportedException(HttpServletRequest request, HttpServletResponse response,
			HttpRequestMethodNotSupportedException exception) {
		return this.errorHandler(request, response, Message.fail(Message.Code.BAD_REQUEST, "请求方法不支持"), exception);
	}

	// 缺少必须参数
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public Object missingServletRequestParameterException(HttpServletRequest request, HttpServletResponse response,
			MissingServletRequestParameterException exception) {
		return this.errorHandler(request, response,
				Message.fail(Message.Code.BAD_REQUEST, "缺少必须参数：" + exception.getParameterName()), exception);
	}

	// 业务异常
	@ExceptionHandler(ServiceException.class)
	public Object businessException(HttpServletRequest request, HttpServletResponse response,
			ServiceException exception) {
		return this.errorHandler(request, response, exception.message(), exception);
	}

	// 非法请求
	@ExceptionHandler(value = { HttpMessageNotReadableException.class, IllegalArgumentException.class,
			MethodArgumentTypeMismatchException.class, HttpMediaTypeNotSupportedException.class,
			ServletRequestBindingException.class })
	public Object badRequestException(HttpServletRequest request, HttpServletResponse response, Exception e)
			throws IOException {
		return this.errorHandler(request, response, Message.fail(Message.Code.BAD_REQUEST, "非法请求:" + e.getMessage()), e);
	}

	@ExceptionHandler(value = { BindException.class })
	public Object validateFail(HttpServletRequest request, HttpServletResponse response, BindException e)
			throws IOException {
		String errorMessage = e.getAllErrors().stream().map(ObjectError::getDefaultMessage).limit(1)
				.collect(Collectors.toList()).get(0);
		return this.errorHandler(request, response, Message.fail(Message.Code.BAD_REQUEST, errorMessage), e);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public Object validateFail(HttpServletRequest request, HttpServletResponse response, ConstraintViolationException e)
			throws IOException {
//		e.getMessage();   提示信息会包含前缀：[方法名].[参数名]: 
		String errorMessage = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).limit(1)
				.collect(Collectors.toList()).get(0);
		return this.errorHandler(request, response, Message.fail(Message.Code.BAD_REQUEST, errorMessage), e);
	}

	// 系统异常
	@ExceptionHandler(Throwable.class)
	public Object exception(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Throwable throwable) {

		// TODO 记录日志

		String tackTrace = WebUtils.throwableStackTrace(throwable);
		LOGGER.error(tackTrace);

		return this.errorHandler(httpServletRequest, httpServletResponse,
				Message.fail(Message.Code.INTERNAL_SERVER_ERROR,
						"服务器异常(" + throwable.getClass().getName() + "):" + throwable.getMessage(),
						HttpStatus.INTERNAL_SERVER_ERROR),
				throwable);
	}

	protected Object errorHandler(HttpServletRequest request, HttpServletResponse response, Message<Void> message,
			Throwable throwable) {

//		if (throwable instanceof ServiceException) {
//			// 原始异常
//			Throwable rawThrowable = ((ServiceException) throwable).throwable();
//		}

		MediaType mediaType = WebUtils.acceptMediaType(request);

		if (mediaType.equalsTypeAndSubtype(MediaType.TEXT_HTML)) {
			ModelAndView modelAndView = new ModelAndView(ERROR_VIEW, message.getHttpStatus());
			modelAndView.addObject("message", message);
			// TODO 不要在生产环境中渲染完整的堆栈信息
			modelAndView.addObject("stackTrace", WebUtils.throwableStackTrace(throwable));
			return modelAndView;
		} else if (mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)) {
			return ResponseEntity.status(message.getHttpStatus()).contentType(MediaType.APPLICATION_JSON).body(message);
		} else {
			return ResponseEntity.status(message.getHttpStatus()).contentType(MediaType.TEXT_PLAIN)
					.body(message.getMessage());
		}
	}
}
