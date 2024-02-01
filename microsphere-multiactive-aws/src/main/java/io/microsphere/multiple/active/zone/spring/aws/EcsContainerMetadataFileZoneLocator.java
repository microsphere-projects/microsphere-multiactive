package io.microsphere.multiple.active.zone.spring.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.microsphere.multiple.active.zone.spring.AbstractZoneLocator;
import io.microsphere.multiple.active.zone.spring.ZoneLocator;
import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 *
 * The {@link ZoneLocator} based on <a href=
 * "https://docs.aws.amazon.com/AmazonECS/latest/developerguide/container-metadata.html">Amazon ECS
 * container metadata file</a>.
 * 
 * Beginning with version 1.15.0 of the Amazon ECS container agent, various container metadata is
 * available within your containers or the host container instance.
 *
 * @see ZoneLocator
 * @see EcsTaskMetadataEndpointV4ZoneLocator
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class EcsContainerMetadataFileZoneLocator extends AbstractZoneLocator {

    public static final String METADATA_FILE_ENV_NAME = "ECS_CONTAINER_METADATA_FILE";

    public static final int DEFAULT_ORDER = 5;

    private static final String ZONE_FIELD_NAME = "AvailabilityZone";

    public EcsContainerMetadataFileZoneLocator() {
        super(DEFAULT_ORDER);
    }

    @Override
    public boolean supports(Environment environment) {
        return environment.containsProperty(METADATA_FILE_ENV_NAME);
    }

    @Override
    public String locate(Environment environment) {
        String metadataFilePath = environment.getProperty(METADATA_FILE_ENV_NAME);
        String zone = null;
        if (StringUtils.hasText(metadataFilePath)) {
            File metadataFile = new File(metadataFilePath);
            if (metadataFile.canRead()) {
                try {
                    String json = FileUtils.readFileToString(metadataFile, StandardCharsets.UTF_8);
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map metadata = objectMapper.readValue(json, Map.class);
                    Object zoneValue = metadata.get(ZONE_FIELD_NAME);
                    zone = zoneValue == null ? null : String.valueOf(zoneValue);
                    logger.info("The zone ['{}'] was located from the Amazon ECS Container metadata file [path: : '{}' , property name: '{}']", zone,
                            metadataFile.getAbsolutePath(), METADATA_FILE_ENV_NAME);
                } catch (Throwable e) {
                    logger.error("Read Amazon ECS container metadata file [path : '{}'] failed", metadataFile.getAbsolutePath(), e);
                }
            } else {
                logger.warn("The Amazon ECS Container metadata file [path : '{}' ] can't be read", metadataFile.getAbsolutePath());
            }
        } else {
            logger.debug("The environment variable [name : '{}' ] of Amazon ECS container metadata file can't be found", METADATA_FILE_ENV_NAME);
        }
        return zone;
    }

}
