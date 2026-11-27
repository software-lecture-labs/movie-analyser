package com.movieanalyser.repository;

import com.movieanalyser.entity.Review;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.util.Optional;

/**
 * Yorumlara veri erişimi. Uygulama çalışma zamanında üretilir;
 * sorgular, sıralamaları dahil, metot adlarından türetilir.
 *
 * <p>Yorum listeleri sayfalıdır ve en yeni önce sıralanır; birden fazla yorum aynı
 * zaman damgasını paylaştığında sayfaların kararlı kalması için id eşitlik bozucu olarak kullanılır.
 * Eşleşen indeksler {@code MongoIndexes} içinde tanımlıdır. Sıralama metot adının bir parçasıdır;
 * çünkü mevcut eşleme uygulaması @OrderBy anotasyonunu türetilmiş sorgularda desteklemez.</p>
 */
@Repository
public interface ReviewRepository extends CrudRepository<Review, String> {

    /**
     * Bir film için yazılan yorumların bir sayfası, en yeni önce.
     */
    Page<Review> findByMovieIdOrderByCreatedAtDescIdDesc(String movieId, PageRequest pageRequest);

    /**
     * Bir kullanıcının yazdığı yorumların bir sayfası, en yeni önce.
     */
    Page<Review> findByUserIdOrderByCreatedAtDescIdDesc(String userId, PageRequest pageRequest);

    /**
     * Bir kullanıcının bir film için yazdığı yorum, varsa. En fazla bir tane vardır;
     * bu (userId, movieId) üzerindeki benzersiz indeksle garanti edilir.
     */
    Optional<Review> findByUserIdAndMovieId(String userId, String movieId);
}
