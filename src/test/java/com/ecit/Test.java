package com.ecit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

import java.util.function.BiConsumer;

public class Test {

    public static void main(String[] args) {
       /* Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> e) throws Exception {
                e.onSuccess("test");
            }
        }).subscribe(s -> {System.out.println(s);});*/

        Single single = Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<String> e) throws Exception {
                e.onSuccess("test");
            }
        });
        single.map(s -> "hello " + s);

        single.subscribe(s -> {System.out.println(s);});
    }
}
