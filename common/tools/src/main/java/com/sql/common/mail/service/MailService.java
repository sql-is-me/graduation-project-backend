package com.sql.common.mail.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import com.sql.common.exception.ServiceException;
import com.sql.common.mail.constants.MailConstants;
import com.sql.common.redis.service.RedisService;

@Component
@ConditionalOnProperty(name = "spring.mail.username")
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisService redisService;

    @Value("${spring.mail.username}")
    private String mailFrom;

    /**
     * 生成6位随机验证码
     */
    public String generateEmailCode() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }

    /**
     * 发送验证码邮件
     */
    public void sendEmailCode(String email, String emailCode) {
        MimeMessagePreparator msg = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("【LoveSport】 邮箱验证码");
            message.setText(
                    "<div style='background-color:#0d1117; color:#ffffff; border:1px solid #30363d; padding: 20px; border-radius: 8px; font-family: Arial, sans-serif; font-size: 16px;'>"
                            + "<h2 style='color:#58a6ff;'>LoveSport 安全验证</h2>"
                            + "<p style='margin-top:10px;'>您好，</p>"
                            + "<p>这是您的 <strong>LoveSport</strong> 账号生成的临时验证码：</p>"
                            + "<p style='font-size:32px; font-weight:bold; color:#ffffff; background-color:#21262d; padding:10px 15px; display:inline-block; border-radius:6px; border:1px dashed #58a6ff;'>"
                            + emailCode + "</p>"
                            + "<p style='margin-top:20px;'>有效期5分钟</p>"
                            + "<p style='margin-top:20px;'>如非本人操作,请忽略此邮件</p>"
                            + "<hr style='margin-top:30px; border:none; border-top:1px solid #30363d;'>"
                            + "<p style='font-size:12px; color:#8b949e;'>loveSport 官方团队</p>"
                            + "</div>",
                    true);
        };
        mailSender.send(msg);
    }

    /**
     * 将邮箱验证码存入redis
     * 
     * @param email
     * @return emailCode
     */
    public String setEmailCode2Cache(String email) {
        String codeKey = MailConstants.EMAIL_CODE_KEY + email;
        String emailCode = generateEmailCode();
        redisService.setCacheObject(codeKey, emailCode, MailConstants.EMAIL_CODE_EXPIRATION, TimeUnit.MINUTES);

        return emailCode;
    }

    /**
     * 验证邮箱验证码是否有效
     * 
     * 若无效直接抛出ServiceException
     * 
     * @param email
     * @param emailCode
     */
    public void verifyEmailCode(String email, String emailCode) {
        String codeKey = MailConstants.EMAIL_CODE_KEY + email;
        String cachedCode = redisService.getCacheObject(codeKey);

        if (cachedCode == null) {
            throw new ServiceException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(emailCode)) {
            throw new ServiceException("验证码错误");
        }
        // 验证码使用后立即删除
        redisService.deleteObject(codeKey);
    }

    /**
     * 更改绑定的邮箱时，给老邮箱发送警告
     * 
     * @param email
     */
    public void sendWarningEmail(String email) {
        MimeMessagePreparator msg = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("【LoveSport】 警告信息");
            message.setText(
                    "<div style='background-color:#0d1117; color:#ffffff; border:1px solid #30363d; padding: 20px; border-radius: 8px; font-family: Arial, sans-serif; font-size: 16px;'>"
                            + "<h2 style='color:#58a6ff;'>LoveSport 警告信息</h2>"
                            + "<p style='margin-top:10px;'>您好，</p>"
                            + "<p style='margin-top:20px;'>您的账号正在更改绑定邮箱</p>"
                            + "<p style='margin-top:20px;'>如非本人操作,请立即联系工作人员</p>"
                            + "<hr style='margin-top:30px; border:none; border-top:1px solid #30363d;'>"
                            + "<p style='font-size:12px; color:#8b949e;'>loveSport 官方团队</p>"
                            + "</div>",
                    true);
        };
        mailSender.send(msg);
    }
}
