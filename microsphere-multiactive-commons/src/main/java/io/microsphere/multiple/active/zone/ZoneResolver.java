package io.microsphere.multiple.active.zone;

import java.util.function.Function;

/**
 * The zone resolver from the specified entity.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public interface ZoneResolver<E> extends Function<E, String> {

    default String apply(E entity) {
        return resolve(entity);
    }

    /**
     * Resolve the zone
     *
     * @param entity the specified entity
     * @return null if can't be resolved
     */
    String resolve(E entity);
}
