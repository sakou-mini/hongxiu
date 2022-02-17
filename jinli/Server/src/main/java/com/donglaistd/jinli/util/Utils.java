package com.donglaistd.jinli.util;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Utils {

    public static String createUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String makeLiveToken() {
        return createUUID();
    }

    public static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    public static SSLContext createSSLContext(String type, String path, String password){
        SSLContext sslContext = null;
        try {
            KeyStore ks = KeyStore.getInstance(type);
            InputStream ksInputStream = new FileInputStream(path);
            ks.load(ksInputStream, password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password.toCharArray());
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }


    @SuppressWarnings("rawtypes")
    public static ArrayList<Class> getClasses(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        var path = packageName.replace('.', '/');
        var resources = classLoader.getResources(path);
        var dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            var resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        var classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    @SuppressWarnings("rawtypes")
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    /**
     * guava get hash值。
     */
    public static long hash(String key) {
        Charset charset = Charset.forName("UTF-8");
        return Math.abs(Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(charset)).asInt());
    }

    public static String getMd5Hash(String input){
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(StandardCharsets.UTF_8.encode(input));
        return String.format("%032x", new BigInteger(1, messageDigest.digest()));
    }
}
