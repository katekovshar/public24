package com.voidaspect.public24;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test utils
 */
public final class Tests {

    private Tests(){}

    public static String loadTestResourceAsString(String path) {
        URI uri;
        try {
            uri = Tests.class.getResource(path).toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            return new String(Files.readAllBytes(Paths.get(uri)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
