package io.microsphere.multiple.active.zone.spring.boot.autoconfigure;

import io.microsphere.multiple.active.zone.ZoneContext;
import io.microsphere.multiple.active.zone.spring.CompositeZoneLocator;
import io.microsphere.multiple.active.zone.spring.event.ZoneContextChangedListener;
import io.microsphere.multiple.active.zone.spring.ZoneLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.microsphere.multiple.active.zone.ZoneConstants.ENABLED_PROPERTY_NAME_SUFFIX;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_PROPERTY_NAME;
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
@ConditionalOnClass(value = {
        ZoneContext.class, // from microsphere-multiactive-commons
        ZoneLocator.class, // from microsphere-multiactive-spring
})
@ConditionalOnProperty(prefix = ZONE_PROPERTY_NAME, name = ENABLED_PROPERTY_NAME_SUFFIX)
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
        List<ZoneLocator> zoneLocators = loadFactories(ZoneLocator.class, context);
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
