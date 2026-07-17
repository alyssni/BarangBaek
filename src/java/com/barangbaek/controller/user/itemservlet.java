package com.barangbaek.controller.user;

import com.barangbaek.bean.category;
import com.barangbaek.bean.item;
import com.barangbaek.bean.user;
import com.barangbaek.dao.cartdao;
import com.barangbaek.dao.categorydao;
import com.barangbaek.dao.itemdao;
import com.barangbaek.dao.userdao;
import com.barangbaek.dao.wishlistdao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Set;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class itemservlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_ITEM_PHOTO = "default-item.png";

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
        String action = request.getParameter("action");

        if ("dashboard".equals(action)) {
            showDashboard(request, response);
        } else if ("details".equals(action)) {
            showItemDetails(request, response);
        } else if ("sellerProfile".equals(action)) {
            showSellerProfile(request, response);
        } else if ("mylistings".equals(action)) {
            showMyListings(request, response);
        } else if ("add".equals(action)) {
            showAddForm(request, response);
        } else if ("edit".equals(action)) {
            showEditForm(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=dashboard"
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addItem(request, response);
        } else if ("edit".equals(action)) {
            editItem(request, response);
        } else if ("status".equals(action)) {
            updateStatus(request, response);
        } else if ("delete".equals(action)) {
            removeListing(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=dashboard"
            );
        }
    }

    private void showDashboard(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String keyword = trim(request.getParameter("keyword"));
        Integer categoryID = parsePositiveInteger(
                request.getParameter("categoryID")
        );
        String condition = normaliseConditionFilter(
                request.getParameter("condition")
        );
        Double minimumPrice = parseOptionalPrice(
                request.getParameter("minPrice")
        );
        Double maximumPrice = parseOptionalPrice(
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
            cartdao cartDao = new cartdao(conn);
            wishlistdao wishlistDao = new wishlistdao(conn);

            List<item> items = itemDao.getAvailableItems(
                    keyword,
                    categoryID,
                    condition,
                    minimumPrice,
                    maximumPrice,
                    sortBy
            );

            Integer userID = getLoggedInUserID(request);
            Set<Integer> wishlistItemIDs = null;

            if (userID != null) {
                wishlistItemIDs = wishlistDao.getWishlistItemIDs(userID);
                request.getSession().setAttribute(
                        "wishlistCount",
                        wishlistDao.countWishlistItems(userID)
                );
            }

            request.setAttribute("items", items);
            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );
            request.setAttribute("keyword", keyword);
            request.setAttribute("selectedCategoryID", categoryID);
            request.setAttribute("selectedCondition", condition);
            request.setAttribute("minimumPrice", minimumPrice);
            request.setAttribute("maximumPrice", maximumPrice);
            request.setAttribute("selectedSort", sortBy);
            request.setAttribute("wishlistItemIDs", wishlistItemIDs);
            request.setAttribute("resultCount", items.size());

            refreshCartCount(request, cartDao);

            request.getRequestDispatcher(
                    "/user/dashboard.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(
                    "error",
                    "The marketplace could not be loaded."
            );
            request.getRequestDispatcher(
                    "/user/dashboard.jsp"
            ).forward(request, response);
        }
    }

    private void showItemDetails(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer loggedInUserID = getLoggedInUserID(request);

        if (loggedInUserID == null) {
            redirectToLogin(request, response);
            return;
        }

        Integer itemID = parsePositiveInteger(
                request.getParameter("id")
        );

        if (itemID == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=dashboard"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            userdao userDao = new userdao(conn);
            categorydao categoryDao = new categorydao(conn);
            cartdao cartDao = new cartdao(conn);
            wishlistdao wishlistDao = new wishlistdao(conn);

            item selectedItem = itemDao.getItemById(itemID);

            if (selectedItem == null) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/item?action=dashboard&error=itemNotFound"
                );
                return;
            }

            user seller = userDao.getPublicSellerByID(
                    selectedItem.getSellerID()
            );

            request.setAttribute("item", selectedItem);
            request.setAttribute("seller", seller);
            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );
            request.setAttribute(
                    "wishlisted",
                    wishlistDao.isWishlisted(
                            loggedInUserID,
                            selectedItem.getItemID()
                    )
            );

            request.getSession().setAttribute(
                    "wishlistCount",
                    wishlistDao.countWishlistItems(loggedInUserID)
            );

            refreshCartCount(request, cartDao);

            request.getRequestDispatcher(
                    "/user/itemdetails.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=dashboard&error=itemDetails"
            );
        }
    }

    private void showSellerProfile(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer loggedInUserID = getLoggedInUserID(request);

        if (loggedInUserID == null) {
            redirectToLogin(request, response);
            return;
        }

        Integer sellerID = parsePositiveInteger(
                request.getParameter("sellerID")
        );

        if (sellerID == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=dashboard"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            userdao userDao = new userdao(conn);
            categorydao categoryDao = new categorydao(conn);
            cartdao cartDao = new cartdao(conn);

            user seller = userDao.getPublicSellerByID(sellerID);

            if (seller == null) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/item?action=dashboard&error=sellerNotFound"
                );
                return;
            }

            request.setAttribute("seller", seller);
            request.setAttribute(
                    "sellerItems",
                    itemDao.getItemsBySeller(sellerID)
            );
            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );

            refreshCartCount(request, cartDao);

            request.getRequestDispatcher(
                    "/user/sellerprofile.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=dashboard&error=sellerProfile"
            );
        }
    }

    private void showMyListings(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        String statusFilter = normaliseFilter(
                request.getParameter("status")
        );

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            categorydao categoryDao = new categorydao(conn);
            cartdao cartDao = new cartdao(conn);

            List<item> allItems = itemDao.getItemsBySeller(userID);
            List<item> filteredItems = itemDao.getItemsBySeller(
                    userID,
                    statusFilter
            );

            int availableCount = 0;
            int soldCount = 0;
            int unavailableCount = 0;
            int outOfStockCount = 0;

            for (item listedItem : allItems) {
                if (listedItem.getStock() == 0) {
                    outOfStockCount++;
                }

                if ("Available".equals(listedItem.getItemStatus())
                        && listedItem.getStock() > 0) {
                    availableCount++;
                } else if ("Sold".equals(listedItem.getItemStatus())) {
                    soldCount++;
                } else if ("Unavailable".equals(
                        listedItem.getItemStatus()
                )) {
                    unavailableCount++;
                }
            }

            request.setAttribute("items", filteredItems);
            request.setAttribute("totalCount", allItems.size());
            request.setAttribute("availableCount", availableCount);
            request.setAttribute("soldCount", soldCount);
            request.setAttribute(
                    "unavailableCount",
                    unavailableCount
            );
            request.setAttribute(
                    "outOfStockCount",
                    outOfStockCount
            );
            request.setAttribute("selectedStatus", statusFilter);
            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );

            refreshCartCount(request, cartDao);

            request.getRequestDispatcher(
                    "/user/mylistings.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=dashboard"
            );
        }
    }

    private void showAddForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        forwardItemForm(
                request,
                response,
                "/user/additem.jsp",
                null
        );
    }

    private void showEditForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);
        Integer itemID = parsePositiveInteger(
                request.getParameter("id")
        );

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        if (itemID == null) {
            redirectToInventory(request, response, "", "invalidItem");
            return;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            categorydao categoryDao = new categorydao(conn);
            cartdao cartDao = new cartdao(conn);

            item ownedItem = itemDao.getOwnedItem(itemID, userID);

            if (ownedItem == null) {
                redirectToInventory(
                        request,
                        response,
                        "",
                        "notOwner"
                );
                return;
            }

            request.setAttribute("item", ownedItem);
            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );
            refreshCartCount(request, cartDao);

            request.getRequestDispatcher(
                    "/user/edititem.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            redirectToInventory(
                    request,
                    response,
                    "",
                    "loadFailed"
            );
        }
    }

    private void addItem(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        item submittedItem = buildSubmittedItem(request);
        submittedItem.setSellerID(userID);
        submittedItem.setItemStatus("Available");

        String validationError = validateItem(
                submittedItem,
                false
        );

        if (validationError != null) {
            request.setAttribute("item", submittedItem);
            request.setAttribute("error", validationError);
            forwardItemForm(
                    request,
                    response,
                    "/user/additem.jsp",
                    submittedItem
            );
            return;
        }

        String uploadedPhoto = null;

        try {
            uploadedPhoto = saveUploadedPhoto(
                    request.getPart("itemPhoto"),
                    userID
            );

            submittedItem.setItemPhoto(
                    uploadedPhoto == null
                    ? DEFAULT_ITEM_PHOTO
                    : uploadedPhoto
            );

            try (Connection conn = getConnection()) {
                itemdao itemDao = new itemdao(conn);

                if (!itemDao.addItem(submittedItem)) {
                    throw new ServletException(
                            "The listing could not be saved."
                    );
                }
            }

            redirectToInventory(
                    request,
                    response,
                    "added",
                    ""
            );

        } catch (IllegalArgumentException e) {
            request.setAttribute("item", submittedItem);
            request.setAttribute("error", e.getMessage());
            forwardItemForm(
                    request,
                    response,
                    "/user/additem.jsp",
                    submittedItem
            );

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("item", submittedItem);
            request.setAttribute(
                    "error",
                    "The item could not be added. Please try again."
            );
            forwardItemForm(
                    request,
                    response,
                    "/user/additem.jsp",
                    submittedItem
            );
        }
    }

    private void editItem(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);
        Integer itemID = parsePositiveInteger(
                request.getParameter("itemID")
        );

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        if (itemID == null) {
            redirectToInventory(request, response, "", "invalidItem");
            return;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            item existingItem = itemDao.getOwnedItem(itemID, userID);

            if (existingItem == null) {
                redirectToInventory(
                        request,
                        response,
                        "",
                        "notOwner"
                );
                return;
            }

            item submittedItem = buildSubmittedItem(request);
            submittedItem.setItemID(itemID);
            submittedItem.setSellerID(userID);

            String requestedStatus = normaliseStatus(
                    request.getParameter("itemStatus")
            );

            if (submittedItem.getStock() == 0) {
                requestedStatus = "Sold";
            }

            submittedItem.setItemStatus(requestedStatus);

            String validationError = validateItem(
                    submittedItem,
                    true
            );

            if (validationError != null) {
                submittedItem.setItemPhoto(
                        existingItem.getItemPhoto()
                );
                request.setAttribute("item", submittedItem);
                request.setAttribute("error", validationError);
                forwardItemForm(
                        request,
                        response,
                        "/user/edititem.jsp",
                        submittedItem
                );
                return;
            }

            String uploadedPhoto = saveUploadedPhoto(
                    request.getPart("itemPhoto"),
                    userID
            );

            submittedItem.setItemPhoto(
                    uploadedPhoto == null
                    ? existingItem.getItemPhoto()
                    : uploadedPhoto
            );

            if (submittedItem.getItemPhoto() == null
                    || submittedItem.getItemPhoto().trim().isEmpty()) {
                submittedItem.setItemPhoto(DEFAULT_ITEM_PHOTO);
            }

            if (!itemDao.updateItem(submittedItem)) {
                throw new ServletException(
                        "The listing could not be updated."
                );
            }

            redirectToInventory(
                    request,
                    response,
                    "updated",
                    ""
            );

        } catch (IllegalArgumentException e) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=edit&id="
                    + itemID
                    + "&error=photo"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=edit&id="
                    + itemID
                    + "&error=failed"
            );
        }
    }

    private void updateStatus(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);
        Integer itemID = parsePositiveInteger(
                request.getParameter("itemID")
        );
        String status = normaliseStatus(
                request.getParameter("status")
        );

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        if (itemID == null) {
            redirectToInventory(request, response, "", "invalidItem");
            return;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);

            boolean updated = itemDao.updateItemStatus(
                    itemID,
                    userID,
                    status
            );

            if (updated) {
                redirectToInventory(
                        request,
                        response,
                        "statusUpdated",
                        ""
                );
            } else {
                redirectToInventory(
                        request,
                        response,
                        "",
                        "statusFailed"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectToInventory(
                    request,
                    response,
                    "",
                    "statusFailed"
            );
        }
    }

    private void removeListing(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);
        Integer itemID = parsePositiveInteger(
                request.getParameter("itemID")
        );

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        if (itemID == null) {
            redirectToInventory(request, response, "", "invalidItem");
            return;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            String result = itemDao.removeListingSafely(
                    itemID,
                    userID
            );

            if ("deleted".equals(result)) {
                redirectToInventory(
                        request,
                        response,
                        "deleted",
                        ""
                );
            } else if ("deactivated".equals(result)) {
                redirectToInventory(
                        request,
                        response,
                        "deactivated",
                        ""
                );
            } else {
                redirectToInventory(
                        request,
                        response,
                        "",
                        "deleteFailed"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectToInventory(
                    request,
                    response,
                    "",
                    "deleteFailed"
            );
        }
    }

    private item buildSubmittedItem(HttpServletRequest request) {
        item submittedItem = new item();

        Integer categoryID = parsePositiveInteger(
                request.getParameter("categoryID")
        );

        submittedItem.setCategoryID(
                categoryID == null ? 0 : categoryID
        );
        submittedItem.setItemName(
                trim(request.getParameter("itemName"))
        );
        submittedItem.setItemDesc(
                trim(request.getParameter("itemDesc"))
        );
        submittedItem.setItemCondition(
                trim(request.getParameter("itemCondition"))
        );
        submittedItem.setStock(
                parseNonNegativeInteger(
                        request.getParameter("stock")
                )
        );
        submittedItem.setPrice(
                parseNonNegativeDouble(
                        request.getParameter("price")
                )
        );

        return submittedItem;
    }

    private String validateItem(
            item submittedItem,
            boolean editing
    ) {

        if (submittedItem.getCategoryID() <= 0) {
            return "Please choose an item category.";
        }

        if (submittedItem.getItemName().isEmpty()
                || submittedItem.getItemName().length() > 100) {
            return "Item name must contain 1 to 100 characters.";
        }

        if (submittedItem.getItemDesc().isEmpty()
                || submittedItem.getItemDesc().length() > 1000) {
            return "Description must contain 1 to 1000 characters.";
        }

        if (!isAllowedCondition(
                submittedItem.getItemCondition()
        )) {
            return "Please select a valid item condition.";
        }

        if (submittedItem.getPrice() <= 0
                || submittedItem.getPrice() > 99999999.99) {
            return "Price must be greater than RM 0.00.";
        }

        if (!editing && submittedItem.getStock() < 1) {
            return "A new listing must have at least one item in stock.";
        }

        if (editing && submittedItem.getStock() < 0) {
            return "Stock cannot be negative.";
        }

        return null;
    }

    private void forwardItemForm(
            HttpServletRequest request,
            HttpServletResponse response,
            String jspPath,
            item formItem
    ) throws ServletException, IOException {

        try (Connection conn = getConnection()) {
            categorydao categoryDao = new categorydao(conn);
            cartdao cartDao = new cartdao(conn);

            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );

            if (formItem != null) {
                request.setAttribute("item", formItem);
            }

            refreshCartCount(request, cartDao);

            request.getRequestDispatcher(jspPath).forward(
                    request,
                    response
            );

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String saveUploadedPhoto(
            Part photoPart,
            int userID
    ) throws IOException {

        if (photoPart == null || photoPart.getSize() == 0) {
            return null;
        }

        String submittedName = photoPart.getSubmittedFileName();
        String extension = getImageExtension(submittedName);

        if (extension == null) {
            throw new IllegalArgumentException(
                    "Item photo must be a JPG, PNG or WEBP image."
            );
        }

        String contentType = photoPart.getContentType();

        if (contentType == null
                || !contentType.toLowerCase(Locale.ENGLISH)
                        .startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Please choose a valid image file."
            );
        }

        String fileName =
                "item_"
                + userID
                + "_"
                + System.currentTimeMillis()
                + "."
                + extension;

        Path persistentDirectory = Paths.get(
                System.getProperty("user.home"),
                "BarangBaekUploads",
                "itemphoto"
        );

        Files.createDirectories(persistentDirectory);
        Path persistentFile = persistentDirectory.resolve(fileName);

        try (InputStream input = photoPart.getInputStream()) {
            Files.copy(
                    input,
                    persistentFile,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        String deployedPath = getServletContext().getRealPath(
                "/assets/img/itemphoto"
        );

        if (deployedPath != null) {
            Path deployedDirectory = Paths.get(deployedPath);
            Files.createDirectories(deployedDirectory);
            Files.copy(
                    persistentFile,
                    deployedDirectory.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return fileName;
    }

    private String getImageExtension(String fileName) {
        if (fileName == null) {
            return null;
        }

        String cleanName = new File(fileName).getName();
        int dotIndex = cleanName.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == cleanName.length() - 1) {
            return null;
        }

        String extension = cleanName.substring(
                dotIndex + 1
        ).toLowerCase(Locale.ENGLISH);

        if ("jpg".equals(extension)
                || "jpeg".equals(extension)
                || "png".equals(extension)
                || "webp".equals(extension)) {
            return extension;
        }

        return null;
    }

    private Integer getLoggedInUserID(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("userID") instanceof Integer)) {
            return null;
        }

        return (Integer) session.getAttribute("userID");
    }

    private void refreshCartCount(
            HttpServletRequest request,
            cartdao cartDao
    ) {
        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            return;
        }

        try {
            request.getSession().setAttribute(
                    "cartCount",
                    cartDao.getCartItemCount(userID)
            );
        } catch (Exception e) {
            request.getSession().setAttribute("cartCount", 0);
        }
    }

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        response.sendRedirect(
                request.getContextPath()
                + "/auth?action=login"
        );
    }

    private void redirectToInventory(
            HttpServletRequest request,
            HttpServletResponse response,
            String success,
            String error
    ) throws IOException {

        StringBuilder url = new StringBuilder();
        url.append(request.getContextPath());
        url.append("/item?action=mylistings");

        if (success != null && !success.isEmpty()) {
            url.append("&success=").append(success);
        }

        if (error != null && !error.isEmpty()) {
            url.append("&error=").append(error);
        }

        response.sendRedirect(url.toString());
    }

    private Integer parsePositiveInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parseNonNegativeInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return -1;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed >= 0 ? parsed : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double parseNonNegativeDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return -1;
        }

        try {
            double parsed = Double.parseDouble(value.trim());
            return parsed >= 0 ? parsed : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Double parseOptionalPrice(String value) {
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

    private String normaliseConditionFilter(String condition) {
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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isAllowedCondition(String condition) {
        return "New".equals(condition)
                || "Like New".equals(condition)
                || "Good".equals(condition)
                || "Fair".equals(condition)
                || "Poor".equals(condition);
    }

    private String normaliseStatus(String status) {
        if ("Sold".equalsIgnoreCase(status)) {
            return "Sold";
        }

        if ("Unavailable".equalsIgnoreCase(status)) {
            return "Unavailable";
        }

        return "Available";
    }

    private String normaliseFilter(String status) {
        if ("Available".equalsIgnoreCase(status)) {
            return "Available";
        }

        if ("Sold".equalsIgnoreCase(status)) {
            return "Sold";
        }

        if ("Unavailable".equalsIgnoreCase(status)) {
            return "Unavailable";
        }

        if ("OutOfStock".equalsIgnoreCase(status)) {
            return "OutOfStock";
        }

        return "All";
    }
}