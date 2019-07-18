package com.codecool.web.dao;

import com.codecool.web.model.Coupon;

import java.sql.SQLException;
import java.util.List;

public interface CouponDao {

    List<Coupon> findAll(int id) throws SQLException;

    Coupon findById(int id) throws SQLException;

    Coupon add(String name, int percentage, int user_id) throws SQLException;

    void add(int couponId, int... shopIds) throws SQLException;
}
