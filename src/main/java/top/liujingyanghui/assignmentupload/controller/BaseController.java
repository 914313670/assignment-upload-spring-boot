package top.liujingyanghui.assignmentupload.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.liujingyanghui.assignmentupload.config.UrlConfig;
import top.liujingyanghui.assignmentupload.vo.Result;

import java.io.File;

/**
 * 公共控制器
 */
@RestController
@RequestMapping("api/base")
public class BaseController {
    @Autowired
    private UrlConfig urlConfig;

    @GetMapping("deleteFile")
    public Result deleteFile(String url) {
        String path = StringUtils.substringAfter(urlConfig.getBaseUrl(), url);
        path = urlConfig.getUploadBaseUrl() + path;
        File file = new File(path);
        if (file.exists()) {//文件是否存在
            if (file.delete()) {//存在就删了，返回1
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } else {
            return Result.error("文件不存在");
        }
    }
}
