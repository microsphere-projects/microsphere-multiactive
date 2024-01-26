package io.microsphere.multiple.active.zone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static io.microsphere.multiple.active.zone.ZoneConstants.DEFAULT_ZONE;
import static io.microsphere.multiple.active.zone.ZoneConstants.PREFERENCE_ENABLED_PROPERTY_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_ENABLED_PROPERTY_NAME;
import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;


/**
 * Zone Preference Filter
 *
 * @param <E> the type of entity to filter
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 */
public class ZonePreferenceFilter<E> {

    private final static Logger logger = LoggerFactory.getLogger(ZonePreferenceFilter.class);

    private final ZoneContext zoneContext;

    private final ZoneResolver<E> zoneResolver;

    public ZonePreferenceFilter(ZoneContext zoneContext, ZoneResolver<E> zoneResolver) {
        this.zoneContext = zoneContext;
        this.zoneResolver = zoneResolver;
    }

    public List<E> filter(final List<E> entities) {

        int totalSize = entities == null ? 0 : entities.size();

        // Empty case
        if (totalSize <= 1) {
            return entities;
        }

        // Disabled case
        if (!zoneContext.isEnabled()) {
            logger.debug("Zone Context feature is disabled! It could be enabled if the Spring property '{}' to be set 'true'",
                    ZONE_ENABLED_PROPERTY_NAME);
            return entities;
        }

        if (!zoneContext.isPreferenceEnabled()) {
            logger.debug("Zone Preference feature is disabled as default! It could be enabled if the Spring property '{}' to be set 'true'",
                    PREFERENCE_ENABLED_PROPERTY_NAME);
            return entities;
        }

        // Ignored case
        final String zone = zoneContext.getZone();
        if (isIgnored(zone)) {
            logger.debug("Zone Preference feature will be ignored, caused by zone : '{}'", zone);
            return entities;
        }

        List<E> targetEntities = entities;

        // Disable Zone Case
        String disabledZone = zoneContext.getPreferenceUpstreamDisabledZone();

        if (disabledZone != null) {
            targetEntities = filterDisabledZone(entities, disabledZone, totalSize);
            int currentSize = targetEntities.size();

            if (currentSize <= 1) { // Not enough entity available
                logger.debug("Not enough entity available after disabled zone['{}'] filter, " + "the entities' total size : {} -> actual size : {}",
                        disabledZone, totalSize, currentSize);
                return entities;
            }
            // reassign total size
            totalSize = currentSize;
        }

        // Enabled case
        List<E> sameZoneEntities = new LinkedList<>();

        int zoneCount = 0;

        for (int i = 0; i < totalSize; i++) {
            E entity = targetEntities.get(i);

            String resolvedZone = resolveZone(entity);

            if (resolvedZone != null) {
                zoneCount++;
                if (matches(zone, resolvedZone)) {
                    sameZoneEntities.add(entity);
                }
            }
        }

        // Upstream entities ready case
        int upstreamReadyPercentage = zoneContext.getPreferenceUpstreamZoneReadyPercentage();
        if (isUpstreamZoneNotReady(zoneCount, totalSize, upstreamReadyPercentage)) {
            logger.debug("The ready percentage of entities with zone is under the threshold [{}%], total entities size : {} , "
                    + "ready entities size : {}", upstreamReadyPercentage, totalSize, zoneCount);
            return targetEntities;
        }

        // Zone preference matched case
        int sameZoneEntitiesSize = sameZoneEntities.size();
        if (sameZoneEntitiesSize > 0) {
            // The min available in the same zone threshold case
            int sameZoneMinAvailable = zoneContext.getPreferenceUpstreamSameZoneMinAvailable();
            if (isUnderSameZoneMinAvailableThreshold(sameZoneEntitiesSize, sameZoneMinAvailable)) {
                logger.debug("The size of same zone ['{}'] entities is under the threshold : {}, actual size : {}", zone, sameZoneMinAvailable,
                        sameZoneEntitiesSize);
                return targetEntities;
            }

            logger.debug("The same zone ['{}'] entities[size : {} , total : {}] are found!", zone, sameZoneEntitiesSize, totalSize);
            return sameZoneEntities;
        }

        // No matched
        logger.debug("No same zone ['{}'] entity was found, total entities size : {} , zone count : {}", zone, totalSize, zoneCount);
        return targetEntities;
    }

    public int getOrder() {
        return zoneContext.getPreferenceFilterOrder();
    }

    private List<E> filterDisabledZone(List<E> entities, String disabledZone, int totalSize) {
        List<E> targetEntities = new LinkedList<>();

        String[] disabledZones = commaDelimitedListToStringArray(disabledZone);

        for (int i = 0; i < totalSize; i++) {
            E entity = entities.get(i);

            String resolvedZone = resolveZone(entity);

            if (!isDisabledZone(resolvedZone, disabledZones)) {
                targetEntities.add(entity);
            }
        }

        logger.debug("After filtering the disabled zone['{}'] entities[size : {} -> {}] : {} -> {}", disabledZone, totalSize, targetEntities.size(),
                entities, targetEntities);
        return targetEntities;
    }

    private boolean isDisabledZone(String zone, String[] disabledZones) {
        boolean disabled = false;
        for (String disabledZone : disabledZones) {
            if (Objects.equals(zone, disabledZone)) {
                disabled = true;
                break;
            }
        }
        return disabled;
    }

    private boolean isUpstreamZoneNotReady(int zoneCount, int entitiesSize, int zonePercentThreshold) {
        int percent = (zoneCount * 100 / entitiesSize);
        return percent < zonePercentThreshold;
    }

    private boolean isUnderSameZoneMinAvailableThreshold(int sameZoneEntitiesSize, int zonePreferenceThreshold) {
        return sameZoneEntitiesSize < zonePreferenceThreshold;
    }

    private String resolveZone(E entity) {
        String zone = (entity == null ? null : zoneResolver.resolve(entity));
        return zone;
    }

    private boolean matches(String zone, String resolvedZone) {
        return Objects.equals(zone, resolvedZone);
    }

    protected boolean isIgnored(String zone) {
        return !StringUtils.hasText(zone) || DEFAULT_ZONE.equalsIgnoreCase(zone);
    }
}
