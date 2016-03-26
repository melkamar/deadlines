package com.melkamar.deadlines.services;

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
     * @return First index is the hash, second index the used salt.
     */
    public String[] generatePasswordHash(String plaintextPassword) {
        String salt = randomStringGenerator.generateKey();
        String hash = passwordEncoder.encodePassword(plaintextPassword, salt);

        return new String[]{hash, salt};
    }
}
