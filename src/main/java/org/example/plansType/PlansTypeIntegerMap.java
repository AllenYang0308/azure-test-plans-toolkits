package org.example.plansType;

import java.util.Map;
import java.util.HashMap;
import lombok.Getter;

@Getter
public class PlansTypeIntegerMap extends PlansTypeImp {

    private final Map<String, Integer> value = new HashMap<>();

    public PlansTypeIntegerMap(String key, Integer val) {
        this.value.put(key, val);
    }

    public Map<String, Integer> getValue() {
        return this.value;
    }
}
