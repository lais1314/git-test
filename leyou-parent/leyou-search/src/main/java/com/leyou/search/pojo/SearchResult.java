package com.leyou.search.pojo;

import com.leyou.item.po.Brand;

import java.util.List;
import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-23 20:23
 **/
public class SearchResult {
    private long total;// 总条数
    private long totalPage;// 总页数
    private List<Goods> items;// 当前页数据
    private List<String> categories;// 商品聚合得到的分类id集合，每个数据都包含3级类目用“/”拼接
    private List<Brand> brands;
    private List<Map<String, Object>> specs;

    public SearchResult() {
    }

    public SearchResult(
            long total,
            long totalPage,
            List<Goods> items,
            List<String> categories,
            List<Brand> brands,
            List<Map<String, Object>> specs) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public List<Goods> getItems() {
        return items;
    }

    public void setItems(List<Goods> items) {
        this.items = items;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }


    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }
}
