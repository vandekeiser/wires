package fr.cla.wires.neuron;


import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

//@formatter:off
/**
 * Start trying to implement a neural network on top of Boxes and Wires.
 * (move to a separate Maven module once it reaches a sufficient size)
 */
public class NeuronTest {

    private static final int MULTIPLICITY = 100;

    private List<Wire<Double>> ins;
    private Wire<Double> out;
    private Clock clock;

    //Neuronal parameters
    private double threshold;
    private int multiplicity;
    private List<Double> weigths;

    @Before
    public void setup() {
        setup(MULTIPLICITY);
    }

    private void setup(long multiplicity) {
        ins = Stream.generate(() -> Wire.<Double>make()).limit(multiplicity).collect(toList());
        out = Wire.make();
        clock = Clock.createTime();

        //In this basic test, we don't care about big multiplicities.
        weigths = Stream.generate(() -> Synapse.DEFAULT_WEIGTH)
            .limit(multiplicity)
            .collect(toList())
        ;

        Neuron.ins(ins).out(out).threshold(threshold).weigths(weigths).time(clock);
    }

    @Test
    public void out_should_initially_be_no_signal() {
        given: {
            //Nothing
        }
        when: {
            //No clock.tick()
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.none());
        }
    }

    @Test
    public void Given_threshold_is_not_reached_Then_output_should_be_0() {
        given: {
            threshold_is_not_reached();
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(0.0));
        }
    }

    @Test
    public void Given_threshold_is_reached_Then_output_should_be_1() {
        given: {
            threshold_is_reached();
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(out.getSignal()).isEqualTo(Signal.of(1.0));
        }
    }

    private void threshold_is_not_reached() {
        //Given threshold is 1.0
        threshold = 1.0;

        //Given there are 2 inputs
        multiplicity = 2;
        setup(multiplicity);

        //Given inputs are 0.5 and 0.4
        ins.get(0).setSignal(Signal.of(0.5));
        ins.get(1).setSignal(Signal.of(0.4));

        //Given both weigths are 1.0
        weigths.set(0, 1.0);
        weigths.set(1, 1.0);
    }

    private void threshold_is_reached() {
        //Given threshold is 1.0
        threshold = 1.0;

        //Given there are 2 inputs
        multiplicity = 2;
        setup(multiplicity);

        //Given inputs are 0.5 and 0.5
        ins.get(0).setSignal(Signal.of(0.5));
        ins.get(1).setSignal(Signal.of(0.500000000000001));

        //Given both weigths are 1.0
        weigths.set(0, 1.0);
        weigths.set(1, 1.0);
    }

//    @Test
//    public void Given_threshold_is_0_and_inputs_are_non_negative_and_weigths_are_non_negative_When_click_Then_output_should_be_() {
//        given: {
//            //Nothing
//        }
//        when: {
//            //No clock.tick()
//        }
//        then: {
//            assertThat(out.getSignal()).isEqualTo(Signal.none());
//        }
//    }

}
//@formatter:on
