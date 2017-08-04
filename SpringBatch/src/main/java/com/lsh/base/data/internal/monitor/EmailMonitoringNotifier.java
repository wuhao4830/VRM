package com.lsh.base.data.internal.monitor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import com.lsh.base.data.utils.MailUtils;
import com.lsh.base.data.utils.SmsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by wuhao on 16/5/28.
 */
public class EmailMonitoringNotifier  implements BatchMonitoringNotifier{
    private static final Logger logger = LoggerFactory.getLogger(EmailMonitoringNotifier.class);
    private String formatExceptionMessage(Throwable exception) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }

    private String createMessageContent(JobExecution jobExecution) {
        List<Throwable> exceptions = jobExecution.getFailureExceptions();
        StringBuilder content = new StringBuilder();
        content.append("Job execution #");
        content.append("updateDeliveryJob");
        content.append(" failed with following exceptions:");
        content.append(" 库存同步失败");
        for (Throwable exception : exceptions) {
            content.append("");
            content.append(formatExceptionMessage(exception));
        }
        return content.toString();
    }

    public void notify(JobExecution jobExecution) {
        String content = createMessageContent(jobExecution);
        try {
            MailUtils.send("美批库存数据同步异常",content);
            SmsUtils.sender(content);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

}
