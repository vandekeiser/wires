package fr.cla.wires.neuron;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

//@formatter:off
public class NeuronApp {

    public static void main(String[] args) {
        int MULTIPLICITY = 100;
        double threshold = 50.0;

        List<Wire<Double>> ins = Stream.generate(() -> Wire.<Double>make()).limit(MULTIPLICITY).collect(toList());
        Wire<Double> out = Wire.make();
        Clock clock = Clock.createTime();

        List<Double> weigths = Stream.generate(() -> Synapse.DEFAULT_WEIGTH)
            .limit(MULTIPLICITY)
            .collect(toList())
        ;

        Neuron.ins(ins).out(out).threshold(threshold).weigths(weigths).time(clock);

        while(true) {
            ins.forEach(in -> in.setSignal(
                Signal.of(ThreadLocalRandom.current().nextDouble())
            ));
            clock.tick();
            System.out.printf(
                "now: %s, out: %s%n",
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