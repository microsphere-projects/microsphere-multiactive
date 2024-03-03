package io.microsphere.multiple.active.zone.spring.boot.autoconfigure;

import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.multiple.active.zone.spring.CompositeZoneLocator;
import io.microsphere.multiple.active.zone.spring.ZoneLocator;
import io.microsphere.multiple.active.zone.spring.boot.condition.ConditionalOnEnabledZone;
import io.microsphere.multiple.active.zone.spring.event.ZoneContextChangedListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.microsphere.multiple.active.zone.spring.ZoneUtils.ZONE_CONTEXT_BEAN_NAME;
import static io.microsphere.multiple.active.zone.spring.ZoneUtils.ZONE_LOCATOR_BEAN_NAME;
import static io.microsphere.spring.util.SpringFactoriesLoaderUtils.loadFactories;
import static org.springframework.core.annotation.AnnotationAwareOrderComparator.sort;

/**
 * Zone Auto-Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnEnabledZone
@Import(value = {ZoneContextChangedListener.class})
public class ZoneAutoConfiguration {

    @Bean(name = ZONE_CONTEXT_BEAN_NAME)
    public ZoneContext zoneContext() {
        return ZoneContext.get();
    }

    @Bean(name = ZONE_LOCATOR_BEAN_NAME)
    @Primary
    public CompositeZoneLocator zoneLocator(Collection<ZoneLocator> zoneLocatorBeans, ConfigurableApplicationContext context) {
        // Load from Spring Factories
        List<ZoneLocator> zoneLocators = loadFactories(context, ZoneLocator.class);
        List<ZoneLocator> allZoneLocators = new ArrayList<>(zoneLocatorBeans.size() + zoneLocators.size());
        // Add ZoneLocator Spring Beans
        allZoneLocators.addAll(zoneLocatorBeans);
        // Add ZoneLocator Spring Factories
        allZoneLocators.addAll(zoneLocators);
        // Sort by the order
        sort(allZoneLocators);
        return new CompositeZoneLocator(allZoneLocators);
    }
}
