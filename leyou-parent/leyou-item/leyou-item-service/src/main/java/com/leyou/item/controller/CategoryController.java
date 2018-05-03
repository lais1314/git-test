package com.leyou.item.controller;

import com.leyou.common.exception.ResourceNotFoundException;
import com.leyou.item.api.CategoryApi;
import com.leyou.item.po.Category;
import com.leyou.item.service.CategoryService;
import com.leyou.item.vo.TreeResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-25 01:32
 **/
@RestController
public class CategoryController implements CategoryApi {

    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseEntity<List<Category>> queryByParentId(@RequestParam(value = "pid", defaultValue = "0") Long pid) {
        Category t = new Category();
        t.setParentId(pid);
        List<Category> list = this.categoryService.queryListByWhere(t);
        if (list == null || list.size() < 1) {
            throw new ResourceNotFoundException("商品分类：" + pid + " 不存在子分类。");
        }
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid) {
        List<Category> list = this.categoryService.queryByBrandId(bid);
        if (list == null || list.size() < 1) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<TreeResult>> queryAllAsTree() {
        List<TreeResult> results = this.categoryService.queryAllAsTree();
        return ResponseEntity.ok(results);
    }

    @Override
    public ResponseEntity<String> queryNameByIds(@RequestParam("ids") List<Long> ids) {
        String name = this.categoryService.queryNameByIds(ids);
        if(StringUtils.isBlank(name)){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(name);
    }
}
