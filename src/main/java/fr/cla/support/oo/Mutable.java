package fr.cla.support.oo;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class Mutable<T> {

    private Optional<T> maybe;

    protected Mutable(Optional<T> maybe) {
        this.maybe = requireNonNull(maybe);
    }

    public static <T> Mutable<T> of(Optional<T> initialVal) {
        return new Mutable<>(initialVal);
    }

    public final Optional<T> current() {
        if(maybe == null) throw new AssertionError();
        return maybe;
    }


    //TODO is this not wrong?? should be mutable..
    //----------Optional-like methods----------VVVVVVVVVVVVVVV
    public static <T> Mutable<T> empty() {
        return new Mutable<>(Optional.empty());
    }
    public static <T> Mutable<T> of(T initialVal) {
        return of(Optional.of(initialVal));
    }
    public static <T> Mutable<T> ofNullable(T initialVal) {
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
