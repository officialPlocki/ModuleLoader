package me.refluxo.moduleloader.util;

import me.refluxo.moduleloader.ModuleLoader;
import me.refluxo.moduleloader.module.Command;
import me.refluxo.moduleloader.module.Module;
import me.refluxo.moduleloader.module.ModuleCommand;
import me.refluxo.moduleloader.module.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ModuleManager {

    private final HashMap<PluginModule, Boolean> loaded;
    private final HashMap<String, PluginModule> modules;
    private final HashMap<PluginModule, List<Listener>> listeners;
    private final HashMap<ModuleCommand, List<String>> permissions;
    private final HashMap<PluginModule, List<ModuleCommand>> commands;
    private final HashMap<PluginModule, List<Class<?>>> moduleClasses;
    private final HashMap<ModuleCommand, org.bukkit.command.Command> bukkitCommands;
    private final Loader loader;

    public ModuleManager() {
        modules = new HashMap<>();
        listeners = new HashMap<>();
        permissions = new HashMap<>();
        commands = new HashMap<>();
        loaded = new HashMap<>();
        moduleClasses = new HashMap<>();
        bukkitCommands = new HashMap<>();
        loader = new Loader(new File("plugins/ModuleLoader/modules"), this);
    }

    public void initializeAllModules() {
        loader.loadModules();
    }

    public void unloadAllUnusedClasses() {
        modules.forEach((s, module) -> disableModule(module));
        loader.unloadModules();
    }

    public List<PluginModule> getModules() {
        return modules.values().stream().toList();
    }

    public ModuleCommand getModuleCommand(String command) {
        Collection<List<ModuleCommand>> mcmds = commands.values();
        AtomicReference<ModuleCommand> cmd = new AtomicReference<>(null);
        for (List<ModuleCommand> moduleCommand : mcmds) {
            moduleCommand.forEach(c -> {
                if(c.getClass().getAnnotation(Command.class).command().equalsIgnoreCase(command)) {
                    cmd.set(c);
                }
            });
        }
        return cmd.get();
    }

    public void registerModule(PluginModule module) {
        modules.put(module.getClass().getAnnotation(Module.class).moduleName(), module);
        this.listeners.put(module, new ArrayList<>());
        this.commands.put(module, new ArrayList<>());
        loaded.put(module, false);
        moduleClasses.put(module, new ArrayList<>());
    }

    public PluginModule getModule(String module) {
        return modules.get(module);
    }

    public void enableModule(PluginModule module) {
        if(!loaded.get(module)) {
            module.enableModule();
            for (ModuleCommand command : commands.get(module)) {
                registerCommand(module, command);
            }
            for(Listener listener : listeners.get(module)) {
                registerListener(module, listener);
            }
            loaded.put(module, true);
        }
    }

    public void disableModule(PluginModule module) {
        if(loaded.get(module)) {
            module.disableModule();
            for (ModuleCommand command : commands.get(module)) {
                unregisterCommand(command);
            }
            for(Listener listener : listeners.get(module)) {
                unregisterListener(listener);
            }
            loaded.put(module, false);
        }
    }

    public void registerCommand(PluginModule module, ModuleCommand command) {
        List<ModuleCommand> list = new ArrayList<>(commands.get(module));
        if(!list.contains(command)) {
            list.add(command);
            commands.put(module, list);
            assert command != null;
            Command annotation = command.getClass().getAnnotation(Command.class);
            org.bukkit.command.Command cmd = new org.bukkit.command.Command(annotation.command(), annotation.description(), annotation.usage(), Arrays.stream(annotation.aliases()).toList()) {
                @Override
                public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                    return command.onCommand(sender, this, commandLabel, args);
                }
            };
            bukkitCommands.put(command, cmd);
            permissions.put(command, Arrays.stream(annotation.permissions()).toList());
        }
        Bukkit.getServer().getCommandMap().register(bukkitCommands.get(command).getName() + "-fallback", bukkitCommands.get(command));
        if(command.getClass().getAnnotation(Command.class).tabCompleterIsEnabled()) {
            PluginCommand pc = Bukkit.getServer().getPluginCommand(bukkitCommands.get(command).getName());
            assert pc != null;
            pc.setTabCompleter(command);
        }
    }

    public void unregisterCommand(ModuleCommand command) {
        Command annotation = command.getClass().getAnnotation(Command.class);
        Object result = null;
        try {
            result = getPrivateField(Bukkit.getServer().getPluginManager(), "commandMap");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        SimpleCommandMap commandMap = (SimpleCommandMap) result;
        unRegisterBukkitCommand(commandMap.getCommand(annotation.command()));
    }

    public void registerListener(PluginModule module, Listener listener) {
        List<Listener> list = listeners.getOrDefault(module, new ArrayList<>());
        if(!list.contains(listener)) {
            list.add(listener);
            listeners.put(module, list);
        }
        Bukkit.getServer().getPluginManager().registerEvents(listener, ModuleLoader.getPlugin());
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public List<Listener> getListeners(PluginModule module) {
        return listeners.getOrDefault(module, new ArrayList<>());
    }

    public List<ModuleCommand> getCommands(PluginModule module) {
        return commands.get(module);
    }

    public List<String> getPermissions(ModuleCommand command) {
        return permissions.get(command);
    }

    public List<Class<?>> getModuleClasses(PluginModule module) {
        return moduleClasses.get(module);
    }

    public void addModuleClass(PluginModule module, Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>(moduleClasses.get(module));
        classes.add(clazz);
        moduleClasses.put(module, classes);
    }

    public HashMap<ModuleCommand, List<String>> getPermissions(PluginModule module) {
        HashMap<ModuleCommand, List<String>> map = new HashMap<>();
        List<ModuleCommand> commands = this.commands.get(module);
        commands.forEach(command -> {
            map.put(command, permissions.get(command));
        });
        return map;
    }


    //utils

    private Object getPrivateField(Object object, String field)throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = clazz.getDeclaredField(field);
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    private void unRegisterBukkitCommand(org.bukkit.command.Command cmd) {
        try {
            CommandMap map = Bukkit.getCommandMap();
            map.getKnownCommands().remove(cmd.getName());
            for (String alias : cmd.getAliases()){
                map.getKnownCommands().remove(alias);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
