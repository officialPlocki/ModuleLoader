package me.refluxo.moduleloader.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String command();

    String[] aliases();

    String[] permissions();

    String description();

    String usage();

    boolean tabCompleterIsEnabled();

}
