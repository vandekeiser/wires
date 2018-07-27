package fr.cla.wires.support.oo;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.support.oo.ddd.support.pbt.RandomVo;
import fr.cla.wires.support.oo.ddd.support.pbt.VoSingleton;
import org.junit.runner.RunWith;


@RunWith(JUnitQuickcheck.class)
public class Accumulable_PbtTest {

    private static final int TRIALS = 100_000;

    @Property(trials = TRIALS)
    public void x (
        @RandomVo VoSingleton initialAndNewValues
    ) {
    }

}