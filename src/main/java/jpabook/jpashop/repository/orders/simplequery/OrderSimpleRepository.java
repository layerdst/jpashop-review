package jpabook.jpashop.repository.orders.simplequery;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


@Repository
@AllArgsConstructor
public class OrderSimpleRepository {

    private final EntityManager em;

    public List<SimpleOrderQueryDto> findOrderDtos(){
        return em.createQuery(
                "select new jpabook.jpashop.repository.SimpleOrderQueryDto" +
                        "(o.id, m.name, o.orderDate, o.orderStatus, d.address)  from Orders o " +
                        "join o.member m " +
                        "join o.delivery d", SimpleOrderQueryDto.class)
                .getResultList();
    }
}
