package com.barangbaek.bean;

import java.sql.Timestamp;

public class payment {
    private int paymentID;
    private int orderID;
    private String paymentMethod;
    private String paymentStatus;
    private Timestamp paymentDateTime;
    
    public payment() {}
    
    public int getPaymentID() { return paymentID; }
    public void setPaymentID(int paymentID) { this.paymentID = paymentID; }
    
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public Timestamp getPaymentDateTime() { return paymentDateTime; }
    public void setPaymentDateTime(Timestamp paymentDateTime) { this.paymentDateTime = paymentDateTime; }
}