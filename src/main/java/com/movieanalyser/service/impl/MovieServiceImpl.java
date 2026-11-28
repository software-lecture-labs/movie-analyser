package com.movieanalyser.service.impl;

import com.movieanalyser.entity.EntityIds;
import com.movieanalyser.entity.Movie;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.exception.ResourceNotFoundException;
import com.movieanalyser.repository.MovieRepository;
import com.movieanalyser.service.MovieService;
import com.movieanalyser.service.MovieSort;
import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.LocalDate;

/**
 * Film kurallarını uygular ve okuma/yazmayı {@link MovieRepository}'ye bırakır.
 */
@ApplicationScoped
public class MovieServiceImpl implements MovieService {

    /** Bilinen ilk film 1888 yapımı; daha eski bir yıl yazım hatasıdır. */
    static final int FIRST_FILM_YEAR = 1888;

    /** Duyurulmuş ama henüz çıkmamış filmler için birkaç yıl ileri izin verilir. */
    static final int MAX_YEARS_AHEAD = 5;

    // Her sıralama en sonda id ile biter; sayfalar arasında kayma olmasın diye.
    // Bu sıralamalara uygun index'ler MongoIndexes'te tanımlı.
    private static final Order<Movie> BY_TITLE = Order.by(Sort.asc("titleLower"), Sort.asc("id"));
    private static final Order<Movie> BY_RATING =
            Order.by(Sort.desc("averageRating"), Sort.desc("ratingCount"), Sort.asc("id"));
    private static final Order<Movie> BY_YEAR =
            Order.by(Sort.desc("releaseYear"), Sort.asc("titleLower"), Sort.asc("id"));

    private MovieRepository movies;
    private Clock clock;

    @Inject
    public MovieServiceImpl(MovieRepository movies, Clock clock) {
        this.movies = movies;
        this.clock = clock;
    }

    /** CDI'nin bu application scoped bean için proxy oluşturabilmesi için gerekli. */
    protected MovieServiceImpl() {
        // doğrudan kullanılmaz
    }

    @Override
    public Movie addMovie(String title, String director, int releaseYear, String description, String posterUrl) {
        requireText(title, "title");
        requireText(director, "director");
        int latestYear = LocalDate.now(clock).getYear() + MAX_YEARS_AHEAD;
        if (releaseYear < FIRST_FILM_YEAR || releaseYear > latestYear) {
            throw new BusinessRuleException("releaseYear " + FIRST_FILM_YEAR + " ile " + latestYear
                    + " arasında olmalı, gelen: " + releaseYear);
        }
        return movies.save(Movie.create(title.trim(), director.trim(), releaseYear, description, posterUrl));
    }

    @Override
    public Movie getMovie(String id) {
        if (!EntityIds.isValid(id)) {
            throw new ResourceNotFoundException("Film bulunamadı: " + id);
        }
        return movies.findById(id).orElseThrow(() -> new ResourceNotFoundException("Film bulunamadı: " + id));
    }

    @Override
    public Page<Movie> listMovies(int page, int size, MovieSort sort) {
        Order<Movie> order = switch (sort) {
            case RATING -> BY_RATING;
            case YEAR -> BY_YEAR;
            case TITLE -> BY_TITLE;
        };
        return movies.findAll(Paging.of(page, size), order);
    }

    @Override
    public Page<Movie> searchMovies(String query, int page, int size) {
        requireText(query, "q");
        // Kayıtlı başlıklarla aynı yöntemle normalleştirilir, yoksa eşleşmez
        return movies.findByTitleLowerContainsOrderByTitleLowerAscIdAsc(
                Movie.normalizeTitle(query), Paging.of(page, size));
    }

    private static void requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleException(name + " boş olamaz");
        }
    }
}
