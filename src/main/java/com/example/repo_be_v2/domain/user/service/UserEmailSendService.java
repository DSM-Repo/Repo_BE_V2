package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.EmailAlreadyExistsException;
import com.example.repo_be_v2.domain.user.exception.EmailSendFailedException;
import com.example.repo_be_v2.domain.user.exception.EmailVerificationRequestLimitException;
import com.example.repo_be_v2.domain.user.presentation.dto.request.EmailVerificationSendRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateEngineException;
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
    private static final String MAIL_TEMPLATE = "mail/email-verification";
    private static final String LOGO_CONTENT_ID = "repo-logo";
    private static final ClassPathResource LOGO = new ClassPathResource("mail/repo-logo.png");

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final ITemplateEngine templateEngine;
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
        }catch (MailException | MessagingException | TemplateEngineException e){
            redisTemplate.delete(cooldownKey);
            redisTemplate.delete(codeKey);
            throw new EmailSendFailedException(e);
        }
    }

    /**
     * 인증 메일을 만든다.
     *
     * HTML을 못 띄우는 메일 앱을 위해 같은 내용의 텍스트 본문도 함께 담는다.
     * 로고는 메일에 첨부하고 cid로 건다. 외부 주소로 걸면 메일 앱이 이미지를 막을 수 있고,
     * 원본 SVG는 Gmail 등에서 그려지지 않기 때문이다.
     */
    private MimeMessage createMessage(String email, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailSender);
        helper.setTo(email);
        helper.setSubject("[REPO] 이메일 인증 코드");
        helper.setText(
                "이메일 인증 코드는 " + code + "입니다. " + CODE_EXPIRATION.toMinutes() + "분 안에 입력해 주세요.",
                renderHtml(code)
        );
        //본문을 넣은 뒤에 붙여야 메일 앱이 cid를 제대로 찾는다.
        helper.addInline(LOGO_CONTENT_ID, LOGO, "image/png");

        return message;
    }

    private String renderHtml(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("expireMinutes", CODE_EXPIRATION.toMinutes());
        context.setVariable("logoUrl", "cid:" + LOGO_CONTENT_ID);

        return templateEngine.process(MAIL_TEMPLATE, context);
    }

    private String generateCode() { //인증코드 생성 메서드
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
