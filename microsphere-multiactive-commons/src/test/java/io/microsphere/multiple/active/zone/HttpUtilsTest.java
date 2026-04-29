package io.microsphere.multiple.active.zone;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link HttpUtils}
 */
class HttpUtilsTest {

    @Test
    void testDoGetWithNullUrl() throws IOException {
        String result = HttpUtils.doGet(null, 1000);
        assertNull(result);
    }

    @Test
    void testDoGetWithEmptyUrl() throws IOException {
        String result = HttpUtils.doGet("", 1000);
        assertNull(result);
    }

    @Test
    void testDoGetWithBlankUrl() throws IOException {
        String result = HttpUtils.doGet("   ", 1000);
        assertNull(result);
    }

    @Test
    void testDoGetWithNonHttpUrl(@TempDir Path tempDir) throws IOException {
        // Create a temp file to use with file:// URL
        Path tempFile = tempDir.resolve("test.txt");
        Files.write(tempFile, "test content".getBytes());

        // A file:// URL will create a FileURLConnection (not HttpURLConnection)
        // so the instanceof check returns false, and the method returns null
        String result = HttpUtils.doGet(tempFile.toUri().toString(), 1000);
        assertNull(result);
    }

    @Test
    void testDoGetWithHttpUrl() throws IOException {
        String expectedContent = "hello zone";

        // Start a local HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        int port = server.getAddress().getPort();
        server.createContext("/zone", exchange -> {
            byte[] response = expectedContent.getBytes();
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });
        server.start();
        try {
            String url = "http://localhost:" + port + "/zone";
            String result = HttpUtils.doGet(url, 3000);
            assertEquals(expectedContent, result);
        } finally {
            server.stop(0);
        }
    }
}
