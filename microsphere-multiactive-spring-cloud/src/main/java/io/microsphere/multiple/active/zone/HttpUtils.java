package io.microsphere.multiple.active.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * The utilities class for simple http request.
 *
 * @author <a href="mailto:warlklown@gmail.com">Walklown<a/>
 * @since 1.0.0
 */
public abstract class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * Retrieve a representation by doing a GET on the specified URL. The response (if any) is returned.
     *
     * @param url the URL
     * @param timeout timeout – an int that specifies the connect timeout value in milliseconds
     * @return The response context
     * @throws IOException
     */
    public static String doGet(String url, int timeout) throws IOException {
        String content = null;
        if (StringUtils.hasText(url)) {
            URLConnection urlConnection = new URL(url).openConnection();
            if (urlConnection instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                urlConnection.setConnectTimeout(timeout);
                urlConnection.setReadTimeout(timeout);
                try (InputStream inputStream = httpURLConnection.getInputStream()) {
                    content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                    logger.info("The response of Availability Zone Endpoint[URI : '{}'] : {}", url, content);
                } finally {
                    httpURLConnection.disconnect();
                }
            }
        }
        return content;
    }
}
