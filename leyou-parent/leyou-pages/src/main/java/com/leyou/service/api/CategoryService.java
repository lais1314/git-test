package com.leyou.service.api;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-25 17:02
 **/
@FeignClient(value = "api-gateway", path = "/api/item")
public interface CategoryService extends CategoryApi {
}
