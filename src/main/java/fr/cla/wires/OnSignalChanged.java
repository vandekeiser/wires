package fr.cla.wires;

import java.util.function.Consumer;

//TODO: Could some composition methods be placed here?
//@formatter:off
@FunctionalInterface
interface OnSignalChanged<V> extends Consumer<Signal<V>> {

}
//@formatter:on
