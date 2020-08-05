package com.max.single;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum EnumSingle {

    INSTANCE;
    public EnumSingle getInstance(){
        return INSTANCE;
    }

}
class Test{
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<EnumSingle> enumSingleConstructor =  EnumSingle.class.getDeclaredConstructor(null);
        enumSingleConstructor.setAccessible(true);
        EnumSingle instance2 = enumSingleConstructor.newInstance();
    }
}