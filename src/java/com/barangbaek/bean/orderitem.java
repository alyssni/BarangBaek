package com.barangbaek.bean;

public class orderitem {
    private int orderID;
    private int itemID;
    private int quantity;
    private double priceAtPurchase;
    
    // Joined fields
    private String itemName;
    private String itemPhoto;
    
    public orderitem() {}
    
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }
    
    public int getItemID() { return itemID; }
    public void setItemID(int itemID) { this.itemID = itemID; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(double priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getItemPhoto() { return itemPhoto; }
    public void setItemPhoto(String itemPhoto) { this.itemPhoto = itemPhoto; }
}