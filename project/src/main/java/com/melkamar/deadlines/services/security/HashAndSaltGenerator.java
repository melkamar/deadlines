/*
 * Copyright (c) 2016 Martin Melka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.melkamar.deadlines.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

/**
 * @author Martin Melka
 */
@Service
public class HashAndSaltGenerator {
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
