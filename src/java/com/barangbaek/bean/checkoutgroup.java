package com.barangbaek.bean;

import java.util.ArrayList;
import java.util.List;

public class checkoutgroup {

    private int sellerID;
    private String sellerName;
    private final List<cartitem> items;

    public checkoutgroup() {
        this.items = new ArrayList<cartitem>();
    }

    public int getSellerID() {
        return sellerID;
    }

    public void setSellerID(int sellerID) {
        this.sellerID = sellerID;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<cartitem> getItems() {
        return items;
    }

    public void addItem(cartitem item) {
        items.add(item);
    }

    public int getTotalQuantity() {
        int total = 0;

        for (cartitem item : items) {
            total += item.getQuantity();
        }

        return total;
    }

    public double getGroupTotal() {
        double total = 0.0;

        for (cartitem item : items) {
            total += item.getSubtotal();
        }

        return total;
    }
}