package com.movieanalyser.service;

import com.movieanalyser.entity.Review;

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
}
