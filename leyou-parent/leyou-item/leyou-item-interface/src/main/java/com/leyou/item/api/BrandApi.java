package com.leyou.item.api;

import com.leyou.common.dto.Pagination;
import com.leyou.common.vo.PageResult;
import com.leyou.item.po.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-01 20:40
 **/
@RequestMapping("brand")
public interface BrandApi {

    /**
     * 分页查询品牌，并且根据条件过滤
     *
     * @param pagination 分页及排序信息
     * @param key        过滤条件
     * @return
     */
    @GetMapping("page")
    ResponseEntity<PageResult<Brand>> queryBrandByPage(Pagination pagination, @RequestParam("key") String key);

    /**
     * 新增品牌
     * @param brand
     * @return
     */
    @PostMapping
    ResponseEntity<Void> saveBrand(Brand brand);

    /**
     * 修改品牌
     * @param brand
     * @return
     */
    @PutMapping
    ResponseEntity<Void> updateBrand(Brand brand);

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @DeleteMapping
    ResponseEntity<Void> deleteBrand(@RequestParam("id") Long id);

    /**
     * 根据商品分类查询品牌
     * @param cid
     * @return
     */
    @GetMapping("cid/{cid}")
    ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid") Long cid);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id);

    /**
     * 根据id集合批量查询
     * @param ids
     * @return
     */
    @GetMapping("list")
    ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids);
}
