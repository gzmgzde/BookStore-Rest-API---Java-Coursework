/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.model;

/**
 *
 * @author gizem
 */
public class Authors {
    private String name;
    private String biography;
    private int id;
    
    //Constructor
    public Authors(String name, String biography, int id) {
        this.name = name;
        this.biography = biography;
        this.id = id;
    }
    
    public Authors(){
    }

    //Getters & Setters 
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
