package com.movieanalyser.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.movieanalyser.dto.CreateMovieRequest;
import com.movieanalyser.dto.MovieResponse;
import com.movieanalyser.dto.PageResponse;
import com.movieanalyser.entity.Movie;
import com.movieanalyser.service.MovieService;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovieResourceTest {

    private static final String MOVIE_ID = "6b060c259c09bf675bb8c53f";

    @Mock
    private MovieService service;

    private MovieResource resource;

    @BeforeEach
    void setUp() {
        resource = new MovieResource(service);
    }

    @Test
    void createReturns201WithLocationAndBody() {
        Movie stored = Movie.create("Inception", "Christopher Nolan", 2010, null, null);
        when(service.addMovie("Inception", "Christopher Nolan", 2010, null, null)).thenReturn(stored);
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePath()).thenReturn(URI.create("http://localhost/api/movies"));

        try (Response response = resource.create(
                new CreateMovieRequest("Inception", "Christopher Nolan", 2010, null, null), uriInfo)) {
            assertEquals(201, response.getStatus());
            assertEquals("Inception", ((MovieResponse) response.getEntity()).title());
            // Mock servis id üretmediği için adresin sonu "null"; gerçek id Payara'da doğrulanır
            assertEquals(URI.create("http://localhost/api/movies/null"), response.getLocation());
        }
    }

    @Test
    void getReturnsMappedMovie() {
        when(service.getMovie(MOVIE_ID)).thenReturn(Movie.create("Inception", "Nolan", 2010, null, null));

        assertEquals("Inception", resource.get(MOVIE_ID).title());
    }

    @Test
    @SuppressWarnings("unchecked")
    void listReturnsPageResponse() {
        Page<Movie> page = mock(Page.class);
        when(page.content()).thenReturn(List.of(Movie.create("A", "B", 2000, null, null)));
        when(page.pageRequest()).thenReturn(PageRequest.ofPage(1).size(20));
        when(service.listMovies(1, 20)).thenReturn(page);

        PageResponse<MovieResponse> response = resource.list(1, 20);

        assertEquals(1, response.content().size());
        assertEquals("A", response.content().get(0).title());
    }

    @Test
    @SuppressWarnings("unchecked")
    void searchPassesQueryAndPaging() {
        Page<Movie> page = mock(Page.class);
        when(page.content()).thenReturn(List.of());
        when(page.pageRequest()).thenReturn(PageRequest.ofPage(2).size(5));
        when(service.searchMovies("ist", 2, 5)).thenReturn(page);

        PageResponse<MovieResponse> response = resource.search("ist", 2, 5);

        assertSame(List.of().getClass(), response.content().getClass());
        assertEquals(2, response.page());
    }
}
