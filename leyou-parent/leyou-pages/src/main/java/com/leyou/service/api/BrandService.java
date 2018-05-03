package com.leyou.service.api;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-25 17:03
 **/
@FeignClient(value = "api-gateway", path = "/api/item")
public interface BrandService extends BrandApi {
}
