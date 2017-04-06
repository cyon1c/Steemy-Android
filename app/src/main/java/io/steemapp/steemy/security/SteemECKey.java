package io.steemapp.steemy.security;

import org.bitcoin.NativeSecp256k1;
import org.bitcoin.NativeSecp256k1Util;
import org.bitcoin.Secp256k1Context;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.LazyECPoint;
import org.bitcoinj.crypto.TransactionSignature;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;
import org.spongycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by John on 9/7/2016.
 */
public class SteemECKey extends ECKey {

    protected SteemECDSASigner mSteemSigner;

    public SteemECKey() {
        super();
    }

    public SteemECKey(SecureRandom secureRandom) {
        super(secureRandom);
    }

    protected SteemECKey(@Nullable BigInteger priv, ECPoint pub) {
        super(priv, pub);
    }

    protected SteemECKey(@Nullable BigInteger priv, LazyECPoint pub) {
        super(priv, pub);
    }

    public SteemECKey(@Nullable byte[] privKeyBytes, @Nullable byte[] pubKey) {
        super(privKeyBytes, pubKey);
    }

    @Override
    protected ECDSASignature doSign(Sha256Hash input, BigInteger privateKeyForSigning) {
        if (Secp256k1Context.isEnabled()) {
            try {
                byte[] signature = NativeSecp256k1.sign(
                        input.getBytes(),
                        Utils.bigIntegerToBytes(privateKeyForSigning, 32)
                );
                return ECDSASignature.decodeFromDER(signature);
            } catch (NativeSecp256k1Util.AssertFailException e) {
                throw new RuntimeException(e);
            }
        }
        if (FAKE_SIGNATURES)
            return TransactionSignature.dummy();
        checkNotNull(privateKeyForSigning);
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKeyForSigning, CURVE);
        if(mSteemSigner == null){
            mSteemSigner = new SteemECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            mSteemSigner.init(true, privKey);
        }
        BigInteger[] components = mSteemSigner.generateSignature(input.getBytes());
        ECDSASignature sig =  new ECDSASignature(components[0], components[1]);
        while(sig.r.toByteArray().length != 32 || sig.s.toByteArray().length != 32){
            components = mSteemSigner.generateSignature(input.getBytes());
            sig = new ECDSASignature(components[0], components[1]);
        }
        return sig;
    }
}
