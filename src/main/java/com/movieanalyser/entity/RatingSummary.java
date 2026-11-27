package com.movieanalyser.entity;

/**
 * Bir filmin ortalama puanı ve puan sayısı.
 *
 * <p>Değiştirilemez (immutable): her değişiklik yeni bir özet döndürür. Ortalama,
 * önceki ortalama ve sayıdan yeniden hesaplanır; bu yüzden tek tek puanlara gerek yoktur.</p>
 *
 * @param average ortalama puan, puan yoksa 0.0
 * @param count   puan sayısı
 */
public record RatingSummary(double average, int count) {

    public RatingSummary {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative, was " + count);
        }
    }

    /** Hiç puanı olmayan bir film. */
    public static RatingSummary empty() {
        return new RatingSummary(0.0, 0);
    }

    /** Yeni bir puan eklendikten sonraki özet. */
    public RatingSummary add(int rating) {
        int newCount = count + 1;
        return new RatingSummary((double) (total() + rating) / newCount, newCount);
    }

    /** Mevcut bir puan silindikten sonraki özet. */
    public RatingSummary remove(int rating) {
        requireRatings();
        int newCount = count - 1;
        if (newCount == 0) {
            return empty();
        }
        return new RatingSummary((double) (total() - rating) / newCount, newCount);
    }

    /** Mevcut bir puan değiştirildikten sonraki özet; puan sayısı aynı kalır. */
    public RatingSummary replace(int oldRating, int newRating) {
        requireRatings();
        return new RatingSummary((double) (total() - oldRating + newRating) / count, count);
    }

    /**
     * Tüm puanların toplamı. Puanlar tam sayı olduğu için toplam da tam sayıdır; yuvarlama,
     * ortalama * sayı işleminden doğan kayan nokta hatasını giderir. Aksi halde bu hata
     * çok sayıda değişiklikte birikirdi (4.5 yerine 4.500000000000001).
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
