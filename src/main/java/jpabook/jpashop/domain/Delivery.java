package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name="delivery_id")
    private Long id;

    @JsonIgnore
    @OneToOne(mappedBy ="delivery")
    private Orders orders;

    @Embedded
    private Address address;

    private DeliveryStatus status;



}
