package fr.cla.wires.exampleusage.pbt;


import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.support.tests.pbt.RandomBooleanSignals;
import fr.cla.support.tests.pbt.RandomBooleans;
import fr.cla.wires.Signal;
import fr.cla.wires.Time;
import fr.cla.wires.Wire;
import fr.cla.wires.exampleusage.CollectMultipleAnd;
import fr.cla.wires.exampleusage.ReduceMultipleAnd;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static fr.cla.support.tests.pbt.BooleansGenerator.MULTIPLICITY;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class MultipleAnd_ReduceAndCollectShouldBeEquivalent_PbtTest {

    private static final int TRIALS = 1000;

    private Set<Wire<Boolean>> ins;
    private Wire<Boolean> collectOut, reduceOut;
    private Time time;

    @Before
    public void setup() {
        setup(MULTIPLICITY);
    }

    private void setup(long multiplicity) {
        if(multiplicity > MULTIPLICITY) throw new IllegalArgumentException(format(
            "multiplicity must be <= %d, was %d"
        ));
        ins = Stream.generate(() -> Wire.<Boolean>make()).limit(multiplicity).collect(toSet());
        collectOut = Wire.make();
        reduceOut = Wire.make();
        time = Time.create();
        ReduceMultipleAnd.ins(ins).out(reduceOut).time(time);
        CollectMultipleAnd.ins(ins).out(collectOut).time(time);
    }

    @Property(trials = TRIALS)
    public void should_give_same_result_when_inputs_are_all_set(
        @RandomBooleans List<Boolean> signals
    ) {
        Iterator<Boolean> _signals = signals.iterator();
        ins.forEach(w -> w.setSignal(Signal.of(_signals.next())));

        //When
        time.tick();

        //Then
        assertThat(collectOut.getSignal()).isEqualTo(reduceOut.getSignal());
    }

    @Ignore //TODO see BooleanSignalsGenerator::listOfBooleanSignals?
    @Property(trials = TRIALS)
    public void should_give_same_result_even_when_inputs_are_not_all_set(
        @RandomBooleanSignals List<Signal<Boolean>> signals
    ) {
        Iterator<Signal<Boolean>> _signals = signals.iterator();
        ins.forEach(w -> w.setSignal(_signals.next()));

        //When
        time.tick();

        //Then
        assertThat(collectOut.getSignal()).isEqualTo(reduceOut.getSignal());
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

    @Ignore //TODO see BooleanSignalsGenerator::listOfBooleanSignals?
    @Property(trials = TRIALS)
    public void should_sometimes_give_not_set(
        @RandomBooleanSignals List<Signal<Boolean>> signals
    ) {
        should_sometimes_give0(signals, Signal.none());
    }

    private void should_sometimes_give0(
        List<Signal<Boolean>> signals,
        Signal<Boolean> assumedResult
    ) {
        Iterator<Signal<Boolean>> _signals = signals.iterator();
        ins.forEach(w -> w.setSignal(_signals.next()));

        //When
        time.tick();

        //Then
        assumeThat(collectOut.getSignal(), is(assumedResult));
    }

}
//@formatter:on
