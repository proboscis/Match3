package com.glyph.scala.test;

import java.util.LinkedList;

/**
 * @author glyph
 */
public class Test4 {
    private LinkedList<Integer> list = new LinkedList<Integer>();
    public Test4(){
        for( int i = 0; i < 1000; i ++){
            list.push(i);
        }
    }
    public void test(){
        while(!list.isEmpty()){
            list.pop();
        }
    }
}
