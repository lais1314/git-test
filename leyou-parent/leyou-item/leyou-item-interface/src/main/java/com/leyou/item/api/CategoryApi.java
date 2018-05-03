package com.leyou.item.api;

import com.leyou.item.po.Category;
import com.leyou.item.vo.TreeResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-25 01:22
 **/
@RequestMapping("category")
public interface CategoryApi {

    /**
     * 根据父节点id查询商品分类
     *
     * @param pid
     * @return
     */
    @GetMapping("/list")
    ResponseEntity<List<Category>> queryByParentId(@RequestParam(value = "pid", defaultValue = "0") Long pid);

    /**
     * 根据品牌id查询商品分类
     * @param bid
     * @return
     */
    @GetMapping("/bid/{bid}")
    ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid);

    /**
     * 查询所有的商品分类，并封装为树结构
     * @return
     */
    @GetMapping("/tree")
    ResponseEntity<List<TreeResult>> queryAllAsTree();

    /**
     * @return 3级分类的名称拼接，如果只传1个id，只返回当前分类的名称
     */
    @GetMapping("name")
    ResponseEntity<String> queryNameByIds(@RequestParam("ids") List<Long> ids);
}
