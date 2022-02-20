package me.refluxo.moduleloader.module;

import java.net.URL;
import java.net.URLClassLoader;

public class ClassPathImporter extends URLClassLoader {

    public ClassPathImporter(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    /**
     * Loads a class from the class loader
     *
     * @param name The name of the class to load.
     * @return Nothing.
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    /**
     * Add a URL to the list of URLs to be downloaded
     *
     * @param url The URL to be added to the classloader.
     */
    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

}
