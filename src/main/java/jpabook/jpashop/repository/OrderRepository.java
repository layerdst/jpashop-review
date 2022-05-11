package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Orders order){
        em.persist(order);
    }

    public Orders findOne(Long id){
        return em.find(Orders.class, id);
    }

    public List<Orders> findAll(OrderSearch orderSearch){

        String jpql = "select o From Orders o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Orders> query = em.createQuery(jpql, Orders.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    public List<Orders> findAllWithMemberRepository() {
        return em.createQuery("select o from Orders  o " +
                "join fetch o.member m " +
                "join fetch o.delivery d", Orders.class).getResultList();
    }


    public List<Orders> findAllWithItem() {
        return em.createQuery("select distinct o from Orders o " +
                                    "join fetch o.member m " +
                                    "join fetch o.delivery d " +
                                    "join fetch o.orderItems oi " +
                                    "join fetch oi.item i ", Orders.class).getResultList();

    }


    public List<Orders> findAllWithMemberDelivery() {
        return em.createQuery("select distinct o from Orders o " +
                "join fetch o.member m " +
                "join fetch o.delivery d ", Orders.class ).getResultList();
    }

    /**
     * fetch 조인시 페이징의 문제가 발생할 수 있다.
     * 1. ToOne 의 조건에만 fetch 조인을 적용한다
     * 2. application.yml 의 hibernate.default_batch_fetch_size 설정을 통해
     *   in 쿼리를 적용할 수 있게끔 하여 N+1 문제를 일부 해결이 가능하다.
     * */
    public List<Orders> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select distinct o from Orders o " +
                "join fetch o.member m " +
                "join fetch o.delivery d ", Orders.class)
                .setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}
