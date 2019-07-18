package com.codecool.web.servlet;

import com.codecool.web.dao.database.DatabaseCouponDao;
import com.codecool.web.model.Coupon;
import com.codecool.web.model.User;
import com.codecool.web.service.exception.ServiceException;
import com.codecool.web.service.simple.SimpleCouponService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/protected/profile")
public final class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        try(Connection connection = dataSource.getConnection()) {
            DatabaseCouponDao dbc = new DatabaseCouponDao(connection);
            SimpleCouponService scs = new SimpleCouponService(dbc);
            User user = (User) req.getSession().getAttribute("user");
            req.setAttribute("user", user);
            req.setAttribute("coupons", scs.getCoupons(String.valueOf(user.getId())));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
        }


        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }
}
