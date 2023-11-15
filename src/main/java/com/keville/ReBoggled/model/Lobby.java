package com.keville.ReBoggled.model;

import org.springframework.data.annotation.Id;

public class Lobby {

    @Id
    private Integer id;
    private String name;
    //private Boolean isPrivate; todo
    private int capacity;

    public Lobby(String name, int capacity) {
        this.name = name;
        //this.isPrivate = isPrivate;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    */

}
