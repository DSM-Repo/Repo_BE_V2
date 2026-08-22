package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.EmailAlreadyExistsException;
import com.example.repo_be_v2.domain.user.exception.EmailSendFailedException;
import com.example.repo_be_v2.domain.user.exception.EmailVerificationRequestLimitException;
import com.example.repo_be_v2.domain.user.presentation.dto.request.EmailVerificationSendRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserEmailSendService {
    private static final String CODE_KEY_PREFIX = "email-verification:code:";
    private static final String VERIFIED_KEY_PREFIX = "email-verification:verified:";
    private static final String COOLDOWN_KEY_PREFIX = "email-verification:cooldown:";
    private static final Duration CODE_EXPIRATION = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofMinutes(5);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String emailSender;

    public void execute(EmailVerificationSendRequest request) {
        String email = request.email();
        if (userRepository.existsByStudentEmail(email)) { //이메일 중복 확인
            throw new EmailAlreadyExistsException();
        }

        String cooldownKey = COOLDOWN_KEY_PREFIX + email; //이메일 재전송 쿨타임
        Boolean requestAccepted = redisTemplate.opsForValue()
                .setIfAbsent(cooldownKey, "true", RESEND_COOLDOWN);

        if (!Boolean.TRUE.equals(requestAccepted)) {
            throw new EmailVerificationRequestLimitException();
        }

        //새 인증코드 전송
        String codeKey = CODE_KEY_PREFIX + email;
        String code = generateCode();
        redisTemplate.delete(VERIFIED_KEY_PREFIX + email);
        redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRATION);

        try{ // 오류 생기면 저장한 인증코드 레디스 서버에서 삭제하는 코드
            mailSender.send(createMessage(email,code));
        }catch (MailException mailException){
            redisTemplate.delete(cooldownKey);
            redisTemplate.delete(codeKey);
            throw new EmailSendFailedException(mailException);
        }
    }
    private SimpleMailMessage createMessage(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailSender);
        message.setTo(email);
        message.setSubject("[REPO] 이메일 인증 코드");
        message.setText("이메일 인증 코드는 " + code + "입니다. 5분 안에 입력해 주세요.");
        return message;
    }

    private String generateCode() { //인증코드 생성 메서드
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
