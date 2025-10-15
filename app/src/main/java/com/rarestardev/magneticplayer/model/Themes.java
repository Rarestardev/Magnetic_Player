package com.rarestardev.magneticplayer.model;

public class Themes {

    private String themeName;
    private int themeColor;

    public Themes(String themeName, int themeColor) {
        this.themeName = themeName;
        this.themeColor = themeColor;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(int themeColor) {
        this.themeColor = themeColor;
    }
}
