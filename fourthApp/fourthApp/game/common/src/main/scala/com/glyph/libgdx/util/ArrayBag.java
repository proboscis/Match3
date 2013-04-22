package com.glyph.libgdx.util;

/**
 * @author glyph
 */
public class ArrayBag<T> {
    protected Object[] elements;
    public ArrayBag(){
        this(1);
    }
    public ArrayBag(int size){
        elements = new Object[size];
    }
    public void ensureCapacity(int size){
        while(elements.length <= size){
            Object[] next = new Object[elements.length * 2];
            System.arraycopy(elements,0,next,0,elements.length);
            elements = next;
        }
    }

    public T setWithResult(int index,T e){
        ensureCapacity(index);
        elements[index] = e;
        return e;
    }
    public void set(int index,T e){
        ensureCapacity(index);
        elements[index] = e;
    }

    /**
     * fills all elements with null
     */
    public void clear(){
        for(int i = 0; i < elements.length; i++){
            elements[i] = null;
        }
    }

    @SuppressWarnings("unchecked")
    public T get(int index){
        if( index < elements.length){
            return (T)elements[index];
        }else{
            return null;
        }
    }
    public int size(){
        return elements.length;
    }
}
