package top.liujingyanghui.assignmentupload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Token配置
 */
@Component
@Data
public class TokenConfig {

    /**
     * Token请求头key Authorization
     */
    @Value("${jwt.header}")
    private String tokenHeader;

    /**
     * Token头 Bearer
     */
    @Value("${jwt.tokenHead}")
    private String tokenHead;
}
