package fr.cla.wires;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

//@formatter:off
public class Agenda implements Clock {

    private Tick now = Tick.ZERO;
    private final Map<Tick, TickQueue> appointments = new HashMap<>();

    @Override public void tick() {
        now = now.plus(Delay.of(1));

        TickQueue todo = appointments.get(now);
        if(todo == null) return;

        todo.runAll();
        appointments.remove(now);
    }

    @Override public Tick now() {
        return now;
    }

    <V> OnSignaledChanged<V> afterDelay(Delay delay, OnSignaledChanged<V> callback) {
        OnSignaledChanged<V> _callback = requireNonNull(callback);
        Delay _delay = requireNonNull(delay);

        return v -> waitFor(_delay).thenCall(_callback, v);
    }

    private TickQueue waitFor(Delay delay) {
        return appointments.computeIfAbsent(
            now.plus(delay),
            k -> new TickQueue()
        );
    }

}
//@formatter:on
