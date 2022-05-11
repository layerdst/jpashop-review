package jpabook.jpashop.repository.orders.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o-> {
            List<OrderItemQueryDto> orderItems = findOrdersItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    private List<OrderItemQueryDto> findOrdersItems(Long orderId) {
        return em.createQuery(
                "select new jpabook.jpashop.repository.orders.query.OrderItemQueryDto(oi.orders.id, i.name, oi.orderPrice, oi.count)" +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.orders.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.orders.query.OrderQueryDto(o.id, m.name, o.orderDate, o.orderStatus, d.address) from " +
                        "Orders o " +
                        "join o.member m " +
                        "join o.delivery d", OrderQueryDto.class)
        .getResultList();
    }

    public List<OrderQueryDto> findAllByDtoOptimization() {
        List<OrderQueryDto> result = findOrders();

        List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.orders.query.OrderItemQueryDto(oi.orders.id, i.name, oi.orderPrice, oi.count)" +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.orders.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    public List<OrderFlatDto> findAllByDtoFlat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.orders.query.OrderFlatDto(o.id, m.name, o.orderDate, o.orderStatus, d.address, i.name, oi.orderPrice, oi.count) " +
                        "from Orders o " +
                        "join o.member m " +
                        "join o.delivery d " +
                        "join o.orderItems oi " +
                        "join oi.item i ", OrderFlatDto.class)
                .getResultList();

    }
}
