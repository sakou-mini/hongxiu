package com.donglaistd.jinli.util;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.logging.Logger;

public class SSL {
    private static volatile SSLContext sslContext = null;
    static Logger logger = Logger.getLogger(Utils.class.getName());
    public static SSLContext createSSLContext(String type, String path, String password) throws Exception {
        if (null == sslContext) {
            synchronized (SSL.class) {
                if (null == sslContext) {
                    KeyStore ks = KeyStore.getInstance(type);
                    try (FileInputStream ksInputStream = new FileInputStream(path)) {
                        ks.load(ksInputStream, password.toCharArray());
                        var kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                        kmf.init(ks, password.toCharArray());
                        sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(kmf.getKeyManagers(), null, null);
                    }
                }
            }
        }
        return sslContext;
    }

    public static SslContext createSsLContext(){
        File certChainFile = new File("./config/ssl/server/server-cert.crt");    //server root certificate
        File keyFile = new File("./config/ssl/server/pkcs8_server.key");    //private key
        SslContext sslContext = null;
        try {
            sslContext = SslContextBuilder.forServer(certChainFile, keyFile).build();
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return sslContext;
    }
}
