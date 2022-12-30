package org.example;

import java.util.HashMap;

class Component {

    public String name;
    public double time;
    public HashMap<String, Integer> subComponents;

    public Component(String name, double time) {
        this.name = name;
        this.time = time;
        this.subComponents = new HashMap<String, Integer>();
    }

    public Component addSubcomponent(String name, int amount) {
        subComponents.put(name, amount);
        return this;
    }
}