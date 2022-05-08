package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitSevice initSevice;

    @PostConstruct
    public void init(){
        initSevice.dbInit();
        initSevice.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitSevice{

        private final EntityManager em;

        public void dbInit(){
            Member member = createMember("userA", "서울", 1, "1111");
            em.persist(member);

            Book book1 = createBook("JPA BOOK1", 100,10000);
            em.persist(book1);

            Book book2 = createBook("JPA BOOK2", 100,20000);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);

            Orders order = Orders.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        private Book createBook(String name, int stockQuantity, int price) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setStockQuantity(stockQuantity);
            book1.setPrice(price);
            return book1;
        }

        public void dbInit2(){
            Member member = createMember("userB", "부산", 2, "222");
            em.persist(member);

            Book book1 = createBook("JPA BOOK1", 100,20000);
            em.persist(book1);

            Book book2 = createBook("JPA BOOK2", 200, 40000);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);

            Orders order = Orders.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);

        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Member createMember(String name, String city, int street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
    }

}

