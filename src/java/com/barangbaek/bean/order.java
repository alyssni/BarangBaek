package com.barangbaek.bean;

import java.sql.Timestamp;

public class order {
    private int orderID;
    private int buyerID;
    private int sellerID;
    private Timestamp orderDateTime;
    private double totalAmount;
    private String orderStatus;
    
    // Joined fields
    private String buyerName;
    private String sellerName;
    
    public order() {}
    
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }
    
    public int getBuyerID() { return buyerID; }
    public void setBuyerID(int buyerID) { this.buyerID = buyerID; }
    
    public int getSellerID() { return sellerID; }
    public void setSellerID(int sellerID) { this.sellerID = sellerID; }
    
    public Timestamp getOrderDateTime() { return orderDateTime; }
    public void setOrderDateTime(Timestamp orderDateTime) { this.orderDateTime = orderDateTime; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
}