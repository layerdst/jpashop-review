package jpabook.jpashop.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Orders {

    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;


    /**
     * fetch
     * 1. EAGER - 즉시로딩 : 연관된 엔티티를 즉시 조회한다.
     * @MANYTOONE @ONETOONE 기본값
     *
     * 2. LAZY - 지연로징 : 연관된 엔티티는 조회하지 않으며, 실제 사용할때 조회한다.
     * @ONETOMANY @MANYTOMANY 기본값
     *
     */

    /**
     * cascade : 영속성전이
     * ALL : 모두 적용
     * persist : 영속
     * merge : 병합
     * remove : 삭제
     * refresh : refresh
     * detach : 준영속
     */


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    
    //-- 연관관계 메서드
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrders(this);
    }

    public void addOrderItems(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrders(this);
    }
    
    //--생성메서드
    public static Orders createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Orders orders = new Orders();
        orders.setMember(member);
        orders.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            orders.addOrderItems(orderItem);
        }
        orders.setOrderStatus(OrderStatus.ORDER);
        orders.setOrderDate(LocalDateTime.now());
        return orders;
    }
    public void cancel(){
        if(delivery.getStatus()==DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소 불가능");
        }

        this.setOrderStatus(OrderStatus.CANCEL);

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
}
