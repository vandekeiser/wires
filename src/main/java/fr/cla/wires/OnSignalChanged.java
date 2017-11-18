package fr.cla.wires;

import java.util.function.Consumer;

//@formatter:off
@FunctionalInterface
public interface OnSignalChanged<V> extends Consumer<Signal<V>> {

}
//@formatter:on
