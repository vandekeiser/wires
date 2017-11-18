package fr.cla.wires.exampleusage.pbt;

import com.pholser.junit.quickcheck.From;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({PARAMETER, FIELD, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@From(BooleansGenerator.class)
public @interface RandomBooleans {
}