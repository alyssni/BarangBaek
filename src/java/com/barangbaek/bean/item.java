/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.barangbaek.bean;

/**
 *
 * @author alyssa hani riduan
 */
import java.sql.Timestamp;

public class item {
    private int itemID;
    private int sellerID;
    private int categoryID;
    private String itemName;
    private String itemDesc;
    private String itemPhoto;
    private String itemCondition;
    private String itemStatus;
    private int stock;
    private double price;
    private Timestamp createdAt;
    private String categoryName;
    private String sellerName;
    
    public item() {}
    
    public item(int itemID, int sellerID, int categoryID, String itemName, 
                String itemDesc, String itemPhoto, String itemCondition, 
                String itemStatus, int stock, double price, Timestamp createdAt) {
        this.itemID = itemID;
        this.sellerID = sellerID;
        this.categoryID = categoryID;
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemPhoto = itemPhoto;
        this.itemCondition = itemCondition;
        this.itemStatus = itemStatus;
        this.stock = stock;
        this.price = price;
        this.createdAt = createdAt;
    }
    
    public int getItemID() { 
        return itemID; 
    }
    
    public void setItemID(int itemID) { 
        this.itemID = itemID; 
    }
    
    public int getSellerID() { 
        return sellerID; 
    }
    
    public void setSellerID(int sellerID) { 
        this.sellerID = sellerID; 
    }
    
    public int getCategoryID() { 
        return categoryID; 
    }
    
    public void setCategoryID(int categoryID) { 
        this.categoryID = categoryID; 
    }
    
    public String getItemName() { 
        return itemName; 
    }
    
    public void setItemName(String itemName) { 
        this.itemName = itemName; 
    }
    
    public String getItemDesc() { 
        return itemDesc; 
    }
    
    public void setItemDesc(String itemDesc) { 
        this.itemDesc = itemDesc; 
    }
    
    public String getItemPhoto() { 
        return itemPhoto; 
    }
    
    public void setItemPhoto(String itemPhoto) { 
        this.itemPhoto = itemPhoto; 
    }
    
    public String getItemCondition() { 
        return itemCondition; 
    }
    
    public void setItemCondition(String itemCondition) { 
        this.itemCondition = itemCondition; 
    }
    
    public String getItemStatus() { 
        return itemStatus; 
    }
    
    public void setItemStatus(String itemStatus) { 
        this.itemStatus = itemStatus; 
    }
    
    public int getStock() { 
        return stock; 
    }
    
    public void setStock(int stock) { 
        this.stock = stock; 
    }
    
    public double getPrice() { 
        return price; 
    }
    
    public void setPrice(double price) { 
        this.price = price; 
    }
    
    public Timestamp getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(Timestamp createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public String getCategoryName() { 
        return categoryName; 
    }
    
    public void setCategoryName(String categoryName) { 
        this.categoryName = categoryName; 
    }
    
    public String getSellerName() { 
        return sellerName; 
    }
    
    public void setSellerName(String sellerName) { 
        this.sellerName = sellerName; 
    }
}