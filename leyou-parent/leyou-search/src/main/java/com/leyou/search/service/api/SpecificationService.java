package com.leyou.search.service.api;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: HuYi.Zhang
 * @create: 2018-04-24 17:06
 **/
@FeignClient(value = "api-gateway",path = "/api/item")
public interface SpecificationService extends SpecificationApi{
}
