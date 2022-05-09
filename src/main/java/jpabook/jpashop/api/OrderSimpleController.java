package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.Orders;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.orders.simplequery.OrderSimpleRepository;
import jpabook.jpashop.repository.orders.simplequery.SimpleOrderQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleController {

    private final OrderRepository orderRepository;
    private final OrderSimpleRepository orderSimpleRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Orders> orderV1(){
        List<Orders> all = orderRepository.findAll(new OrderSearch());
        for (Orders orders : all) {
            orders.getMember().getName();
        }
        return all;
    }


    /**
     * DTO 로 변환
     * 다만, 쿼리 조회 성능문제가 있음
     * */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){

        /**
            * 쿼리 조회 성능 문제
         * *  Order 수 조회 1건  N
         *    Order 마다 N명의 Member 조회
         *    Order 마다 N명의 Delivery 조회
         *    = 1+2N
        * */
        List<Orders> orders = orderRepository.findAll(new OrderSearch());

        System.out.println(orders.get(0).getDelivery().getAddress());
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Orders orders){
            orderId= orders.getId();
            name = orders.getMember().getName();
            orderDate = orders.getOrderDate();
            orderStatus = orders.getOrderStatus();
            address = orders.getDelivery().getAddress();


        }
    }

    /**
    * 쿼리 성능 문제 해결
    *  Entity -> Dto 를 데이터를 가져오기
    * */

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
       List<Orders> orders =  orderRepository.findAllWithMemberRepository();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }


    /**
     * Entity 를 거치지 않고 DTO로 데이터 가져오기
     * */
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> orderV4(){
        return orderSimpleRepository.findOrderDtos();
    }

    /**
    * V3, V4 성능상의 큰 차이는 없으나,
    * V4는 코드의 재사용성이 떨어지고, 데이터를 수정하는게 불가능하다
    * 최대한 V3 방법을 유지하되, 성능상의 이슈가 발생될 경우
    * V4 와 유사한 메소드를 Repository를 분리하여 만드는게 좋다.
    *
    * */
}
