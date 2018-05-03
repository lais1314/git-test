package com.leyou.search.controller;

import com.leyou.common.dto.Pagination;
import com.leyou.common.exception.ResourceNotFoundException;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-22 10:57
 **/
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("list")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest request) {
        // 查询
        SearchResult result = this.searchService.search(request);
        if (result == null || result.getItems().size() == 0) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(result);
    }
}
