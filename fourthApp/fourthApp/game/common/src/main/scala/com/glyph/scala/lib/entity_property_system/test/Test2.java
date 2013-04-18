package com.glyph.scala.lib.entity_property_system.test;

import java.util.LinkedList;

/**
 * @author glyph
 */
public class Test2 {
    private LinkedList<Dummy> list = new LinkedList<Dummy>();
    public Test2(){
        for(int i = 0; i < 1000; i++){
            list.add(new Dummy(i));
        }
    }
    class Dummy{
        private int value = 0;
        Dummy(int i){
            value = i;
        }
    }
}
