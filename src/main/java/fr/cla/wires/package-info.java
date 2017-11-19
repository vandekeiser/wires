/**
 * Take care that nothing in this package is thread-safe for now!
 *
 * Therefore, as explained in JCIP (Java concurrency in Practice),
 *  thread-safety for users of this API can only be achieved
 *  by using the following implementation patterns:
 *      -"thread-confinement", by either:
 *          -a convention of only accessing objects from 1 specific Thread, like the EDT in Swing
 *          -putting stuff into a ThreadLocal
 *      -"stack-confinement" (scoping local variables properly)
 *
 * TODO: where BinaryOperator<A> combiner / reduction is used,
 *  indicate that that operator must be associative
 *  & respect the other constraints from Collectors::reducing
 */
package fr.cla.wires;