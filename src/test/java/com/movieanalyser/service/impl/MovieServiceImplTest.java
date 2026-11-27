package com.movieanalyser.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.movieanalyser.entity.Movie;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.exception.ResourceNotFoundException;
import com.movieanalyser.repository.MovieRepository;
import jakarta.data.Order;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    private static final String MOVIE_ID = "6b060c259c09bf675bb8c53f";
    // Saat 2026'yı gösteriyor; en ileri kabul edilen yapım yılı 2031
    private static final Instant NOW = Instant.parse("2026-11-28T10:00:00Z");

    @Mock
    private MovieRepository movies;

    @Mock
    private Page<Movie> page;

    private MovieServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MovieServiceImpl(movies, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void addSavesMovieAndReturnsStoredOne() {
        Movie stored = Movie.create("Inception", "Christopher Nolan", 2010, "Rüya", null);
        when(movies.save(any(Movie.class))).thenReturn(stored);

        Movie result = service.addMovie("  Inception ", "Christopher Nolan", 2010, "Rüya", null);

        assertSame(stored, result);
        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        verify(movies).save(captor.capture());
        assertEquals("Inception", captor.getValue().getTitle());
    }

    @Test
    void addRejectsBlankTitle() {
        assertThrows(BusinessRuleException.class, () -> service.addMovie(" ", "Nolan", 2010, null, null));
        verifyNoInteractions(movies);
    }

    @Test
    void addRejectsBlankDirector() {
        assertThrows(BusinessRuleException.class, () -> service.addMovie("Inception", null, 2010, null, null));
        verifyNoInteractions(movies);
    }

    @Test
    void addRejectsYearBeforeFirstFilm() {
        assertThrows(BusinessRuleException.class, () -> service.addMovie("Eski", "Biri", 1887, null, null));
    }

    @Test
    void addAcceptsYearBoundaries() {
        when(movies.save(any(Movie.class))).thenAnswer(call -> call.getArgument(0));

        assertEquals(1888, service.addMovie("İlk", "Le Prince", 1888, null, null).getReleaseYear());
        assertEquals(2031, service.addMovie("Gelecek", "Biri", 2031, null, null).getReleaseYear());
    }

    @Test
    void addRejectsYearTooFarInFuture() {
        assertThrows(BusinessRuleException.class, () -> service.addMovie("Çok ileri", "Biri", 2032, null, null));
    }

    @Test
    void getRejectsMalformedIdWithoutTouchingRepository() {
        assertThrows(ResourceNotFoundException.class, () -> service.getMovie("abc"));
        verifyNoInteractions(movies);
    }

    @Test
    void getOfMissingMovieIsNotFound() {
        when(movies.findById(MOVIE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getMovie(MOVIE_ID));
    }

    @Test
    void getReturnsMovie() {
        Movie movie = Movie.create("Inception", "Christopher Nolan", 2010, null, null);
        when(movies.findById(MOVIE_ID)).thenReturn(Optional.of(movie));

        assertSame(movie, service.getMovie(MOVIE_ID));
    }

    @Test
    @SuppressWarnings("unchecked")
    void listUsesRequestedPageSortedByTitle() {
        when(movies.findAll(any(PageRequest.class), any(Order.class))).thenReturn(page);

        assertSame(page, service.listMovies(2, 10));

        ArgumentCaptor<PageRequest> request = ArgumentCaptor.forClass(PageRequest.class);
        ArgumentCaptor<Order<Movie>> order = ArgumentCaptor.forClass(Order.class);
        verify(movies).findAll(request.capture(), order.capture());
        assertEquals(2, request.getValue().page());
        assertEquals(10, request.getValue().size());
        assertEquals("titleLower", order.getValue().sorts().get(0).property());
    }

    @Test
    void listRejectsInvalidPageWithoutTouchingRepository() {
        assertThrows(BusinessRuleException.class, () -> service.listMovies(0, 10));
        verifyNoInteractions(movies);
    }

    @Test
    void searchNormalizesQueryLikeStoredTitles() {
        when(movies.findByTitleLowerContainsOrderByTitleLowerAscIdAsc(any(), any())).thenReturn(page);

        assertSame(page, service.searchMovies("  İSTANBUL ", 1, 20));

        verify(movies).findByTitleLowerContainsOrderByTitleLowerAscIdAsc(
                org.mockito.ArgumentMatchers.eq("istanbul"), any(PageRequest.class));
    }

    @Test
    void searchRejectsBlankQuery() {
        assertThrows(BusinessRuleException.class, () -> service.searchMovies("  ", 1, 20));
        verifyNoInteractions(movies);
    }
}
