package com.melkamar.deadlines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 14:23
 */

@Component
public class AfterStart {
    @Autowired
    StringKeyGenerator stringKeyGenerator;

    @PostConstruct
    public void doStuff(){
        for (int i=0; i<10; i++){
            System.out.println("GenKey: "+stringKeyGenerator.generateKey());
        }
    }
}
