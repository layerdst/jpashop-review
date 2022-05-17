package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Orders;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OrderQuerySerivce {

    private final OrderRepository orderRepository;

    public List<OrderDto> ordersV3(){
        List<Orders> all = orderRepository.findAllWithItem();

        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }
}
