package com.voidaspect.public24;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test utils
 */
public final class Tests {

    private Tests(){}

    public static String loadTestResourceAsString(String path) {
        Resource resource = new ClassPathResource(path);
        try {
            return new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
