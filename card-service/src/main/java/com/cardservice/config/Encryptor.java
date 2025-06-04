package com.cardservice.config;

import com.cardservice.exception.*;
import io.jsonwebtoken.io.SerializationException;
import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SerializationUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

@Configuration
public class Encryptor implements AttributeConverter<Object, String> {
    @Value("${application.security.card.secret-key}")
    private String encryptionKey;
    @Value("${application.security.card.encryption-cipher}")
    private String encryptionCipher;

    private Key key;
    private Cipher cipher;

    private Key getKey() {
        if (key == null) {
            key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        }
        return key;
    }

    private Cipher getCipher() {
        try {

            if (cipher == null) {
                cipher = Cipher.getInstance("AES");
            }
            return cipher;
        } catch (GeneralSecurityException e) {
            throw new CipherInitializationException("Error in get instance Cipher", e);
        }
    }

    private void initCipher(int encryptMode) {
        try {
            getCipher().init(encryptMode, getKey());
        } catch (GeneralSecurityException e) {
            throw new CipherInitializationException("Initialization error Cipher", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            initCipher(Cipher.ENCRYPT_MODE);
            byte[] bytes = SerializationUtils.serialize(attribute);
            return Base64.getEncoder().encodeToString(getCipher().doFinal(bytes));
        } catch (GeneralSecurityException e) {
            throw new DataEncryptionException("Encrypt error", e);
        } catch (SerializationException e) {
            throw new DataSerializationException("Serialization error ", e);
        } catch (IllegalArgumentException e) {
            throw new DataProcessingException("Uncorrected data", e);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        initCipher(Cipher.DECRYPT_MODE);
        try {
            byte[] bytes = getCipher().doFinal(Base64.getDecoder().decode(dbData));
            return SerializationUtils.deserialize(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new DataDecryptionException("Error while decrypt data", e);
        }
    }
}
