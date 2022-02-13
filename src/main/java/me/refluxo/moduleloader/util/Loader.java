package me.refluxo.moduleloader.util;

import me.refluxo.moduleloader.module.Module;
import me.refluxo.moduleloader.module.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Loader {

    private final File folder;
    private final ModuleManager manager;

    public Loader(File folder, ModuleManager manager) {
        this.manager = manager;
        this.folder = folder;
        if(!this.folder.exists()) {
            this.folder.mkdirs();
        }
    }

    @SuppressWarnings("deprecation")
    public void loadModules() {
        final HashMap<String, PluginModule> modules = new HashMap<>();
        final HashMap<PluginModule, List<Class<?>>> moduleClasses = new HashMap<>();
        final HashMap<PluginModule, List<ModuleListener<?>>> listeners = new HashMap<>();
        final HashMap<PluginModule, List<ModuleCommand>> commands = new HashMap<>();
        List<File> paths = Arrays.stream(Objects.requireNonNull(folder.listFiles())).toList();
        paths.forEach(file -> {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert jarFile != null;
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = new URL[0];
            try {
                urls = new URL[]{ new URL("jar:file:" + file.getAbsolutePath() +"!/") };
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            List<Class<?>> classes = new ArrayList<>();
            AtomicReference<PluginModule> module = new AtomicReference<>(null);
            List<ModuleCommand> cmds = new ArrayList<>();
            List<ModuleListener<?>> ls = new ArrayList<>();
            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if(je.isDirectory() || !je.getName().endsWith(".class")){
                    continue;
                }
                // -6 because of .class
                String className = je.getName().substring(0,je.getName().length()-6);
                className = className.replace('/', '.');
                try {
                    Class<?> c = cl.loadClass(className);
                    List<Annotation> annotations = new ArrayList<>(Arrays.stream(c.getAnnotations()).toList());
                    annotations.forEach(annotation -> {
                        if(annotation instanceof Command) {
                            try {
                                cmds.add((ModuleCommand) c.newInstance());
                            } catch (InstantiationException | IllegalAccessException ex) {
                                ex.printStackTrace();
                            }
                        } else if(annotation instanceof Listener) {
                            try {
                                ls.add((ModuleListener<?>) c.newInstance());
                            } catch (InstantiationException | IllegalAccessException ex) {
                                ex.printStackTrace();
                            }
                        } else if(annotation instanceof Module) {
                            try {
                                module.set((PluginModule) c.newInstance());
                            } catch (InstantiationException | IllegalAccessException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    classes.add(c);
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            if(module.get() != null) {
                modules.put(module.get().getClass().getAnnotation(Module.class).moduleName(), module.get());
                moduleClasses.put(module.get(), classes);
                listeners.put(module.get(), ls);
                commands.put(module.get(), cmds);
            }
        });
        modules.forEach((mName, module) -> {
            manager.registerModule(module);
            for (Class<?> aClass : moduleClasses.get(module)) {
                manager.addModuleClass(module, aClass);
            }
            for (ModuleCommand command : commands.get(module)) {
                manager.registerCommand(module, command);
            }
            for (ModuleListener<?> moduleListener : listeners.get(module)) {
                manager.registerListener(module, moduleListener);
            }
            manager.enableModule(module);
        });
    }

    public void unloadModules() {
        System.gc();
    }

}
