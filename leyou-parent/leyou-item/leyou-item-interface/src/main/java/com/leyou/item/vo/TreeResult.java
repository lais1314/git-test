package com.leyou.item.vo;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-09 13:55
 **/
public class TreeResult {
    private long id;
    private long parentId;
    private String name;
    private List<TreeResult> children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeResult> getChildren() {
        return children;
    }

    public void setChildren(List<TreeResult> children) {
        this.children = children;
    }
}
