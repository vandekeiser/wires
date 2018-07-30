module fr.cla.wires.core {
    requires transitive fr.cla.wires.support;
    exports fr.cla.wires.core;
    exports fr.cla.wires.core.boxes;

//Exported test packages to allow test-scoped-dependencies and white-box-testing at the same time.
//It's a "main & test module", don't know yet if that pattern is acceptable.
    exports fr.cla.wires.core.test.random.boolsignals;
}