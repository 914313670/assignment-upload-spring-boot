package top.liujingyanghui.assignmentupload.utils;

import java.util.Random;

/**
 * @author wdh
 * @date 2019/12/30 10:48
 */
public class CommonUtil {

    /**
     * 验证码生成
     *
     * @param length 验证码位数
     * @return
     */
    public static String createCode(int length) {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}
