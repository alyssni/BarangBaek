package com.barangbaek.dao;

import com.barangbaek.bean.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class wishlistdao {

    private final Connection conn;

    public wishlistdao(Connection conn) {
        this.conn = conn;
    }

    public boolean isWishlisted(int userID, int itemID)
            throws SQLException {

        String sql = "SELECT WishlistItemID FROM WISHLIST_ITEMS "
                + "WHERE UserID = ? AND ItemID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, itemID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean addToWishlist(int userID, int itemID)
            throws SQLException {

        if (isWishlisted(userID, itemID)) {
            return true;
        }

        String sql = "INSERT INTO WISHLIST_ITEMS "
                + "(UserID, ItemID, AddedAt) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, itemID);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeFromWishlist(int userID, int itemID)
            throws SQLException {

        String sql = "DELETE FROM WISHLIST_ITEMS "
                + "WHERE UserID = ? AND ItemID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, itemID);
            return ps.executeUpdate() > 0;
        }
    }

    public int countWishlistItems(int userID)
            throws SQLException {

        String sql = "SELECT COUNT(*) AS Total "
                + "FROM WISHLIST_ITEMS WHERE UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("Total") : 0;
            }
        }
    }

    public Set<Integer> getWishlistItemIDs(int userID)
            throws SQLException {

        Set<Integer> itemIDs = new HashSet<Integer>();
        String sql = "SELECT ItemID FROM WISHLIST_ITEMS WHERE UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    itemIDs.add(rs.getInt("ItemID"));
                }
            }
        }

        return itemIDs;
    }

    public List<item> getWishlistItems(int userID)
            throws SQLException {

        List<item> items = new ArrayList<item>();

        String sql = "SELECT i.*, c.CategoryName, "
                + "u.FullName AS SellerName "
                + "FROM WISHLIST_ITEMS w "
                + "INNER JOIN ITEMS i ON w.ItemID = i.ItemID "
                + "LEFT JOIN CATEGORIES c "
                + "ON i.CategoryID = c.CategoryID "
                + "LEFT JOIN USERS u "
                + "ON i.SellerID = u.UserID "
                + "WHERE w.UserID = ? "
                + "ORDER BY w.AddedAt DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItem(rs));
                }
            }
        }

        return items;
    }

    private item mapItem(ResultSet rs)
            throws SQLException {

        item mapped = new item();

        mapped.setItemID(rs.getInt("ItemID"));
        mapped.setSellerID(rs.getInt("SellerID"));
        mapped.setCategoryID(rs.getInt("CategoryID"));
        mapped.setItemName(rs.getString("ItemName"));
        mapped.setItemDesc(rs.getString("ItemDesc"));
        mapped.setItemPhoto(rs.getString("ItemPhoto"));
        mapped.setItemCondition(rs.getString("ItemCondition"));
        mapped.setItemStatus(rs.getString("ItemStatus"));
        mapped.setStock(rs.getInt("Stock"));
        mapped.setPrice(rs.getDouble("Price"));
        mapped.setCreatedAt(rs.getTimestamp("CreatedAt"));
        mapped.setCategoryName(rs.getString("CategoryName"));
        mapped.setSellerName(rs.getString("SellerName"));

        return mapped;
    }
}