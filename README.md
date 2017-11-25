# Java port of the "Simulator for digital circuits" from SICP
A Java port of the "Simulator for digital circuits" from SICP (Structure and Interpretation of Computer Programs).

 
See:
 - Classes in `/src/main/java/fr/cla/wires/boxes/exampleusage/` for example usages of how to connect `Wire`s to `Box`es
 - Tests in `/src/test/java/fr/cla/wires/boxes/exampleusage/` for expected behaviour and examples of how to tick the clock. 
    - In particular tests in `/src/test/java/fr/cla/wires/boxes/exampleusage/pbt/` use *Property-Based Testing* to assert the invariants of various `Box`es. 







##### (Developper note)
If you're wondering what the `//@formatter:off` `//@formatter:on` comments are all about, it's because: 
 - Even though IntelliJ is overall a better IDE than Eclipse IMO, it's also more annoying in some regards (*La perfection n'est pas de ce monde*),
    - generally in that it sometimes tends to be too *opinionated*,
    - and specifically in that unlike Eclipse, it doesn't have a global, *Don't reformat on save* / *Thanks, but I know what I'm doing* option (at least give us options: "Never" __OR__ "Never for Java files", since this issue is more pregnant in the "core").
        - and they outright say they won't support it.
 - So instead of just configuring IJ, I have to sprinkle it all over the place. 
Overall thanks IJ for the stable IDE experience, but please be less *apple-like*. 
