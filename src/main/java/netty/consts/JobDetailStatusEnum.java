package netty.consts;

public enum JobDetailStatusEnum {
    TASK_RUNNING(4, "任务运行中"),
    TASK_STAGING(3, "任务启动"),
    TASK_FINISHED(5, "任务完成");
    private Integer code;
    private String desc;

    JobDetailStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
