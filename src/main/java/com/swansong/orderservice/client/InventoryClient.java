package com.swansong.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", url = "http://localhost:8080")
public interface InventoryClient {

    @GetMapping("/api/inventory/{skuCode}")
    Boolean checkStock(@PathVariable String skuCode);

}
