package com.leyou.item.api;

import com.leyou.item.po.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-06 20:29
 **/
@RequestMapping("spec")
public interface SpecificationApi {

    @GetMapping("/{id}")
    ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id") Long id);

    @GetMapping
    ResponseEntity<List<String>> querySpecificationByCategoryIds(@RequestParam("ids") List<Long> ids);

    @PostMapping
    ResponseEntity<Void> insertSpecification(Specification spec);

    @PutMapping
    ResponseEntity<Void> updateSpecification(Specification spec);
}
