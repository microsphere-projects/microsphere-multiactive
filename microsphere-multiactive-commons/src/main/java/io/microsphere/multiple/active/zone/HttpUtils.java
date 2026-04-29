package io.microsphere.multiple.active.zone;

import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static io.microsphere.io.IOUtils.copyToString;
import static io.microsphere.util.StringUtils.isNotBlank;
import static java.nio.charset.StandardCharsets.UTF_8;

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
     * @param url     the URL
     * @param timeout timeout – an int that specifies the connect timeout value in milliseconds
     * @return The response context
     * @throws IOException
     */
    public static String doGet(String url, int timeout) throws IOException {
        String content = null;
        if (isNotBlank(url)) {
            URLConnection urlConnection = new URL(url).openConnection();
            if (urlConnection instanceof HttpURLConnection) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                urlConnection.setConnectTimeout(timeout);
                urlConnection.setReadTimeout(timeout);
                try (InputStream inputStream = httpURLConnection.getInputStream()) {
                    content = copyToString(inputStream, UTF_8);
                    logger.info("The response of Availability Zone Endpoint[URI : '{}'] : {}", url, content);
                } finally {
                    httpURLConnection.disconnect();
                }
            }
        }
        return content;
    }
}
