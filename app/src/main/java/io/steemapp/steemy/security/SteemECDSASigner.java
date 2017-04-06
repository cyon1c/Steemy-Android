package io.steemapp.steemy.security;

import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DSA;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.signers.DSAKCalculator;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECMultiplier;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by John on 9/7/2016.
 */
public class SteemECDSASigner implements ECConstants, DSA {

    private final DSAKCalculator kCalculator;

    private ECKeyParameters key;
    private SecureRandom random;
    private byte[] currentDigest;
    private SHA256Digest kDigest;
    byte i = 0;

    /**
     * Configuration with an alternate, possibly deterministic calculator of K.
     *
     * @param kCalculator a K value calculator.
     */
    public SteemECDSASigner(DSAKCalculator kCalculator)
    {
        this.kCalculator = kCalculator;
    }

    public void init(
            boolean                 forSigning,
            CipherParameters param)
    {
        SecureRandom providedRandom = null;
        i = 0;
        if (forSigning)
        {
            if (param instanceof ParametersWithRandom)
            {
                ParametersWithRandom rParam = (ParametersWithRandom)param;

                this.key = (ECPrivateKeyParameters)rParam.getParameters();
                providedRandom = rParam.getRandom();
            }
            else
            {
                this.key = (ECPrivateKeyParameters)param;
            }
        }
        else
        {
            this.key = (ECPublicKeyParameters)param;
        }

        this.random = initSecureRandom(forSigning && !kCalculator.isDeterministic(), providedRandom);
    }

    // 5.3 pg 28
    /**
     * generate a signature for the given message using the key we were
     * initialised with. For conventional DSA the message should be a SHA-1
     * hash of the message of interest.
     *
     * @param message the message that will be verified later.
     */
    public BigInteger[] generateSignature(
            byte[] message)
    {
        ECDomainParameters ec = key.getParameters();
        BigInteger n = ec.getN();
        BigInteger e = calculateE(n, message);
        BigInteger d = ((ECPrivateKeyParameters)key).getD();
        if(currentDigest == null)
            currentDigest = message;

        kDigest = new SHA256Digest();
        kDigest.update(currentDigest, 0, currentDigest.length);
        kDigest.update(i++);
        currentDigest = new byte[kDigest.getDigestSize()];
        kDigest.doFinal(currentDigest, 0);

        if (kCalculator.isDeterministic()) {
            kCalculator.init(n, d, currentDigest);
        } else {
            kCalculator.init(n, random);
        }

        BigInteger r, s;

        ECMultiplier basePointMultiplier = createBasePointMultiplier();

        // 5.3.2
        do // generate s
        {
            BigInteger k;
            do // generate r
            {
                k = kCalculator.nextK();

                ECPoint p = basePointMultiplier.multiply(ec.getG(), k).normalize();

                // 5.3.3
                r = p.getAffineXCoord().toBigInteger().mod(n);
            }
            while (r.equals(ZERO));

            s = k.modInverse(n).multiply(e.add(d.multiply(r))).mod(n);
        }
        while (s.equals(ZERO));

        return new BigInteger[]{ r, s };
    }

    // 5.4 pg 29
    /**
     * return true if the value r and s represent a DSA signature for
     * the passed in message (for standard DSA the message should be
     * a SHA-1 hash of the real message to be verified).
     */
    public boolean verifySignature(
            byte[]      message,
            BigInteger  r,
            BigInteger  s)
    {
        ECDomainParameters ec = key.getParameters();
        BigInteger n = ec.getN();
        BigInteger e = calculateE(n, message);

        // r in the range [1,n-1]
        if (r.compareTo(ONE) < 0 || r.compareTo(n) >= 0)
        {
            return false;
        }

        // s in the range [1,n-1]
        if (s.compareTo(ONE) < 0 || s.compareTo(n) >= 0)
        {
            return false;
        }

        BigInteger c = s.modInverse(n);

        BigInteger u1 = e.multiply(c).mod(n);
        BigInteger u2 = r.multiply(c).mod(n);

        ECPoint G = ec.getG();
        ECPoint Q = ((ECPublicKeyParameters)key).getQ();

        ECPoint point = ECAlgorithms.sumOfTwoMultiplies(G, u1, Q, u2).normalize();

        // components must be bogus.
        if (point.isInfinity())
        {
            return false;
        }

        BigInteger v = point.getAffineXCoord().toBigInteger().mod(n);

        return v.equals(r);
    }

    protected BigInteger calculateE(BigInteger n, byte[] message)
    {
        int log2n = n.bitLength();
        int messageBitLength = message.length * 8;

        BigInteger e = new BigInteger(1, message);
        if (log2n < messageBitLength)
        {
            e = e.shiftRight(messageBitLength - log2n);
        }
        return e;
    }

    protected ECMultiplier createBasePointMultiplier()
    {
        return new FixedPointCombMultiplier();
    }

    protected SecureRandom initSecureRandom(boolean needed, SecureRandom provided)
    {
        return !needed ? null : (provided != null) ? provided : new SecureRandom();
    }
}
