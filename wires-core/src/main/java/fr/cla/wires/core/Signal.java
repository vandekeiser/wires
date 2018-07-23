package fr.cla.wires.core;

import fr.cla.wires.support.functional.Indexed;
import fr.cla.wires.support.functional.Streams;
import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A signal each transiting on a Wire.
 */
public final class Signal<V> extends AbstractValueObject<Signal<V>> {
    private final V value;

    private Signal(V v, boolean acceptNull) {
        super(signalOfV());
        this.value = acceptNull ? v : requireNonNull(v);
    }

    public static <V> Signal<V> of(V v) {
        return new Signal<>(v, false);
    }

    public static <V> Signal<V> none() {
        return new Signal<>(null, true);
    }

    @Override
    protected List<Object> equalityCriteria() {
        //Bad idea to use Java9 List.of here since in an equals method field could potentially be legally null.
        //For Signal it is effectively the case that this.value can be null.
        return singletonList(value);
    }

    public Optional<V> value() {
        return Optional.ofNullable(value);
    }

    private static <V> Class<Signal<V>> signalOfV() {
        Class<?> unbounded = Signal.class;

        //Doesn't matter, as this is only used in AbstractValueObject::equals, for the isInstance check.
        //This unchecked cast means that Signals of all types are compared together without ClassCastException,
        // but this doesn't matter because Signals with equal values should be equal.
        //This is proved by SignalTest::should_not_get_classcast_when_calling_equals_on_signals_of_different_types
        // and SignalTest::equals_should_be_true_for_signals_of_different_types_but_same_value
        @SuppressWarnings("unchecked")
        Class<Signal<V>> signalOfV = (Class<Signal<V>>) unbounded;

        return signalOfV;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    //----------Functional methods to transform and/or aggregate Signals//----------VVVVVVVVVV
    <W> Signal<W> map(Function<V, W> mapper) {
        return value().map(mapper).map(Signal::of).orElse(Signal.none());
    }

    static <V1, V2, W> Signal<W> map(Signal<V1> s1, Signal<V2> s2, BiFunction<V1, V2, W> mapper) {
        return map(s1.value(), s2.value(), mapper);
    }
    private static <V1, V2, W> Signal<W> map(Optional<V1> v1, Optional<V2> v2, BiFunction<V1, V2, W> mapper) {
        if(!v1.isPresent() ||!v2.isPresent()) return Signal.none();
        return Signal.of(mapper.apply(v1.get(), v2.get()));
    }

    /**
     * Collect an aggregate result from the inputs of N Wires, using Stream::reduce.
     * Can do less than this::collect but less complex.
     * @param inputs The in Signals. No Signal is allowed to be Signal.none(), since that was already check by Wire::mapAndReduce.
     * @param accumulationValue Maps in signals to values which are then accumulated during the reduction.
     * @param accumulator This accumulation function (technically a java.util.function.BinaryOperator) must be associative, per Stream::reduce.
     * @param identity This must be the neutral element of the group associated with the reducer (ex: 0 for +, 1 for *).
     * @param <T> The type of Signal that transits on the target Wire
     * @param <O> The type of Signal that transits on the observed Wire
     * @return If if any input is none then Signal.none(), else the result of applying the reducer to the "accumulation value" of all inputs.
     */
    static <O, T> Signal<T> mapAndReduce(
        Stream<Signal<O>> inputs,
        Function<O, T> accumulationValue,
        BinaryOperator<T> accumulator,
        T identity
    ) {
        return Signal.of(inputs
            .map(Signal::value)
            .map(Optional::get)
            .map(accumulationValue)
            .reduce(identity, accumulator)
        );
    }

    static <O, T> Signal<T> mapAndReduceIndexed(
        Stream<Signal<O>> inputs,
        Function<Indexed<O>, T> accumulationValue,
        BinaryOperator<T> accumulator,
        T identity
    ) {
        Stream<O> values = inputs.map(Signal::value).map(Optional::get);
        Stream<Indexed<O>> indexedValues = Streams.index(values);

        return Signal.of(
            indexedValues.map(accumulationValue).reduce(identity, accumulator)
        );
    }

    /**
     * Collect an aggregate result from the inputs of N Wires, using a java.util.stream.Collector.
     * Can do more than this::mapAndReduce but more complex.
     * @param inputs The in Signals. No Signal is allowed to be Signal.none(), since that was already check by Wire::collect.
     * @param collector This accumulator is more general (but complex) than mapAndReduce()'s one, since:
     *  -The value to accumulate doesn't have to be of the same type as the input Signal.
     *  -The accumulation doesn't have to use a BinaryOperator (it is implemented by the Collector itself).
     * On the other hand, the same precondition are demanded from this parameter as in mapAndReduce():
     *  -The collector::accumulator and collector::combiner implementations must be associative, per Stream::collect.
     * @param <T> The type of Signal that transits on the target Wire
     * @param <O> The type of Signal that transits on the observed Wire
     * @return If if any input is none then Signal.none(), else the result of applying the collector to all inputs.
     */
    static <O, T> Signal<T> collect(
        Stream<Signal<O>> inputs,
        Collector<O, ?, T> collector
    ) {
        return Signal.of(inputs
            .map(Signal::value)
            .map(Optional::get)
            .collect(collector)
        );
    }

    static <O, T> Signal<T> collectIndexed(
        Stream<Signal<O>> inputs,
        Collector<Indexed<O>, ?, T> collector
    ) {
        Stream<O> values = inputs.map(Signal::value).map(Optional::get);
        Stream<Indexed<O>> indexedValues = Streams.index(values);

        return Signal.of(indexedValues
            .collect(collector)
        );
    }

    //----------Functional methods to transform and/or aggregate Signals//----------^^^^^^^^^^

}
//@formatter:on
