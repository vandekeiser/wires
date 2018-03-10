package fr.cla.wires.core.boxes.exampleusage.reentrant;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Wire;

public class CounterApp {

    public static void main(String[] args) {
        Wire<Long> out = Wire.make();
        Clock clock = Clock.createTime();
        Counter.out(out).time(clock);

        while(true) {
            System.out.printf(
                "now: %s, signal: %s%n",
                clock.now(), out.getSignal()
            );
            clock.tick();
        }

    }

}
