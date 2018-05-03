package com.leyou.item.api;

import com.leyou.common.dto.Pagination;
import com.leyou.common.vo.PageResult;
import com.leyou.item.po.Sku;
import com.leyou.item.po.Spu;
import com.leyou.item.po.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品相关的接口
 *
 * @author: HuYi.Zhang
 * @create: 2018-04-13 19:17
 **/
@RequestMapping("goods")
public interface GoodsApi {

    /**
     * 分页查询SPU
     *
     * @param pagination 分页参数，包含page和rows
     * @param key        搜索条件
     * @param saleable   是否上架
     * @return
     */
    @GetMapping("spu/list")
    ResponseEntity<PageResult<Spu>> querySpuList(Pagination pagination,
                                                 @RequestParam("key") String key,
                                                 @RequestParam("saleable") Boolean saleable);

    /**
     * 根据id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id);

    /**
     * 根据id查询spu详情
     *
     * @param id
     * @return
     */
    @GetMapping("spu/detail/{id}")
    ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id);

    /**
     * 根据spu的id查询所有sku信息
     *
     * @param id
     * @return
     */
    @GetMapping("sku/list")
    ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id);

    /**
     * 新增商品
     *
     * @param spu
     * @return
     */
    @PostMapping
    ResponseEntity<Void> saveGoods(Spu spu);

    /**
     * 更新商品信息
     *
     * @param spu
     * @return
     */
    @PutMapping
    ResponseEntity<Void> updateGoods(Spu spu);

    /**
     * 上架或下架商品
     *
     * @param id       spu的id
     * @param saleable true则上架，false则下架
     * @return
     */
    @PutMapping("status")
    ResponseEntity<Void> updateStatus(@RequestParam("id") Long id, @RequestParam("saleable") Boolean saleable);
}
