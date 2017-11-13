package fr.cla.wires;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class Agenda {

    private Tick now = Tick.ZERO;
    private final Map<Tick, TickQueue> agenda = new HashMap<>();

    <V> Action<V> afterDelay(Delay delay, Action<V> action) {
        Action<V> _action = requireNonNull(action);
        Delay _delay = requireNonNull(delay);
        return v -> waitFor(_delay).thenCall(_action, v);
    }

    private TickQueue waitFor(Delay delay) {
        return agenda.computeIfAbsent(
            now.plus(delay),
            k -> new TickQueue()
        );
    }

    public void tick() {
        now = now.plus(Delay.of(1));

        TickQueue currentTasks = agenda.get(now);
        if(currentTasks == null) return;

        currentTasks.runAll();
        agenda.remove(now);
    }

    public Tick now() {
        return now;
    }

}
