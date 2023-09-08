package org.example.plansType;

import java.util.List;
import java.util.ArrayList;

public class PlansTypeStringList extends PlansTypeImp {

    public List<String> value = new ArrayList<>();
    public PlansTypeStringList() {}

    public void addPlansTypeStringList(String v) {
        this.value.add(v);
    }

    public List<String> getValue() {
        return this.value;
    }
}
