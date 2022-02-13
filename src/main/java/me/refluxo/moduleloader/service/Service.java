package me.refluxo.moduleloader.service;

public interface Service {

    default boolean isAvailable(){
        return true;
    }
}
