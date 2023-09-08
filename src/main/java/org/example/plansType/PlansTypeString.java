package org.example.plansType;

import lombok.Getter;

@Getter
public class PlansTypeString extends PlansTypeImp {

    private final String value;

    public PlansTypeString(String v) {
        this.value = v;
    }

    public String getValue() {
        return this.value;
    }
}
