package fr.cla.wires.support.pbt;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

//@formatter:off
public class VoPairGenerator extends Generator<VoPair> {

    public VoPairGenerator() {
        super(VoPair.class);
    }

    @Override
    public VoPair generate(SourceOfRandomness rand, GenerationStatus status) {
        return new VoPair(
            VoGenerator.generate(rand),
            VoGenerator.generate(rand)
        );
    }

}
//@formatter:on