package fr.cla.support.oo;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * This doesn't follow the generally heard advice to not use Optional as field or parameter.
 * But that advice sounds (from what Brian Goetz says in his "the good, the bad, and the ugly" talk)
 *  like a limitation of how Optional is not Serializable because that's too much JDK maintenance work,
 *  and thus not a prime Entity property, not as an absolute prohibition.
 * -->As long as that Optional is not used as an "Entity property" (persistence/serialization/...)
 *  it should be OK.
 *  Maybe I should rename this to "TransientMutable"?
 *  That sounds good since instances of this class are intended to be used are intermediary accumulators.
 * @param <T> type of value held
 */
public class Mutable<T> {

    private T maybe;

    //TODO make private
    protected Mutable(T maybe) {
        this.maybe = maybe;
    }

    public static <T> Mutable<T> initially(T initialVal) {
        return new Mutable<>(initialVal);
    }

    public static <T> Mutable<T> initiallyEmpty() {
        return new Mutable<>(null);
    }

    public final T current() {
        if(maybe == null) throw new AssertionError();
        return maybe;
    }

    public final boolean isPresent() {
        return maybe != null;
    }

    /**
     * @throws java.util.NoSuchElementException if !isPresent
     */
    public final T get() {
        return maybe;
    }

    public final void set(T t) {
        maybe = t;
    }

    //----------Equality based solely on current()----------VVVVVVVVVVVVVVV
    @Override public final boolean equals(Object obj) {
        //An optimization, but also avoids StackOverflows on cyclic object graphs.
        if(obj == this) return true;

        if(! (obj instanceof Mutable)) return false;
        Mutable<?> that = (Mutable<?>) obj;

        return Objects.equals(
            this.current(),
            that.current()
        );
    }

    @Override public final int hashCode() {
        return Objects.hash(current());
    }

    @Override public String toString() {
        return String.format(
            "%s@%s%s",
            getClass().getSimpleName(),
            Integer.toHexString(System.identityHashCode(this)),
            current()
        );
    }
    //----------Equality based solely on current()----------^^^^^^^^^^^^^^^

}
//@formatter:on
