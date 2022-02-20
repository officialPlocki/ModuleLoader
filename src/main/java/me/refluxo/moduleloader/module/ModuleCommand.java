package me.refluxo.moduleloader.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleCommand {

    // This is the command that the user will type in the console to execute the command.
    String command();

   // It's a list of aliases for the command.
    String[] aliases();

    // It's a list of permissions that the user needs to execute the command.
    String[] permissions();

    // It's a description of the command.
    String description();

    // It's a description of the command.
    String usage();

    // It's a flag that tells the command executor if it should use tab completion for this command.
    boolean tabCompleterIsEnabled();

}
