package netty.consts;

public enum JobTypeEnum {
    TIMER(1, "定时任务"),
    TRIGGER(2, "触发任务");

    private int code;
    private String memo;

    private JobTypeEnum(int tyep, String memo) {
        this.code = tyep;
        this.memo = memo;
    }

    public int getCode() {
        return this.code;
    }

    public String getMemo() {
        return this.memo;
    }

    public static String getMemo(int type) {
        JobTypeEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            JobTypeEnum typeEnum = var1[var3];
            if (typeEnum.getCode() == type) {
                return typeEnum.getMemo();
            }
        }

        return null;
    }
}
