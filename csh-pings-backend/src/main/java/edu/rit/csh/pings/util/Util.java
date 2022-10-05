package edu.rit.csh.pings.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
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

    public static String readFully(InputStream in) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024 * 1024);
        byte[] block = new byte[1024];
        int read;
        while ((read = in.read(block)) == block.length) {
            buf.put(block);
        }
        if (read != -1) {
            buf.put(block, 0, read);
        }
        return new String(buf.array(), 0, buf.position());
    }
}
