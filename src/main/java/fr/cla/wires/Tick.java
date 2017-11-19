package fr.cla.wires;

import fr.cla.support.oo.ddd.AbstractValueObject;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A discretized time unit.
 */
public final class Tick extends AbstractValueObject<Tick> {
    public static final Tick ZERO = new Tick(0L);

    private final long tick;

    private Tick(long tick) {
        super(Tick.class);
        if(tick < 0) throw new IllegalArgumentException("tick must be >= 0, was: " + tick);
        this.tick = tick;
    }

    @Override
    protected List<Object> equalityCriteria() {
        return singletonList(tick);
    }

    public Tick plus(Delay delay) {
        return new Tick(tick + delay.duration());
    }

    public static Tick number(long number) {
        return new Tick(number);
    }




    /**
     * Remembers the callbacks to call at a given Tick.
     */
    static final class Queue {

        //Run callbacks in FIFO order
        private final java.util.Queue<Runnable> todos = new ArrayDeque<>();

        <V> void thenCall(OnSignalChanged<V> callback, Signal<V> signal) {
            OnSignalChanged<V> _callback = requireNonNull(callback);
            Signal<V> _signal = requireNonNull(signal);

            todos.add(() -> _callback.accept(_signal));
        }

        void runAll() {
            todos.forEach(Runnable::run);
        }
    }

}
//@formatter:on