package top.liujingyanghui.assignmentupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.liujingyanghui.assignmentupload.config.TokenConfig;
import top.liujingyanghui.assignmentupload.config.UrlConfig;
import top.liujingyanghui.assignmentupload.utils.JwtUtil;
import top.liujingyanghui.assignmentupload.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 公共控制器
 */
@RestController
@RequestMapping("api/base")
public class BaseController {
    @Autowired
    private UrlConfig urlConfig;
    @Autowired
    private TokenConfig tokenConfig;

//    /**
//     * 文件删除
//     * @param url
//     * @return
//     */
//    @GetMapping("deleteFile")
//    public Result deleteFile(String url) {
//        String path = StringUtils.substringAfter(urlConfig.getBaseUrl(), url);
//        path = urlConfig.getUploadBaseUrl() + path;
//        File file = new File(path);
//        if (file.exists()) {//文件是否存在
//            if (file.delete()) {//存在就删了，返回1
//                return Result.success();
//            } else {
//                return Result.error("删除失败");
//            }
//        } else {
//            return Result.error("文件不存在");
//        }
//    }

    /**
     * 公共文件上传
     * @param request
     * @param file
     * @return
     */
    @PostMapping("upload")
    public Result upload(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return Result.error("上传失败");
        }
        String fileName = file.getOriginalFilename();
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        StringBuilder tempName = new StringBuilder();
        String token = request.getHeader(tokenConfig.getTokenHeader()).substring(tokenConfig.getTokenHead().length());
        Long id = JwtUtil.getSubject(token);
        String newFileName=id.toString()+"_"+sdf.format(new Date())+suffixName;
        tempName.append(urlConfig.getUploadBaseUrl()+"common/").append(newFileName);
        String fielPath = tempName.toString();
        File dest = new File(fielPath);
        // 判断路径是否存在，如果不存在则创建
        if(!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            // 保存文件
            file.transferTo(dest);
            return Result.success(urlConfig.getBaseUrl()+"resource/common/"+newFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error("上传失败");
    }
}
