package com.max.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {


    private Integer id;
    private String name;
    private Integer age;

    public static void main(String[] args) {
        User u1 = new User(1,"a",11);
        User u2 = new User(2,"b",22);
        User u3 = new User(3,"c",33);
        User u4 = new User(4,"d",44);
        User u5 = new User(5,"e",11);

        List<User> list = Arrays.asList(u1,u2,u3,u4,u5);

        list.stream().filter(u->{ return u.id%2 == 0;}).filter(u->{return u.age>23;}).map(user -> user.name.toUpperCase()).limit(1).forEach(System.out::println);

    }
}
