package fr.cla.wires.core;

import fr.cla.wires.support.functional.Streams;
import fr.cla.wires.support.oo.test.AbstractValueObjectTest;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class MavenVsJavaModulesCompatibilityTest {

    @Test
    public void should_compile_and_run_while_depending_on_wires_support_both_main_and_test() {
        //wires.support/src/main
        Streams.index(Stream.empty());

        //wires.support/src/test
        new AbstractValueObjectTest();
    }

    @Test
    public void should_be_able_to_whitebox_test_package_private_methods() {
        new Clock.Agenda();
    }

    @Test
    public void should_be_able_to_do_deep_reflection() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Clock.Agenda> packagePrivateConstructor = Clock.Agenda.class.getDeclaredConstructor(new Class<?>[]{});
        assertThat(visibility(packagePrivateConstructor)).isEqualTo(Visibility.PACKAGE_PRIVATE);
        packagePrivateConstructor.newInstance(new Object[]{});
    }

    private Visibility visibility(Constructor<?> cstr) {
        int flags = cstr.getModifiers();
        if(Modifier.isPublic(flags)) return Visibility.PUBLIC;
        else if(Modifier.isPublic(flags)) return Visibility.PRIVATE;
        else if(Modifier.isPublic(flags)) return Visibility.PROTECTED;
        else return Visibility.PACKAGE_PRIVATE;
    }

}
