package ichttt.gradle.forgelogin;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class ForgeLoginWrapper {

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InterruptedException {
        if (Arrays.stream(args).anyMatch(s -> s.equals("--username")) && Arrays.stream(args).noneMatch(s -> s.equals("--password"))) {
            String[] newArgs = new String[args.length + 2];
            System.arraycopy(args, 0, newArgs, 0, args.length);
            CountDownLatch latch = GradleLoginGUI.create();
            latch.await();
            newArgs[newArgs.length - 2] = "--password";
            newArgs[newArgs.length - 1] = Objects.requireNonNull(GradleLoginGUI.getPasswordAndDiscard());
            args = newArgs;
        }
        Class<?> clazz;
        try {
            clazz = Class.forName("GradleStart");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find GradleStart - Invalid classpath?");
        }

        try {
            clazz.getDeclaredMethod("main", String[].class).invoke(null, (Object) args);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof RuntimeException) {
                Throwable cause2 = cause.getCause();
                if (cause2 != null && cause2.getClass().getName().equals("com.mojang.authlib.exceptions.InvalidCredentialsException"))
                    JOptionPane.showMessageDialog(null, cause2.getMessage());
            }
        }
    }
}
