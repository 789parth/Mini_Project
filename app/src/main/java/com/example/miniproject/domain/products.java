package com.example.miniproject.domain;

public class products {
    String Category,ImagePath,Name,ProductId,Quntity,StoreId;
    int Price;
    boolean TrendingItem;

    public products(){
    }

    public products(String category, String imagePath, String name, String productId, String quntity, String storeId, int price, boolean trendingItem) {
        Category = category;
        ImagePath = imagePath;
        Name = name;
        ProductId = productId;
        Quntity = quntity;
        StoreId = storeId;
        Price = price;
        TrendingItem = trendingItem;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public boolean isTrendingItem() {
        return TrendingItem;
    }

    public void setTrendingItem(boolean trendingItem) {
        TrendingItem = trendingItem;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getQuntity() {
        return Quntity;
    }

    public void setQuntity(String quntity) {
        Quntity = quntity;
    }

    public String getStoreId() {
        return StoreId;
    }

    public void setStoreId(String storeId) {
        StoreId = storeId;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }
}
