package com.example.obyx_test;

import java.util.Map;

public class Data {

    private Map<String,Object> map;

    public Data() {
    }

    public Data(Map<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
