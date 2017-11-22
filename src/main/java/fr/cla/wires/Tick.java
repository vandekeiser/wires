package fr.cla.wires;

import fr.cla.support.oo.ddd.AbstractValueObject;

import java.util.ArrayDeque;
import java.util.List;

import static java.lang.String.*;
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
        if(tick < 0) throw new AssertionError("tick must be >= 0, was: " + tick);
        this.tick = tick;
    }

    @Override
    protected List<Object> equalityCriteria() {
        return singletonList(tick);
    }

    /**
     * @throws IllegalArgumentException if the addition overflows long //TODO test
     */
    public Tick plus(Delay delay) {
        //Don't need to do any checks other than overflow here,
        // since Delay::duration guarantees duration is >0 and Delay is final

        long newTick, currentTick = this.tick;
        try {
            newTick = Math.addExact(tick, delay.duration()); //throws ArithmeticException if overflows long
        } catch(ArithmeticException overflow) {
            throw new IllegalArgumentException(
                format(
                    "Tick overflow! currentTick: %d, delay: %s",
                    currentTick, delay
                ),
                overflow //Do propagate as "Caused by: " in the stacktrace!
            );
        }
        return new Tick(newTick);
    }

    public static Tick number(long number) {
        if(number < 0) throw new IllegalArgumentException("Tick number must be >= 0, was: " + number);
        return new Tick(number);
    }




    /**
     * Remembers the callbacks to call at a given Tick.
     */
    static final class Queue {
        private final Tick tick;
        //Run callbacks in FIFO order
        private final java.util.Queue<Runnable> todos = new ArrayDeque<>();

        Queue(Tick tick) {
            this.tick = requireNonNull(tick);
        }

        <V> void thenCall(OnSignalChanged<V> callback, Signal<V> signal) {
            OnSignalChanged<V> _callback = requireNonNull(callback);
            Signal<V> _signal = requireNonNull(signal);

            todos.add(() -> _callback.accept(_signal));
        }

        void runAll() {
            todos.forEach(Runnable::run);
        }

        @Override public String toString() {
            return String.format(
                "{tick: %s, todos:%s}",
                tick, todos
            );
        }
    }

}
//@formatter:on