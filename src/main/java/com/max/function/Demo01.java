package com.max.function;

import java.util.function.Function;

public class Demo01 {

    public static void main(String[] args) {

        Function function = (str)->{
            return str;
        };

        System.out.println(function.apply("sss"));

    }
}
