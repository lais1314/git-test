package com.leyou.common.dto;

/**
 * 分页信息对象
 * @author: HuYi.Zhang
 * @create: 2018-04-01 21:53
 **/
public class Pagination {
    private Integer page = 1;
    private Integer rows = 200;
    private String sortBy;
    private Boolean asc = false;

    public Integer getPage() {
        if(page == null){
            return 1;
        }
        return Math.max(page, 1);
    }

    public void setPage(Integer page) {
        this.page = Math.max(page, 1);
    }

    public Integer getRows() {
        if(rows == null){
            return 200;
        }
        return Math.min(rows, 200);
    }

    public void setRows(Integer size) {
        this.rows = Math.min(size, 200);
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public String getOrderByClause(){
        if(this.sortBy == null || this.sortBy.trim().equals("")){
            return null;
        }
        return this.sortBy + " " + (this.asc ? "asc" : "desc");
    }
}
