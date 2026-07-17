package com.barangbaek.dao;

import com.barangbaek.bean.parcel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class parceldao {

    private final Connection conn;

    public parceldao(Connection conn) {
        this.conn = conn;
    }

    public int createParcel(parcel p)
            throws SQLException {

        String sql =
                "INSERT INTO PARCELS "
                + "(OrderID, DeliveryType, PickupLocation, "
                + "TrackingNumber, DeliveryStatus) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            ps.setInt(1, p.getOrderID());
            ps.setString(2, p.getDeliveryType());
            ps.setString(3, p.getPickupLocation());
            ps.setString(4, p.getTrackingNumber());
            ps.setString(5, p.getDeliveryStatus());

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

    public parcel getParcelByOrder(int orderID)
            throws SQLException {

        String sql =
                "SELECT p.*, o.BuyerID, o.SellerID, "
                + "o.TotalAmount, o.OrderStatus, o.OrderDateTime, "
                + "buyer.FullName AS BuyerName, "
                + "seller.FullName AS SellerName "
                + "FROM PARCELS p "
                + "INNER JOIN ORDERS o ON p.OrderID = o.OrderID "
                + "INNER JOIN USERS buyer ON o.BuyerID = buyer.UserID "
                + "INNER JOIN USERS seller ON o.SellerID = seller.UserID "
                + "WHERE p.OrderID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapParcel(rs);
                }
            }
        }

        return null;
    }

    public parcel getParcelByID(int parcelID)
            throws SQLException {

        String sql =
                "SELECT p.*, o.BuyerID, o.SellerID, "
                + "o.TotalAmount, o.OrderStatus, o.OrderDateTime, "
                + "buyer.FullName AS BuyerName, "
                + "seller.FullName AS SellerName "
                + "FROM PARCELS p "
                + "INNER JOIN ORDERS o ON p.OrderID = o.OrderID "
                + "INNER JOIN USERS buyer ON o.BuyerID = buyer.UserID "
                + "INNER JOIN USERS seller ON o.SellerID = seller.UserID "
                + "WHERE p.ParcelID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parcelID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapParcel(rs);
                }
            }
        }

        return null;
    }

    public List<parcel> getParcelsByBuyer(int buyerID)
            throws SQLException {

        return getParcelsForUser("o.BuyerID", buyerID);
    }

    public List<parcel> getParcelsBySeller(int sellerID)
            throws SQLException {

        return getParcelsForUser("o.SellerID", sellerID);
    }

    private List<parcel> getParcelsForUser(
            String userColumn,
            int userID
    ) throws SQLException {

        List<parcel> parcels = new ArrayList<parcel>();

        String sql =
                "SELECT p.*, o.BuyerID, o.SellerID, "
                + "o.TotalAmount, o.OrderStatus, o.OrderDateTime, "
                + "buyer.FullName AS BuyerName, "
                + "seller.FullName AS SellerName "
                + "FROM PARCELS p "
                + "INNER JOIN ORDERS o ON p.OrderID = o.OrderID "
                + "INNER JOIN USERS buyer ON o.BuyerID = buyer.UserID "
                + "INNER JOIN USERS seller ON o.SellerID = seller.UserID "
                + "WHERE " + userColumn + " = ? "
                + "ORDER BY o.OrderDateTime DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    parcels.add(mapParcel(rs));
                }
            }
        }

        return parcels;
    }

    public boolean updateParcelBySeller(
            int parcelID,
            int sellerID,
            String trackingNumber,
            String deliveryStatus
    ) throws SQLException {

        String sql =
                "UPDATE PARCELS SET "
                + "TrackingNumber = ?, "
                + "DeliveryStatus = ?, "
                + "UpdatedAt = CURRENT_TIMESTAMP "
                + "WHERE ParcelID = ? "
                + "AND OrderID IN ("
                + "SELECT OrderID FROM ORDERS "
                + "WHERE SellerID = ?"
                + ")";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingNumber);
            ps.setString(2, deliveryStatus);
            ps.setInt(3, parcelID);
            ps.setInt(4, sellerID);

            return ps.executeUpdate() > 0;
        }
    }

    private parcel mapParcel(ResultSet rs)
            throws SQLException {

        parcel p = new parcel();

        p.setParcelID(rs.getInt("ParcelID"));
        p.setOrderID(rs.getInt("OrderID"));
        p.setDeliveryType(rs.getString("DeliveryType"));
        p.setPickupLocation(rs.getString("PickupLocation"));
        p.setTrackingNumber(rs.getString("TrackingNumber"));
        p.setDeliveryStatus(rs.getString("DeliveryStatus"));
        p.setUpdatedAt(rs.getTimestamp("UpdatedAt"));

        p.setBuyerID(rs.getInt("BuyerID"));
        p.setSellerID(rs.getInt("SellerID"));
        p.setBuyerName(rs.getString("BuyerName"));
        p.setSellerName(rs.getString("SellerName"));
        p.setTotalAmount(rs.getDouble("TotalAmount"));
        p.setOrderStatus(rs.getString("OrderStatus"));
        p.setOrderDateTime(rs.getTimestamp("OrderDateTime"));

        return p;
    }
}