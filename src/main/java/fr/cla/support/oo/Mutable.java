package fr.cla.support.oo;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * @param <T> The type of value held
 */
public class Mutable<T> {

    private T current;

    /**
     * @param initial Only the initial value is nullable 
     */
    Mutable(T initial) {
        this.current = initial;
    }

    public final boolean isPresent() {
        return current != null;
    }

    public final T get() {
        return current;
    }

    public final void set(T t) {
        this.current = requireNonNull(t);
    }

}
//@formatter:on
