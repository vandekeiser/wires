package fr.cla.wires.support.oo;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off
/**
 * An Accumulable has an initial value and knows how to take in new partial values ({@code accumulator}),
 * and how to combine itself with another Accumulable. This way it can easily be used by a Collector.
 * @param <I> Type of inputs.
 *           Their "weights" are determined by {@code weight}
 *           then accumulated into the current value by {@code accumulator}
 * @param <A> Type of accumulated value
 */
public class Accumulable<I, A> extends MutableValue<A> {

    private final A EMPTY = null;
    private final Function<I, A> weight;
    private final BinaryOperator<A> accumulator;

    protected Accumulable(
        A initialValue,
        boolean acceptNull, 
        Function<I, A> weight,
        BinaryOperator<A> accumulator
    ) {
        super(initialValue, acceptNull);
        this.weight = requireNonNull(weight);
        this.accumulator = requireNonNull(accumulator);
    }

    public static <I, A> Accumulable<I, A> initiallyEmpty(
        Function<I, A> weight,
        BinaryOperator<A> accumulator
    ) {
        return new Accumulable<>(null, true, weight, accumulator);
    }

    public static <I, A> Accumulable<I, A> initially(
        A initialValue,
        Function<I, A> weight,
        BinaryOperator<A> accumulator
    ) {
        return new Accumulable<>(initialValue, false, weight, accumulator);
    }

    public final void accumulate(I elt) {
        mutableEquivalentToInitially(weight(elt));
    }

    public final Accumulable<I, A> combine(Accumulable<I, A> that) {
        mutableEquivalentToInitially(combineValues(that));
        return this;
    }

    private A weight(I elt) {
        A eltWeight = this.weight.apply(elt);
        if(this.isPresent() ) {
            return accumulator.apply(this.get(), eltWeight);
        } else {
            return eltWeight;
        }
    }

    private A combineValues(Accumulable<I, A> that) {
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

    /**
     * TODO: Must make it public for now because of https://issues.apache.org/jira/browse/MCOMPILER-354
     * which prevents me from putting Accumulable_PbtTest in fr.cla.wires.support.oo
     *
     * Package private, to allow only whitebox tests to suppress warnings.
     * This method must not be called from anything other than a test.
     * (then if something throws ClassCastException later, it will be detectable before pushing)
     * It could even take Object, but the tests only to pass an AbstractValueObject<?>.
     * If newValue is not an instance of AbstractValueObject<A>, the result and later behaviour is undefined.
     * @param newValue Must be an AbstractValueObject<A>
     * @throws NullPointerException if newValue is null
     */
    public void mutableEquivalentToInitially(AbstractValueObject<?> newValue) {
        //void mutableEquivalentToInitially(AbstractValueObject<?> newValue) {
        @SuppressWarnings("unchecked") //See javadoc
        A unsafeNewValue = (A) newValue;
        mutableEquivalentToInitially(unsafeNewValue);
    }

    public static <O, T> java.util.stream.Collector<O, ?, T> collector(
        Function<O, T> weight,
        BinaryOperator<T> accumulator,
        UnaryOperator<T> finisher
    ) {
        return new Collector<>(weight, accumulator, finisher);
    }




    public static class Collector<O, T>
    implements java.util.stream.Collector<O, Accumulable<O, T>, T> {
        private final Function<Accumulable<O, T>, T> getAccumulated;
        private final Function<O, T> weight;
        private final BinaryOperator<T> accumulator;
        private final UnaryOperator<T> finisher;

        private Collector(
            Function<O, T> weight,
            BinaryOperator<T> accumulator,
            UnaryOperator<T> finisher
        )  {
            this.getAccumulated = MutableValue::get;
            this.weight = requireNonNull(weight);
            this.accumulator = requireNonNull(accumulator);
            this.finisher = requireNonNull(finisher);
        }

        @Override public Supplier<Accumulable<O, T>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                weight, accumulator
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
