package fr.cla.wires;

import java.util.HashMap;
import java.util.Map;

public class Agenda {

    private Tick now = Tick.ZERO;
    private final Map<Tick, TickQueue> agenda = new HashMap<>();

    <V> Action<V> afterDelay(Delay delay, Action<V> action) {
        return v -> waitFor(delay).thenCall(action, v);
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
