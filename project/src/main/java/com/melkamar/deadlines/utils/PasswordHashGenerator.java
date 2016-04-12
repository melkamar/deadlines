package com.melkamar.deadlines.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 26.03.2016 15:13
 */
@Service
public class PasswordHashGenerator {
    @Autowired
    private ShaPasswordEncoder passwordEncoder;
    @Autowired
    private StringKeyGenerator randomStringGenerator;

    /**
     * Generates a secure password by hashing the salted plaintext.
     *
     * @param plaintextPassword Original password in plaintext.
     * @return HashAndSalt class with filled fields.
     */
    public HashAndSalt generatePasswordHash(String plaintextPassword) {
        String salt = randomStringGenerator.generateKey();
        String hash = passwordEncoder.encodePassword(plaintextPassword, salt);

        return new HashAndSalt(hash, salt);
    }

    public class HashAndSalt{
        final public String hash;
        final public String salt;

        public HashAndSalt(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }
    }
}
