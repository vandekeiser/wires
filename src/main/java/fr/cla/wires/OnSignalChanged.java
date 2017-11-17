package fr.cla.wires;

import java.util.function.Consumer;

@FunctionalInterface
public interface OnSignalChanged<V> extends Consumer<Signal<V>> {

}
