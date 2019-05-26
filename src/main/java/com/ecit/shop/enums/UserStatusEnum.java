package com.ecit.shop.enums;

/**
 * Created by shwang on 2018/2/5.
 */
public enum UserStatusEnum {
    /**
     * 0=未激活
     */
    INACTIVATED(0, "未激活"),
    /**
     *1=激活
     */
    ACTIVATION(1, "激活"),
    /**
     * 2=禁用
     */
    DISABLED(2, "禁用"),
    /**
     *-1=删除
     */
    DELETED(-1, "删除");

    private int status;
    private String desc;

    UserStatusEnum() {
    }

    UserStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
