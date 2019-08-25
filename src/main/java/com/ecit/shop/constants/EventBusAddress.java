package com.ecit.shop.constants;

public interface EventBusAddress {

    /**
     * 查询食物
     */
    public static final String SEARCH_FOOD = "searchFood";
    /**
     * 食物详情
     */
    public static final String FOOD_DETAIL = "foodDetail";
    /**
     * 热量详情
     */
    public static final String HEAT_DETAIL = "heatDetail";
    /**
     * 授权
     */
    public static final String ACCREDIT = "accredit";
    /**
     * 检查校验token
     */
    public static final String USER_CHECK = "userCheck";
    /**
     * 获取用户信息
     */
    public static final String GET_USER = "getUser";
    /**
     * 更新用户信息
     */
    public static final String UPDATE_USER = "updateUser";
    /**
     * 初始化luence 索引
     */
    public static final String INIT_FOOD = "initFood";
}
