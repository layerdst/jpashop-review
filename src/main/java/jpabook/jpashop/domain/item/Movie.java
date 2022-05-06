package jpabook.jpashop.domain.item;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("M")
@Getter
public class Movie extends Item{

    private String director;
    private String actor;
}
