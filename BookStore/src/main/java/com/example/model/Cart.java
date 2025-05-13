/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.model;

/**
 *
 * @author gizem
 */
public class Cart {
    private int bookId;
    private String bookName;
    private int quantity;



    //Constructor
    public Cart(int bookId, String bookName, int quantity) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.quantity = quantity;
        

    }
    
    public Cart(){
        
    }

    
    //getter&setter
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }




    
    
    
    
}
