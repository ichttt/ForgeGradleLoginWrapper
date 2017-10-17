package ichttt.gradle.forgelogin;

import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.io.IOException;
import java.net.Proxy;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class LoginHandler {

    public static String[] loginPlayer(String username, String[] args) throws InterruptedException {
        boolean hadUsername = username != null;
        if (!hadUsername) {
            username = JOptionPane.showInputDialog("Please enter you email address");
            if (Strings.isNullOrEmpty(username))
                throw new RuntimeException("User does not want to give us his username :/");
        }

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("username", username);
        SecretKey key = EncryptionService.loadKey();
        if (key == null) {
            try {
                key = EncryptionService.generateKey();
                EncryptionService.saveKey(key);
            } catch (NoSuchAlgorithmException | IOException e) {
                throw new RuntimeException("Could not generate encryption key!");
            }
        }
        List<String> token = TokenHandler.readToken(key);
        boolean loggedIn = false;
        YggdrasilUserAuthentication auth;
        if (token != null) {
            auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
            credentials.put("accessToken", token.get(1));

            auth.loadFromStorage(credentials);
            try {
                auth.logIn();
                args = getArgs(args, auth, hadUsername, token);
                loggedIn = true;
            } catch (AuthenticationException e) {
                ForgeLoginWrapper.LOGGER.info("Login with token failed!", e);
            }
        } else {
            ForgeLoginWrapper.LOGGER.info("Skipping token login attempt as no token could be found");
        }
        auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);

        if (!loggedIn) {
            LoginGUI gui = new LoginGUI();
            CountDownLatch latch = gui.getLatch();
            latch.await();
            String password = gui.getPasswordAndDiscard();
            auth.setPassword(password);
            if (!Strings.isNullOrEmpty(password)) {
                try {
                    List<String> newArgs = attemptLogin(auth);
                    TokenHandler.saveArgs(key, newArgs);
                    args = getArgs(args, auth, hadUsername, newArgs);
                } catch (AuthenticationException e) {
                    JOptionPane.showMessageDialog(null, "Failed to log in!\n" + e.toString(), "Login Failed", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(e);
                }
            }
        }
        return args;
    }


    private static String[] getArgs(String[] args, YggdrasilUserAuthentication auth, boolean hadUsername, List<String> newArgs) throws AuthenticationException {

        if (hadUsername) {
            String[] oldArgs = new String[args.length - 2];
            boolean skip = false;
            int i = 0;
            for (String s : args) {
                if (skip)
                    skip = false;
                else if (s.equals("--username"))
                    skip = true;
                else {
                    oldArgs[i] = s;
                    i++;
                }
            }
            args = oldArgs;
        }

        newArgs.addAll(Arrays.asList(args));
        args = newArgs.toArray(new String[newArgs.size()]);
        return args;
    }

    //Stolen and modified from FG
    private static List<String> attemptLogin(YggdrasilUserAuthentication auth) throws AuthenticationException {
        auth.logIn();

        ForgeLoginWrapper.LOGGER.info("Login Succesful!");
        List<String> data = new ArrayList<>(10);
        put(data, "accessToken", auth.getAuthenticatedToken());
        put(data, "uuid", auth.getSelectedProfile().getId().toString().replace("-", ""));
        put(data, "username", auth.getSelectedProfile().getName());
        put(data, "userType", auth.getUserType().getName());

        // 1.8 only apperantly.. -_-
        put(data, "userProperties", new GsonBuilder().registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer()).create().toJson(auth.getUserProperties()));
        return data;
    }

    private static void put(List<String> where, String key, String value) {
        where.add("--" + key);
        where.add(value);
    }
}
