package com.blazemaple.forum;

import com.blazemaple.forum.common.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ForumApplication.class)
class ForumApplicationTests {

    @Autowired
    private MailClient mailClient;

    @Test
    public void testSentMail(){
        mailClient.sendMail("193237147@qq.com","TEST","测试");
    }
}
