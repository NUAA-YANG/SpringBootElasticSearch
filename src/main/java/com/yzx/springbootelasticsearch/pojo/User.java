package com.yzx.springbootelasticsearch.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Component
public class User {
    String name;
    int age;
    String desc;
}
