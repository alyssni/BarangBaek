package com.barangbaek.dao;

import com.barangbaek.bean.cartitem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class cartdao {

    private final Connection conn;

    public cartdao(Connection conn) {
        this.conn = conn;
    }

    public List<cartitem> getCartItems(int userID)
            throws SQLException {

        List<cartitem> cartItems = new ArrayList<cartitem>();

        String sql =
                "SELECT c.CartItemID, c.UserID, c.ItemID, "
                + "c.Quantity, c.AddedAt, "
                + "i.ItemName, i.ItemPhoto, i.ItemCondition, "
                + "i.ItemStatus, i.Stock, i.Price, "
                + "i.SellerID, u.FullName AS SellerName "
                + "FROM CART_ITEMS c "
                + "INNER JOIN ITEMS i ON c.ItemID = i.ItemID "
                + "INNER JOIN USERS u ON i.SellerID = u.UserID "
                + "WHERE c.UserID = ? "
                + "ORDER BY c.AddedAt DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cartItems.add(mapCartItem(rs));
                }
            }
        }

        return cartItems;
    }

    public List<cartitem> getCartItemsByIDs(
            int userID,
            List<Integer> cartItemIDs
    ) throws SQLException {

        List<cartitem> selectedItems = new ArrayList<cartitem>();

        if (cartItemIDs == null || cartItemIDs.isEmpty()) {
            return selectedItems;
        }

        for (Integer cartItemID : cartItemIDs) {
            if (cartItemID == null) {
                continue;
            }

            cartitem selectedItem = getCartItemByID(
                    cartItemID,
                    userID
            );

            if (selectedItem != null) {
                selectedItems.add(selectedItem);
            }
        }

        return selectedItems;
    }

    public cartitem getCartItemByID(
            int cartItemID,
            int userID
    ) throws SQLException {

        String sql =
                "SELECT c.CartItemID, c.UserID, c.ItemID, "
                + "c.Quantity, c.AddedAt, "
                + "i.ItemName, i.ItemPhoto, i.ItemCondition, "
                + "i.ItemStatus, i.Stock, i.Price, "
                + "i.SellerID, u.FullName AS SellerName "
                + "FROM CART_ITEMS c "
                + "INNER JOIN ITEMS i ON c.ItemID = i.ItemID "
                + "INNER JOIN USERS u ON i.SellerID = u.UserID "
                + "WHERE c.CartItemID = ? AND c.UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemID);
            ps.setInt(2, userID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCartItem(rs);
                }
            }
        }

        return null;
    }

    public int getCartQuantity(
            int userID,
            int itemID
    ) throws SQLException {

        String sql =
                "SELECT Quantity FROM CART_ITEMS "
                + "WHERE UserID = ? AND ItemID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            ps.setInt(2, itemID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Quantity");
                }
            }
        }

        return 0;
    }

    public boolean addOrUpdateCartItem(
            int userID,
            int itemID,
            int quantity
    ) throws SQLException {

        int existingQuantity = getCartQuantity(userID, itemID);

        if (existingQuantity > 0) {
            String updateSql =
                    "UPDATE CART_ITEMS SET Quantity = ?, "
                    + "AddedAt = CURRENT_TIMESTAMP "
                    + "WHERE UserID = ? AND ItemID = ?";

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, existingQuantity + quantity);
                ps.setInt(2, userID);
                ps.setInt(3, itemID);

                return ps.executeUpdate() > 0;
            }
        }

        String insertSql =
                "INSERT INTO CART_ITEMS "
                + "(UserID, ItemID, Quantity, AddedAt) "
                + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userID);
            ps.setInt(2, itemID);
            ps.setInt(3, quantity);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateQuantity(
            int cartItemID,
            int userID,
            int quantity
    ) throws SQLException {

        String sql =
                "UPDATE CART_ITEMS SET Quantity = ? "
                + "WHERE CartItemID = ? AND UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartItemID);
            ps.setInt(3, userID);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeCartItem(
            int cartItemID,
            int userID
    ) throws SQLException {

        String sql =
                "DELETE FROM CART_ITEMS "
                + "WHERE CartItemID = ? AND UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemID);
            ps.setInt(2, userID);

            return ps.executeUpdate() > 0;
        }
    }

    public int removeCartItems(
            int userID,
            List<Integer> cartItemIDs
    ) throws SQLException {

        if (cartItemIDs == null || cartItemIDs.isEmpty()) {
            return 0;
        }

        String sql =
                "DELETE FROM CART_ITEMS "
                + "WHERE CartItemID = ? AND UserID = ?";

        int removed = 0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer cartItemID : cartItemIDs) {
                if (cartItemID == null) {
                    continue;
                }

                ps.setInt(1, cartItemID);
                ps.setInt(2, userID);
                removed += ps.executeUpdate();
            }
        }

        return removed;
    }

    public int clearCart(int userID)
            throws SQLException {

        String sql =
                "DELETE FROM CART_ITEMS WHERE UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            return ps.executeUpdate();
        }
    }

    public int getCartItemCount(int userID)
            throws SQLException {

        String sql =
                "SELECT COALESCE(SUM(Quantity), 0) AS CartCount "
                + "FROM CART_ITEMS WHERE UserID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CartCount");
                }
            }
        }

        return 0;
    }

    private cartitem mapCartItem(ResultSet rs)
            throws SQLException {

        cartitem c = new cartitem();

        c.setCartItemID(rs.getInt("CartItemID"));
        c.setUserID(rs.getInt("UserID"));
        c.setItemID(rs.getInt("ItemID"));
        c.setQuantity(rs.getInt("Quantity"));
        c.setAddedAt(rs.getTimestamp("AddedAt"));

        c.setItemName(rs.getString("ItemName"));
        c.setItemPhoto(rs.getString("ItemPhoto"));
        c.setItemCondition(rs.getString("ItemCondition"));
        c.setItemStatus(rs.getString("ItemStatus"));
        c.setStock(rs.getInt("Stock"));
        c.setPrice(rs.getDouble("Price"));

        c.setSellerID(rs.getInt("SellerID"));
        c.setSellerName(rs.getString("SellerName"));

        return c;
    }
}