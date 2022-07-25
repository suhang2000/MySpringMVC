package util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

@Slf4j
public class ClassUtil {
    public static final String FILE_PROTOCOL = "file";
    public static final String JAR_PROTOCOL = "jar";

    /**
     * get ClassLoader
     * @return ContextClassLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * get Class
     */
    public static @NotNull Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * instantiate Class
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(String className) {
        try {
            Class<?> clazz = loadClass(className);
            return (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * set field of class
     */
    public static void setField(Field field, Object target, Object value) {
        setField(field, target, value, true);
    }

    public static void setField(Field field, Object target, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("setField error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * scan basePackage
     * @param basePackage
     * @return set of classes
     */
    public static Set<Class<?>> getPackageClass(String basePackage) {
        URL url = getClassLoader().getResource(basePackage.replace(".", "/"));
        if (url == null) {
            throw new RuntimeException("can not found package: " + basePackage);
        }
        try {
            if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
                // plain folder, traverse
                File file = new File(url.getFile());
                Path path = file.toPath();
                return Files.walk(path)
                        .filter(path1 -> path1.toFile().getName().endsWith(".class"))
                        .map(path1 -> getClassByPath(path1, path, basePackage))
                        .collect(Collectors.toSet());
            } else if (url.getProtocol().equalsIgnoreCase(JAR_PROTOCOL)) {
                // Parse entry in JAR package
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                return jarURLConnection.getJarFile()
                        .stream()
                        .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                        .filter(jarEntry -> jarEntry.getName().replaceAll("/", ".").startsWith(basePackage))
                        .map(ClassUtil::getClassByJar)
                        .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (IOException e) {
            log.error("Load package failed", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * obtain Class from jar
     * @param jarEntry
     * @return class from jar
     */
    private static Class<?> getClassByJar(JarEntry jarEntry) {
        String jarEntryName = jarEntry.getName().replaceAll("/", ".");
        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."));
        return loadClass(className);
    }

    private static Class<?> getClassByPath(Path classpath, Path basePath, String basePackage) {
        String packageName = classpath.toString().replace(basePath.toString(), "");
        String className = (basePackage + packageName).replace("/", ".")
                .replace("\\", ".")
                .replace(".class", "");
        // if classname is under root path, remove the first '.'
        if (className.charAt(0) == '.') {
            className = className.substring(1);
        }
        return loadClass(className);
    }

}
