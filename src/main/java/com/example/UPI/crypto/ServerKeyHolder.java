package com.example.UPI.crypto;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class ServerKeyHolder {

    private static final Logger log = LoggerFactory.getLogger(ServerKeyHolder.class);

    @Value("${upi.security.private-key-path:#{null}}")
    private String privateKeyPath;

    @Value("${upi.security.public-key-path:#{null}}")
    private String publicKeyPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        if (privateKeyPath != null && publicKeyPath != null &&
                Files.exists(Paths.get(privateKeyPath)) && Files.exists(Paths.get(publicKeyPath))) {
            loadKeysFromDisk();
        } else {
            log.warn("Secure key paths not found or not configured. Falling back to ephemeral keys for local development.");
            generateEphemeralKeys();
        }
    }

    private void loadKeysFromDisk() throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Load Public Key (X.509)
        byte[] pubBytes = Files.readAllBytes(Paths.get(publicKeyPath));
        // Remove standard PEM headers if they exist
        String pubString = new String(pubBytes).replaceAll("-----(BEGIN|END) (.*)KEY-----", "").replaceAll("\\s", "");
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(Base64.getDecoder().decode(pubString));
        this.publicKey = keyFactory.generatePublic(pubSpec);

        // Load Private Key (PKCS#8)
        byte[] privBytes = Files.readAllBytes(Paths.get(privateKeyPath));
        String privString = new String(privBytes).replaceAll("-----(BEGIN|END) (.*)KEY-----", "").replaceAll("\\s", "");
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privString));
        this.privateKey = keyFactory.generatePrivate(privSpec);

        log.info("Production RSA keys successfully loaded from secure storage.");
    }

    private void generateEphemeralKeys() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
        log.info("Ephemeral RSA keypair generated. Public key fingerprint: {}",
                getPublicKeyBase64().substring(0, 32) + "...");
    }

    public PublicKey getPublicKey() { return publicKey; }
    public PrivateKey getPrivateKey() { return privateKey; }

    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}