package com.f2prateek.dfg.core;



import static java.util.Locale.US;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Helper to get a gravatar hash for an email
 */
public class GravatarUtils {

    /**
     * Length of generated hash
     */
    public static final int HASH_LENGTH = 32;

    /**
     * Algorithm used for hashing
     */
    public static final String HASH_ALGORITHM = "MD5"; //$NON-NLS-1$

    /**
     * Charset used for hashing
     */
    public static final String CHARSET = "CP1252"; //$NON-NLS-1$

    private static String digest(final String value) {
        byte[] digested;
        try {
            digested = MessageDigest.getInstance(HASH_ALGORITHM).digest(
                    value.getBytes(CHARSET));
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        String hashed = new BigInteger(1, digested).toString(16);
        int padding = HASH_LENGTH - hashed.length();
        if (padding == 0)
            return hashed;

        char[] zeros = new char[padding];
        Arrays.fill(zeros, '0');
        return new StringBuilder(HASH_LENGTH).append(zeros).append(hashed)
                .toString();
    }

    /**
     * Get avatar hash for specified e-mail address
     *
     * @param email
     * @return hash
     */
    public static String getHash(String email) {
        if (TextUtils.isEmpty(email))
            return null;
        email = email.trim().toLowerCase(US);
        return email.length() > 0 ? digest(email) : null;
    }
}