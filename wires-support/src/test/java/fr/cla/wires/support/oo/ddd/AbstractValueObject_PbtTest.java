package fr.cla.wires.support.oo.ddd;


import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.support.oo.ddd.support.pbt.*;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class AbstractValueObject_PbtTest {

    private static final int TRIALS = 10_000;

    @Property(trials = TRIALS)
    public void equals_should_be_reflexive(
        @RandomVo VoSingleton s
    ) {
        assertThat(s.x.equals(s.x)).isTrue();
    }

    @Property(trials = TRIALS)
    public void equals_should_be_symmetric(
        @RandomVoPair VoPair p
    ) {
        assertThat(
            p.x.equals(p.y)).isEqualTo(p.y.equals(p.x)
        ).overridingErrorMessage(
            "Expected equals to be transitive " +
            "(vo1 eq vo2) eq (vo2 eq vo1).%n" +
            "Actual: (vo1 eq vo2)==%b, because vo1==%b and vo2==%b,%n" +
            "   (vo2 eq vo1)==%b, because vo2==%b and vo1==%b.%n"
        );
    }

    @Property(trials = TRIALS)
    public void equals_should_be_transitive(
        @RandomVoTriplet VoTriplet t
    ) {
        if(t.x.equals(t.y) && t.y.equals(t.z)){
            assertTrue(t.x.equals(t.z));
        }
    }

    /**
     * Not part of the equals contract but can't possibly work
     */
    @Property(trials = TRIALS)
    public void a_correct_equals_cant_be_true_for_different_type(
        @RandomVoPair VoPair p
    ) {
        if(!p.x.equals(p.y)) return;

        assertThat(
            p.x.getClass().equals(p.y.getClass())
        )
        .as(
            "Expected only VOs of same time to be equal. Actual: %n" +
            "    p.x: %s%n" +
            "    p.y: %s%n" +
            "    p.x.equals(p.y): %b%n" +
            "    p.x.getClass(): %s%n" +
            "    p.y.getClass(): %s%n" +
            "    p.x.getClass().equals(p.y.getClass(): %b",
            p.x, p.y, p.x.equals(p.y),
            p.x.getClass(), p.y.getClass(), p.x.getClass().equals(p.y.getClass())
        )
        .isTrue();
    }

}
//@formatter:on
