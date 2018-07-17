package fr.cla.wires.core.boxes.exampleusage.multipleinputs;

import fr.cla.wires.core.Clock;
import fr.cla.wires.core.Delay;
import fr.cla.wires.core.Wire;
import fr.cla.wires.core.boxes.CollectHomogeneousInputsToOutputOfSameType;

import java.util.List;
import java.util.function.BinaryOperator;

//@formatter:off
/**
 * An example usage of how to connect wires to boxes.
 * @see fr.cla.wires.core.exampleusage
 *
 * Discussion of possible design choices:
 * 1. This class is an alternative implementation to CollectMultipleAnd.
 * It takes the other design trade-off compared to CollectMultipleAnd:
 *  -It is shorter and has a better "Payload / Builder code ratio"
 *  -On the other hand it does not expose a DSL to instantiate it like this:
 *      CollectMultipleAnd.ins(ins).out(out).clock(clock);
 *   So clients of this API need to do this:
 *      SimpleCollectMultipleAnd.create(ins, out, clock);
 *
 * 2. It could thus be considered a less expressive API,
 *  and makes it more likely that the client of this API will
 *  switch in1/in2 by mistake if several inputs have the same type.
 *
 * 3. The trade-off depends on:
 *  -how many in/out wires the box has,
 *  -the taste of the implementer,
 *  -actual occurences of switching the wires by mistake,
 *  -whether this class is itself the business logic
 *   or a component used by the real business logic (in that case favor the DSL).
 *
 * 4. The point is that:
 *  even though the API is internally implemented using the elaborate approach,
 *  the API's client may or may not use the simpler approach.
 */
public final class SimpleCollectMultipleAnd
extends CollectHomogeneousInputsToOutputOfSameType<Boolean> {

//Do your business-specific magic here:
//-------------Payload section of the class-------------VVVVVVVVVVVVVVVVVVV
    @Override protected BinaryOperator<Boolean> accumulator() {
        return this::and;
    }

    private boolean and(boolean b1, boolean b2) {
        return b1 && b2;
    }
//-------------Payload section of the class-------------^^^^^^^^^^^^^^^^^^^

//This implementation wants to keep this as short as possible:
//-------------Builder section of the class-------------VVVVVVVVVVVVVVVVVVV
    /**
     * Even though the purpose of this class is to show a CollectMultipleAnd implementation
     *  with less "builder code", it is still worth having at least a factory method to hide the startup:
     *
     * This method is used to not do the startup in the constructor,
     * to not let "this" escape through the method ref,
     * so that the Box is "properly constructed".
     *
     * @return a started Box.
     */
    public static SimpleCollectMultipleAnd create(
        List<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock
    ) {
        SimpleCollectMultipleAnd gate = new SimpleCollectMultipleAnd(ins, out, clock, DEFAULT_DELAY);
        gate.startup();
        return gate;
    }

    private SimpleCollectMultipleAnd(List<Wire<Boolean>> ins, Wire<Boolean> out, Clock clock, Delay delay) {
        super(ins, out, clock, delay);
    }
//-------------Builder section of the class-------------^^^^^^^^^^^^^^^^^^^

}
//@formatter:on
