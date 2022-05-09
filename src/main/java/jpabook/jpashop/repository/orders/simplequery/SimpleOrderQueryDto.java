package jpabook.jpashop.repository.orders.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.Orders;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleOrderQueryDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderQueryDto(Orders orders){
        orderId= orders.getId();
        name = orders.getMember().getName();
        orderDate = orders.getOrderDate();
        orderStatus = orders.getOrderStatus();
        address = orders.getDelivery().getAddress();
    }

    public SimpleOrderQueryDto(Long id, String name, LocalDateTime orderDate, OrderStatus orderStatus, Address address){
        this.orderId = id;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;

    }


}
