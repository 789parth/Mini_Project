package com.example.miniproject.domain;

public class categorys
{
    String category_id,category_image,category_title,store_id;

    public categorys(){
    }

    public categorys(String category_id, String category_image, String category_title, String store_id) {
        this.category_id = category_id;
        this.category_image = category_image;
        this.category_title = category_title;
        this.store_id = store_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getCategory_title() {
        return category_title;
    }

    public void setCategory_title(String category_title) {
        this.category_title = category_title;
    }
}

