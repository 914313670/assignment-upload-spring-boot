package top.liujingyanghui.assignmentupload.vo;

import lombok.Data;

@Data
public class Result {

    private int status;

    private String message;

    private Object data;

    public Result(Object data) {
        this.status = ResultEnum.SUCCESS.getStatus();
        this.message = ResultEnum.SUCCESS.getMessage();
        this.data = data;
    }

    public Result(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public Result(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static Result success() {
        return new Result(ResultEnum.SUCCESS.getStatus(), ResultEnum.SUCCESS.getMessage());
    }

    public static  Result success(Object data) {
        return new Result(ResultEnum.SUCCESS.getStatus(), ResultEnum.SUCCESS.getMessage(), data);
    }

    public static  Result success(String message, Object data) {
        return new Result(0, message, data);
    }

    public static  Result success(int status, String message, Object data) {
        return new Result(status, message, data);
    }

    public static Result success(ResultEnum resultEnum) {
        return new Result(resultEnum.getStatus(), resultEnum.getMessage());
    }

    public static  Result success(ResultEnum resultEnum, Object data) {
        return new Result(resultEnum.getStatus(), resultEnum.getMessage(), data);
    }

    public static Result error() {
        return new Result(ResultEnum.ERROR.getStatus(), ResultEnum.ERROR.getMessage());
    }

    public static  Result error(Object data) {
        return new Result(ResultEnum.ERROR.getStatus(), data.toString());
    }

    public static  Result error(String message, Object data) {
        return new Result(0, message, data);
    }

    public static  Result error(int status, String message) {
        return new Result(status, message, null);
    }

    public static  Result error(int status, String message,Object data) {
        return new Result(status, message, data);
    }

    public static Result error(ResultEnum resultEnum) {
        return new Result(resultEnum.getStatus(), resultEnum.getMessage());
    }

    public static  Result error(ResultEnum resultEnum, Object data) {
        return new Result(resultEnum.getStatus(), resultEnum.getMessage(), data);
    }
}
