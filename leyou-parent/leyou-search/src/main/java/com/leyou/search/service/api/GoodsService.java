package com.leyou.search.service.api;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-29 10:24
 **/
@FeignClient(value = "api-gateway", path = "/api/item")
public interface GoodsService extends GoodsApi {
}
