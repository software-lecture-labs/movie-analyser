package com.movieanalyser.controller;

import com.movieanalyser.dto.CreateMovieRequest;
import com.movieanalyser.dto.MovieResponse;
import com.movieanalyser.dto.PageResponse;
import com.movieanalyser.entity.Movie;
import com.movieanalyser.mapper.MovieMapper;
import com.movieanalyser.mapper.PageMapper;
import com.movieanalyser.service.MovieService;
import com.movieanalyser.service.MovieSort;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
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

    private MovieService movies;

    @Inject
    public MovieResource(MovieService movies) {
        this.movies = movies;
    }

    /** CDI'nin bu bean için proxy oluşturabilmesi için gerekli. */
    protected MovieResource() {
        // doğrudan kullanılmaz
    }

    /** {@code ?page=} varsayılan 1, {@code ?size=} varsayılan 20, {@code ?sort=title|rating|year} varsayılan title. */
    @GET
    public PageResponse<MovieResponse> list(@QueryParam("page") String page, @QueryParam("size") String size,
                                            @QueryParam("sort") String sort) {
        return PageMapper.toResponse(
                movies.listMovies(QueryParams.page(page), QueryParams.size(size), MovieSort.from(sort)),
                MovieMapper::toResponse);
    }

    /** Başarılı olursa 201 ve yeni filmin adresi ({@code Location}) döner. */
    @POST
    public Response create(@Valid @NotNull CreateMovieRequest request, @Context UriInfo uriInfo) {
        Movie movie = movies.addMovie(request.title(), request.director(), request.releaseYear(),
                request.description(), request.posterUrl());
        // UriBuilder yol sonundaki "/" ile çift "/" oluşmasını engeller
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(movie.getId())).build();
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
                                              @QueryParam("page") String page,
                                              @QueryParam("size") String size) {
        return PageMapper.toResponse(movies.searchMovies(query, QueryParams.page(page), QueryParams.size(size)),
                MovieMapper::toResponse);
    }
}
