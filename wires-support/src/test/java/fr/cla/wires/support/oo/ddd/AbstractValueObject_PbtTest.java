package fr.cla.wires.support.oo.ddd;


import com.pholser.junit.quickcheck.Property;
import fr.cla.wires.support.oo.ddd.support.pbt.VoSingleton;

//@formatter:off
public class AbstractValueObject_PbtTest {

    private static final int TRIALS = 100_000;

    @Property(trials = TRIALS)
    public void equals_should_be_reflexive(
        VoSingleton s
    ) {
    }

}
//@formatter:on
