package com.example.repo_be_v2.global.error;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.util.List;

/**
 * 컨트롤러에서 올라온 예외를 공통 에러 응답으로 바꾼다.
 *
 * 보낸 쪽 잘못(4xx)은 흔한 일이라 warn 한 줄만 남기고, 스택 트레이스는 서버 잘못(5xx)일 때만 남긴다.
 * 필터 단계에서 끝나는 인증 실패는 여기까지 오지 않고 SecurityErrorResponder가 같은 형식으로 응답한다.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 비즈니스 로직 에러
    @ExceptionHandler(REPOException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(REPOException e) {
        ErrorCode errorCode = e.getErrorCode();

        if (errorCode.getStatusCode() >= 500) {
            log.error("비즈니스 로직 처리중 에러: {}", errorCode, e);
        } else {
            log.warn("비즈니스 로직 처리중 에러: {} - {}", errorCode, e.getMessage());
        }

        return toResponse(ErrorResponse.of(errorCode, e.getMessage()));
    }

    // DTO 필드 검증 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldErrorResponse> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(this::toFieldError)
                .toList();

        return validationFailed("DTO 필드 검증 실패", errors);
    }

    // 파라미터 검증 에러 (@Validated가 붙은 빈의 메서드 인자)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
        List<FieldErrorResponse> errors = e.getConstraintViolations()
                .stream()
                .map(violation -> new FieldErrorResponse(lastNodeOf(violation), violation.getMessage()))
                .toList();

        return validationFailed("파라미터 검증 실패", errors);
    }

    // 파라미터 검증 에러 (컨트롤러 인자에 직접 붙인 @Min, @NotBlank 등)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        List<FieldErrorResponse> errors = e.getParameterValidationResults()
                .stream()
                .flatMap(result -> result.getResolvableErrors()
                        .stream()
                        .map(error -> new FieldErrorResponse(
                                error instanceof FieldError fieldError
                                        ? fieldError.getField()
                                        : result.getMethodParameter().getParameterName(),
                                error.getDefaultMessage()
                        )))
                .toList();

        return validationFailed("컨트롤러 인자 검증 실패", errors);
    }

    // 필수 쿼리 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return missing("필수 파라미터 누락", e.getParameterName());
    }

    // 필수 헤더 누락
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        return missing("필수 헤더 누락", e.getHeaderName());
    }

    // multipart 요청에 필요한 파트(파일)가 빠진 경우
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return missing("필수 파트 누락", e.getRequestPartName());
    }

    // 파라미터 타입 불일치 (숫자 자리에 문자가 온 경우 등)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = "%s 값의 형식이 올바르지 않습니다.".formatted(e.getName());
        log.warn("파라미터 타입 불일치: {}={}", e.getName(), e.getValue());

        return toResponse(ErrorResponse.of(
                ErrorCode.BAD_REQUEST,
                message,
                List.of(new FieldErrorResponse(e.getName(), message))
        ));
    }

    /**
     * 요청 본문을 읽지 못한 경우.
     *
     * 본문이 비었거나, JSON 문법이 깨졌거나, 필드 타입이 안 맞는 경우(enum에 없는 값 포함)가 여기로 온다.
     * 보낸 쪽 잘못이므로 500이 아니라 400으로 돌려주고, 어느 필드인지 알 수 있으면 알려준다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("요청 본문 파싱 실패: {}", e.getMessage());

        if (!(e.getCause() instanceof JacksonException cause)) {
            return toResponse(ErrorResponse.of(ErrorCode.BAD_REQUEST, "요청 본문이 필요합니다."));
        }

        if (cause instanceof StreamReadException) {
            return toResponse(ErrorResponse.of(ErrorCode.BAD_REQUEST, "요청 본문의 JSON 형식이 올바르지 않습니다."));
        }

        String field = fieldPathOf(cause);
        if (field.isEmpty()) {
            return toResponse(ErrorResponse.of(ErrorCode.BAD_REQUEST, "요청 본문을 읽을 수 없습니다."));
        }

        String message = cause instanceof InvalidFormatException invalidFormat && invalidFormat.getTargetType().isEnum()
                ? "%s 값은 %s 중 하나여야 합니다.".formatted(field, List.of(invalidFormat.getTargetType().getEnumConstants()))
                : "%s 값의 형식이 올바르지 않습니다.".formatted(field);

        return toResponse(ErrorResponse.of(
                ErrorCode.BAD_REQUEST,
                message,
                List.of(new FieldErrorResponse(field, message))
        ));
    }

    // 지원하지 않는 메서드 호출
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("지원하지 않는 메서드 호출: {}", e.getMethod());

        return toResponse(ErrorResponse.of(
                ErrorCode.METHOD_NOT_ALLOWED,
                "%s 메서드는 이 경로에서 지원하지 않습니다.".formatted(e.getMethod())
        ));
    }

    // 지원하지 않는 Content-Type (JSON 자리에 form을 보낸 경우 등)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        log.warn("지원하지 않는 Content-Type: {}", e.getContentType());

        return toResponse(ErrorResponse.of(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "Content-Type은 %s 중 하나여야 합니다.".formatted(e.getSupportedMediaTypes())
        ));
    }

    // 지원하지 않는 Accept
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException e) {
        log.warn("지원하지 않는 Accept: {}", e.getMessage());

        return toResponse(ErrorResponse.of(ErrorCode.NOT_ACCEPTABLE));
    }

    // 업로드 용량 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("업로드 용량 초과: {}", e.getMessage());

        return toResponse(ErrorResponse.of(ErrorCode.PAYLOAD_TOO_LARGE, "업로드할 수 있는 파일 크기를 넘었습니다."));
    }

    // multipart 요청이 아니거나 형식이 깨진 경우
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException e) {
        log.warn("multipart 요청 처리 실패: {}", e.getMessage());

        return toResponse(ErrorResponse.of(ErrorCode.BAD_REQUEST, "multipart/form-data 형식으로 요청해야 합니다."));
    }

    // 존재하지 않는 경로
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("존재하지 않는 경로 요청: {}", e.getResourcePath());

        return toResponse(ErrorResponse.of(ErrorCode.NOT_FOUND));
    }

    // 컨트롤러 안쪽에서 올라온 인증 실패. 필터 단계의 인증 실패는 SecurityErrorResponder가 처리한다.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn("인증 실패: {}", e.getMessage());

        return toResponse(ErrorResponse.of(ErrorCode.UNAUTHORIZED));
    }

    // 컨트롤러 안쪽에서 올라온 권한 부족. 아래 Exception 핸들러가 먼저 잡아 500으로 바꾸지 않게 따로 받는다.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("권한 부족: {}", e.getMessage());

        return toResponse(ErrorResponse.of(ErrorCode.FORBIDDEN));
    }

    /**
     * 유니크 제약에 걸린 경우.
     *
     * 서비스에서 미리 중복을 확인해도 동시에 들어온 요청은 저장 단계에서야 걸린다.
     * 제약 이름 같은 내부 정보가 메시지에 들어 있어 응답에는 싣지 않는다.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("데이터 제약 위반: {}", e.getMessage());

        return toResponse(ErrorResponse.of(ErrorCode.CONFLICT));
    }

    // 그 외 에러들
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상하지 못한 에러: ", e);

        return toResponse(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    //검증 실패는 걸린 항목을 모두 싣고, description에는 첫 항목의 문구를 둔다.
    private ResponseEntity<ErrorResponse> validationFailed(String logMessage, List<FieldErrorResponse> errors) {
        log.warn("{}: {}", logMessage, errors);

        String description = errors.isEmpty()
                ? ErrorCode.BAD_REQUEST.getErrorMessage()
                : errors.get(0).message();

        return toResponse(ErrorResponse.of(ErrorCode.BAD_REQUEST, description, errors));
    }

    private ResponseEntity<ErrorResponse> missing(String logMessage, String name) {
        String message = "%s 값이 필요합니다.".formatted(name);
        log.warn("{}: {}", logMessage, name);

        return toResponse(ErrorResponse.of(
                ErrorCode.BAD_REQUEST,
                message,
                List.of(new FieldErrorResponse(name, message))
        ));
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorResponse response) {
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.status()));
    }

    //필드에 걸린 검증이면 필드 이름을, 클래스에 걸린 검증이면 객체 이름을 쓴다.
    private FieldErrorResponse toFieldError(ObjectError error) {
        String field = error instanceof FieldError fieldError
                ? fieldError.getField()
                : error.getObjectName();

        return new FieldErrorResponse(field, error.getDefaultMessage());
    }

    //경로가 execute.grade처럼 메서드 이름부터 시작하므로 마지막 마디만 쓴다.
    private String lastNodeOf(ConstraintViolation<?> violation) {
        String name = "";
        for (Path.Node node : violation.getPropertyPath()) {
            name = node.getName();
        }

        return name;
    }

    //pages[0].project.startDate처럼 본문 안에서의 위치를 만든다.
    private String fieldPathOf(JacksonException e) {
        StringBuilder path = new StringBuilder();

        for (JacksonException.Reference reference : e.getPath()) {
            if (reference.getPropertyName() == null) {
                path.append('[').append(reference.getIndex()).append(']');
                continue;
            }

            if (!path.isEmpty()) {
                path.append('.');
            }
            path.append(reference.getPropertyName());
        }

        return path.toString();
    }
}
