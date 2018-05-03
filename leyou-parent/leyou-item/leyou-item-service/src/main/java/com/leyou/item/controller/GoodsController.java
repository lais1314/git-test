package com.leyou.item.controller;

import com.leyou.common.dto.Pagination;
import com.leyou.common.exception.InvalidArgumentsException;
import com.leyou.common.exception.ResourceNotFoundException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.po.Sku;
import com.leyou.item.po.Spu;
import com.leyou.item.po.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-13 20:01
 **/
@RestController
public class GoodsController implements GoodsApi {

    @Autowired
    private GoodsService goodsService;

    @Override
    public ResponseEntity<PageResult<Spu>> querySpuList(
            Pagination pagination,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable) {
        // 查询
        PageResult<Spu> result = this.goodsService.querySpuList(pagination, key, saleable);
        if (result == null || result.getItems().size() == 0) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        Spu spu = this.goodsService.querySpuById(id);
        if (spu == null) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(spu);
    }

    @Override
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id) {
        SpuDetail detail = this.goodsService.querySpuDetailById(id);
        if (detail == null) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(detail);
    }

    @Override
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id) {
        List<Sku> skus = this.goodsService.querySkuBySpuId(id);
        if (skus == null || skus.size() == 0) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(skus);
    }

    @Override
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        try {
            this.goodsService.save(spu);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu) {
        if (spu.getId() == null) {
            throw new InvalidArgumentsException();
        }
        try {
            this.goodsService.update(spu);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Void> updateStatus(@RequestParam("id") Long id, @RequestParam("saleable") Boolean saleable) {
        int count = this.goodsService.updateStatus(id, saleable);
        if (count == 0) {
            throw new InvalidArgumentsException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
