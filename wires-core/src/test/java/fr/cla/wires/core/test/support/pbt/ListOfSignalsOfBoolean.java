package fr.cla.wires.core.test.support.pbt;

import fr.cla.wires.core.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableList;

/**
 * The only purpose of this class is to reify List<Signal<Boolean>>
 *     to help junit-quick-check find the generator for List<Signal<Boolean>>
 */
public class ListOfSignalsOfBoolean implements Supplier<List<Signal<Boolean>>> {

    private final List<Signal<Boolean>> xs;

    public ListOfSignalsOfBoolean(List<Signal<Boolean>> xs) {
        this.xs = new ArrayList<>(xs);
    }

    @Override
    public List<Signal<Boolean>> get() {
        return unmodifiableList(xs);
    }
}
