package com.rarestardev.magneticplayer.model;

public class SystemThemes {
    private String name;
    private int theme;
    private int icon;

    public SystemThemes(String name, int theme, int icon) {
        this.name = name;
        this.theme = theme;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
