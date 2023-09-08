package org.example.plansType;

import lombok.Getter;

@Getter
public class PlansTypeInteger extends PlansTypeImp {

    private final Integer value;

    public PlansTypeInteger(Integer v) {
        this.value = v;
    }

    public Integer getValue() {
        return this.value;
    }
}
