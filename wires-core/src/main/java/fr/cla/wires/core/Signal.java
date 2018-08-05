package fr.cla.wires.core;

import fr.cla.wires.support.functional.Indexed;
import fr.cla.wires.support.functional.Streams;
import fr.cla.wires.support.oo.AbstractValueObject;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

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
        return valueOf(value);
    }

    //----------Functional methods to transform and/or aggregate Signals//----------VVVVVVVVVV
    <W> Signal<W> map(Function<V, W> mapper) {
        return value().map(mapper).map(Signal::of).orElse(Signal.none());
    }

    static <V> Signal<V> combine(
        Signal<V> s1,
        Signal<V> s2,
        BinaryOperator<V> combiner,
        Signal.WhenCombining combiningPolicy
    ) {
        if (combiningPolicy.returnNoneIfAnySignalIsFloating(s1, s2)) return Signal.none();
        return combiningPolicy.combine(s1, s2, combiner);
    }


    /**
     * Collect an aggregate result from the inputs of N Wires, using Stream::reduce.
     * Can do less than this::collect but less complex.
     * @param <T> The type of Signal that transits on the target Wire
     * @param <O> The type of Signal that transits on the observed Wire
     * @param inputs The in Signals. No Signal is allowed to be Signal.none(), since that was already check by Wire::mapAndReduce.
     * @param weight Maps in signals to values which are then accumulated during the reduction.
     * @param accumulator This accumulation function (technically a java.util.function.BinaryOperator) must be associative, per Stream::reduce.
     * @param combiningPolicy
     * @return If if any input is none then Signal.none(), else the result of applying the reducer to the "accumulation value" of all inputs.
     */
    static <O, T> Signal<T> mapAndReduce(
        Collection<Signal<O>> inputs,
        Function<O, T> weight,
        BinaryOperator<T> accumulator,
        Signal.WhenCombining combiningPolicy
    ) {
        if (combiningPolicy.returnNoneIfAnySignalIsFloating(inputs)) return Signal.none();

        return inputs.stream()
            .map(Signal::value)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(weight)
            .reduce(accumulator)
            .map(Signal::of)
            .orElseGet(Signal::none)
        ;
    }

    static <O, T> Signal<T> mapAndReduceIndexed(
        Collection<Signal<O>> inputs,
        Function<Indexed<O>, T> weight,
        BinaryOperator<T> accumulator,
        Signal.WhenCombining combiningPolicy
    ) {
        if (combiningPolicy.returnNoneIfAnySignalIsFloating(inputs)) return Signal.none();

        Stream<Optional<O>> values = inputs.stream().map(Signal::value);
        Stream<Indexed<Optional<O>>> indexedMaybes = Streams.index(values);

        Stream<Optional<Indexed<O>>> maybeIndices = indexedMaybes.map(
            indexed -> indexed.getValue().map(
                o -> Indexed.index(indexed.getIndex(), o)
            )
        );

        return maybeIndices
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(weight)
            .reduce(accumulator)
            .map(Signal::of)
            .orElseGet(Signal::none)
        ;

    }

    /**
     * Collect an aggregate result from the inputs of N Wires, using a java.util.stream.Collector.
     * Can do more than this::mapAndReduce but more complex.
     * @param <T> The type of Signal that transits on the target Wire
     * @param <O> The type of Signal that transits on the observed Wire
     * @param inputs The in Signals. No Signal is allowed to be Signal.none(), since that was already check by Wire::collect.
     * @param collector This collector is more general (but complex) than mapAndReduce()'s accumulator, since:
     *  -The value to accumulate doesn't have to be of the same type as the input Signal.
     *  -The accumulation doesn't have to use a BinaryOperator (it is implemented by the Collector itself).
     * On the other hand, the same precondition are demanded from this parameter as in mapAndReduce():
     *  -The collector::accumulator and collector::combiner implementations must be associative, per Stream::collect.
     * @param combiningPolicy
     * @return If if any input is none then Signal.none(), else the result of applying the collector to all inputs.
     */
    static <O, T> Signal<T> collect(
        Collection<Signal<O>> inputs,
        Collector<O, ?, T> collector,
        Signal.WhenCombining combiningPolicy
    ) {
        if (combiningPolicy.returnNoneIfAnySignalIsFloating(inputs)) return Signal.none();

        return Signal.of(inputs.stream()
            .map(Signal::value)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(collector)
        );
    }

    static <O, T> Signal<T> collectIndexed(
        Collection<Signal<O>> inputs,
        Collector<Indexed<O>, ?, T> collector,
        Signal.WhenCombining combiningPolicy
    ) {
        if (combiningPolicy.returnNoneIfAnySignalIsFloating(inputs)) return Signal.none();

        List<O> values = inputs.stream().map(Signal::value).map(Optional::get).collect(toList());
        Stream<Indexed<O>> indexedValues = Streams.index(values);

        return Signal.of(indexedValues.collect(collector));
    }

    private static <T> boolean anySignalIsFloating(Collection<Signal<T>> inputs) {
        return inputs.stream().anyMatch(Signal.none()::equals);
    }

    private static <V1, V2> boolean anySignalIsFloating(Signal<V1> v1, Signal<V2> v2) {
        return v1.equals(Signal.none()) || v2.equals(Signal.none());
    }

    //----------Functional methods to transform and/or aggregate Signals//----------^^^^^^^^^^




    public enum WhenCombining {
        /**
         * Forces the evaluation of combiner if one of the signal is missing.
         * Obviously this only works if combiner supports that, eg. AnswerFirst/Second
         */
        PRESENT_WINS {
            @Override
            public <V> boolean returnNoneIfAnySignalIsFloating(Signal<V> s1, Signal<V> s2) {
                return false;
            }

            @Override
            public <V> boolean returnNoneIfAnySignalIsFloating(Collection<Signal<V>> inputs) {
                return false;
            }

            @Override
            public <V> Signal<V> combine(Signal<V> s1, Signal<V> s2, BinaryOperator<V> combiner) {
                Optional<V> v1 = s1.value();
                Optional<V> v2 = s2.value();

                if (v1.isPresent() || v2.isPresent()) {
                    V v = combiner.apply(
                        v1.orElse(null), v2.orElse(null)
                    );
                    return v==null ? Signal.none() : Signal.of(v);
                } else {
                    return Signal.none();
                }
            }
        },
        /**
         * Output is none if any input is none
         */
        ABSENT_WINS {
            @Override
            public <V> boolean returnNoneIfAnySignalIsFloating(Signal<V> s1, Signal<V> s2) {
                return anySignalIsFloating(s1, s2);
            }

            @Override
            public <V> boolean returnNoneIfAnySignalIsFloating(Collection<Signal<V>> inputs) {
                return anySignalIsFloating(inputs);
            }

            @Override
            public <V> Signal<V> combine(Signal<V> s1, Signal<V> s2, BinaryOperator<V> combiner) {
                Optional<V> v1 = s1.value();
                Optional<V> v2 = s2.value();

                if (v1.isPresent() && v2.isPresent()) {
                    return Signal.of(combiner.apply(v1.get(), v2.get()));
                } else if (v1.isPresent()) {
                    return Signal.of(v1.get());
                } else if (v2.isPresent()) {
                    return Signal.of(v2.get());
                } else {
                    return Signal.none();
                }
            }
        },
        ;

        public abstract <V> boolean returnNoneIfAnySignalIsFloating(Signal<V> s1, Signal<V> s2);
        public abstract <V> boolean returnNoneIfAnySignalIsFloating(Collection<Signal<V>> inputs);
        public abstract <V> Signal<V> combine(Signal<V> s1, Signal<V> s2, BinaryOperator<V> combiner);
    }

}
//@formatter:on
