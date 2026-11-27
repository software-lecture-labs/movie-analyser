package com.movieanalyser.service;

import com.movieanalyser.entity.Movie;
import jakarta.data.page.Page;

/**
 * Film ekleme, getirme, listeleme ve başlığa göre arama.
 */
public interface MovieService {

    /**
     * Yeni bir film ekler.
     *
     * @throws com.movieanalyser.exception.BusinessRuleException başlık/yönetmen boşsa ya da yıl geçersizse
     */
    Movie addMovie(String title, String director, int releaseYear, String description, String posterUrl);

    /**
     * @throws com.movieanalyser.exception.ResourceNotFoundException id bozuksa ya da film yoksa
     */
    Movie getMovie(String id);

    /**
     * Filmleri başlığa göre alfabetik sırayla, sayfa sayfa döndürür.
     *
     * @throws com.movieanalyser.exception.BusinessRuleException sayfa bilgisi geçersizse
     */
    Page<Movie> listMovies(int page, int size);

    /**
     * Başlığında arama metni geçen filmler; büyük/küçük harf ve Türkçe "İ" farkı gözetilmez.
     *
     * @throws com.movieanalyser.exception.BusinessRuleException arama metni boşsa ya da sayfa bilgisi geçersizse
     */
    Page<Movie> searchMovies(String query, int page, int size);
}
