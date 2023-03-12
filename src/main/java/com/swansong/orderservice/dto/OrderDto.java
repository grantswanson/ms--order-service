package com.swansong.orderservice.dto;

import com.swansong.orderservice.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private List<OrderLineItems> orderLineItemsList;
}
