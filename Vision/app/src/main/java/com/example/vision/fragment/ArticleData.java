package com.example.vision.fragment;

public class ArticleData {
    String url;
    String title;
    int imageResource;
    public ArticleData(String url, String title, int imageResource){
        this.url = url;
        this.title = title;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getImageResource() {
        return imageResource;
    }
}
