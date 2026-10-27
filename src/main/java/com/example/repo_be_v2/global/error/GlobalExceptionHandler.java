package com.example.repo_be_v2.global.error;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * 컨트롤러에서 올라온 예외를 공통 에러 응답으로 바꾼다.
 *
 * 보낸 쪽 잘못(4xx)은 흔한 일이라 warn 한 줄만 남기고, 스택 트레이스는 서버 잘못(5xx)일 때만 남긴다.
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

    // 필수 쿼리 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return missing("필수 파라미터 누락", e.getParameterName());
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
     * JSON 문법이 깨졌거나, enum에 없는 값이 온 경우가 여기로 온다.
     * 보낸 쪽 잘못이므로 500이 아니라 400으로 돌려준다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("요청 본문 파싱 실패: {}", e.getMessage());

        return toResponse(ErrorResponse.of(ErrorCode.BAD_REQUEST, "요청 본문을 읽을 수 없습니다."));
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

    // 존재하지 않는 경로
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("존재하지 않는 경로 요청: {}", e.getResourcePath());

        return toResponse(ErrorResponse.of(ErrorCode.NOT_FOUND));
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
}
