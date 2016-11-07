package com.letv.whatslive.model.mysql.system;

import java.util.List;

public class TreeVo {

    private String title;

    private String key;

    private boolean isFolder;

    private boolean select;

    private boolean hideCheckbox;

    private List<TreeVo> children;

    private String parentKey;

    public boolean isHideCheckbox() {
        return hideCheckbox;
    }

    public void setHideCheckbox(boolean hideCheckbox) {
        this.hideCheckbox = hideCheckbox;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public List<TreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<TreeVo> children) {
        this.children = children;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }
}
