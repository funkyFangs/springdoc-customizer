package io.funky.fangs.springdoc.customizer.utility;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utilities related to reflective operations.
 *
 * @author Harper Price
 * @since 2.1.0
 */
@UtilityClass
public class ReflectionUtilities {
    /**
     * Attempts to get the value of a static {@link Field}.
     *
     * @param field the {@link Field} to get the value of
     * @return the value of a {@link Field} if possible or else null
     * @param <T> the type of the field to get
     */
    @Nullable
    public <T> T getFieldValue(Field field) {
        return Modifier.isStatic(field.getModifiers())
                ? getFieldValue(field, null)
                : null;
    }

    /**
     * Attempts to get the value of a {@link Field}.
     *
     * @param field the {@link Field} to get the value of
     * @return the value of a {@link Field} if possible or else null
     * @param <T> the type of the field to get
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getFieldValue(Field field, Object instance) {
        try {
            var canAccess = field.canAccess(instance);

            if (!canAccess) {
                field.trySetAccessible();
            }

            var value = (T) field.get(instance);

            if (!canAccess) {
                field.setAccessible(false);
            }

            return value;
        }
        catch (IllegalAccessException | ClassCastException exception) {
            return null;
        }
    }

    public Class<?> getClassSafely(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ignored) {
            return null;
        }
    }
}