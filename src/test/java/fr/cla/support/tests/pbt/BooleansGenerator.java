package fr.cla.support.tests.pbt;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

//@formatter:off
public class BooleansGenerator extends Generator<List<Boolean>> {

    public static final int MULTIPLICITY = 100;

    public BooleansGenerator() {
        super(listOfBooleans());
    }

    @Override
    public List<Boolean> generate(SourceOfRandomness rand, GenerationStatus status) {
        return Stream.generate(rand::nextBoolean).limit(MULTIPLICITY).collect(toList());
    }

    private static Class<List<Boolean>> listOfBooleans() {
        Class<?> unbounded = List.class;

        //We promise to only ever produce List<Boolean>
        @SuppressWarnings("unchecked")
        Class<List<Boolean>> listOfBooleans = (Class<List<Boolean>>) unbounded;

        return listOfBooleans;
    }

}
//@formatter:on