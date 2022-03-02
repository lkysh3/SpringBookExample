package com.ksh.template;

public interface LineCallback<T> {
    T doSomethingWithLine(String line, T value);
}
