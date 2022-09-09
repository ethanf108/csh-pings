package edu.rit.csh.pings.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.Random;

public enum Util {
    ;
    private static final Random RANDOM;

    static {
        RANDOM = new SecureRandom();
    }

    public static String generateNoise() {
        return RANDOM
                .ints('A', 'z' + 1)
                .filter(Character::isAlphabetic)
                .limit(255)
                .mapToObj(Character::toString)
                .reduce(String::concat)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot generate random noise :("));
    }
}
