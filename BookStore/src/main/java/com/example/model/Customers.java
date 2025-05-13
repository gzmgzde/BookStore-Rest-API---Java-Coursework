/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.model;

/**
 *
 * @author gizem
 */
public class Customers {
    private String custName;
    private String email;
    private String password;
    private int custId;
    
    //Constructor
    public Customers(String custName, String email, String password, int custId) {
        this.custName = custName;
        this.email = email;
        this.password = password;
        this.custId = custId;
    }
    
    public Customers(){
        
    }

    //Getters & Setters
    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }
    
    
    
    
}
