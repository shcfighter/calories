package com.ecit.shop.constants;

/**
 * Created by shwang on 2018/2/5.
 */
public interface HeatSql {

    /**
     * 根据食物id查询热量信息
     */
    String SELECT_FOOD_BY_FOODID_SQL = "select id::text, food_id, heat_name, heat_value, unit, cardinal_number, is_deleted, version from t_heat where food_id = ? and is_deleted = ?";
}