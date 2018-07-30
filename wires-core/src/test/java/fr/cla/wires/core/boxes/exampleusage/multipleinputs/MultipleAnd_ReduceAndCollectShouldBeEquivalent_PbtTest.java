package fr.cla.wires.core.boxes.exampleusage.multipleinputs;


import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Signal;
import fr.cla.wires.core.Wire;
import fr.cla.wires.core.test.random.boolsignals.ListOfSignalsOfBoolean;
import fr.cla.wires.core.test.random.boolsignals.RandomBooleanSignals;
import fr.cla.wires.support.test.random.bools.RandomBooleans;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static fr.cla.wires.core.test.random.boolsignals.BooleanSignalsGenerator.MULTIPLICITY;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class MultipleAnd_ReduceAndCollectShouldBeEquivalent_PbtTest {

//    private static final int TRIALS = 100_000; //Checked: passes
    private static final int TRIALS = 1_000;

    private List<Wire<Boolean>> ins;
    private Wire<Boolean> collectOut, reduceOut;
    private Clock clock;

    @Before public void setup() {
        setup(MULTIPLICITY);
    }

    private void setup(long multiplicity) {
        if(multiplicity > MULTIPLICITY) throw new IllegalArgumentException(format(
            "multiplicity must be <= %d, was %d",
            MULTIPLICITY, multiplicity
        ));
        ins = Stream.generate(() -> Wire.<Boolean>make()).limit(multiplicity).collect(toList());
        collectOut = Wire.make();
        reduceOut = Wire.make();
        clock = Clock.createTime();
        ReduceMultipleAnd.ins(ins).out(reduceOut).time(clock);
        CollectMultipleAnd.ins(ins).out(collectOut).time(clock);
    }

    @Property(trials = TRIALS)
    public void should_give_same_result_when_inputs_are_all_set(
        @RandomBooleans List<Boolean> signals
    ) {
        given: {
            Iterator<Boolean> _signals = signals.iterator();
            ins.forEach(w -> w.setSignal(Signal.of(_signals.next())));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(collectOut.getSignal()).isEqualTo(reduceOut.getSignal());
        }
    }

    @Property(trials = TRIALS)
    public void should_give_same_result_even_when_inputs_are_not_all_set(
        @RandomBooleanSignals ListOfSignalsOfBoolean signals
    ) {
        given: {
            Iterator<Signal<Boolean>> _signals = signals.get().iterator();
            ins.forEach(w -> w.setSignal(_signals.next()));
        }
        when: {
            clock.tick();
        }
        then: {
            assertThat(collectOut.getSignal()).isEqualTo(reduceOut.getSignal());
        }
    }

    @Property(trials = TRIALS)
    public void should_sometimes_give_false(
        @RandomBooleans List<Boolean> signals
    ) {
        should_sometimes_give(signals, false);
    }

    @Property(trials = TRIALS)
    public void should_sometimes_give_true(
        @RandomBooleans List<Boolean> signals
    ) {
        //Otherwise we would have 1/2^MULTIPLICITY == 1/2^100 of the test passing..
        long probabilityCutoff = 4;
        setup(probabilityCutoff);

        should_sometimes_give(signals, true);
    }

    private void should_sometimes_give(
        List<Boolean> signals,
        boolean assumedResult
    ) {
        should_sometimes_give(signals, Signal.of(assumedResult));
    }

    private void should_sometimes_give(
        List<Boolean> signals,
        Signal<Boolean> assumedResult
    ) {
        should_sometimes_give0(
            signals.stream().map(Signal::of).collect(toList()),
            assumedResult
        );
    }

    @Property(trials = TRIALS)
    public void should_sometimes_give_not_set(
        @RandomBooleanSignals ListOfSignalsOfBoolean signals
    ) {
        should_sometimes_give0(signals.get(), Signal.none());
    }

    private void should_sometimes_give0(
        List<Signal<Boolean>> signals,
        Signal<Boolean> assumedResult
    ) {
        given: {
            Iterator<Signal<Boolean>> _signals = signals.iterator();
            ins.forEach(w -> w.setSignal(_signals.next()));
        }
        when: {
            clock.tick();
        }
        then: {
            assumeThat(collectOut.getSignal(), is(assumedResult));
        }
    }

}
//@formatter:on
