package top.liujingyanghui.assignmentupload.service;

/**
 * @author wdh
 * @date 2019/12/26 18:27
 */
public interface EmailService {
    /**
     * 发送简单邮件
     *
     * @param to 收件人邮箱
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleEmail(String to, String subject, String content);

    /**
     * 发送html格式邮件
     *
     * @param to
     * @param subject
     * @param content
     */
    void sendHtmlEmail(String to, String subject, String content);

    /**
     * 发送带附件的邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param filePath
     */
    void sendAttachmentsEmail(String to, String subject, String content, String filePath);

    /**
     * 发送带静态资源的邮件
     *
     * @param to
     * @param subject
     * @param content
     * @param rscPath
     * @param rscId
     */
    void sendInlineResourceEmail(String to, String subject, String content, String rscPath, String rscId);
}
