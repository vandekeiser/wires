package fr.cla;

import java.util.Optional;

public class MutableOptional<T> {

    private Optional<T> optional = Optional.empty();

    private Optional<T> toOptional() {
        if(optional == null) throw new AssertionError();
        return optional;
    }

    public static <T> MutableOptional<T> create() {
        return new MutableOptional<>();
    }

    public boolean isPresent() {
        return optional.isPresent();
    }

    public T get() {
        return optional.get();
    }
}
