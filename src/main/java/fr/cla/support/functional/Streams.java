package fr.cla.support.functional;

import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//@formatter:off
public class Streams {

    /**
     * Zip the source stream together with the stream of indices() to provide a stream of indexed values.
     * @param source  The source stream.
     * @param <T> The type over which the source stream streams.
     * @return A stream of indexed values.
     */
    public static <T> Stream<Indexed<T>> index(Stream<T> source) {
        return zip(
            indices().mapToObj(Long::valueOf),
            source,
            Indexed::index
        ).onClose(source::close);
    }

    /**
     * Constructs an infinite (although in practice bounded by Long.MAX_VALUE) stream of longs 0, 1, 2, 3...
     * for use as indices.
     * @return A stream of longs.
     */
    private static LongStream indices() {
        return LongStream.iterate(0L, l -> l + 1);
    }

    /**
     * Zip together the "left" and "right" streams until either runs out of values.
     * Each pair of values is combined into a single value using the supplied combiner function.
     * @param lefts The "left" stream to zip.
     * @param rights The "right" stream to zip.
     * @param combiner The function to combine "left" and "right" values.
     * @param <L> The type over which the "left" stream streams.
     * @param <R> The type over which the "right" stream streams.
     * @param <O> The type created by the combiner out of pairs of "left" and "right" values, over which the resulting
     *           stream streams.
     * @return A stream of zipped values.
     */
    private static <L, R, O> Stream<O> zip(Stream<L> lefts, Stream<R> rights, BiFunction<L, R, O> combiner) {
        return StreamSupport.stream(
            ZippingSpliterator.zipping(lefts.spliterator(), rights.spliterator(), combiner),
            false
        );
    }

    //@formatter:off
    private static class ZippingSpliterator<L, R, O> implements Spliterator<O> {

        static <L, R, O> Spliterator<O> zipping(Spliterator<L> lefts, Spliterator<R> rights, BiFunction<L, R, O> combiner) {
            return new ZippingSpliterator<>(lefts, rights, combiner);
        }

        private final Spliterator<L> lefts;
        private final Spliterator<R> rights;
        private final BiFunction<L, R, O> combiner;
        private boolean rightHadNext = false;

        private ZippingSpliterator(Spliterator<L> lefts, Spliterator<R> rights, BiFunction<L, R, O> combiner) {
            this.lefts = lefts;
            this.rights = rights;
            this.combiner = combiner;
        }

        @Override
        public boolean tryAdvance(Consumer<? super O> action) {
            rightHadNext = false;
            boolean leftHadNext = lefts.tryAdvance(l ->
            rights.tryAdvance(r -> {
                rightHadNext = true;
                action.accept(combiner.apply(l, r));
            }));
            return leftHadNext && rightHadNext;
        }

        @Override
        public Spliterator<O> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Math.min(lefts.estimateSize(), rights.estimateSize());
        }

        @Override
        public int characteristics() {
            return lefts.characteristics() & rights.characteristics()
            & ~(Spliterator.DISTINCT | Spliterator.SORTED);
        }
    }
}
//@formatter:on
