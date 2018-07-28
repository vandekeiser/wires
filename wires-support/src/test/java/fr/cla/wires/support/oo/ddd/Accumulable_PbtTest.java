package fr.cla.wires.support.oo.ddd;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.java.math.BigIntegerGenerator;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.support.oo.AbstractValueObject;
import fr.cla.wires.support.oo.Accumulable;
import fr.cla.wires.support.oo.ddd.support.pbt.RandomVo;
import fr.cla.wires.support.oo.ddd.support.pbt.RandomVoPair;
import fr.cla.wires.support.oo.ddd.support.pbt.VoPair;
import fr.cla.wires.support.oo.ddd.support.pbt.VoSingleton;
import org.junit.runner.RunWith;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class Accumulable_PbtTest {

    private static final int TRIALS = 100_000;

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
            accumulator
        );

        //When
        acc.mutableEquivalentToInitially(initialAndNewValues.y);

        //Then
        assertThat(
            acc
        ).isEqualTo(
            Accumulable.initially(initialAndNewValues.y, weight, accumulator)
        );

        assertThat(
            acc.hashCode()
        ).isEqualTo(
            Accumulable.initially(initialAndNewValues.y, weight, accumulator).hashCode()
        );
    }

}
//@formatter:on