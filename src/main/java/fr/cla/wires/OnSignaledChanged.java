package fr.cla.wires;

import java.util.function.Consumer;

@FunctionalInterface
public interface OnSignaledChanged<V> extends Consumer<Signal<V>> {

}
