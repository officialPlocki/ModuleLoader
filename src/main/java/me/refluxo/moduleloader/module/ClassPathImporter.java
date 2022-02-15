package me.refluxo.moduleloader.module;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathImporter extends URLClassLoader {

    public ClassPathImporter(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

}
