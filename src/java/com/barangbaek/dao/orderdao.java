package com.barangbaek.dao;

import com.barangbaek.bean.order;
import com.barangbaek.bean.orderitem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class orderdao {

    private final Connection conn;

    public orderdao(Connection conn) {
        this.conn = conn;
    }

    public List<order> getOrdersByBuyer(int buyerID)
            throws SQLException {

        List<order> orders = new ArrayList<order>();

        String sql =
                "SELECT o.*, u1.FullName AS BuyerName, "
                + "u2.FullName AS SellerName "
                + "FROM ORDERS o "
                + "INNER JOIN USERS u1 ON o.BuyerID = u1.UserID "
                + "INNER JOIN USERS u2 ON o.SellerID = u2.UserID "
                + "WHERE o.BuyerID = ? "
                + "ORDER BY o.OrderDateTime DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, buyerID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }

        return orders;
    }

    public List<order> getOrdersBySeller(int sellerID)
            throws SQLException {

        List<order> orders = new ArrayList<order>();

        String sql =
                "SELECT o.*, u1.FullName AS BuyerName, "
                + "u2.FullName AS SellerName "
                + "FROM ORDERS o "
                + "INNER JOIN USERS u1 ON o.BuyerID = u1.UserID "
                + "INNER JOIN USERS u2 ON o.SellerID = u2.UserID "
                + "WHERE o.SellerID = ? "
                + "ORDER BY o.OrderDateTime DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }

        return orders;
    }

    public order getOrderById(int orderID)
            throws SQLException {

        String sql =
                "SELECT o.*, u1.FullName AS BuyerName, "
                + "u2.FullName AS SellerName "
                + "FROM ORDERS o "
                + "INNER JOIN USERS u1 ON o.BuyerID = u1.UserID "
                + "INNER JOIN USERS u2 ON o.SellerID = u2.UserID "
                + "WHERE o.OrderID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        }

        return null;
    }

    public List<orderitem> getOrderItemsByOrder(int orderID)
            throws SQLException {

        List<orderitem> items = new ArrayList<orderitem>();

        String sql =
                "SELECT oi.*, i.ItemName, i.ItemPhoto "
                + "FROM ORDER_ITEMS oi "
                + "INNER JOIN ITEMS i ON oi.ItemID = i.ItemID "
                + "WHERE oi.OrderID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orderitem oi = new orderitem();
                    oi.setOrderID(rs.getInt("OrderID"));
                    oi.setItemID(rs.getInt("ItemID"));
                    oi.setQuantity(rs.getInt("Quantity"));
                    oi.setPriceAtPurchase(
                            rs.getDouble("PriceAtPurchase")
                    );
                    oi.setItemName(rs.getString("ItemName"));
                    oi.setItemPhoto(rs.getString("ItemPhoto"));
                    items.add(oi);
                }
            }
        }

        return items;
    }

    public int createOrder(order o)
            throws SQLException {

        String sql =
                "INSERT INTO ORDERS "
                + "(BuyerID, SellerID, TotalAmount, "
                + "OrderStatus, OrderDateTime) "
                + "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            ps.setInt(1, o.getBuyerID());
            ps.setInt(2, o.getSellerID());
            ps.setDouble(3, o.getTotalAmount());
            ps.setString(4, o.getOrderStatus());

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

    public boolean addOrderItem(orderitem oi)
            throws SQLException {

        String sql =
                "INSERT INTO ORDER_ITEMS "
                + "(OrderID, ItemID, Quantity, PriceAtPurchase) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, oi.getOrderID());
            ps.setInt(2, oi.getItemID());
            ps.setInt(3, oi.getQuantity());
            ps.setDouble(4, oi.getPriceAtPurchase());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateOrderStatus(
            int orderID,
            String status
    ) throws SQLException {

        String sql =
                "UPDATE ORDERS SET OrderStatus = ? "
                + "WHERE OrderID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderID);

            return ps.executeUpdate() > 0;
        }
    }

    private order mapOrder(ResultSet rs)
            throws SQLException {

        order o = new order();

        o.setOrderID(rs.getInt("OrderID"));
        o.setBuyerID(rs.getInt("BuyerID"));
        o.setSellerID(rs.getInt("SellerID"));
        o.setOrderDateTime(rs.getTimestamp("OrderDateTime"));
        o.setTotalAmount(rs.getDouble("TotalAmount"));
        o.setOrderStatus(rs.getString("OrderStatus"));
        o.setBuyerName(rs.getString("BuyerName"));
        o.setSellerName(rs.getString("SellerName"));

        return o;
    }
}