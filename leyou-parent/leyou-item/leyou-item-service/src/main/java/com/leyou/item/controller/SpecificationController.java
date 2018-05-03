package com.leyou.item.controller;

import com.leyou.common.exception.InvalidArgumentsException;
import com.leyou.common.exception.ResourceNotFoundException;
import com.leyou.item.api.SpecificationApi;
import com.leyou.item.po.Specification;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-06 20:33
 **/
@RestController
public class SpecificationController implements SpecificationApi {

    @Autowired
    private SpecificationService specificationService;

    @Override
    public ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id") Long id) {
        Specification spec = this.specificationService.queryById(id);
        if (spec == null) {
            throw new ResourceNotFoundException("商品分类：" + id + " 不存在规格参数。");
        }
        return ResponseEntity.ok(spec.getSpecifications());
    }

    @Override
    public ResponseEntity<List<String>> querySpecificationByCategoryIds(@RequestParam("ids") List<Long> ids) {
        List<Specification> specs = this.specificationService.queryByIds("categoryId", ids);
        if (specs == null || specs.size() == 0) {
            throw new ResourceNotFoundException("商品分类：" + ids + " 不存在规格参数。");
        }
        return ResponseEntity.ok(specs.stream().map(Specification::getSpecifications).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Void> insertSpecification(Specification spec) {
        if (spec.getCategoryId() == null) {
            throw new InvalidArgumentsException("商品分类id不能为空！");
        }
        this.specificationService.insert(spec);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> updateSpecification(Specification spec) {
        if (spec.getCategoryId() == null) {
            throw new InvalidArgumentsException("商品分类id不能为空！");
        }
        this.specificationService.update(spec);
        return ResponseEntity.noContent().build();
    }
}
