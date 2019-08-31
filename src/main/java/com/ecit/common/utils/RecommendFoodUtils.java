package com.ecit.common.utils;

import org.apache.commons.lang3.StringUtils;

public class RecommendFoodUtils {

    /**
     * 推荐食物
     * @param foodName
     * @return
     */
    public static String randomFoodName(String foodName){
        if (StringUtils.isNotEmpty(foodName)) {
            return foodName;
        }
        return "青菜";
    }
}
