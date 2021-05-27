package fr.ostracraft.towns.utils;

import fr.ostracraft.towns.OstraTowns;
import fr.ostracraft.towns.utils.iterator.EnumerationIterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionUtil {

    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        try (JarFile jar = new JarFile(OstraTowns.get().getFile())) {
            packageName = packageName.replace('.', '/') + '/';
            for (final JarEntry e : new EnumerationIterable<>(jar.entries())) {
                if (!e.getName().startsWith(packageName) || e.getName().equalsIgnoreCase(packageName))
                    continue;
                String className = e.getName().replace(".class", "").replace('/', '.');
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

}
