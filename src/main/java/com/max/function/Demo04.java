package com.max.function;

import java.util.function.Supplier;

public class Demo04 {

    public static void main(String[] args) {
        Supplier<String> stringSupplier = ()->{
            return "aaaa";
        };
    }
}
