package fr.cla.support.oo;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * @param <T> The type of value held
 */
public class Mutable<T> {

    private T current;

    /**
     * @param initial The initial value is nullable 
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

    //----------Equality based solely on get()----------VVVVVVVVVVVVVVV
    @Override public final boolean equals(Object obj) {
        //An optimization, but also avoids StackOverflows on cyclic object graphs.
        if(obj == this) return true;

        if(! (obj instanceof Mutable)) return false;
        Mutable<?> that = (Mutable<?>) obj;

        return Objects.equals(
            this.get(),
            that.get()
        );
    }

    @Override public final int hashCode() {
        return Objects.hash(get());
    }

    @Override public String toString() {
        return String.format(
            "%s@%s%s",
            getClass().getSimpleName(),
            Integer.toHexString(System.identityHashCode(this)),
            get()
        );
    }
    //----------Equality based solely on get()----------^^^^^^^^^^^^^^^

}
//@formatter:on
