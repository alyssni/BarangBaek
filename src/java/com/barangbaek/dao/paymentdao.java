package com.barangbaek.dao;

import com.barangbaek.bean.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class paymentdao {

    private final Connection conn;

    public paymentdao(Connection conn) {
        this.conn = conn;
    }

    public int createPayment(payment p)
            throws SQLException {

        String sql =
                "INSERT INTO PAYMENTS "
                + "(OrderID, PaymentMethod, PaymentStatus, "
                + "PaymentDateTime) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            ps.setInt(1, p.getOrderID());
            ps.setString(2, p.getPaymentMethod());
            ps.setString(3, p.getPaymentStatus());

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        return -1;
    }

    public payment getPaymentByOrder(int orderID)
            throws SQLException {

        String sql =
                "SELECT * FROM PAYMENTS WHERE OrderID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    payment p = new payment();
                    p.setPaymentID(rs.getInt("PaymentID"));
                    p.setOrderID(rs.getInt("OrderID"));
                    p.setPaymentMethod(
                            rs.getString("PaymentMethod")
                    );
                    p.setPaymentStatus(
                            rs.getString("PaymentStatus")
                    );
                    p.setPaymentDateTime(
                            rs.getTimestamp("PaymentDateTime")
                    );
                    return p;
                }
            }
        }

        return null;
    }

    public boolean markCashOnPickupAsPaid(int orderID)
            throws SQLException {

        String sql =
                "UPDATE PAYMENTS SET PaymentStatus = 'Paid', "
                + "PaymentDateTime = CURRENT_TIMESTAMP "
                + "WHERE OrderID = ? "
                + "AND PaymentMethod = 'Cash on Pickup'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ps.executeUpdate();
            return true;
        }
    }
}