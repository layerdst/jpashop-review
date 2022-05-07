package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
<<<<<<< HEAD
import java.util.List;
=======
>>>>>>> aa7e07f8ce8645c608d486b85fc6acf20dce38f3

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        if(item.getId()==null){
            em.persist(item);
        }else{
            em.merge(item);
        }
    }

<<<<<<< HEAD
    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }

=======
>>>>>>> aa7e07f8ce8645c608d486b85fc6acf20dce38f3
}
