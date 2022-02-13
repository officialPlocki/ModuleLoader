package me.refluxo.moduleloader.module;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class ModuleListener<T extends Event> implements Listener {

    @EventHandler
    public void onEvent(T event) {

    }

}
