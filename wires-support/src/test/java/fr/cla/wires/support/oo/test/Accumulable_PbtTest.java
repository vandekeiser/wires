package fr.cla.wires.support.oo.test;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.support.oo.AbstractValueObject;
import fr.cla.wires.support.oo.Accumulable;
import fr.cla.wires.support.pbt.RandomVoPair;
import fr.cla.wires.support.pbt.VoPair;
import org.junit.runner.RunWith;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class Accumulable_PbtTest {

    private static final int TRIALS = 100_000;
    //TODO randomize
    private static final Accumulable.WhenCombining TESTED_POLICY_FOR_COMBINING_WITH_ABSENT_VALUES = Accumulable.WhenCombining.ABSENT_WINS;

    //Note:
    // Not using var here even though these types are a mouthful since they're not obvious either.
    // A "type alias" would be ideal in this case..
    @Property(trials = TRIALS)
    public void if_newVal_is_not_null_mutableEquivalentToInitially_should_be_equivalent_to_initially (
        @RandomVoPair VoPair initialAndNewValues //we know its x and y are not null
    ) {
        // TODO: randomize those 2 among enums
        Function<AbstractValueObject<?>, AbstractValueObject<?>> weight = Function.identity();
        BinaryOperator<AbstractValueObject<?>> accumulator = (x, y) -> x;

        //Given
        Accumulable<AbstractValueObject<?>, AbstractValueObject<?>> acc =  Accumulable.initially(
            initialAndNewValues.x,
            weight,
            accumulator,
            TESTED_POLICY_FOR_COMBINING_WITH_ABSENT_VALUES
        );

        //When
        acc.unsafeMutableEquivalentToInitially(initialAndNewValues.y);

        //Then
        assertThat(
            acc
        ).isEqualTo(
            Accumulable.initially(
                initialAndNewValues.y,
                weight,
                accumulator,
                TESTED_POLICY_FOR_COMBINING_WITH_ABSENT_VALUES
            )
        );

        assertThat(
            acc.hashCode()
        ).isEqualTo(
            Accumulable.initially(
                initialAndNewValues.y,
                weight,
                accumulator,
                TESTED_POLICY_FOR_COMBINING_WITH_ABSENT_VALUES
            ).hashCode()
        );
    }

}
//@formatter:on