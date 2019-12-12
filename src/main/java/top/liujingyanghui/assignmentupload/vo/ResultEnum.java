package top.liujingyanghui.assignmentupload.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResultEnum {

    SUCCESS(0,"请求成功"),
    ERROR(1,"未知错误"),

    USER_IS_EXIST(2,"用户已存在"),
    SCHOOL_IS_EXIST(3,"该学校已存在"),
    INPUT_IS_NULL(4,"输入的值为空"),
    CLASS_IS_EXIST(5,"该学校已存在"),
    NOT_JOIN_CLASS(6,"请先加入班级"),
    ;
    private int status;
    private String message;
}
