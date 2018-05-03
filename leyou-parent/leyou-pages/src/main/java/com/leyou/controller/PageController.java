package com.leyou.controller;

import com.leyou.service.GoodsPageService;
import com.leyou.service.api.BrandService;
import com.leyou.service.api.CategoryService;
import com.leyou.service.api.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.view.ThymeleafView;

import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-25 22:22
 **/
@Controller
@RequestMapping("pages")
public class PageController {

    @Autowired
    private GoodsPageService goodsService;

    @GetMapping("{id}")
    public String viewHtml(@PathVariable("id") Long id, ModelMap model) {

        Map<String,Object> models =  this.goodsService.getModels(id);

        model.addAllAttributes(models);

        return "item";
    }

    @PostMapping("{id}")
    public ResponseEntity<Void> createHtml(@PathVariable("id") Long id) {
        try {
            this.goodsService.createHtml(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
