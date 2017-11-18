package fr.cla.support.oo;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class Mutable<T> {

    private Optional<T> maybe;

    protected Mutable(Optional<T> maybe) {
        this.maybe = requireNonNull(maybe);
    }

    public static <T> Mutable<T> initiallyUnset() {
        return new Mutable<>(Optional.empty());
    }

    public static <T> Mutable<T> initially(T initialVal) {
        return new Mutable<>(Optional.of(initialVal));
    }

    public final Optional<T> current() {
        if(maybe == null) throw new AssertionError();
        return maybe;
    }

    public final boolean isPresent() {
        return maybe.isPresent();
    }

    /**
     * @throws java.util.NoSuchElementException if !isPresent
     */
    public final T get() {
        return maybe.get();
    }

    public final void set(T t) {
        maybe = Optional.of(t);
    }

    public final void set(Optional<T> o) {
        maybe = requireNonNull(o);
    }

}
//@formatter:on
