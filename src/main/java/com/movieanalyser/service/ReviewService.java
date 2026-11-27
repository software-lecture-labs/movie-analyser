package com.movieanalyser.service;

import com.movieanalyser.entity.Review;
import jakarta.data.page.Page;

/**
 * Yorum ekleme, değiştirme ve silme. Her değişiklik, filmin ortalama puanını ve
 * puan sayısını da aynı transaction içinde günceller.
 */
public interface ReviewService {

    /**
     * Bir kullanıcının bir film için yorumunu ekler.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException id hatalı biçimdeyse veya film yoksa
     * @throws com.movieanalyser.exception.BusinessRuleException puan veya yorum bir kuralı çiğniyorsa
     * @throws com.movieanalyser.exception.ConflictException kullanıcı filme zaten yorum yapmışsa
     */
    Review addReview(String userId, String movieId, int rating, String comment);

    /**
     * Bir yorumun puanını ve metnini değiştirir.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException id hatalı biçimdeyse veya yorum yoksa
     * @throws com.movieanalyser.exception.BusinessRuleException puan veya yorum bir kuralı çiğniyorsa
     */
    Review updateReview(String reviewId, int rating, String comment);

    /**
     * Bir yorumu siler.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException id hatalı biçimdeyse veya yorum yoksa
     */
    void deleteReview(String reviewId);

    /**
     * @throws com.movieanalyser.exception.ResourceNotFoundException id bozuksa ya da yorum yoksa
     */
    Review getReview(String reviewId);

    /**
     * Bir filmin yorumları, en yeniden eskiye, sayfa sayfa.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException film id'si bozuksa
     * @throws com.movieanalyser.exception.BusinessRuleException sayfa bilgisi geçersizse
     */
    Page<Review> listByMovie(String movieId, int page, int size);

    /**
     * Bir kullanıcının yorumları, en yeniden eskiye, sayfa sayfa.
     *
     * @throws com.movieanalyser.exception.ResourceNotFoundException kullanıcı id'si bozuksa
     * @throws com.movieanalyser.exception.BusinessRuleException sayfa bilgisi geçersizse
     */
    Page<Review> listByUser(String userId, int page, int size);
}
