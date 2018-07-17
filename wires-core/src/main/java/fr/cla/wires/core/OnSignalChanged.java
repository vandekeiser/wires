package fr.cla.wires.core;

import java.util.function.Consumer;

//TODO: Could some composition methods be placed here?
//@formatter:off
//Don't make package-private as this is the only alternative
// to the "Staged Builder" in Box, using Box::onSignalChanged
@FunctionalInterface
public interface OnSignalChanged<V> extends Consumer<Signal<V>> {

}
//@formatter:on
