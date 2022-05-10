package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Orders> ordersv1(){
        List<Orders> all = orderRepository.findAll(new OrderSearch());
        for (Orders orders : all) {
            orders.getMember().getName();
            orders.getDelivery().getAddress();
            List<OrderItem> orderItems = orders.getOrderItems();
            orderItems.stream().forEach(o->o.getItem().getName());

        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Orders> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> all = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return all;
    }


    /**
     *
     * fetch join 에 distinct 를 추가하면 entity의 id 식별자가 중복인 경우 컬렉선에서 따로 분류해준다.
     *
     * 이때 치명적인 단점이 있는데, 페이징이 불가능해진다!
     */

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Orders> all = orderRepository.findAllWithItem();

        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }


    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Orders o){
            orderId = o.getId();
            name = o.getMember().getName();
            orderDate = o.getOrderDate();
            orderStatus = o.getOrderStatus();
            address = o.getDelivery().getAddress();

//            o.getOrderItems().stream().forEach(os -> os.getItem().getName());
//            orderItems = o.getOrderItems();

            orderItems = o.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }


    @Getter
    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}