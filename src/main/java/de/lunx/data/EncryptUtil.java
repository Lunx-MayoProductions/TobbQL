package de.lunx.data;

import com.google.gson.Gson;
import de.lunx.Main;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.security.MessageDigest;
import java.util.Base64;

@Slf4j
public class EncryptUtil {
    private static final Gson GSON = new Gson();
    private static final String secret = Main.getInstance().getDataSecret();

    /**
     * Loads a JSON file from the specified file path and deserializes it into the specified type.
     *
     * <p>If encryption is enabled, the method attempts to decrypt the file content
     * before deserializing it.</p>
     *
     * @param path The file path of the JSON file to be loaded.
     * @param type The type of the object to be deserialized. This can be specified
     *             using a {@link com.google.gson.reflect.TypeToken}.
     * @return The deserialized object of type {@code T}, or {@code null} if an error occurs
     *         while reading or deserializing the file.
     * @throws NullPointerException if the specified file path is {@code null}.
     * @throws NoSuchFileException  if the specified file does not exist.
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * Type mapType = new TypeToken<Map<String, Integer>>(){}.getType();
     * Map<String, Integer> myMap = loadObject("path/to/file.json", mapType);
     * }</pre>
     */
    @Nullable
    public static  <T> T loadObject(String path, Type type) throws NoSuchFileException {
        if (!new File(path).exists()) {
            throw new NoSuchFileException(path);
        }
        try {
            byte[] encryptedData = loadEncryptedData(path);
            byte[] key = secret.getBytes();
            byte[] iv = secret.getBytes();
            String decryptedData = decrypt(encryptedData, key, iv);
            return GSON.fromJson(decryptedData, type);
        } catch (Exception e) {
            log.error("Could not parse file. Maybe it's not encrypted?");
            log.error(e.getMessage());
            return null;
        }

    }

    /**
     * Saves the given object as a JSON file at the specified path.
     *
     * <p>If encryption is enabled, the method encrypts the JSON content before writing it to the file.</p>
     *
     * @param directory The directory where the JSON file should be saved.
     *                  If the directory does not exist, it will be created.
     * @param fileName  The name of the JSON file (without the path).
     * @throws NullPointerException if {@code directory} or {@code fileName} is {@code null}.
     */
    public static void saveObject(File directory, String fileName, Object o) {
        if (directory.mkdirs()) {

            log.info("Creating data folders...");
        }
        try {
            byte[] encryptedData = encrypt(GSON.toJson(o), secret.getBytes(), secret.getBytes());
            try (FileOutputStream fos = new FileOutputStream(new File(directory, fileName))) {
                fos.write(encryptedData);
            }
        } catch (Exception e) {
            log.error("Failed to save encrypted data:");
            log.error(e.getMessage());
        }
    }

    /**
     * Decrypts the given encrypted data using AES.
     *
     * @param encryptedData The encrypted data as a byte array.
     * @param key           The AES key as a byte array.
     * @param iv            The initialization vector as a byte array.
     * @return The decrypted data as a {@link String}.
     * @throws Exception If decryption fails.
     */
    private static String decrypt(byte[] encryptedData, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedData);
        return new String(decryptedBytes);
    }

    /**
     * Encrypts the given data using AES.
     *
     * @param data The data to encrypt as a {@link String}.
     * @param key  The AES key as a byte array.
     * @param iv   The initialization vector as a byte array.
     * @return The encrypted data as a byte array.
     * @throws Exception If encryption fails.
     */
    private static byte[] encrypt(String data, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(data.getBytes());
    }

    /**
     * Loads encrypted data from a file.
     *
     * @param filePath The path to the file containing the encrypted data.
     * @return The encrypted data as a byte array.
     * @throws IOException If reading the file fails.
     */
    private static byte[] loadEncryptedData(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return fis.readAllBytes();
        }
    }

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Failed to hash password");
            log.error(e.getMessage());
        }
        return "";
    }
}