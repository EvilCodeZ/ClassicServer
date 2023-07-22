package de.evilcodez.classicserver.plugin;

import java.net.URL;
import java.net.URLClassLoader;

class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }
}
