package io.steemapp.steemy.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

//import com.google.bitcoin.core.Base58;
//import com.google.bitcoin.core.ECKey;
//import com.google.bitcoin.core.Sha256Hash;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.spongycastle.crypto.digests.RIPEMD160Digest;

import java.math.BigInteger;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.steemapp.steemy.SteemyGlobals;
import io.steemapp.steemy.models.Account;
import io.steemapp.steemy.models.OwnerKeyAuth;


/**
 * Created by maxke on 25.07.2016.
 * General AuthManager base class
 */

public abstract class AuthManager {

    protected String KEY_ALIAS = "steemy";

    protected String USER = "user";
    protected String KEY = "48B6878EC466ABBE";

    protected Context ctx;
    protected KeyStore ks;

    protected SteemyGlobals.KEY_TYPE mHighestAuth;

    public AuthManager(Context ctx) {
        this.ctx = ctx;
    }

    protected void init() throws AuthManagerException {
        try {
            ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            maybeCreateKey(ks);
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    public boolean authenticate(Account account, String keyOrMaster, SteemyGlobals.KEY_TYPE keyType) throws AuthManagerException{
        if(keyType == SteemyGlobals.KEY_TYPE.MASTER){
            HashMap<SteemyGlobals.KEY_TYPE, Pair<String, String>> keys = getKeyPairsWithMaster(account.getName(), keyOrMaster);
            if(verifyKeysGeneratedSuccessfully(keys)) {
                if (verifyAccount(account, keys)){
                    initNewAuthUser(account, keys);
                    return true;
                } else
                    return false;
            }else{
                throw new AuthManagerException(new Exception("keys not generated correctly"));
            }
        }else{
            ECKey ecKey = getPrivateKeyFromWIF(keyOrMaster);
            Pair <String, String> key = new Pair<>(ecKey.getPrivateKeyAsHex(), ecKey.getPublicKeyAsHex());
            initNewAuthUser(account, key, keyType);
            return true;
        }
    }

    public void updateAccount(Account account){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit().putString(USER, account.toJson()).apply();
    }

    protected boolean verifyKeysGeneratedSuccessfully(HashMap<SteemyGlobals.KEY_TYPE, Pair<String, String>> keys){
        return(keys != null && keys.containsKey(SteemyGlobals.KEY_TYPE.OWNER)
                && keys.containsKey(SteemyGlobals.KEY_TYPE.ACTIVE)
                && keys.containsKey(SteemyGlobals.KEY_TYPE.POSTING)
                && keys.containsKey(SteemyGlobals.KEY_TYPE.MEMO));
    }

    protected void initNewAuthUser(Account account, HashMap<SteemyGlobals.KEY_TYPE, Pair<String, String>> keys) throws AuthManagerException{
        for(Map.Entry<SteemyGlobals.KEY_TYPE, Pair<String, String>> key : keys.entrySet()){
            saveUserCredentials(account, key.getValue(), key.getKey());
        }
    }

    protected void initNewAuthUser(Account account, Pair<String, String> key, SteemyGlobals.KEY_TYPE keyType) throws AuthManagerException{
        saveUserCredentials(account, key, keyType);
    }

    protected abstract void maybeCreateKey(KeyStore ks) throws AuthManagerException;
    protected abstract String encrypt(String input) throws AuthManagerException;
    protected abstract String decrypt(String base64) throws AuthManagerException;

    public void saveUserCredentials(Account user, Pair<String, String> key, SteemyGlobals.KEY_TYPE auth) throws AuthManagerException {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String pw = encrypt(key.first);

//            Timber.d(pw);
            prefs.edit()
                    .putString(USER, user.toJson())
                    .putString(KEY+Integer.toString(auth.getValue()), pw)
                    .apply();
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    public String getAuthUser(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getString(USER, null);
    }

    public Pair<String, byte[]> getHighestUserKey() throws AuthManagerException {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String pw = null;
            int currentLevel = SteemyGlobals.KEY_TYPE.MASTER.getValue();
            while(currentLevel > -1) { //-1 because the lowest level of auth is MEMO == 0;
                pw = prefs.getString(KEY + currentLevel, null);
                if(pw != null) {
                    switch (currentLevel){
                        case 4:
                            mHighestAuth = SteemyGlobals.KEY_TYPE.MASTER;
                            break;
                        case 3:
                            mHighestAuth = SteemyGlobals.KEY_TYPE.OWNER;
                            break;
                        case 2:
                            mHighestAuth = SteemyGlobals.KEY_TYPE.ACTIVE;
                            break;
                        case 1:
                            mHighestAuth = SteemyGlobals.KEY_TYPE.POSTING;
                            break;
                        case 0:
                            mHighestAuth = SteemyGlobals.KEY_TYPE.MEMO;
                            break;
                    }
                    break;
                }
                currentLevel--;
            }
            return new Pair<>(prefs.getString(USER, null), hexStringToByteArray(decrypt(pw)));
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    public Pair<String, byte[]> getUserKey(SteemyGlobals.KEY_TYPE keyType) throws AuthManagerException {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String pw = prefs.getString(KEY+Integer.toString(keyType.getValue()), null);
//            Timber.d(pw);
            return new Pair<>(prefs.getString(USER, null), hexStringToByteArray(decrypt(pw)));
        } catch (Exception e) {
            throw new AuthManagerException(e);
        }
    }

    private HashMap<SteemyGlobals.KEY_TYPE, Pair<String, String>> getKeyPairsWithMaster(String username, String password){
        HashMap<SteemyGlobals.KEY_TYPE, Pair<String, String>> keys = new HashMap<>(2);
        try {
            SteemyGlobals.KEY_TYPE[] rolls = {
                    SteemyGlobals.KEY_TYPE.ACTIVE,
                    SteemyGlobals.KEY_TYPE.POSTING,
            };
            for(int i = 0; i < 2; i++) {
                String seed = username + rolls[i].toString() + password;
                byte[] pk = Sha256Hash.of(seed.getBytes("UTF-8")).getBytes();
                keys.put(rolls[i], new Pair<String, String>(byteArrayToHexString(pk), getPublicKeyFromPrivate(pk)));
            }
            return keys;
        }catch (Exception e){
            Log.i("LoginUtil", e.getMessage());
        }
        return null;
    }

    private String getPublicKeyFromPrivate(byte[] pk){
        byte[] pubKey = ECKey.publicKeyFromPrivate(new BigInteger(1, pk), true);
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(pubKey, 0, pubKey.length);
        byte[] hashedKey = new byte[20];
        digest.doFinal(hashedKey, 0);
        byte[] checksum = Arrays.copyOfRange(hashedKey, 0, 4);
        byte[] dest = new byte[pubKey.length + checksum.length];
        System.arraycopy(pubKey, 0, dest, 0, pubKey.length);
        System.arraycopy(checksum, 0, dest, pubKey.length, checksum.length);
        return Base58.encode(dest);
    }

    protected boolean verifyAccount(Account account, HashMap<SteemyGlobals.KEY_TYPE, Pair<String, String>> keys){
        String[] pubKeys = new String[4];
        for(Map.Entry<SteemyGlobals.KEY_TYPE, Pair<String, String>> key : keys.entrySet()){
            switch(key.getKey()){
                case OWNER:
                    pubKeys[0] = key.getValue().second;
                    break;
                case ACTIVE:
                    pubKeys[1] = key.getValue().second;
                    break;
                case POSTING:
                    pubKeys[2] = key.getValue().second;
                    break;
                case MEMO:
                    pubKeys[3] = key.getValue().second;
                    break;
            }
        }
        pubKeys[0] = "STM" + pubKeys[0];
        pubKeys[1] = "STM" + pubKeys[1];
        pubKeys[2] = "STM" + pubKeys[2];
        pubKeys[3] = "STM" + pubKeys[3];
        if(pubKeys[0].equals(account.getOwnerKeyAuth().getKeyAuths().get(0).get(0))){
            if(pubKeys[1].equals(account.getActiveKeyAuth().getKeyAuths().get(0).get(0))){
                if(pubKeys[2].equals(account.getPostingKeyAuth().getKeyAuths().get(0).get(0))){
                    if(pubKeys[3].equals(account.getMemoKey())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean verifyAccount(Account account, SteemyGlobals.KEY_TYPE keyType, Pair<String, String> key){
        String pubKey = new String();
        switch(keyType){
            case OWNER:
                pubKey = key.second;
                pubKey = "STM" + pubKey;
                if(pubKey.equals(account.getOwnerKeyAuth().getKeyAuths().get(0).get(0))){
                    return true;
                }
                break;
            case ACTIVE:
                pubKey = key.second;
                pubKey = "STM" + pubKey;
                if(pubKey.equals(account.getActiveKeyAuth().getKeyAuths().get(0).get(0))){
                    return true;
                }
                break;
            case POSTING:
                pubKey = key.second;
                pubKey = "STM" + pubKey;
                if(pubKey.equals(account.getPostingKeyAuth().getKeyAuths().get(0).get(0))){
                    return true;
                }
                break;
            case MEMO:
                pubKey = key.second;
                pubKey = "STM" + pubKey;
                if(pubKey.equals(account.getMemoKey())){
                    return true;
                }
                break;
        }
        return false;
    }


    private ECKey getPrivateKeyFromWIF(String wif){
        return DumpedPrivateKey.fromBase58(null, wif).getKey();
    }

    public void logout(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit()
                .remove(USER)
                .remove(KEY+ Integer.toString(SteemyGlobals.KEY_TYPE.OWNER.getValue()))
                .remove(KEY+ Integer.toString(SteemyGlobals.KEY_TYPE.ACTIVE.getValue()))
                .remove(KEY+ Integer.toString(SteemyGlobals.KEY_TYPE.POSTING.getValue()))
                .remove(KEY+ Integer.toString(SteemyGlobals.KEY_TYPE.MEMO.getValue()))
                .apply();
    }

    public void removeKey(SteemyGlobals.KEY_TYPE key_type){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        prefs.edit()
                .remove(KEY+ Integer.toString(key_type.getValue()))
                .apply();
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private String byteArrayToHexString(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
