/**
 * Classes in this package should be in fr.cla.wires.support.oo:
 * I want to do whitebox testing of AbstractValueObject but can't because of MCOMPILER-354
 *
 * This happens only because fr.cla.wires.support.oo is an exported package,
 * we use maven test-scoped-dependencies which patch the module with tests,
 * and these tests drag their unexported dependencies.
 */
package fr.cla.wires.support.oo.test;