package ichttt.gradle.forgelogin;

import net.minecraftforge.gradle.GradleStartCommon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ForgeLoginWrapper {
    public static final Logger LOGGER = LogManager.getLogger("ForgeGradleLoginWrapper");

    public static void main(String[] args) throws Throwable {
        String username = getUsername(args);
        args = LoginHandler.loginPlayer(username, args);

        Field field = GradleStartCommon.class.getDeclaredField("LOGGER");
        field.setAccessible(true);
        field.set(null, new LoggerWrapper((org.apache.logging.log4j.core.Logger) field.get(null)));

        Class<?> gradleStartClass;
        try {
            gradleStartClass = Class.forName("GradleStart");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find GradleStart - Invalid classpath?");
        }

        try {
            gradleStartClass.getDeclaredMethod("main", String[].class).invoke(null, (Object) args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause != null)
                throw cause;
            else
                throw e;
        }
    }

    private static String getUsername(String[] rawData) {
        String username = null;
        boolean nextIsUsername = false;
        for (String s : rawData) {
            if (nextIsUsername) {
                username = s;
                break;
            }
            if (s.equals("--username"))
                nextIsUsername = true;
        }
        return username;
    }
}
