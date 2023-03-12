package com.swansong.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "order_line_items")
public class OrderLineItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //EAGER is the default
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(name = "skucode")
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;


}
