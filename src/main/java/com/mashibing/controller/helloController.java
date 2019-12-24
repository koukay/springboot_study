package com.mashibing.controller;

import com.mashibing.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloController {
    @Autowired
    private Person person;
    @RequestMapping("/test")
    public String test(){
        System.out.println(person.getSex());
        System.out.println(person.getAge());
        System.out.println(person.getName());
        return "Hello_Word";
    }
}
