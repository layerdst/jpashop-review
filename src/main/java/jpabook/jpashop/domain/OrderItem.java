package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Orders orders;

    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;

    private int orderPrice;
    private int count;

    public void cancel() {
        getItem().addStock(count);
    }

    public int getTotalPrice(){
        return getOrderPrice() * getCount();
    }

    public OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setCount(count);
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        item.removeStock(count);
        return orderItem;
    }
}
