package me.refluxo.moduleloader.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    // A method that returns a String.
    String moduleName();

}
