package fr.cla.wires;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class Signal<V> extends AbstractValueObject<Signal<V>> {
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
    protected Collection<Object> attributesToIncludeInEqualityCheck() {
        return Arrays.asList(value);
    }

    public Optional<V> getValue() {
        return Optional.ofNullable(value);
    }

    public <W> Signal<W> map(Function<V, W> gate) {
        return getValue().map(gate).map(Signal::of).orElse(Signal.none());
    }

    public static <V1, V2, W> Signal<W> map(Signal<V1> s1, Signal<V2> s2, BiFunction<V1, V2, W> mapper) {
        if(!s1.getValue().isPresent()) return Signal.none();
        if(!s2.getValue().isPresent()) return Signal.none();
        return Signal.of(mapper.apply(s1.getValue().get(), s2.getValue().get()));
    }

    private static <V> Class<Signal<V>> signalOfV() {
        Class<?> unbounded = Signal.class;

        //Doesn't matter, as this is only used in AbstractValueObject::equals,
        // for the isInstance check.
        //This unchecked cast means that Signals of all types are compared together,
        // but this doesn't matter because Signals with equal values should be equal.
        @SuppressWarnings("unchecked")
        Class<Signal<V>> signalOfV = (Class<Signal<V>>) unbounded;

        return signalOfV;
    }
}
