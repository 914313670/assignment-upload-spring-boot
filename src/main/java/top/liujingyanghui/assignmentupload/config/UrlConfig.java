package top.liujingyanghui.assignmentupload.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * url配置类
 */
@Component
@Data
public class UrlConfig {
    /**
     * 根路由
     */
    @Value("${base.url}")
    private String baseUrl;

    /**
     * 上传根路由
     */
    @Value("${upload.base.url}")
    private String uploadBaseUrl;
}
