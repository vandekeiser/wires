package fr.cla.wires;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

//@formatter:off
/**
 * A discretization of time:
 * exposes {@code void tick()} and {@code Tick now()}.
 * The same Clock instance is to be shared between all Boxes of a circuit.
 */
public final class Clock {

    private final Agenda agenda = new Agenda();

    private Clock() {}

    /**
     * @return the non-null Agenda
     */
    Agenda agenda() {
        if(agenda == null) throw new AssertionError();
        return agenda;
    }

    public static Clock createTime() {
        return new Clock();
    }

    public void tick() {
        agenda().tick();
    }

    /**
     * @return the current non-null Tick
     */
    public Tick now() {
        Tick now = agenda().now();
        if(now == null) throw new AssertionError();
        return now;
    }




    /**
     * Remembers which tasks (Tick.Queue) to execute at each Tick.
     */
    static final class Agenda {
        private Tick now = Tick.ZERO;
        private final Map<Tick, Tick.Queue> appointments = new HashMap<>();

        void tick() {
            now = now.plus(Delay.of(1));

            Tick.Queue todo = appointments.get(now);
            if(todo == null) return;

            try {
                todo.runAll();
            } finally {
                appointments.remove(now);
            }
        }

        Tick now() {
            if(now == null) throw new AssertionError();
            return now;
        }

        <V> OnSignalChanged<V> afterDelay(Delay delay, OnSignalChanged<V> callback) {
            OnSignalChanged<V> _callback = requireNonNull(callback);
            Delay _delay = requireNonNull(delay);

            return v -> waitFor(_delay).thenCall(_callback, v);
        }

        private Tick.Queue waitFor(Delay delay) {
            return appointments.computeIfAbsent(
                now.plus(delay),
                Tick.Queue::new
            );
        }
    }

}
//@formatter:on
