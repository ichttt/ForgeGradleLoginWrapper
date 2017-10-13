package ichttt.gradle.forgelogin;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

public class EncryptionService {
    private static final Path GLOBAL_PATH = Paths.get(System.getProperty("user.home") + (System.getProperty("os.name").toLowerCase().startsWith("windows") ?"/AppData/Roaming/" : "/")  + ".minecraft/DEV_AES_GLOBAL_KEY.aes");

    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        ForgeLoginWrapper.LOGGER.info("Generating new AES key...");
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(128); //standard java only supports this
        return generator.generateKey();
    }

    public static void saveKey(SecretKey key) throws IOException {
        Files.deleteIfExists(GLOBAL_PATH);
        Files.createFile(GLOBAL_PATH);
        byte[] encoded = key.getEncoded();
        StringBuilder builder = new StringBuilder();
        for (byte b : encoded)
            builder.append(b).append("/");
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(GLOBAL_PATH);
            writer.write(builder.toString());
            writer.flush();
        } finally {
            Utils.closeSilent(writer);
        }
    }

    public static SecretKey loadKey()
    {
        if (!Files.exists(GLOBAL_PATH))
            return null;
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(GLOBAL_PATH);
            String[] read = reader.readLine().split("/");
            List<Byte> bytes = new ArrayList<>();
            for (String s : read)
                bytes.add(Byte.parseByte(s));

            return new SecretKeySpec(convert(bytes), "AES");
        } catch (IOException | NumberFormatException e) {
            ForgeLoginWrapper.LOGGER.error("Failed to read AES key!", e);
            return null;
        } finally {
            Utils.closeSilent(reader);
        }
    }

    public static String encryptString(SecretKey key, String s) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] ciphertext = cipher.doFinal(s.getBytes("UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (byte b : iv)
            builder.append(b).append(",");
        builder.append("##");
        for (byte b : ciphertext)
            builder.append(b).append(",");
        return builder.toString();
    }

    public static String decryptString(SecretKey key, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, NumberFormatException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        List<Byte> ivList = new ArrayList<>();
        List<Byte> ciphertextList = new ArrayList<>();
        String[] baseSplit = data.split("##");
        String[] ivSplit = baseSplit[0].split(",");
        String[] ciphertextSplit = baseSplit[1].split(",");
        for (String s : ivSplit)
            ivList.add(Byte.parseByte(s));
        for (String s : ciphertextSplit)
            ciphertextList.add(Byte.parseByte(s));
        byte[] iv = convert(ivList);
        byte[] ciphertext = convert(ciphertextList);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return new String(cipher.doFinal(ciphertext), "UTF-8");
    }

    private static byte[] convert(List<Byte> bytes) {
        //convert object to primitive array... sigh
        int size = bytes.size();
        byte[] byteArray = new byte[bytes.size()];
        for(int i = 0 ; i < size; i++)
            byteArray[i] = bytes.get(i);
        return byteArray;
    }
}
