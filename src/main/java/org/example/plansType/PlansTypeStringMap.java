package org.example.plansType;

import java.util.Map;
import java.util.HashMap;
import lombok.Getter;

@Getter
public class PlansTypeStringMap extends PlansTypeImp {

    private final Map<String, String> value = new HashMap<>();


    public PlansTypeStringMap() {}
    public void setPlansTypeStringMap(String key, String val) {
        this.value.put(key, val);
    }

    public Map<String, String> getValue() {
        return this.value;
    }
}
