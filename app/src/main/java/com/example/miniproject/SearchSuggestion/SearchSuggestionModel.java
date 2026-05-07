package com.example.miniproject.SearchSuggestion;
public class SearchSuggestionModel {

    public static final String TYPE_PRODUCT = "product";
    public static final String TYPE_CATEGORY = "category";

    private String id;
    private String title;
    private String subtitle;
    private String image;
    private String type;
    private Object data;

    public SearchSuggestionModel(String id, String title, String subtitle,
                                 String image, String type, Object data) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.type = type;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}