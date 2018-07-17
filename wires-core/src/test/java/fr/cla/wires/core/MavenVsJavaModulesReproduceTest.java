package fr.cla.wires.core;

import fr.cla.wires.support.functional.Streams;
import fr.cla.wires.support.oo.ddd.AbstractValueObjectTest;
import org.junit.Test;

import java.util.stream.Stream;

public class MavenVsJavaModulesReproduceTest extends AbstractValueObjectTest {

    @Test
    public void should_compile_and_run_while_depending_on_wires_support_both_main_and_test() {
        //wires.support/src/main
        Streams.index(Stream.empty());

        //wires.support/src/test
        new AbstractValueObjectTest();
    }

}
