package ichttt.gradle.forgelogin;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TokenHandler {
    private static final Path LOGIN_TOKEN = Paths.get(System.getProperty("user.dir") + "\\authtoken.dat");

    public static List<String> readToken(SecretKey key) {
        if (!Files.exists(LOGIN_TOKEN))
            return null;
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(LOGIN_TOKEN);
            List<String> args = new ArrayList<>();
            String read;
            while ((read = reader.readLine()) != null)
                args.add(read);
            args.set(1, EncryptionService.decryptString(key, args.get(1)));
            return args;
        } catch (Exception e) {
            ForgeLoginWrapper.LOGGER.error("Could not read token!", e);
        } finally {
            Utils.closeSilent(reader);
        }
        return null;
    }

    public static void saveArgs(SecretKey key, List<String> data) {
        BufferedWriter writer = null;
        try {
            Files.deleteIfExists(LOGIN_TOKEN);
            Files.createFile(LOGIN_TOKEN);
            writer = Files.newBufferedWriter(LOGIN_TOKEN);
            boolean setNow = false;
            boolean hasSet = false;
            for (String s : data) {
                if (setNow && !hasSet) {
                    writer.write(EncryptionService.encryptString(key, s));
                    hasSet = true;
                } else
                    writer.write(s);
                writer.newLine();
                setNow = true;
            }
            writer.flush();
        } catch (Exception e) {
            ForgeLoginWrapper.LOGGER.error("Could not save token!", e);
        } finally {
            Utils.closeSilent(writer);
        }
    }
}
