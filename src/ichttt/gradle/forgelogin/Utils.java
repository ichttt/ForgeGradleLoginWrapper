package ichttt.gradle.forgelogin;

import java.io.Closeable;
import java.io.IOException;

public class Utils {

    public static void closeSilent(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException e) {
            ForgeLoginWrapper.LOGGER.catching(e);
        }
    }
}
