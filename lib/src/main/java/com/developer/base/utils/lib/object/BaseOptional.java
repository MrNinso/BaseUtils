package com.developer.base.utils.lib.object;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class BaseOptional<T> {
    private static final BaseOptional<?> EMPTY = new BaseOptional<>();

    private final T value;

    private BaseOptional() {
        this.value = null;
    }

    private BaseOptional(@NonNull T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BaseOptional)) {
            return false;
        }

        BaseOptional<?> other = (BaseOptional<?>) obj;
        if (other.get() == null) {
            return this.get() == null;
        } else {
            return other.get().equals(this.get());
        }
    }

    @Override
    public int hashCode() {
        if (this.value != null) {
            return this.value.hashCode();
        } else {
            throw new NullPointerException("Value is Null");
        }

    }

    @NonNull
    @Override
    public String toString() {
        return value != null
                ? String.format("BaseOptional[%s]", value)
                : "BaseOptional.empty";
    }

    public T get() {
        return this.value;
    }

    public boolean ifPresent(IfPresent<T> p) {
        if (this.value != null) {
            p.present(this.value);
            return true;
        } else {
            return false;
        }
    }

    public static  <T> BaseOptional<T> empty() {
        return (BaseOptional<T>) EMPTY;
    }

    public static <T> BaseOptional<T> of(T value) {
        if (value == null)
            return empty();
        else if (value instanceof BaseOptional<?>)
            return (BaseOptional<T>) value;
        else
            return new BaseOptional<>(value);
    }

    public static <T> T from(BaseOptional<T> optional) {
        return optional != null ? optional.get() : null;
    }

    public static boolean isEmpty(BaseOptional<?> optional) {
        if (optional == null)
            return true;

        return optional.get() == null;
    }

    public interface IfPresent<T> {
        void present(T value);
    }
}
