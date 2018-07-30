package fr.cla.wires.core.pbt;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import fr.cla.wires.core.Signal;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

//@formatter:off
public class BooleanSignalsGenerator extends Generator<ListOfSignalsOfBoolean> {

    public static final int MULTIPLICITY = 100;

    public BooleanSignalsGenerator() {
        super(ListOfSignalsOfBoolean.class);
    }

    @Override
    public ListOfSignalsOfBoolean generate(SourceOfRandomness rand, GenerationStatus status) {
        List<Signal<Boolean>> signals = Stream
            .generate(() -> nextTrilean(rand))
            .map(Trilean::toBooleanSignal)
            .limit(MULTIPLICITY)
            .collect(toList())
        ;
        return new ListOfSignalsOfBoolean(signals);
    }

    private Trilean nextTrilean(SourceOfRandomness rand) {
        switch (rand.nextInt(3)) {
            case 0: return Trilean.TRUE;
            case 1: return Trilean.FALSE;
            case 2: return Trilean.NOT_SET;
            default: throw new AssertionError();
        }
    }

    private enum Trilean {
        TRUE {
            @Override public Signal<Boolean> toBooleanSignal() {
                return Signal.of(true);
            }
        },
        FALSE {
            @Override public Signal<Boolean> toBooleanSignal() {
                return Signal.of(false);
            }
        },
        NOT_SET {
            @Override public Signal<Boolean> toBooleanSignal() {
                return Signal.none();
            }
        }
        ;

        public abstract Signal<Boolean> toBooleanSignal();
    }
}
//@formatter:on