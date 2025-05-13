/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.model;

import java.util.*;

/**
 *
 * @author gizem
 */

public class Order {
    private int orderNo;
    private int customerId;
    private List<Cart> items; 

    //Constructor
    public Order() {
        
    }

    public Order(int orderNo, int customerId, List<Cart> items) {
        this.orderNo = orderNo;
        this.customerId = customerId;
        this.items = items;
    }

    //getters&setters
    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public List<Cart> getItems() {
        return items;
    }

    public void setItems(List<Cart> items) {
        this.items = items;
    }
    
    

}
