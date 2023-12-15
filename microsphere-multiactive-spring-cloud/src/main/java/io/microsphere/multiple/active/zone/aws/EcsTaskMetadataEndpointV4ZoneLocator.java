package io.microsphere.multiple.active.zone.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.microsphere.multiple.active.zone.AbstractZoneLocator;
import io.microsphere.multiple.active.zone.ZoneLocator;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * The {@link ZoneLocator} based on <a href=
 * "https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-metadata-endpoint-v4.html">Task
 * metadata endpoint version 4</a>.
 * <p>
 * The environment variable is injected by default into the containers of Amazon ECS tasks launched
 * on Amazon EC2 Linux instances that are running at least version 1.39.0 of the Amazon ECS
 * container agent. For Amazon EC2 Windows instances that use awsvpc network mode, the Amazon ECS
 * container agent must be at least version 1.54.0
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see ZoneLocator
 * @since 1.0.0
 */
public class EcsTaskMetadataEndpointV4ZoneLocator extends AbstractZoneLocator {

    public static final String METADATA_URI_V4_ENV_NAME = "ECS_CONTAINER_METADATA_URI_V4";

    public static final int DEFAULT_ORDER = 10;

    private static final String ZONE_FIELD_NAME = "AvailabilityZone";

    public EcsTaskMetadataEndpointV4ZoneLocator() {
        super(DEFAULT_ORDER);
    }

    @Override
    public boolean supports(Environment environment) {
        return environment.containsProperty(METADATA_URI_V4_ENV_NAME);
    }

    @Override
    public String locate(Environment environment) {
        String uri = environment.getProperty(METADATA_URI_V4_ENV_NAME);
        String zone = null;
        if (StringUtils.hasText(uri)) {
            String taskURL = uri + "/task";
            try {
                String json = doGet(taskURL);
                ObjectMapper objectMapper = new ObjectMapper();
                Map metadata = objectMapper.readValue(json, Map.class);
                Object zoneValue = metadata.get(ZONE_FIELD_NAME);
                zone = zoneValue == null ? null : String.valueOf(zoneValue);
                logger.info("The zone ['{}'] was located from the Amazon ECS Task metadata endpoint version 4 [URL : '{}' , property name: '{}']",
                        zone, taskURL, METADATA_URI_V4_ENV_NAME);
            } catch (Throwable e) {
                logger.error("Request Amazon ECS Task metadata endpoint version 4 [URL : '{}'] failed", taskURL, e);
            }
        } else {
            logger.debug("The environment variable [name : '{}' ] of Amazon ECS Task metadata endpoint version 4 can't be found",
                    METADATA_URI_V4_ENV_NAME);
        }
        return zone;
    }
}
