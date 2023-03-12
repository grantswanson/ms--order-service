package com.swansong.orderservice.controller;

import com.swansong.orderservice.client.InventoryClient;
import com.swansong.orderservice.dto.OrderDto;
import com.swansong.orderservice.model.Order;
import com.swansong.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/order")
@Log
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final StreamBridge streamBridge;

    @PostMapping
    public String placeOrder(@RequestBody OrderDto orderDto) {
        log.info("Placing Order:" + orderDto);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("inventory");
        Supplier<Boolean> allProductsInStockSupplier = () -> orderDto.getOrderLineItemsList().stream()
                .allMatch(orderLineItems -> inventoryClient.checkStock(orderLineItems.getSkuCode()));

        boolean allProductsInStock = circuitBreaker.run(allProductsInStockSupplier,
                throwable -> handleInventoryServiceDown());


        if (!allProductsInStock) {
            return "Order failed. One of the products in the order is not in stock.";
        }

        // else all in stock
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        orderRepository.save(order);

        // strange that I manually have to do this. I would have thought that JPA would do it for me
        // but if I don't do this, then JPA tries to insert orderLineItems first and fails because
        // order id is NULL (because the order is not inserted, so no id created in db yet)
        orderDto.getOrderLineItemsList().forEach(dto -> dto.setOrder(order));
        order.setOrderLineItems(orderDto.getOrderLineItemsList());
        orderRepository.save(order);

        log.info("Sending Order Details to Notification Service");
        streamBridge.send("notificationEventSupplier-out-0", order.getId());
        return "Order placed successfully";

    }

    private Boolean handleInventoryServiceDown() {
        // indicate the item is NOT in stock when the inventory service is down
        return false;
    }

//
//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<Order> findAll() {
//        return orderRepository.findAll();
//    }
//
//    @GetMapping("/api/order/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public Order findById(@PathVariable Long id ) {
//        System.out.println("finding order id:"+id);
//        return orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("No order with id:"+id));
//    }
}
