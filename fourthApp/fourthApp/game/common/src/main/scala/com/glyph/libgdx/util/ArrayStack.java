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

    /**
     * push an element and return an index where the element is put
     * @param e
     * @return index of e in the stack
     */
    public int push(T e){
       // Glyph.log("ArrayQueue","push"+currentIndex);
        ensureCapacity(currentIndex);
        elements[currentIndex] = e;
        return currentIndex++;
    }
    @SuppressWarnings("unchecked")
    public T pop(){
       // Glyph.log("ArrayQueue","pop:"+currentIndex);
        //ensureCapacity(currentIndex-1);
        return (T)elements[--currentIndex];
    }

    /**
     * removes all the elements returned true with( == )operator
     * @param e
     */
    public void remove(T e){
        for(int i = 0; i < currentIndex;i++){
            if(e == elements[i]){
                nonOrderedRemoveIndex(i);
            }
        }
    }
    public void nonOrderedRemoveIndex(int index){
        elements[index] = elements[--currentIndex];
        elements[currentIndex +1] = 0;
    }
    @Override
    public void clear(){
        super.clear();
        currentIndex = 0;
    }

    public void clearStack(){
        for(int i =0 ; i < currentIndex;i ++){
            elements[i] = null;
        }
        currentIndex = 0;
    }

    /**
     * check whether this stack contains element or not
     * @param e
     * @return
     */
    public boolean contains(T e){
        for(int i =0 ; i < currentIndex;i ++){
            if(elements[i] == e) return true;
        }
        return false;
    }

    public boolean isEmpty(){
        return currentIndex <= 0;
    }

    public int size(){
       return currentIndex;
    }
}
