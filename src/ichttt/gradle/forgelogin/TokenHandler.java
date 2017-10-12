package ichttt.gradle.forgelogin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TokenHandler {
    private static final Path LOGIN_TOKEN = Paths.get(System.getProperty("user.dir") + "\\authtoken.dat");

    public static List<String> readToken() {
        if (!Files.exists(LOGIN_TOKEN))
            return null;
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(LOGIN_TOKEN);
            List<String> args = new ArrayList<>();
            String read;
            while ((read = reader.readLine()) != null)
                args.add(read);
            return args;
        } catch (IOException e) {
            ForgeLoginWrapper.LOGGER.error("Could not read token!", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    ForgeLoginWrapper.LOGGER.catching(e);
                }
            }
        }
        return null;
    }

    public static void saveArgs(List<String> data) {
        BufferedWriter writer = null;
        try {
            Files.deleteIfExists(LOGIN_TOKEN);
            Files.createFile(LOGIN_TOKEN);
            writer = Files.newBufferedWriter(LOGIN_TOKEN);
            for (String s : data) {
                writer.write(s);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            ForgeLoginWrapper.LOGGER.error("Could not save token!", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    ForgeLoginWrapper.LOGGER.catching(e);
                }
            }
        }
    }
}
