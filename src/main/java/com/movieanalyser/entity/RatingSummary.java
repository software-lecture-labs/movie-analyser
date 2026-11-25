package com.movieanalyser.entity;

/**
 * Average rating and number of ratings of a movie.
 *
 * <p>Immutable: every change returns a new summary. The average is recalculated
 * from the previous average and count, so the individual ratings are not needed.</p>
 *
 * @param average the average rating, 0.0 when there are no ratings
 * @param count   the number of ratings
 */
public record RatingSummary(double average, int count) {

    public RatingSummary {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative, was " + count);
        }
    }

    /** A movie without ratings. */
    public static RatingSummary empty() {
        return new RatingSummary(0.0, 0);
    }

    /** The summary after a new rating is added. */
    public RatingSummary add(int rating) {
        int newCount = count + 1;
        return new RatingSummary((double) (total() + rating) / newCount, newCount);
    }

    /** The summary after an existing rating is removed. */
    public RatingSummary remove(int rating) {
        requireRatings();
        int newCount = count - 1;
        if (newCount == 0) {
            return empty();
        }
        return new RatingSummary((double) (total() - rating) / newCount, newCount);
    }

    /** The summary after an existing rating is changed; the count stays the same. */
    public RatingSummary replace(int oldRating, int newRating) {
        requireRatings();
        return new RatingSummary((double) (total() - oldRating + newRating) / count, count);
    }

    /**
     * The sum of all ratings. Ratings are whole numbers, so the sum is too; rounding
     * removes the floating point error of average * count, which would otherwise
     * build up over many changes (4.500000000000001 instead of 4.5).
     */
    private long total() {
        return Math.round(average * count);
    }

    private void requireRatings() {
        if (count == 0) {
            throw new IllegalStateException("there are no ratings to remove or replace");
        }
    }
}
