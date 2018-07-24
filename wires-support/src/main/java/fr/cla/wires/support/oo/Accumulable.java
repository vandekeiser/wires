package fr.cla.wires.support.oo;

import fr.cla.wires.support.functional.Indexed;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.Characteristics.UNORDERED;

//@formatter:off
public class Accumulable<I, A> extends Mutable<A> {

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
        if (this.isPresent() && that.isPresent()) return initially(
            accumulator.apply(this.get(), that.get()),
            accumulationValue, accumulator
        );
        else if (this.isPresent()) return initially(
            this.get(),
            accumulationValue, accumulator
        );
        else if (that.isPresent()) return initially(
            that.get(),
            accumulationValue, accumulator
        );
        else return initiallyEmpty(
            accumulationValue, accumulator
        );
    }

    public static <O, T> java.util.stream.Collector<O, ?, T> collector(
        Function<O, T> accumulationValue,
        BinaryOperator<T> accumulator,
        UnaryOperator<T> finisher
    ) {
        return new Collector<>(accumulationValue, accumulator, finisher);
    }

    public static <O, T> java.util.stream.Collector<Indexed<O>, ?, T> indexedCollector(
        Function<Indexed<O>, T> accumulationValue,
        BinaryOperator<T> accumulator,
        UnaryOperator<T> finisher
    ) {
        return new IndexedCollector<>(accumulationValue, accumulator, finisher);
    }




    public static class Collector<O, T>
    implements java.util.stream.Collector<O, Accumulable<O, T>, T> {
        private final Function<Accumulable<O, T>, T> getAccumulated = Mutable::get;
        private final Function<O, T> accumulationValue;
        private final BinaryOperator<T> accumulator;
        private final UnaryOperator<T> finisher;

        public Collector(
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




    public static class IndexedCollector<O, T>
    implements java.util.stream.Collector<Indexed<O>, Accumulable<Indexed<O>, T>, T> {
        private final Function<Accumulable<Indexed<O>, T>, T> getAccumulated = Mutable::get;
        private final Function<Indexed<O>, T> accumulationValue;
        private final BinaryOperator<T> accumulator;
        private final UnaryOperator<T> finisher;

        private IndexedCollector(
            Function<Indexed<O>, T> accumulationValue,
            BinaryOperator<T> accumulator,
            UnaryOperator<T> finisher
        )  {
            this.accumulationValue = requireNonNull(accumulationValue);
            this.accumulator = requireNonNull(accumulator);
            this.finisher = requireNonNull(finisher);
        }

        @Override public Supplier<Accumulable<Indexed<O>, T>> supplier() {
            return () -> Accumulable.initiallyEmpty(
                accumulationValue, accumulator
            );
        }

        @Override public BiConsumer<Accumulable<Indexed<O>, T>, Indexed<O>> accumulator() {
            return Accumulable::accumulate;
        }

        @Override public BinaryOperator<Accumulable<Indexed<O>, T>> combiner() {
            return Accumulable::combine;
        }

        @Override public Function<Accumulable<Indexed<O>, T>, T> finisher() {
            return getAccumulated.andThen(finisher);
        }

        @Override public Set<Characteristics> characteristics() {
            //TODO some collectors might not be UNORDERED
            return EnumSet.of(UNORDERED);
        }
    }

}
//@formatter:on
