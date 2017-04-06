package io.steemapp.steemy.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by maxke on 25.07.2016.
 * AuthManager for devices on API 23+
 */
@TargetApi(Build.VERSION_CODES.M)
public class AuthManager23 extends AuthManager {

    public AuthManager23(Context ctx) {
        super(ctx);
    }

    @Override
    protected void maybeCreateKey(KeyStore ks) throws AuthManagerException {
        try {
            if (!ks.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder(KEY_ALIAS,
                                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                                .build());
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    @Override
    protected String encrypt(String input) throws AuthManagerException {
        try {
            SecretKey key = (SecretKey) ks.getKey(KEY_ALIAS, null);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            cipherOutputStream.write(input.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();

            // Save IV and ciphertext together
            return Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP) + "%" + Base64.encodeToString(vals, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    @Override
    protected String decrypt(String base64) throws AuthManagerException {
        try {
            // Split the IV from ciphertext
            String[] ivAndCipher = base64.split("%");

            SecretKey key = (SecretKey) ks.getKey(KEY_ALIAS, null);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, Base64.decode(ivAndCipher[0], Base64.NO_WRAP)));

            CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(Base64.decode(ivAndCipher[1], Base64.NO_WRAP)), cipher);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i);
            }

            return new String(bytes, 0, bytes.length, "UTF-8");
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }
}
