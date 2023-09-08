package org.example.plansType;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Getter
public class PlansTypeObjectList extends PlansTypeImp {

    public List<Map<String, PlansTypeImp>> value = new ArrayList<>();
    public PlansTypeObjectList() {}

    public void addPlansTypeObjectList(Map<String, PlansTypeImp> p) {
        this.value.add(p);
    }

    public List<Map<String, PlansTypeImp>> getValue() {
        return this.value;
    }

}
