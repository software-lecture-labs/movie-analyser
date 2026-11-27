package com.movieanalyser.controller;

import com.movieanalyser.dto.CreateMovieRequest;
import com.movieanalyser.dto.MovieResponse;
import com.movieanalyser.dto.PageResponse;
import com.movieanalyser.entity.Movie;
import com.movieanalyser.mapper.MovieMapper;
import com.movieanalyser.mapper.PageMapper;
import com.movieanalyser.service.MovieService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Film uç noktaları: {@code /api/movies}. Kurallar serviste; burada yalnızca
 * istek/yanıt dönüşümü yapılır. Hatalar exception mapper'larla JSON gövdeye çevrilir.
 */
@Path("/movies")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MovieResource {

    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_SIZE = "20";

    private MovieService movies;

    @Inject
    public MovieResource(MovieService movies) {
        this.movies = movies;
    }

    /** CDI'nin bu bean için proxy oluşturabilmesi için gerekli. */
    protected MovieResource() {
        // doğrudan kullanılmaz
    }

    @GET
    public PageResponse<MovieResponse> list(@QueryParam("page") @DefaultValue(DEFAULT_PAGE) int page,
                                            @QueryParam("size") @DefaultValue(DEFAULT_SIZE) int size) {
        return PageMapper.toResponse(movies.listMovies(page, size), MovieMapper::toResponse);
    }

    /** Başarılı olursa 201 ve yeni filmin adresi ({@code Location}) döner. */
    @POST
    public Response create(@Valid @NotNull CreateMovieRequest request, @Context UriInfo uriInfo) {
        Movie movie = movies.addMovie(request.title(), request.director(), request.releaseYear(),
                request.description(), request.posterUrl());
        URI location = URI.create(uriInfo.getAbsolutePath() + "/" + movie.getId());
        return Response.created(location).entity(MovieMapper.toResponse(movie)).build();
    }

    @GET
    @Path("/{id}")
    public MovieResponse get(@PathParam("id") String id) {
        return MovieMapper.toResponse(movies.getMovie(id));
    }

    @GET
    @Path("/search")
    public PageResponse<MovieResponse> search(@QueryParam("q") String query,
                                              @QueryParam("page") @DefaultValue(DEFAULT_PAGE) int page,
                                              @QueryParam("size") @DefaultValue(DEFAULT_SIZE) int size) {
        return PageMapper.toResponse(movies.searchMovies(query, page, size), MovieMapper::toResponse);
    }
}
