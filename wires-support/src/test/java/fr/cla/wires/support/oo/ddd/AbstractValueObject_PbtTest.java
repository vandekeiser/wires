package fr.cla.wires.support.oo.ddd;


import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.support.oo.ddd.support.pbt.RandomVo;
import fr.cla.wires.support.oo.ddd.support.pbt.VoSingleton;
import org.junit.runner.RunWith;

//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class AbstractValueObject_PbtTest {

    private static final int TRIALS = 100_000;

    @Property(trials = TRIALS)
    public void equals_should_be_reflexive(
        @RandomVo VoSingleton s
    ) {
    }

}
//@formatter:on
