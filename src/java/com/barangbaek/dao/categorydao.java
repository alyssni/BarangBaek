package com.barangbaek.dao;

import com.barangbaek.bean.category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class categorydao {

    private Connection conn;

    public categorydao(Connection conn) {
        this.conn = conn;
    }

    public List<category> getAllCategories() throws SQLException {
        List<category> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIES ORDER BY CategoryName";

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            category c = new category();
            c.setCategoryID(rs.getInt("CategoryID"));
            c.setCategoryName(rs.getString("CategoryName"));
            c.setCategoryDesc(rs.getString("CategoryDesc"));
            categories.add(c);
        }

        rs.close();
        stmt.close();
        return categories;
    }
}