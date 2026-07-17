package com.barangbaek.controller.publicview;

import com.barangbaek.bean.category;
import com.barangbaek.bean.item;
import com.barangbaek.dao.categorydao;
import com.barangbaek.dao.itemdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebServlet;

public class publicdashboardservlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private Connection getConnection() throws Exception {
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        return DriverManager.getConnection(
                "jdbc:derby://localhost:1527/barangbaek_db",
                "app",
                "app"
        );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userID") != null) {
            response.sendRedirect(
                    request.getContextPath() + "/item?action=dashboard"
            );
            return;
        }

        String action = request.getParameter("action");
        if (action == null || "dashboard".equals(action)) {
            showDashboard(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/public?action=dashboard"
        );
    }

    private void showDashboard(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String keyword = clean(request.getParameter("keyword"));
        Integer categoryID = parseCategoryID(
                request.getParameter("categoryID")
        );
        String condition = normaliseCondition(
                request.getParameter("condition")
        );
        Double minimumPrice = parsePrice(
                request.getParameter("minPrice")
        );
        Double maximumPrice = parsePrice(
                request.getParameter("maxPrice")
        );
        String sortBy = normaliseSort(
                request.getParameter("sort")
        );

        if (minimumPrice != null
                && maximumPrice != null
                && minimumPrice > maximumPrice) {
            Double temporary = minimumPrice;
            minimumPrice = maximumPrice;
            maximumPrice = temporary;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            categorydao categoryDao = new categorydao(conn);

            List<item> items = itemDao.getAvailableItems(
                    keyword,
                    categoryID,
                    condition,
                    minimumPrice,
                    maximumPrice,
                    sortBy
            );

            List<category> categories = categoryDao.getAllCategories();

            request.setAttribute("items", items);
            request.setAttribute("categories", categories);
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedCategoryID", categoryID);
            request.setAttribute("selectedCondition", condition);
            request.setAttribute("minimumPrice", minimumPrice);
            request.setAttribute("maximumPrice", maximumPrice);
            request.setAttribute("selectedSort", sortBy);
            request.setAttribute("resultCount", items.size());

            request.getRequestDispatcher(
                    "/public/dashboard.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "The marketplace could not be loaded. Please check the database connection."
            );

            request.getRequestDispatcher(
                    "/public/dashboard.jsp"
            ).forward(request, response);
        }
    }

    private Double parsePrice(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            double parsed = Double.parseDouble(value.trim());
            return parsed >= 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normaliseCondition(String condition) {
        if ("New".equals(condition)
                || "Like New".equals(condition)
                || "Good".equals(condition)
                || "Fair".equals(condition)
                || "Poor".equals(condition)) {
            return condition;
        }

        return "";
    }

    private String normaliseSort(String sortBy) {
        if ("oldest".equals(sortBy)
                || "priceLow".equals(sortBy)
                || "priceHigh".equals(sortBy)
                || "nameAZ".equals(sortBy)
                || "condition".equals(sortBy)) {
            return sortBy;
        }

        return "newest";
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer parseCategoryID(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            int id = Integer.parseInt(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}