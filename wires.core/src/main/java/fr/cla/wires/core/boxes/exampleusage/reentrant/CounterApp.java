package fr.cla.wires.core.boxes.exampleusage.reentrant;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Wire;

//@formatter:off
public class CounterApp {

    public static void main(String[] args) {
        Wire<Long> out = Wire.make();
        Clock clock = Clock.createTime();
        Counter.out(out).time(clock);

        while(true) {
            clock.tick();
            System.out.printf(
                "now: %s, signal: %s%n",
                clock.now(), out.getSignal()
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }
        }

    }

}
//@formatter:on