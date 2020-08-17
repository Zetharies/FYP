package com.aston.blindfitness.Model;

import androidx.annotation.NonNull;

public class LanguageRowItem {

    private String title;
    private String desc;
    private Integer image;

    public LanguageRowItem(String title, String desc, Integer image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getImage() {
        return image;
    }

    @NonNull
    @Override
    public String toString() {
        return title + "\n" + desc;
    }
}
