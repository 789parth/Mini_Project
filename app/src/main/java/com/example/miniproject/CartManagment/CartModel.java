package com.example.miniproject.CartManagment;

public class CartModel {

    private String userId;
    private String productId;

    private String product_image;
    private String product_name;
    private int product_price;
    private int quantity;

    public CartModel() {
        // Required for Firebase
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public String getProduct_image() {
        return product_image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public int getProduct_price() {
        return product_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
