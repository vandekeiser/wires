package fr.cla;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

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

    public Optional<T> current() {
        if(maybe == null) throw new AssertionError();
        return maybe;
    }

    protected final boolean isPresent() {
        return maybe.isPresent();
    }

    /**
     * @throws java.util.NoSuchElementException if !isPresent()
     */
    protected final T get() {
        return maybe.get();
    }

    protected final void set(T t) {
        maybe = Optional.of(t);
    }

    protected final void set(Optional<T> o) {
        maybe = requireNonNull(o);
    }

}
//@formatter:on
