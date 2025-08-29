package auruda.auruda.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 이메일 서비스
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * [서비스 로직]
     * 메일 전송
     * @param recipient 수신자 이메일
     * @param subject 이메일 제목
     * @param content 이메일 내용
     */
    public void sendEmail(String recipient, String subject, String content) {
        try{
            //SimpleMailMessage 객체 생성
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipient); //수신자 이메일
            message.setSubject(subject); //이메일 제목
            message.setText(content); // 이메일 내용
            message.setFrom("아우르다 <dlwnsgkr8318@gmail.com>");

            //이메일 전송
            mailSender.send(message);
            System.out.println("이메일 전송 성공!");

        }catch (Exception e){
            System.out.println("이메일 전송 실패: "+e.getMessage());
        }
    }

}
