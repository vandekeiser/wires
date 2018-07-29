Usage:
    -mvn clean install at the project root
    -go unzip the resulting zip
        -To find where the resulting zip is deployed, look at the end of the logs for something like:
            [INFO] --- maven-install-plugin:2.5.2:install (default-install) @ wires-jlink ---
            [INFO] Installing G:\projets\wires\wires\wires\wires-jlink\target\wires-jlink-1.0-SNAPSHOT.zip to C:\Users\User\.m2\repository\fr\cla\wires-jlink\1.0-SNAPSHOT\wires-jlink-1.0-SNAPSHOT.zip
    -./java -version to check everthing works
        -If it says "permission denied" on windows, make an exception for the unzipped directory in your antivirus
    -./java --list-modules
        -You should get something like:
            java.base@10.0.2
            wires.core
            wires.neuron
            wires.support
        -Somehow (for now) jlink or the maven plugin is creating the modules from the jar name, even though they have a module descriptor..
            ./java -m fr.cla.wires.neuron/fr.cla.wires.neuron.NeuronApp
            java.lang.module.FindException: Module fr.cla.wires.neuron not found
    -./java -m wires.neuron/fr.cla.wires.neuron.NeuronApp
        -You should get something like:
            User@User-PC MINGW64 ~/.m2/repository/fr/cla/wires-jlink
            $ ./1.0-SNAPSHOT/wires-jlink-1.0-SNAPSHOT/bin/java -m wires.neuron/fr.cla.wires.neuron.NeuronApp
            now: 1, out: 0.0
            now: 2, out: 1.0
            now: 3, out: 1.0
            now: 4, out: 0.0
            now: 5, out: 0.0
            now: 6, out: 1.0


