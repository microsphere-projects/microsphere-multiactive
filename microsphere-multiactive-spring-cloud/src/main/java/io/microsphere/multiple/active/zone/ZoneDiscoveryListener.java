package io.microsphere.multiple.active.zone;

import io.github.microsphere.spring.cloud.context.OnceMainApplicationPreparedEventListener;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static io.microsphere.multiple.active.zone.ZoneUtils.getZoneContext;
import static io.microsphere.multiple.active.zone.ZoneUtils.getZoneLocator;

/**
 * A listener for the {@link ApplicationPreparedEvent Spring application prepared event} to
 * {@link #discoverZone discover zone}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ZoneDiscoveryListener extends OnceMainApplicationPreparedEventListener {

    /**
     * Default Order
     */
    public static final int DEFAULT_ORDER = 100;

    public ZoneDiscoveryListener() {
        super();
        setOrder(DEFAULT_ORDER);
    }

    @Override
    protected void onApplicationEvent(SpringApplication springApplication, String[] args, ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        ZoneLocator zoneLocator = getZoneLocator(beanFactory);
        if (zoneLocator.supports(environment)) {
            discoverZone(context, beanFactory, environment, zoneLocator);
        } else {
            logger.warn("The zone can't be discovered by {}", zoneLocator);
        }
    }

    private void discoverZone(ConfigurableApplicationContext context, ConfigurableListableBeanFactory beanFactory,
                              ConfigurableEnvironment environment, ZoneLocator zoneLocator) {
        String zone = zoneLocator.locate(environment);
        ZoneContext zoneContext = getZoneContext(beanFactory);
        zoneContext.setZone(zone);
    }
}
