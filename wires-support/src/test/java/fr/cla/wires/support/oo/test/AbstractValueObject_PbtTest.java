package fr.cla.wires.support.oo.test;


import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.support.pbt.*;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class AbstractValueObject_PbtTest {

    private static final int TRIALS = 100_000;

    @Property(trials = TRIALS)
    public void equals_should_be_reflexive(
        @RandomVo VoSingleton s
    ) {
        assertThat(
            s.x.equals(s.x)
        ).isTrue();
    }

    @Property(trials = TRIALS)
    public void equals_should_be_symmetric(
        @RandomVoPair VoPair p
    ) {
        assertThat(
            p.x.equals(p.y)
        ).isEqualTo(
            p.y.equals(p.x)
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

    @Property(trials = TRIALS)
    public void equals_null_should_be_false(
    @RandomVo VoSingleton s
    ) {
        assertThat(
           s.x.equals(null)
        ).isFalse();
    }

    @Property(trials = TRIALS)
    public void equals_should_be_consistent(
        @RandomVoPair VoPair p
    ) {
        assertThat(
            p.x.equals(p.y)
        ).isEqualTo(
            p.x.equals(p.y)
        );
    }

    @Property(trials = TRIALS)
    public void equals_implies_same_hashCode(
        @RandomVoPair VoPair p
    ) {
        if(p.x.equals(p.y)){
            assertEquals(p.x.hashCode(), p.y.hashCode());
        }
    }

    @Property(trials = TRIALS)
    public void hashCode_should_be_consistent(
        @RandomVo VoSingleton s
    ) {
        assertThat(
            s.x.hashCode()
        ).isEqualTo(
            s.x.hashCode()
        );
    }

    /**
     * Not part of the equals contract but part of the
     */
    //Uncomment to test AbstractValueObject.Equatability.IS_INSTANCE
    //Comment to test AbstractValueObject.Equatability.SAME_CONCRETE_CLASS
    //Uncomment to test AbstractValueObject.Equatability.CAN_EQUAL
    //    - as long as all overrides X of AbstractValueObject::canEqual return (obj instanceof X)
    //)
    //@Ignore
    @Property(trials = TRIALS)
    public void equals_should_be_false_for_different_types(
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
