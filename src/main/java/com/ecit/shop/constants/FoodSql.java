package com.ecit.shop.constants;

/**
 * Created by shwang on 2018/2/5.
 */
public interface FoodSql {

    /**
     * 查询食物数量
     */
    String SELECT_FOOD_ROWNUM_SQL = "select count(id) row_num from t_food where is_deleted = ?";

    /**
     * 查询食物信息
     */
    String SELECT_FOOD_SQL = "select id, name, alias, food_url, heat_value, unit, cardinal_number, is_deleted, version from t_food where is_deleted = ? limit ? offset ?";

    /**
     * 根据id查询食物信息
     */
    String SELECT_FOOD_BY_ID_SQL = "select id::text, name, alias, food_url, heat_value, unit, cardinal_number, is_deleted, version from t_food where id = ? and is_deleted = ?";
}