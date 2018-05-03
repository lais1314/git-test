package com.leyou.service.api;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-25 16:38
 **/
@FeignClient(value = "api-gateway", path = "/api/item")
public interface GoodsService extends GoodsApi {
}
