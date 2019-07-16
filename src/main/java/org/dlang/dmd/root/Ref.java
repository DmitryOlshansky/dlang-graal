package org.dlang.dmd.root;

public class Ref<T> {
    public T value;
    public Ref(T value) { this.value = value; }
    public Ref<T> copy(){ return new Ref<>(value); }
}
