package com.barangbaek.dao;

import com.barangbaek.bean.item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class itemdao {

    private final Connection conn;

    public itemdao(Connection conn) {
        this.conn = conn;
    }

    public List<item> getAvailableItems(
            String keyword,
            Integer categoryID
    ) throws SQLException {

        return getAvailableItems(
                keyword,
                categoryID,
                "",
                null,
                null,
                "newest"
        );
    }

    public List<item> getAvailableItems(
            String keyword,
            Integer categoryID,
            String condition,
            Double minimumPrice,
            Double maximumPrice,
            String sortBy
    ) throws SQLException {

        List<item> items = new ArrayList<item>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT i.*, c.CategoryName, ");
        sql.append("u.FullName AS SellerName ");
        sql.append("FROM ITEMS i ");
        sql.append("LEFT JOIN CATEGORIES c ");
        sql.append("ON i.CategoryID = c.CategoryID ");
        sql.append("LEFT JOIN USERS u ");
        sql.append("ON i.SellerID = u.UserID ");
        sql.append("WHERE i.ItemStatus = 'Available' ");
        sql.append("AND i.Stock > 0 ");

        boolean hasKeyword =
                keyword != null
                && !keyword.trim().isEmpty();

        boolean hasCategory =
                categoryID != null
                && categoryID > 0;

        boolean hasCondition =
                condition != null
                && !condition.trim().isEmpty();

        boolean hasMinimumPrice =
                minimumPrice != null
                && minimumPrice >= 0;

        boolean hasMaximumPrice =
                maximumPrice != null
                && maximumPrice >= 0;

        if (hasKeyword) {
            sql.append("AND (LOWER(i.ItemName) LIKE LOWER(?) ");
            sql.append("OR LOWER(i.ItemDesc) LIKE LOWER(?) ");
            sql.append("OR LOWER(c.CategoryName) LIKE LOWER(?)) ");
        }

        if (hasCategory) {
            sql.append("AND i.CategoryID = ? ");
        }

        if (hasCondition) {
            sql.append("AND i.ItemCondition = ? ");
        }

        if (hasMinimumPrice) {
            sql.append("AND i.Price >= ? ");
        }

        if (hasMaximumPrice) {
            sql.append("AND i.Price <= ? ");
        }

        sql.append(buildSortClause(sortBy));

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int parameterIndex = 1;

            if (hasKeyword) {
                String search = "%" + keyword.trim() + "%";
                ps.setString(parameterIndex++, search);
                ps.setString(parameterIndex++, search);
                ps.setString(parameterIndex++, search);
            }

            if (hasCategory) {
                ps.setInt(parameterIndex++, categoryID);
            }

            if (hasCondition) {
                ps.setString(parameterIndex++, condition.trim());
            }

            if (hasMinimumPrice) {
                ps.setDouble(parameterIndex++, minimumPrice);
            }

            if (hasMaximumPrice) {
                ps.setDouble(parameterIndex, maximumPrice);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        }

        return items;
    }

    private String buildSortClause(String sortBy) {
        if ("oldest".equalsIgnoreCase(sortBy)) {
            return "ORDER BY i.CreatedAt ASC";
        }

        if ("priceLow".equalsIgnoreCase(sortBy)) {
            return "ORDER BY i.Price ASC, i.CreatedAt DESC";
        }

        if ("priceHigh".equalsIgnoreCase(sortBy)) {
            return "ORDER BY i.Price DESC, i.CreatedAt DESC";
        }

        if ("nameAZ".equalsIgnoreCase(sortBy)) {
            return "ORDER BY LOWER(i.ItemName) ASC";
        }

        if ("condition".equalsIgnoreCase(sortBy)) {
            return "ORDER BY CASE i.ItemCondition "
                    + "WHEN 'New' THEN 1 "
                    + "WHEN 'Like New' THEN 2 "
                    + "WHEN 'Good' THEN 3 "
                    + "WHEN 'Fair' THEN 4 "
                    + "WHEN 'Poor' THEN 5 "
                    + "ELSE 6 END, i.CreatedAt DESC";
        }

        return "ORDER BY i.CreatedAt DESC";
    }

    public List<item> getAllAvailableItems()
            throws SQLException {

        return getAvailableItems("", null);
    }

    public List<item> searchAvailableItems(String keyword)
            throws SQLException {

        return getAvailableItems(keyword, null);
    }

    public List<item> getItemsBySeller(int sellerID)
            throws SQLException {

        return getItemsBySeller(sellerID, "All");
    }

    public List<item> getItemsBySeller(
            int sellerID,
            String statusFilter
    ) throws SQLException {

        List<item> items = new ArrayList<item>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT i.*, c.CategoryName, ");
        sql.append("u.FullName AS SellerName ");
        sql.append("FROM ITEMS i ");
        sql.append("LEFT JOIN CATEGORIES c ");
        sql.append("ON i.CategoryID = c.CategoryID ");
        sql.append("LEFT JOIN USERS u ");
        sql.append("ON i.SellerID = u.UserID ");
        sql.append("WHERE i.SellerID = ? ");

        String filter = statusFilter == null
                ? "All"
                : statusFilter.trim();

        if ("Available".equalsIgnoreCase(filter)) {
            sql.append("AND i.ItemStatus = 'Available' AND i.Stock > 0 ");
        } else if ("Sold".equalsIgnoreCase(filter)) {
            sql.append("AND i.ItemStatus = 'Sold' ");
        } else if ("Unavailable".equalsIgnoreCase(filter)) {
            sql.append("AND i.ItemStatus = 'Unavailable' ");
        } else if ("OutOfStock".equalsIgnoreCase(filter)) {
            sql.append("AND i.Stock = 0 ");
        }

        sql.append("ORDER BY i.CreatedAt DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, sellerID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        }

        return items;
    }

    public item getItemById(int itemID)
            throws SQLException {

        String sql =
                "SELECT i.*, c.CategoryName, "
                + "u.FullName AS SellerName "
                + "FROM ITEMS i "
                + "LEFT JOIN CATEGORIES c "
                + "ON i.CategoryID = c.CategoryID "
                + "LEFT JOIN USERS u "
                + "ON i.SellerID = u.UserID "
                + "WHERE i.ItemID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        }

        return null;
    }

    public item getOwnedItem(
            int itemID,
            int sellerID
    ) throws SQLException {

        String sql =
                "SELECT i.*, c.CategoryName, "
                + "u.FullName AS SellerName "
                + "FROM ITEMS i "
                + "LEFT JOIN CATEGORIES c "
                + "ON i.CategoryID = c.CategoryID "
                + "LEFT JOIN USERS u "
                + "ON i.SellerID = u.UserID "
                + "WHERE i.ItemID = ? AND i.SellerID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemID);
            ps.setInt(2, sellerID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToItem(rs);
                }
            }
        }

        return null;
    }

    public boolean addItem(item item)
            throws SQLException {

        String sql =
                "INSERT INTO ITEMS (SellerID, CategoryID, "
                + "ItemName, ItemDesc, ItemPhoto, ItemCondition, "
                + "ItemStatus, Stock, Price, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            ps.setInt(1, item.getSellerID());
            ps.setInt(2, item.getCategoryID());
            ps.setString(3, item.getItemName());
            ps.setString(4, item.getItemDesc());
            ps.setString(
                    5,
                    item.getItemPhoto() != null
                    ? item.getItemPhoto()
                    : "default-item.png"
            );
            ps.setString(6, item.getItemCondition());
            ps.setString(
                    7,
                    item.getItemStatus() != null
                    ? item.getItemStatus()
                    : "Available"
            );
            ps.setInt(8, item.getStock());
            ps.setDouble(9, item.getPrice());
            ps.setTimestamp(
                    10,
                    new Timestamp(System.currentTimeMillis())
            );

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        item.setItemID(rs.getInt(1));
                    }
                }
            }

            return affectedRows > 0;
        }
    }

    public boolean updateItem(item item)
            throws SQLException {

        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            String sql =
                    "UPDATE ITEMS SET CategoryID = ?, ItemName = ?, "
                    + "ItemDesc = ?, ItemPhoto = ?, ItemCondition = ?, "
                    + "ItemStatus = ?, Stock = ?, Price = ? "
                    + "WHERE ItemID = ? AND SellerID = ?";

            int affectedRows;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, item.getCategoryID());
                ps.setString(2, item.getItemName());
                ps.setString(3, item.getItemDesc());
                ps.setString(4, item.getItemPhoto());
                ps.setString(5, item.getItemCondition());
                ps.setString(6, item.getItemStatus());
                ps.setInt(7, item.getStock());
                ps.setDouble(8, item.getPrice());
                ps.setInt(9, item.getItemID());
                ps.setInt(10, item.getSellerID());

                affectedRows = ps.executeUpdate();
            }

            if (affectedRows > 0
                    && (!"Available".equals(item.getItemStatus())
                    || item.getStock() <= 0)) {

                removeItemFromAllCarts(item.getItemID());
            }

            conn.commit();
            return affectedRows > 0;

        } catch (SQLException e) {
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public boolean updateItemStatus(
            int itemID,
            int sellerID,
            String status
    ) throws SQLException {

        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            if ("Available".equals(status)) {
                String stockSql =
                        "SELECT Stock FROM ITEMS "
                        + "WHERE ItemID = ? AND SellerID = ?";

                try (PreparedStatement ps = conn.prepareStatement(stockSql)) {
                    ps.setInt(1, itemID);
                    ps.setInt(2, sellerID);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt("Stock") <= 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            String sql =
                    "UPDATE ITEMS SET ItemStatus = ? "
                    + "WHERE ItemID = ? AND SellerID = ?";

            int affectedRows;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setInt(2, itemID);
                ps.setInt(3, sellerID);
                affectedRows = ps.executeUpdate();
            }

            if (affectedRows > 0 && !"Available".equals(status)) {
                removeItemFromAllCarts(itemID);
            }

            conn.commit();
            return affectedRows > 0;

        } catch (SQLException e) {
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    /**
     * Returns deleted, deactivated, not_found, or failed.
     */
    public String removeListingSafely(
            int itemID,
            int sellerID
    ) throws SQLException {

        boolean originalAutoCommit = conn.getAutoCommit();

        try {
            conn.setAutoCommit(false);

            if (getOwnedItem(itemID, sellerID) == null) {
                conn.rollback();
                return "not_found";
            }

            removeItemFromAllCarts(itemID);

            if (itemHasOrderHistory(itemID)) {
                String deactivateSql =
                        "UPDATE ITEMS SET ItemStatus = 'Unavailable' "
                        + "WHERE ItemID = ? AND SellerID = ?";

                try (PreparedStatement ps = conn.prepareStatement(deactivateSql)) {
                    ps.setInt(1, itemID);
                    ps.setInt(2, sellerID);

                    if (ps.executeUpdate() > 0) {
                        conn.commit();
                        return "deactivated";
                    }
                }

                conn.rollback();
                return "failed";
            }

            String deleteSql =
                    "DELETE FROM ITEMS "
                    + "WHERE ItemID = ? AND SellerID = ?";

            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, itemID);
                ps.setInt(2, sellerID);

                if (ps.executeUpdate() > 0) {
                    conn.commit();
                    return "deleted";
                }
            }

            conn.rollback();
            return "failed";

        } catch (SQLException e) {
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public boolean updateStock(
            int itemID,
            int newStock
    ) throws SQLException {

        String sql =
                "UPDATE ITEMS SET Stock = ? WHERE ItemID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, itemID);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean reduceStockForPurchase(
            int itemID,
            int quantity
    ) throws SQLException {

        String reduceSql =
                "UPDATE ITEMS SET Stock = Stock - ? "
                + "WHERE ItemID = ? "
                + "AND ItemStatus = 'Available' "
                + "AND Stock >= ?";

        try (PreparedStatement ps = conn.prepareStatement(reduceSql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, itemID);
            ps.setInt(3, quantity);

            if (ps.executeUpdate() == 0) {
                return false;
            }
        }

        String statusSql =
                "UPDATE ITEMS SET ItemStatus = 'Sold' "
                + "WHERE ItemID = ? AND Stock = 0";

        try (PreparedStatement ps = conn.prepareStatement(statusSql)) {
            ps.setInt(1, itemID);
            ps.executeUpdate();
        }

        return true;
    }

    private boolean itemHasOrderHistory(int itemID)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) AS Total "
                + "FROM ORDER_ITEMS WHERE ItemID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemID);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("Total") > 0;
            }
        }
    }

    private void removeItemFromAllCarts(int itemID)
            throws SQLException {

        String sql =
                "DELETE FROM CART_ITEMS WHERE ItemID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemID);
            ps.executeUpdate();
        }
    }

    private item mapResultSetToItem(ResultSet rs)
            throws SQLException {

        item i = new item();

        i.setItemID(rs.getInt("ItemID"));
        i.setSellerID(rs.getInt("SellerID"));
        i.setCategoryID(rs.getInt("CategoryID"));
        i.setItemName(rs.getString("ItemName"));
        i.setItemDesc(rs.getString("ItemDesc"));
        i.setItemPhoto(rs.getString("ItemPhoto"));
        i.setItemCondition(rs.getString("ItemCondition"));
        i.setItemStatus(rs.getString("ItemStatus"));
        i.setStock(rs.getInt("Stock"));
        i.setPrice(rs.getDouble("Price"));
        i.setCreatedAt(rs.getTimestamp("CreatedAt"));

        try {
            i.setCategoryName(rs.getString("CategoryName"));
        } catch (SQLException ignored) {
        }

        try {
            i.setSellerName(rs.getString("SellerName"));
        } catch (SQLException ignored) {
        }

        return i;
    }
}