The goal of this branch is to make mvn clean install pass.
That is the criteria for migrating to Java>8,
 which here is not trivial because Jigsaw modules and maven src-scoped dependencies
 involve adding --patch-module/--add-exports options to javac/java
 and only the most recent version of everything works with Java 10.
Current status is I'm getting:
    [ERROR] Tests run: 5, Failures: 0, Errors: 5, Skipped: 0, Time elapsed: 4.992 s <<< FAILURE! - in fr.cla.wires.core.boxes.exampleusage.propertybasedtesting.MultipleAnd_ReduceAndCollectShouldBeEquivalent_PbtTest
    [ERROR] should_sometimes_give_false(fr.cla.wires.core.boxes.exampleusage.propertybasedtesting.MultipleAnd_ReduceAndCollectShouldBeEquivalent_PbtTest)  Time elapsed: 0.093 s  <<< ERROR!
    com.pholser.junit.quickcheck.internal.ReflectionException: java.lang.IllegalAccessException: class com.pholser.junit.quickcheck.internal.Reflection cannot access class fr.cla.wires.core.support.tests.pbt.BooleansGenerator (in module fr.cla.wires.core) because module fr.cla.wires.core does not export fr.cla.wires.core.support.tests.pbt to unnamed module @1ed1993a
The problem is:
    "module fr.cla.wires.core does not export fr.cla.wires.core.support.tests.pbt to unnamed module"
But should it be:
    -Maven who adds --add-exports to the unnamed module
    -Or javac to require an export to the unnamed module
-->Need to read up and test




# Java port of the "Simulator for digital circuits" from SICP
A Java port of the "Simulator for digital circuits" from SICP (Structure and Interpretation of Computer Programs).
The full book is available online: https://mitpress.mit.edu/sicp/full-text/book/book.html
I have the paper edition, but it's nice to be able to link to a non-paywalled, full edition, so thanks MIT for that!

The idea of this project comes from a friend of mine mentioning connecting neural networks together.
It reminded me of doing it like in that chapter (rather than using Kafka or any complex frameworks): https://mitpress.mit.edu/sicp/full-text/book/book-Z-H-22.html#%_sec_3.3.4
Even though Lisp and Java obviously are very different, with just Java 8 lambdas it's not hard to implement almost exactly the same concepts.
So then, it shouldn't be that hard to implement a neural network from that, right??? (yeah right) 
For now (and for once) I wanna keep from looking at how frameworks like DL4J/TensorFlow/whatever do it.
Instead I'm going to try to use: 
    -SICP concepts (wires, ...) as low-level bricks 
    -to implement the model from the French book "Réseaux neuronaux" by Jean-Philippe Rennard (Editions Vuibert) 
    -using my Java experience to do it in a more object-oriented way than in the book
 
See:
 - Classes in `/src/main/java/fr/cla/wires/boxes/exampleusage/` for example usages of how to connect `Wire`s to `Box`es
 - Tests in `/src/test/java/fr/cla/wires/boxes/exampleusage/` for expected behaviour and examples of how to tick the clock. 
    - Tests in `/src/test/java/fr/cla/wires/boxes/exampleusage/pbt/` use *Property-Based Testing* to assert the invariants of various `Box`es. 







##### (Developper note)
If you're wondering what the `//@formatter:off` `//@formatter:on` comments are all about, it's because: 
 - Even though IntelliJ is overall a better IDE than Eclipse IMO, it's also more annoying in some regards (*La perfection n'est pas de ce monde*),
    - generally in that it sometimes tends to be too *opinionated*,
    - and specifically in that unlike Eclipse, it doesn't have a global, *Don't reformat on save* / *Thanks, but I know what I'm doing* option (at least give us options: "Never" __OR__ "Never for Java files", since this issue is more pregnant in the "core").
        - and they outright say they won't support it.
 - So instead of just configuring IJ, I have to sprinkle it all over the place. 
Overall thanks IJ for the stable IDE experience, but please be less *apple-like*. 
