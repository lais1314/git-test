package com.leyou.item.controller;

import com.leyou.common.dto.Pagination;
import com.leyou.common.exception.InvalidArgumentsException;
import com.leyou.common.exception.ResourceNotFoundException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.api.BrandApi;
import com.leyou.item.po.Brand;
import com.leyou.item.service.BrandService;
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
 * @create: 2018-04-01 21:56
 **/
@RestController
public class BrandController implements BrandApi {

    @Autowired
    private BrandService brandService;

    @Override
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            Pagination pagination, @RequestParam(value = "key", required = false) String key) {
        PageResult<Brand> result = this.brandService.queryBrandByPageAndSort(pagination, key);
        if (result == null || result.getItems().size() == 0) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<Void> saveBrand(Brand brand) {
        this.brandService.saveBrand(brand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> updateBrand(Brand brand) {
        if (brand.getId() == null) {
            throw new InvalidArgumentsException();
        }
        this.brandService.updateBrand(brand);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> deleteBrand(@RequestParam("id") Long id) {
        this.brandService.deleteBrand(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<Brand>> queryBrandByCategory(@PathVariable("cid") Long cid) {
        List<Brand> list = this.brandService.queryBrandByCategory(cid);
        if(list == null){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id) {
        Brand brand = this.brandService.queryById(id);
        if(brand == null){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(brand);
    }

    @Override
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids")  List<Long> ids) {
        List<Brand> brands = this.brandService.queryByIds("id", ids);
        if(brands == null){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(brands);
    }
}
