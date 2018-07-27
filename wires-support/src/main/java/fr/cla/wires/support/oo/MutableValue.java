package fr.cla.wires.support.oo;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A mutable value: equality is by value of {@code current}, not by reference nor by ID.
 *  (so in DDD terms it is neither an Entity nor a ValueObject).
 * Can optionally have a null initial value to represent emptyness,
 *  but can never be set to null again after it's been instantiated,
 *  because null should only ever represent the initial absent value.
 *  (use T=Optional<X> instead if you want your mutation to be able to "reset state to EMPTY").
 * @param <T> The type of value held.
 */
public class MutableValue<T> {

    private T current;

    MutableValue(T initial, boolean acceptNull) {
        this.current = acceptNull ? initial : requireNonNull(initial);
    }

    public final boolean isPresent() {
        return current != null;
    }

    public final T get() {
        return current;
    }

    /**
     * @throws NullPointerException iff t is null, because null should only ever represent the initial missing value.
     * Use a MutableValue<Optional<T>> instead if you want your mutation to be able to "reset state to EMPTY".
     */
    public final void set(T t) {
        this.current = requireNonNull(t);
    }

    @Override public final boolean equals(Object obj) {
        //An optimization, but also avoids StackOverflows on cyclic object graphs.
        if(obj == this) return true;

        if(! (obj instanceof MutableValue)) return false;
        MutableValue<?> that = (MutableValue<?>) obj;

        return Objects.equals(this.current,that.current);
    }

    @Override public final int hashCode() {
        return Objects.hash(current);
    }

    @Override
    public String toString() {
        return String.valueOf(current);
    }


}
//@formatter:on
