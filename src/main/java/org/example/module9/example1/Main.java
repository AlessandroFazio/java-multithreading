package org.example.module9.example1;

import java.util.concurrent.atomic.AtomicReference;

public class Main {
    public static void main(String[] args) {
        String oldName = "gianni";
        String newName = "maria";
        AtomicReference<String> atomicReference = new AtomicReference<>(oldName);

        if(atomicReference.compareAndSet(oldName, newName)) {
            System.out.println("New Value is " + atomicReference.get());
        } else {
            System.out.println("Nothing happened");
        }
    }
}
