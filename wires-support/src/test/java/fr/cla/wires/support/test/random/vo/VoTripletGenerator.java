package fr.cla.wires.support.test.random.vo;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

//@formatter:off
public class VoTripletGenerator extends Generator<VoTriplet> {

    public VoTripletGenerator() {
        super(VoTriplet.class);
    }

    @Override
    public VoTriplet generate(SourceOfRandomness rand, GenerationStatus status) {
        return new VoTriplet(
            VoGenerator.generate(rand),
            VoGenerator.generate(rand),
            VoGenerator.generate(rand)
        );
    }

}
//@formatter:on