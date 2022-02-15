package me.refluxo.moduleloader.module;

import me.refluxo.moduleloader.ModuleLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
        Collection<List<ModuleCommand>> mCMDs = commands.values();
        AtomicReference<ModuleCommand> cmd = new AtomicReference<>(null);
        for (List<ModuleCommand> moduleCommand : mCMDs) {
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

    public ModuleCommand getCommand(String command) {
        AtomicReference<ModuleCommand> cmd = new AtomicReference<>(null);
        commands.forEach((module, commands1) -> commands1.forEach(c -> {
            if(c.getClass().getAnnotation(Command.class).command().equalsIgnoreCase(command)) {
                cmd.set(c);
            } else if(Arrays.stream(c.getClass().getAnnotation(Command.class).aliases()).toList().contains(command)) {
                cmd.set(c);
            }
        }));
        return cmd.get();
    }

    public boolean canExecuted(String command) {
        AtomicReference<ModuleCommand> cmd = new AtomicReference<>(null);
        AtomicBoolean enabled = new AtomicBoolean(true);
        AtomicBoolean now = new AtomicBoolean(false);
        commands.forEach((module, commands1) -> {
            commands1.forEach(c -> {
                if(c.getClass().getAnnotation(Command.class).command().equalsIgnoreCase(command)) {
                    cmd.set(c);
                } else if(Arrays.stream(c.getClass().getAnnotation(Command.class).aliases()).toList().contains(command)) {
                    cmd.set(c);
                }
            });
            if(cmd.get() != null) {
                if(!now.get()) {
                    enabled.set(loaded.get(module));
                    now.set(true);
                }
            }
        });
        return enabled.get();
    }

    public void registerCommand(PluginModule module, ModuleCommand command) {
        List<ModuleCommand> list = new ArrayList<>(commands.get(module));
        if(!list.contains(command)) {
            list.add(command);
            commands.put(module, list);
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
    }

    public void unregisterCommand(ModuleCommand command) {
        unregisterCommand(bukkitCommands.get(command));
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

    public void addModuleClass(PluginModule module, Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>(moduleClasses.get(module));
        classes.add(clazz);
        moduleClasses.put(module, classes);
    }

    @SuppressWarnings("unchecked")
    private void unregisterCommand(org.bukkit.command.Command command) {
        Field commandMap;
        Field knownCommands;
        try {
            commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);
            knownCommands = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommands.setAccessible(true);
            ((Map<String, org.bukkit.command.Command>) knownCommands.get(commandMap.get(Bukkit.getServer())))
                    .remove(command.getName());
            command.unregister((CommandMap) commandMap.get(Bukkit.getServer()));
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

}