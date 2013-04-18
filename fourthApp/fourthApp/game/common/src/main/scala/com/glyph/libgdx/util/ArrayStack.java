package com.glyph.libgdx.util;

/**
 * @author glyph
 */
public class ArrayStack<T> extends ArrayBag<T> {
    int currentIndex = 0;
    public ArrayStack(){
        super();
    }

    public ArrayStack(int size){
        super(size);
    }

    public void push(T e){
       // Glyph.log("ArrayQueue","push"+currentIndex);
        ensureCapacity(currentIndex);
        elements[currentIndex++] = e;
    }
    @SuppressWarnings("unchecked")
    public T pop(){
       // Glyph.log("ArrayQueue","pop:"+currentIndex);
        //ensureCapacity(currentIndex-1);
        return (T)elements[--currentIndex];
    }

    public boolean isEmpty(){
        return currentIndex <= 0;
    }

    public int size(){
       return currentIndex;
    }
}
