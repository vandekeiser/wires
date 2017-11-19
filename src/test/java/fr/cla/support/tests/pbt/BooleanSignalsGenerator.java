package fr.cla.support.tests.pbt;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import fr.cla.wires.Signal;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

//@formatter:off
public class BooleanSignalsGenerator extends Generator<List<Signal<Boolean>>> {

    public static final int MULTIPLICITY = 100;

    public BooleanSignalsGenerator() {
        super(listOfBooleanSignals());
    }

    @Override
    public List<Signal<Boolean>> generate(SourceOfRandomness rand, GenerationStatus status) {
        return Stream
            .generate(() -> nextTrilean(rand))
            .map(Trilean::toBooleanSignal)
            .limit(MULTIPLICITY)
            .collect(toList())
        ;
    }

    private Trilean nextTrilean(SourceOfRandomness rand) {
        switch (rand.nextInt(3)) {
            case 0: return Trilean.TRUE;
            case 1: return Trilean.FALSE;
            case 2: return Trilean.NOT_SET;
            default: throw new AssertionError();
        }
    }

    private static Class<List<Signal<Boolean>>> listOfBooleanSignals() {
        Class<?> unbounded = List.class;

        //We promise to only ever produce List<Signal<Boolean>>
        //...
        //...
        //java.lang.IllegalArgumentException:
        // Cannot find generator for
        //  java.util.List<fr.cla.wires.Signal<java.lang.Boolean>>:
        //  fr.cla.wires.Signal<java.lang.Boolean>
        //  of type fr.cla.wires.Signal<java.lang.Boolean>
        @SuppressWarnings("unchecked")
        Class<List<Signal<Boolean>>> listOfBooleans = (Class<List<Signal<Boolean>>>) unbounded;

        return listOfBooleans;
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