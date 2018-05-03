package com.leyou.search.pojo;

import com.leyou.common.dto.Pagination;

import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-25 08:12
 **/
public class SearchRequest extends Pagination{

    private String key;// 搜索条件
    private Map<String,String> filter;// 过滤字段
    private static final Integer DEFAULT_ROWS = 20;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    public Integer getRows(){
        if(super.getRows() == null){
            return DEFAULT_ROWS;
        }
        return Math.min(super.getRows(), DEFAULT_ROWS);
    }
}
