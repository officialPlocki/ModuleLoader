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

    // This is a hashmap that keeps track of which modules are loaded.
    private final HashMap<PluginModule, Boolean> loaded;
    // This is a hashmap that keeps track of which modules are loaded.
    private final HashMap<String, PluginModule> modules;
    // A hashmap that keeps track of which listeners are registered to which modules.
    private final HashMap<PluginModule, List<Listener>> listeners;
    // This is a hashmap that keeps track of which permissions are registered to which commands.
    private final HashMap<ModuleCommandExecutor, List<String>> permissions;
    // This is a hashmap that keeps track of which commands are registered to which modules.
    private final HashMap<PluginModule, List<ModuleCommandExecutor>> commands;
    // This is a hashmap that keeps track of which classes are loaded for each module.
    private final HashMap<PluginModule, List<Class<?>>> moduleClasses;
    // This is a hashmap that keeps track of which commands are registered to which modules.
    private final HashMap<ModuleCommandExecutor, org.bukkit.command.Command> bukkitCommands;
    // A reference to the `Loader` class.
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

    /**
     * Load all modules in the
     * modules directory
     */
    public void initializeAllModules() {
        loader.loadModules();
    }

    /**
     * It iterates through all the modules and disables them
     */
    public void unloadAllUnusedClasses() {
        modules.forEach((s, module) -> disableModule(module));
        loader.unloadModules();
    }

    /**
     * Returns a list of all the modules in the plugin
     *
     * @return A list of all the modules in the plugin.
     */
    public List<PluginModule> getModules() {
        return modules.values().stream().toList();
    }

    /**
     * Given a command, return the ModuleCommandExecutor that is registered to that command
     *
     * @param command The command that the user types in the chat.
     * @return A ModuleCommandExecutor object.
     */
    public ModuleCommandExecutor getModuleCommand(String command) {
        Collection<List<ModuleCommandExecutor>> mCMDs = commands.values();
        AtomicReference<ModuleCommandExecutor> cmd = new AtomicReference<>(null);
        for (List<ModuleCommandExecutor> moduleCommand : mCMDs) {
            moduleCommand.forEach(c -> {
                if(c.getClass().getAnnotation(ModuleCommand.class).command().equalsIgnoreCase(command)) {
                    cmd.set(c);
                }
            });
        }
        return cmd.get();
    }

    /**
     * It takes a module and adds it to the list of modules
     *
     * @param module The PluginModule that is being registered.
     */
    public void registerModule(PluginModule module) {
        modules.put(module.getClass().getAnnotation(Module.class).moduleName(), module);
        this.listeners.put(module, new ArrayList<>());
        this.commands.put(module, new ArrayList<>());
        loaded.put(module, false);
        moduleClasses.put(module, new ArrayList<>());
    }

    /**
     * Get the module with the given name
     *
     * @param module The name of the module to get.
     * @return The PluginModule object that is associated with the given module name.
     */
    public PluginModule getModule(String module) {
        return modules.get(module);
    }

    /**
     * It enables the module, and then registers all of the commands and listeners that the module has
     *
     * @param module The PluginModule that is being enabled.
     */
    public void enableModule(PluginModule module) {
        if(!loaded.get(module)) {
            module.enableModule();
            for (ModuleCommandExecutor command : commands.get(module)) {
                registerCommand(module, command);
            }
            for(Listener listener : listeners.get(module)) {
                registerListener(module, listener);
            }
            loaded.put(module, true);
        }
    }

    /**
     * It disables the module, unregisters all the commands and listeners registered by the module, and then removes the
     * module from the loaded list
     *
     * @param module The PluginModule that you want to disable.
     */
    public void disableModule(PluginModule module) {
        if(loaded.get(module)) {
            module.disableModule();
            for (ModuleCommandExecutor command : commands.get(module)) {
                unregisterCommand(command);
            }
            for(Listener listener : listeners.get(module)) {
                unregisterListener(listener);
            }
            loaded.put(module, false);
        }
    }

    /**
     * It iterates through all the commands in the module and checks if the command is the same as the command we're
     * looking for. If it is, it returns the command. If it isn't, it checks if the command is an alias of the command
     * we're looking for. If it is, it returns the command. If it isn't, it returns null
     *
     * @param command The command that the user is trying to execute.
     * @return The command executor.
     */
    public ModuleCommandExecutor getCommand(String command) {
        AtomicReference<ModuleCommandExecutor> cmd = new AtomicReference<>(null);
        commands.forEach((module, commands1) -> commands1.forEach(c -> {
            if(c.getClass().getAnnotation(ModuleCommand.class).command().equalsIgnoreCase(command)) {
                cmd.set(c);
            } else if(Arrays.stream(c.getClass().getAnnotation(ModuleCommand.class).aliases()).toList().contains(command)) {
                cmd.set(c);
            }
        }));
        return cmd.get();
    }

    /**
     * It iterates through all the commands in the commands map, and if the command is the same as the command we're trying
     * to execute, it sets the cmd variable to that command. If the command is an alias of the command we're trying to
     * execute, it sets the cmd variable to that command. If the command is found, it sets the enabled variable to the
     * value of the loaded variable in the loaded map. If the command is not found, it sets the enabled variable to true.
     *
     * @param command The command that is being executed.
     * @return The command executor.
     */
    public boolean canExecuted(String command) {
        AtomicReference<ModuleCommandExecutor> cmd = new AtomicReference<>(null);
        AtomicBoolean enabled = new AtomicBoolean(true);
        AtomicBoolean now = new AtomicBoolean(false);
        commands.forEach((module, commands1) -> {
            commands1.forEach(c -> {
                if(c.getClass().getAnnotation(ModuleCommand.class).command().equalsIgnoreCase(command)) {
                    cmd.set(c);
                } else if(Arrays.stream(c.getClass().getAnnotation(ModuleCommand.class).aliases()).toList().contains(command)) {
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

    /**
     * It registers a command with the bukkit command map
     *
     * @param module The plugin module that the command is registered to.
     * @param command The command that the user types in to execute the command.
     */
    public void registerCommand(PluginModule module, ModuleCommandExecutor command) {
        List<ModuleCommandExecutor> list = new ArrayList<>(commands.get(module));
        if(!list.contains(command)) {
            list.add(command);
            commands.put(module, list);
            ModuleCommand annotation = command.getClass().getAnnotation(ModuleCommand.class);

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

    /**
     * It takes a ModuleCommandExecutor and removes it from the list of commands
     *
     * @param command The command you want to unregister.
     */
    public void unregisterCommand(ModuleCommandExecutor command) {
        unregisterCommand(bukkitCommands.get(command));
    }

    /**
     * It registers a listener to a module
     *
     * @param module The module that the listener is for.
     * @param listener The Listener object that will be registered with the PluginManager.
     */
    public void registerListener(PluginModule module, Listener listener) {
        List<Listener> list = listeners.getOrDefault(module, new ArrayList<>());
        if(!list.contains(listener)) {
            list.add(listener);
            listeners.put(module, list);
        }
        Bukkit.getServer().getPluginManager().registerEvents(listener, ModuleLoader.getPlugin());
    }

    /**
     * UnregisterListener(Listener listener)
     *
     * @param listener The Listener object that is being unregistered.
     */
    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    /**
     * Get the listeners for a given module.
     *
     * @param module The module that the listener is for.
     * @return A list of listeners.
     */
    public List<Listener> getListeners(PluginModule module) {
        return listeners.getOrDefault(module, new ArrayList<>());
    }

    /**
     * Returns a list of all the commands that are registered for the specified module
     *
     * @param module The module that the command is being registered for.
     * @return A list of ModuleCommandExecutors.
     */
    public List<ModuleCommandExecutor> getCommands(PluginModule module) {
        return commands.get(module);
    }

    /**
     * Returns the list of permissions for the given command
     *
     * @param command The command that is being executed.
     * @return A list of strings.
     */
    public List<String> getPermissions(ModuleCommandExecutor command) {
        return permissions.get(command);
    }

    /**
     * Add a class to the list of classes that are associated with a module
     *
     * @param module The module that the class is being added to.
     * @param clazz The class that is being added to the module.
     */
    public void addModuleClass(PluginModule module, Class<?> clazz) {
        List<Class<?>> classes = new ArrayList<>(moduleClasses.get(module));
        classes.add(clazz);
        moduleClasses.put(module, classes);
    }

    /**
     * It unregisters the command from the server
     *
     * @param command The command to unregister.
     */
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
