package fr.cla.wires;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.Objects.requireNonNull;

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
    //      -become a Mutable (requires refactoring Mutable)
    //      -become a DDD Entity with a specific ID
    private final Set<OnSignalChanged<T>> listeners = new HashSet<>();

    /**
     * @return the current non-null Signal
     */
    public Signal<T> getSignal() {
        if(signal == null) throw new AssertionError();
        return signal;
    }

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
     * @param accumulationValue Maps in signals to values which are then accumulated during the reduction.
     * @param accumulator This accumulation function must be associative, per Stream::reduce.
     * @param identity This must be the neutral element of the group associated with the reducer (ex: 0 for +, 1 for *).
     * @param <T> The type of Signal that transits on the target Wire
     * @param <O> The type of Signal that transits on the observed Wire
     * @return If if any input is none then Signal.none(), else the result of applying the reducer to the "accumulation value" of all inputs.
     */
    static <O, T> Signal<T> mapAndReduce(
        Collection<Wire<O>> inputs,
        Function<O, T> accumulationValue,
        BinaryOperator<T> accumulator,
        T identity
    ) {
        if(anyWireIsFloating(inputs)) return Signal.none();

        return Signal.mapAndReduce(
            inputs.stream().map(Wire::getSignal),
            accumulationValue, accumulator, identity
        );
    }

    /**
     * Collect an aggregate result from the inputs of N Wires, using a java.util.stream.Collector.
     * Can do more than this::mapAndReduce but more complex.
     * @param inputs The in Wires.
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
        Collection<Wire<O>> inputs,
        Collector<O, ?, T> collector
    ) {
        if(anyWireIsFloating(inputs)) return Signal.none();

        return Signal.collect(
            inputs.stream().map(Wire::getSignal),
            collector
        );
    }

    private static <O> boolean anyWireIsFloating(Collection<Wire<O>> inputs) {
        return inputs.stream().map(Wire::getSignal).anyMatch(Signal.none()::equals);
    }

}
//@formatter:on
