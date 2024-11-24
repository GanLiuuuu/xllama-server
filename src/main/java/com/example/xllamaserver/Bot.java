package com.example.xllamaserver;

import java.sql.Timestamp;

public class Bot {
    private Integer id;          // 用户ID
    private String name;          // bot名
    private String description;
    private String href;
    private String imgSrc;//缩略图路径
    private String avatarUrl;          // 头像
    private Float price;
    private String version;
    private String summary;
    private boolean is_official;
    private String highlight;
    private String content;
    private Integer created_by;
    private Timestamp created_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isIs_official() {
        return is_official;
    }

    public void setIs_official(boolean is_official) {
        this.is_official = is_official;
    }

    public String getHighlight() {
        return highlight;
    }

    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Integer created_by) {
        this.created_by = created_by;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
