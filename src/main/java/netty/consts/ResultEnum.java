package netty.consts;

public enum ResultEnum {
    ARGS_INVALID(2001, "args invalid.");
    private Integer code;
    private String desc;
    ResultEnum(Integer code,String descs){
        this.code=code;
        this.desc=descs;

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
