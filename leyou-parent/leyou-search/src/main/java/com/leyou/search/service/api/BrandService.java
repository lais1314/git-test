package com.leyou.search.service.api;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-23 23:16
 **/
@FeignClient(value = "api-gateway", path = "/api/item")
public interface BrandService extends BrandApi {

}
