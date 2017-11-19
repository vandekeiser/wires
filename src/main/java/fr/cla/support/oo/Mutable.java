package fr.cla.support.oo;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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

    private Optional<T> maybe;

    protected Mutable(Optional<T> maybe) {
        this.maybe = requireNonNull(maybe);
    }

    public static final <T> Mutable<T> of(Optional<T> initialVal) {
        return new Mutable<>(initialVal);
    }

    public final Optional<T> current() {
        if(maybe == null) throw new AssertionError();
        return maybe;
    }


    //TODO is this not wrong?? should be mutable..
    //----------Optional-like methods----------VVVVVVVVVVVVVVV
    public static final <T> Mutable<T> empty() {
        return new Mutable<>(Optional.empty());
    }
    public static final <T> Mutable<T> of(T initialVal) {
        return of(Optional.of(initialVal));
    }
    public static final <T> Mutable<T> ofNullable(T initialVal) {
        return of(Optional.ofNullable(initialVal));
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
    public final <U> Mutable<U> map(Function<T, U> mapper) {
        return Mutable.of(current().map(mapper));
    }
    public final <U> Mutable<U> flatMapMutable(Function<T, Mutable<U>> mapper) {
        return current().map(mapper).orElseGet(Mutable::empty);
    }
    public final <U> Mutable<U> flatMapOptional(Function<T, Optional<U>> mapper) {
        return current().flatMap(mapper).map(Mutable::of).orElseGet(Mutable::empty);
    }
    public final Mutable<T> orElse(T replacement) {
        return Mutable.of(current().orElse(replacement));
    }
    public final Mutable<T> orElseGet(Supplier<? extends T> supplier) {
        return Mutable.of(current().orElseGet(supplier));
    }
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return current().orElseThrow(exceptionSupplier);
    }
    public final void ifPresent(Consumer<? super T> consumer) {
        current().ifPresent(consumer);
    }
    public final Mutable<T> filter(Predicate<? super T> predicate) {
        return Mutable.of(current().filter(predicate));
    }
    //----------Optional-like methods----------^^^^^^^^^^^^^^^


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
