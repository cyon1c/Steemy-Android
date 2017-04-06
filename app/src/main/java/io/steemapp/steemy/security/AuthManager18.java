package io.steemapp.steemy.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.security.KeyPairGeneratorSpec;
import android.support.v4.util.Pair;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

/**
 * Created by maxke on 24.07.2016.
 * AuthManager for devices From API 18 - 22.
 */

@SuppressWarnings("deprecation")
public class AuthManager18 extends AuthManager {

    public AuthManager18(Context ctx) {
        super(ctx);
    }

    @Override
    public void maybeCreateKey(KeyStore ks) throws AuthManagerException {
        try {
            if (!ks.containsAlias(KEY_ALIAS)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 25);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(ctx)
                        .setAlias(KEY_ALIAS)
                        .setKeySize(2048)
                        .setSubject(new X500Principal("CN=Steemy, O=Steemy"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);
                generator.generateKeyPair();
            }
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    @Override
    protected String encrypt(String input) throws AuthManagerException  {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(KEY_ALIAS, null);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            cipherOutputStream.write(input.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    @Override
    protected String decrypt(String base64) throws AuthManagerException {
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(KEY_ALIAS, null);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(Base64.decode(base64, Base64.NO_WRAP)), cipher);
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