Another interesting command:
    -jimage list ../lib/modules | grep 'fr.cla'
        Module: fr.cla.wires.core
            META-INF/maven/fr.cla/wires-core/pom.properties
            META-INF/maven/fr.cla/wires-core/pom.xml
            fr/cla/wires/core/Box$1.class
            fr/cla/wires/core/Box$Applying.class
            fr/cla/wires/core/Box$InputsAndOutputCaptured.class
            fr/cla/wires/core/Box$ObservedAndTargetWiresCaptured.class
            fr/cla/wires/core/Box$ObservedWireCaptured.class
            fr/cla/wires/core/Box$Reducing.class
            fr/cla/wires/core/Box$ReducingIndexed.class
            fr/cla/wires/core/Box.class
            fr/cla/wires/core/Clock$Agenda.class
            fr/cla/wires/core/Clock.class
            fr/cla/wires/core/Delay.class
            fr/cla/wires/core/OnSignalChanged.class
            fr/cla/wires/core/Signal.class
            fr/cla/wires/core/Tick$OverflowException.class
            fr/cla/wires/core/Tick$Queue.class
            fr/cla/wires/core/Tick.class
            fr/cla/wires/core/Wire.class
            fr/cla/wires/core/boxes/CollectHomogeneousInputs.class
            fr/cla/wires/core/boxes/CollectHomogeneousInputsToOutputOfSameType.class
            fr/cla/wires/core/boxes/CollectIndexedHomogeneousInputs.class
            fr/cla/wires/core/boxes/ReduceHomogeneousInputs.class
            fr/cla/wires/core/boxes/ReduceIndexedHomogeneousInputs.class
            fr/cla/wires/core/boxes/exampleusage/basic/And$1.class
            fr/cla/wires/core/boxes/exampleusage/basic/And$Builder.class
            fr/cla/wires/core/boxes/exampleusage/basic/And.class
            fr/cla/wires/core/boxes/exampleusage/basic/AnswerFirst$1.class
            fr/cla/wires/core/boxes/exampleusage/basic/AnswerFirst$Builder.class
            fr/cla/wires/core/boxes/exampleusage/basic/AnswerFirst.class
            fr/cla/wires/core/boxes/exampleusage/basic/AnswerSecond$1.class
            fr/cla/wires/core/boxes/exampleusage/basic/AnswerSecond$Builder.class
            fr/cla/wires/core/boxes/exampleusage/basic/AnswerSecond.class
            fr/cla/wires/core/boxes/exampleusage/basic/LeafHalfAdder$1.class
            fr/cla/wires/core/boxes/exampleusage/basic/LeafHalfAdder$Builder.class
            fr/cla/wires/core/boxes/exampleusage/basic/LeafHalfAdder.class
            fr/cla/wires/core/boxes/exampleusage/basic/Not$1.class
            fr/cla/wires/core/boxes/exampleusage/basic/Not$Builder.class
            fr/cla/wires/core/boxes/exampleusage/basic/Not.class
            fr/cla/wires/core/boxes/exampleusage/basic/Or$1.class
            fr/cla/wires/core/boxes/exampleusage/basic/Or$Builder.class
            fr/cla/wires/core/boxes/exampleusage/basic/Or.class
            fr/cla/wires/core/boxes/exampleusage/composite/CompositeFullAdder$1.class
            fr/cla/wires/core/boxes/exampleusage/composite/CompositeFullAdder$Builder.class
            fr/cla/wires/core/boxes/exampleusage/composite/CompositeFullAdder.class
            fr/cla/wires/core/boxes/exampleusage/composite/CompositeHalfAdder$1.class
            fr/cla/wires/core/boxes/exampleusage/composite/CompositeHalfAdder$Builder.class
            fr/cla/wires/core/boxes/exampleusage/composite/CompositeHalfAdder.class
            fr/cla/wires/core/boxes/exampleusage/multipleinputs/CollectMultipleAnd$1.class
            fr/cla/wires/core/boxes/exampleusage/multipleinputs/CollectMultipleAnd$Builder.class
            fr/cla/wires/core/boxes/exampleusage/multipleinputs/CollectMultipleAnd.class
            fr/cla/wires/core/boxes/exampleusage/multipleinputs/ReduceMultipleAnd$1.class
            fr/cla/wires/core/boxes/exampleusage/multipleinputs/ReduceMultipleAnd$Builder.class
            fr/cla/wires/core/boxes/exampleusage/multipleinputs/ReduceMultipleAnd.class
            fr/cla/wires/core/boxes/exampleusage/multipleinputs/SimpleCollectMultipleAnd.class
            fr/cla/wires/core/boxes/exampleusage/reentrant/Counter$1.class
            fr/cla/wires/core/boxes/exampleusage/reentrant/Counter$Builder.class
            fr/cla/wires/core/boxes/exampleusage/reentrant/Counter$OverflowException.class
            fr/cla/wires/core/boxes/exampleusage/reentrant/Counter.class
            fr/cla/wires/core/boxes/exampleusage/reentrant/CounterApp.class
        Module: fr.cla.wires.neuron
            META-INF/maven/fr.cla/wires-neuron/pom.properties
            META-INF/maven/fr.cla/wires-neuron/pom.xml
            fr/cla/wires/neuron/Connexions.class
            fr/cla/wires/neuron/ExternalUnit.class
            fr/cla/wires/neuron/GroupOfUnits.class
            fr/cla/wires/neuron/Inputs.class
            fr/cla/wires/neuron/Layer$1.class
            fr/cla/wires/neuron/Layer$Builder.class
            fr/cla/wires/neuron/Layer.class
            fr/cla/wires/neuron/Network.class
            fr/cla/wires/neuron/Neuron$1.class
            fr/cla/wires/neuron/Neuron$Builder.class
            fr/cla/wires/neuron/Neuron.class
            fr/cla/wires/neuron/NeuronApp.class
            fr/cla/wires/neuron/Offset.class
            fr/cla/wires/neuron/Outputs.class
            fr/cla/wires/neuron/Synapse.class
            fr/cla/wires/neuron/TransferFunction$1.class
            fr/cla/wires/neuron/TransferFunction$2.class
            fr/cla/wires/neuron/TransferFunction$3.class
            fr/cla/wires/neuron/TransferFunction.class
            fr/cla/wires/neuron/Unit.class
            fr/cla/wires/neuron/perceptron/BackPropagationNetwork.class
            fr/cla/wires/neuron/perceptron/FeedForwardLayer.class
            fr/cla/wires/neuron/perceptron/FeedForwardNetwork.class
            fr/cla/wires/neuron/perceptron/FeedForwardNeuron.class
            fr/cla/wires/neuron/perceptron/example/CompleteConnexions$1.class
            fr/cla/wires/neuron/perceptron/example/CompleteConnexions$Builder.class
            fr/cla/wires/neuron/perceptron/example/CompleteConnexions.class
            fr/cla/wires/neuron/perceptron/example/RecognizeDigits.class
        Module: fr.cla.wires.support
            META-INF/maven/fr.cla/wires-support/pom.properties
            META-INF/maven/fr.cla/wires-support/pom.xml
            fr/cla/wires/support/functional/Indexed.class
            fr/cla/wires/support/functional/Streams$ZippingSpliterator.class
            fr/cla/wires/support/functional/Streams.class
            fr/cla/wires/support/oo/AbstractValueObject$1.class
            fr/cla/wires/support/oo/AbstractValueObject$Equatability$1.class
            fr/cla/wires/support/oo/AbstractValueObject$Equatability$2.class
            fr/cla/wires/support/oo/AbstractValueObject$Equatability$3.class
            fr/cla/wires/support/oo/AbstractValueObject$Equatability.class
            fr/cla/wires/support/oo/AbstractValueObject.class
            fr/cla/wires/support/oo/Accumulable$1.class
            fr/cla/wires/support/oo/Accumulable$Collector.class
            fr/cla/wires/support/oo/Accumulable.class
            fr/cla/wires/support/oo/MutableValue.class
            sun/launcher/resources/launcher_fr.class
            sun/security/tools/keytool/Resources_fr.class
            sun/security/util/AuthResources_fr.class
            sun/security/util/Resources_fr.class

