package com.ecit.shop.enums;

public enum ErrcodeEnum {

    /**
     * 系统繁忙
     */
    SERVER_BUSY(-1, "系统繁忙"),
    /**
     * 请求成功
     */
    OK(0, "请求成功"),
    /**
     * code 无效
     */
    INVALID_CODE(40029, "code 无效"),
    /**
     * 频率限制
     */
    FREQUENCY_LIMITATION(45011, "频率限制");

    int key;
    String value;

    ErrcodeEnum() {
    }

    ErrcodeEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
