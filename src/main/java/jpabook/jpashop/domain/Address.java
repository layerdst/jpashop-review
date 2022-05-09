package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Getter
@Setter
@RequiredArgsConstructor
public class Address {

    private String city;
    private int street;
    private String zipcode;


    public Address(String city, int street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
