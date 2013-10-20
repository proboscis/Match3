package com.glyph.java.test;

/**
 * @author glyph
 */
public class EnumTest {
    enum Testing{
        A,B,C
    }
    public static void main(String[] args) {
        System.out.println(Testing.valueOf(null));
    }
}
