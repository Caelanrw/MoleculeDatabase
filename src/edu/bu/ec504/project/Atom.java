package edu.bu.ec504.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Atom implements Serializable {
    public Atom(String name,int elem){
        atomName = name;
        degree = 0;
        connected = new HashMap<>();
        elementType = elem;
    }
    public void addEdge(Atom i) {
        degree++;
        if(connected.containsKey(i.getName())) {
            connected.put(i.getName(),connected.get(i.getName())+1);
        }
        else
            connected.put(i.getName(),1);
    }
    public String getName() {
        return this.atomName;
    }
    private String atomName;
    private int elementType;
    private int degree;
    private Map<String,Integer> connected;
}

