package com.glyph.libgdx.util;

import java.util.Iterator;

/**
 * @author glyph
 */
public class LinkedList<E> implements Iterable<E>{
    Element head = new Element();
    Element tail = head;

    public class Iterator_ implements Iterator<E>{
        Element prev = head();
        Element current = prev;
        @Override
        public boolean hasNext() {
            boolean result = current.next != null;
            return result;
        }

        @Override
        public E next(){
            prev = current;
            current = current.next;
            return prev.element;
        }

        @Override
        public void remove() {
            prev.next = current.next;
        }
    };
    public void push(E e){
        Element next = new Element();
        next.element = e;
        tail.next = next;
    }

    public Element head(){
        return head;
    }

    public class Element{
        public E element;
        public Element next;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator_();
    }
}
