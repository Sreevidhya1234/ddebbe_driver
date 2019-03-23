package com.DDebbie.entity;

/**
 * Created by appsplanet on 21/1/16.
 */
public class NavItem {
    private String title;
    private int id;

    public NavItem(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
