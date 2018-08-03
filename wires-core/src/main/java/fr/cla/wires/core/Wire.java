package fr.cla.wires.core;

import fr.cla.wires.support.functional.Indexed;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;

//@formatter:off
/**
 * A wire on which a Signal transits.
 * When the Signal changes, all listeners are notified.
 */
public final class Wire<T> {

    private Signal<T> signal = Signal.none();

    //In the publish-subscribe pattern, the order in which listeners are notified should not matter.
    //-->change from Set to something else _iff_ we should not exactly use the publish-subscribe pattern (maybe re-read SICP).
    //At least for now I don't see why listeners should be notified in the order they were registered or any other specific order.
    //But at least they should be uniquely registered, so in the order matters, the new Collection type used should preserve unicity.
    //
    //This also means that the unicity of listeners should be well-defined.
    // For now they are all instances of Wire, thus (since Wire is mutable) it must either:
    //      -continue to inherit equals/hashCode from Object,
    //      -become a MutableValue (requires refactoring MutableValue)
    //      -become a DDD Entity with a specific ID
    private final Set<OnSignalChanged<T>> listeners = new HashSet<>();

    /**
     * @return the current non-null Signal
     */
    public Signal<T> getSignal() {
        if(signal == null) throw new AssertionError();
        return signal;
    }

    /**
     * @param signal A non-null signal
     * @throws NullPointerException if signal is null
     */
    public void setSignal(Signal<T> signal) {
        if(signal.equals(this.signal)) return;

        this.signal = signal;
        listeners.forEach(callback -> callback.accept(signal));
    }

    void onSignalChanged(OnSignalChanged<T> callback) {
        listeners.add(requireNonNull(callback));
    }

    public static <T> Wire<T> make() {
        return new Wire<>();
    }

    private Wire() {}




    /**
     * Collect an aggregate result from the inputs of N Wires, using Stream::reduce.
     * Can do less than this::collect but less complex.
     * @param inputs The in Wires.
     * @param weight Maps in signals to values which are then accumulated during the reduction.
     * @param accumulator This accumulation function must be associative, per Stream::reduce.
     * @param <T> The type of Signal that transits on the target Wire
     * @param <O> The type of Signal that transits on the observed Wire
     * @return If if any input is none then Signal.none(), else the result of applying the reducer to the "accumulation value" of all inputs.
     */
    static <O, T> Signal<T> mapAndReduce(
        Collection<Wire<O>> inputs,
        Function<O, T> weight,
        BinaryOperator<T> accumulator
    ) {
        return Signal.mapAndReduce(
            inputs.stream().map(Wire::getSignal).collect(toList()),
            weight, accumulator
        );
    }

    public static <T, O> Signal<T> mapAndReduceIndexed(
        List<Wire<O>> inputs,
        Function<Indexed<O>, T> weight,
        BinaryOperator<T> accumulator
    ) {
        return Signal.mapAndReduceIndexed(
            inputs.stream().map(Wire::getSignal).collect(toList()),
            weight, accumulator
        );
    }

    /**
     * Collect an aggregate result from the inputs of N Wires, using a java.util.stream.Collector.
     * Can do more than this::mapAndReduce but more complex.
     * @param inputs The in Wires.
     * @param collector This collector is more general (but complex) than mapAndReduce()'s accumulator, since:
     *  -The value to accumulate doesn't have to be of the same type as the input Signal.
     *  -The accumulation doesn't have to use a BinaryOperator (it is implemented by the Collector itself).
     * On the other hand, the same precondition are demanded from this parameter as in mapAndReduce():
     *  -The collector::accumulator and collector::combiner implementations must be associative, per Stream::collect.
     * @param <T> The type of Signal that transits on the target Wire
     * @param <O> The type of Signal that transits on the observed Wire
     * @return If if any input is none then Signal.none(), else the result of applying the collector to all inputs.
     */
    static <O, T> Signal<T> collect(
        Collection<Wire<O>> inputs,
        Collector<O, ?, T> collector
    ) {
        return Signal.collect(
            inputs.stream().map(Wire::getSignal).collect(toList()),
            collector
        );
    }

    public static <T, O> Signal<T> collectIndexed(
        List<Wire<O>> inputs,
        Collector<Indexed<O>, ?, T> collector
    ) {
        return Signal.collectIndexed(
            inputs.stream().map(Wire::getSignal).collect(toList()),
            collector
        );
    }

}
//@formatter:on
