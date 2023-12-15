package io.microsphere.multiple.active.zone;

import io.github.microsphere.spring.cloud.context.OnceMainApplicationPreparedEventListener;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_CONTEXT_BEAN_NAME;
import static io.microsphere.multiple.active.zone.ZoneConstants.ZONE_LOCATOR_BEAN_NAME;
import static io.microsphere.spring.util.BeanFactoryUtils.asBeanDefinitionRegistry;
import static io.microsphere.spring.util.SpringFactoriesLoaderUtils.loadFactories;


/**
 * Zone Initilaized Listener
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class ZoneInitializedListener extends OnceMainApplicationPreparedEventListener {

    public static final int DEFAULT_ORDER = 50;

    public ZoneInitializedListener() {
        super();
        super.setOrder(DEFAULT_ORDER);
    }

    @Override
    protected void onApplicationEvent(SpringApplication springApplication, String[] args, ConfigurableApplicationContext context) {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        BeanDefinitionRegistry registry = asBeanDefinitionRegistry(beanFactory);
        // Register ZoneContext as Spring Bean
        registerZoneContext(registry);
        // Register ZoneLocator as Spring Bean
        registerZoneLocator(registry, context);
    }

    private void registerZoneContext(BeanDefinitionRegistry registry) {
        ZoneContext zoneContext = ZoneContext.get();
        registerBeanInstance(ZONE_CONTEXT_BEAN_NAME, zoneContext, registry);
    }

    private void registerZoneLocator(BeanDefinitionRegistry registry, ConfigurableApplicationContext context) {
        List<ZoneLocator> zoneLocators = loadFactories(ZoneLocator.class, context);
        CompositeZoneLocator compositeZoneLocator = new CompositeZoneLocator(zoneLocators);
        registerBeanInstance(ZONE_LOCATOR_BEAN_NAME, compositeZoneLocator, registry);
    }

    private void registerBeanInstance(String beanName, Object bean, BeanDefinitionRegistry registry) {
        SingletonBeanRegistry singletonBeanRegistry = (SingletonBeanRegistry) registry;
        singletonBeanRegistry.registerSingleton(beanName, bean);
    }
}
