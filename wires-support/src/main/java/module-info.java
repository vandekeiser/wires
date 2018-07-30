module fr.cla.wires.support {
    exports fr.cla.wires.support.functional;
    exports fr.cla.wires.support.oo;

//Exported test packages to allow test-scoped-dependencies and white-box-testing at the same time.
//It's a "main & test module", don't know yet if that pattern is acceptable.
    exports fr.cla.wires.support.test.random.bools;
    exports fr.cla.wires.support.test.random.vo;
    exports fr.cla.wires.support.test.random.vo.types;
}