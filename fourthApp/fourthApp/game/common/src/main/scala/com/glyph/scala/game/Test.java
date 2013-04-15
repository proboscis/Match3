package com.glyph.scala.game;

import com.glyph.scala.Glyph;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author glyph
 */
public class Test {
    public Test() {
//        test0();
//        test1();
//        test2();
//        test3();
        test5();
    }

    private void test5() {
        String tag = "java while loop";
        Glyph.log(tag, "=> start");
        long time = System.nanoTime();
        {
            int i = 10000;
            while (i > 0) {
                i = i - 1;
            }
        }
        long et = System.nanoTime() - time;
        printTime(tag, et);
    }

    private void test0() {
        String tag = "no loop";
        Glyph.log(tag, "=> start");
        long time = System.nanoTime();
        {
        }
        long et = System.nanoTime() - time;
        printTime(tag, et);
    }

    private void test1() {
        String tag = "for loop";
        Glyph.log(tag, "=> start");
        long time = System.nanoTime();
        {
            for (long i = 0; i < 10000; i++) {
            }
        }
        long et = System.nanoTime() - time;
        printTime(tag, et);
    }

    private void test2() {
        String tag = "LinkedList";
        Glyph.log(tag, "=> start");
        LinkedList<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        long time = System.nanoTime();
        {
            for (int i : list) {

            }
        }
        long et = System.nanoTime() - time;
        printTime(tag, et);
    }

    private void test3() {
        String tag = "ArrayList";
        Glyph.log(tag, "=> start");
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 10000; i++) {
            list.add(i);
        }
        long time = System.nanoTime();
        {
            for (int i : list) {

            }
        }
        long et = System.nanoTime() - time;
        printTime(tag, et);
    }


    void printTime(String tag, long t) {
        if (t >= 10000000) {
            t = t / 1000 / 1000;
            System.out.print(tag + ":");
            System.out.print(t);
            System.out.println("milliSec");
        } else if (t >= 10000) {
            t = t / 1000;
            System.out.print(tag + ":");
            System.out.print(t);
            System.out.println("nanoSec");
        } else {
            System.out.print(tag + ":");
            System.out.print(t);
            System.out.println("microSec");
        }
    }
}
