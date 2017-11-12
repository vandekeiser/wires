package fr.cla.wires;

import java.util.function.Consumer;

@FunctionalInterface
public interface Action<V> extends Consumer<Signal<V>> {

}
