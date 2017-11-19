package fr.cla.wires;

import fr.cla.support.oo.ddd.AbstractValueObject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    //cf. CollectMultipleAndTest::out_should_be_none_when_any_input_is_none__even_if_all_others_are_true
    // Signal.none, confusion entre:
    // -pas de Wire
    // -pas de Signal sur un Wire (FLOATING/HIGH_IMPEDANCE?)
    //
    //-->interface Signal avec enum SpecialSignals.FLOATING/NO_SIGNAL?
    //
    //https://en.wikipedia.org/wiki/High_impedance
    //In digital circuits,
    //     a high impedance (also known as hi-Z, tri-stated, or floating) output
    //     is not being driven to any defined logic level by the output circuit.
    //The signal is neither driven to a logical high nor low level;
    //     this third condition leads to the description "tri-stated".
    //Such a signal can be seen as an open circuit (or "floating" wire)
    //     because connecting it to a low impedance circuit
    //          will not affect that circuit;
    //     it will instead
    //          itself be pulled to the same voltage as the actively driven output.
    //-->La logique de Wire::setSignal devra être conforme à cette métaphore.
    public static <V> Signal<V> none() {
        return new Signal<>(null, true);
    }

    @Override
    protected List<Object> equalityCriteria() {
        return singletonList(value);
    }

    public Optional<V> getValue() {
        return Optional.ofNullable(value);
    }

    public <W> Signal<W> map(Function<V, W> mapper) {
        return getValue().map(mapper).map(Signal::of).orElse(Signal.none());
    }

    public static <V1, V2, W> Signal<W> map(Signal<V1> s1, Signal<V2> s2, BiFunction<V1, V2, W> mapper) {
        if(!s1.getValue().isPresent()) return Signal.none();
        if(!s2.getValue().isPresent()) return Signal.none();
        return Signal.of(mapper.apply(s1.getValue().get(), s2.getValue().get()));
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

}
//@formatter:on
