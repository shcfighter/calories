package com.ecit.common.enums;

public enum EventBusStatus {
    NO_DATA(204, "暂无数据"),
    SUCCESS(200, "成功");
    /**
     * event bus 状态
     */
    private int status;
    /**
     * events bus 状态描述
     */
    private String desc;

    EventBusStatus(int status, String desc) {
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
