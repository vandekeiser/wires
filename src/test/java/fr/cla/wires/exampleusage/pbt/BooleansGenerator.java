package fr.cla.wires.exampleusage.pbt;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class BooleansGenerator extends Generator<List<Boolean>> {
    static final int MULTIPLICITY = 100;

    public BooleansGenerator() {
        super(listOfBooleans());
    }

    @Override
    public List<Boolean> generate(SourceOfRandomness rand, GenerationStatus status) {
        return Stream.generate(rand::nextBoolean).limit(MULTIPLICITY).collect(toList());
    }

    private static Class<List<Boolean>> listOfBooleans() {
        Class<?> unbounded = List.class;

        //We promise to only ever produce Set<Boolean>
        @SuppressWarnings("unchecked")
        Class<List<Boolean>> signalOfV = (Class<List<Boolean>>) unbounded;

        return signalOfV;
    }

}
