package fr.cla.wires.support.oo.ddd;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import fr.cla.wires.support.oo.AbstractValueObject;
import fr.cla.wires.support.oo.Accumulable;
import fr.cla.wires.support.oo.ddd.support.pbt.RandomVoPair;
import fr.cla.wires.support.oo.ddd.support.pbt.VoPair;
import org.junit.runner.RunWith;

import java.util.function.BinaryOperator;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;


//@formatter:off
@RunWith(JUnitQuickcheck.class)
public class Accumulable_PbtTest {

    private static final int TRIALS = 100_000;

    /**
     * MAVEN NOTES
     *
     * 2. Whitebox testing
     *  Accumulable_PbtTest::if_newVal_is_not_null_mutableEquivalentToInitially_should_be_equivalent_to_initially
     *  in src/test of fr.cla.wires.support
     *
     * In the call "acc.unsafeMutableEquivalentToInitially(initialAndNewValues.y);"
     *  I want to make unsafeMutableEquivalentToInitially package-private.
     *  To do that I need to move Accumulable_PbtTest to the same package fr.cla.wires.support.oo
     */
    /**
     * 3. The whitebox test class references VoPair from unexported package fr.cla.wires.support.oo.ddd.support.pbt
     *  in a visible way (it has a public method that takes VoPair as an argument)
     */
    /**
     * 1. + 2. + 3.
     *  The whitebox test class is in exported package fr.cla.wires.support.oo of module fr.cla.wires.support,
     *   and references an unexported package in a visible way.
     *  I get a warning compiling fr.cla.wires.support then an error compiling fr.cla.wires.core,
     *   because Accumulable_PbtTest is not usable by any class in another module.
     */
    /**
     * The problem is that I didn't really want to export Accumulable_PbtTest.
     * The test dependency that I do need to share is RandomBooleans.
     *
     * FILTER tests whitebox pr ne pas les inclure ds le patch?
     */
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
        acc.unsafeMutableEquivalentToInitially(initialAndNewValues.y);

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