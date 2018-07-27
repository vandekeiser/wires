package fr.cla.wires.support.oo;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off
public class Accumulable<I, A> extends MutableValue<A> {

    private final A EMPTY = null;
    private final Function<I, A> accumulationValue;
    private final BinaryOperator<A> accumulator;

    protected Accumulable(
        A initialValue,
        boolean acceptNull, 
        Function<I, A> accumulationValue,
        BinaryOperator<A> accumulator
    ) {
        super(initialValue, acceptNull);
        this.accumulationValue = requireNonNull(accumulationValue);
        this.accumulator = requireNonNull(accumulator);
    }

    public static <I, A> Accumulable<I, A> initiallyEmpty(
        Function<I, A> accumulationValue,
        BinaryOperator<A> accumulator
    ) {
        return new Accumulable<>(null, true, accumulationValue, accumulator);
    }

    public static <I, A> Accumulable<I, A> initially(
        A initialValue,
        Function<I, A> accumulationValue,
        BinaryOperator<A> accumulator
    ) {
        return new Accumulable<>(initialValue, false, accumulationValue, accumulator);
    }

    public final void accumulate(I elt) {
        if(this.isPresent() ) {
            set(accumulator.apply(this.get(), accumulationValue.apply(elt)));
        } else {
            set(accumulationValue.apply(elt));
        }
    }

    public final Accumulable<I, A> combine(Accumulable<I, A> that) {
        mutableEquivalentToInitially(newValueConsidering(that));
        return this;
    }

    private A newValueConsidering(Accumulable<I, A> that) {
        if (this.isPresent() && that.isPresent()) {
            return accumulator.apply(this.get(), that.get());
        } else if (this.isPresent()) {
            return this.get();
        } else if (that.isPresent()) {
            return that.get();
        } else {
            return EMPTY;
        }
    }

    /**
     * Modifies this to put it into the same state (as defined by equals)
     * as calling initially (static method which creates a new Accumulable instance) would.
     *
     * -If newValue is null, throws NullPointerException.
     * -Otherwise after calling this method, this.equals(initially(newValue)) is guaranteed to be true.
     * @param newValue If I was calling initially I would pass this single param to it.
     */
    private void mutableEquivalentToInitially(A newValue) {
        set(newValue);
    }

    public static <O, T> java.util.stream.Collector<O, ?, T> collector(
        Function<O, T> accumulationValue,
        BinaryOperator<T> accumulator,
        UnaryOperator<T> finisher
    ) {
        return new Collector<>(accumulationValue, accumulator, finisher);
    }

    /**
     * Package private, to allow only whitebox tests to suppress warnings.
     * This method must not be called from anything other than a test.
     * (then if something throws ClassCastException later, it will be detectable before pushing)
     * It could even take Object, but the tests only to pass an AbstractValueObject<?>.
     * If newValue is not an instance of AbstractValueObject<A>, the result and later behaviour is undefined.
     * @param newValue Must be an AbstractValueObject<A>
     * @throws NullPointerException if newValue is null
     */
    //void mutableEquivalentToInitially(AbstractValueObject<?> newValue) {
    public void mutableEquivalentToInitially(AbstractValueObject<?> newValue) {
        @SuppressWarnings("unchecked") //See javadoc
        A unsafeNewValue = (A) newValue;
        mutableEquivalentToInitially(unsafeNewValue);
    }


    public static class Collector<O, T>
    implements java.util.stream.Collector<O, Accumulable<O, T>, T> {
        private final Function<Accumulable<O, T>, T> getAccumulated = MutableValue::get;
        private final Function<O, T> accumulationValue;
        private final BinaryOperator<T> accumulator;
        private final UnaryOperator<T> finisher;

        private Collector(
            Function<O, T> accumulationValue,
            BinaryOperator<T> accumulator,
            UnaryOperator<T> finisher
        )  {
            this.accumulationValue = requireNonNull(accumulationValue);
            this.accumulator = requireNonNull(accumulator);
            this.finisher = requireNonNull(finisher);
        }

        @Override public Supplier<Accumulable<O, T>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                accumulationValue, accumulator
            );
        }

        @Override public BiConsumer<Accumulable<O, T>, O> accumulator() {
            return Accumulable::accumulate;
        }

        @Override public BinaryOperator<Accumulable<O, T>> combiner() {
            return Accumulable::combine;
        }

        @Override public Function<Accumulable<O, T>, T> finisher() {
            return getAccumulated.andThen(finisher);
        }

        @Override public Set<Characteristics> characteristics() {
            //TODO some collectors might not be UNORDERED
            return EnumSet.of(UNORDERED);
        }
    }

}
//@formatter:on
