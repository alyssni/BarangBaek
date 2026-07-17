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
public class category {
    private int categoryID;
    private String categoryName;
    private String categoryDesc;
    
    public category() {}
    
    public category(int categoryID, String categoryName, String categoryDesc) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryDesc = categoryDesc;
    }
    
    public int getCategoryID() { 
        return categoryID; 
    }
    
    public void setCategoryID(int categoryID) { 
        this.categoryID = categoryID; 
    }
    
    public String getCategoryName() { 
        return categoryName; 
    }
    
    public void setCategoryName(String categoryName) { 
        this.categoryName = categoryName; 
    }
    
    public String getCategoryDesc() { 
        return categoryDesc; 
    }
    
    public void setCategoryDesc(String categoryDesc) { 
        this.categoryDesc = categoryDesc; 
    }
}