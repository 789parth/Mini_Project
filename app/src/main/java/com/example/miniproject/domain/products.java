package com.example.miniproject.domain;

public class products {
    String category_id,category_name,product_description,product_id,product_image,product_name,product_unit;
    int product_price,product_quantity;
    boolean trending_item;

    public products(){
    }

    public products(String category_id, String category_name, String product_description, String product_id, String product_image, String product_name, String product_unit, int product_price, int product_quantity, boolean trending_item) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.product_description = product_description;
        this.product_id = product_id;
        this.product_image = product_image;
        this.product_name = product_name;
        this.product_unit = product_unit;
        this.product_price = product_price;
        this.product_quantity = product_quantity;
        this.trending_item = trending_item;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_unit() {
        return product_unit;
    }

    public void setProduct_unit(String product_unit) {
        this.product_unit = product_unit;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getProduct_price() {
        return product_price;
    }

    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public boolean isTrending_item() {
        return trending_item;
    }

    public void setTrending_item(boolean trending_item) {
        this.trending_item = trending_item;
    }
}
