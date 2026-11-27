package com.movieanalyser.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.movieanalyser.entity.Movie;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/**
 * Repository uygulaması çalışma zamanında üretildiği için burada yalnızca arayüzün
 * sözleşmesi doğrulanır; sorguların gerçek davranışı uygulama açılırken ayrıştırılır.
 */
class MovieRepositoryTest {

    private static final String SEARCH_METHOD = "findByTitleLowerContainsOrderByTitleLowerAscIdAsc";

    @Test
    void isARepositoryForMoviesWithStringIds() {
        assertNotNull(MovieRepository.class.getAnnotation(Repository.class));
        assertTrue(CrudRepository.class.isAssignableFrom(MovieRepository.class));
    }

    @Test
    void titleSearchReturnsAPageAndTakesAPageRequest() throws NoSuchMethodException {
        Method method = MovieRepository.class.getMethod(SEARCH_METHOD, String.class, PageRequest.class);

        assertEquals(Page.class, method.getReturnType());
    }

    @Test
    void searchMethodNameRefersToARealEntityField() throws NoSuchFieldException {
        // Metot adı sorguya çevrilir; adın geçtiği alanlar entity'de bulunmalı
        assertNotNull(Movie.class.getDeclaredField("titleLower"));
        assertNotNull(Movie.class.getDeclaredField("id"));
    }
}
