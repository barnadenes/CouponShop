package com.codecool.web.dao.database;

import com.codecool.web.dao.CouponDao;
import com.codecool.web.model.Coupon;
import com.codecool.web.service.exception.ServiceException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseCouponDao extends AbstractDao implements CouponDao {

    public DatabaseCouponDao(Connection connection) {
        super(connection);
    }

    @Override
    public List<Coupon> findAll(int id) throws SQLException {
        List<Coupon> coupons = new ArrayList<>();
        String sql = "SELECT coupons.id, user_id, name, percentage FROM coupons JOIN users on coupons.user_id = users.id WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
             statement.setInt(1, id);
             ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                coupons.add(fetchCoupon(resultSet));
            }
        }
        return coupons;
    }

    @Override
    public List<Coupon> couponShopUserFilter(int userID, int shopID) throws SQLException {
        List<Coupon> coupons = new ArrayList<>();
        String sql = "select coupons.id, coupons.name, percentage, user_id from coupons\n " +
            "join coupons_shops on coupons_shops.coupon_id = coupons.id\n " +
            "join shops on coupons_shops.shop_id = shops.id\n " +
            "where user_id = ? and shop_id = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userID);
            statement.setInt(2, shopID);
            ResultSet rs = statement.executeQuery();
            while(rs.next()) {
                coupons.add(fetchCoupon(rs));
            }
        }
        return coupons;
    }

    @Override
    public Coupon findById(int id) throws SQLException {
        String sql = "SELECT coupons.id, user_id, name, percentage FROM coupons JOIN users on coupons.user_id = users.id WHERE coupons.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return fetchCoupon(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public Coupon add(String name, int percentage, int user_id) throws SQLException {
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO coupons (name, percentage, user_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.setInt(2, percentage);
            statement.setInt(3, user_id);
            executeInsert(statement);
            int id = fetchGeneratedId(statement);
            connection.commit();
            return new Coupon(id, name, percentage, user_id);
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    @Override
    public void add(int couponId, int... shopIds) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        String sql = "INSERT INTO coupons_shops (coupon_id, shop_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, couponId);
            for (int shopId : shopIds) {
                statement.setInt(2, shopId);
                executeInsert(statement);
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    private Coupon fetchCoupon(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        int percentage = resultSet.getInt("percentage");
        int user_id = resultSet.getInt("user_id");
        return new Coupon(id, name, percentage, user_id);
    }
}
