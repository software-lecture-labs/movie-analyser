package com.movieanalyser.controller;

import com.movieanalyser.dto.CreateReviewRequest;
import com.movieanalyser.dto.ReviewResponse;
import com.movieanalyser.dto.UpdateReviewRequest;
import com.movieanalyser.entity.Review;
import com.movieanalyser.mapper.ReviewMapper;
import com.movieanalyser.service.ReviewService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Yorum uç noktaları: {@code /api/reviews}. Ekleme, güncelleme ve silme filmin
 * ortalamasıyla birlikte tek transaction'da yapılır (bkz. {@link ReviewService}).
 */
@Path("/reviews")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    private ReviewService reviews;

    @Inject
    public ReviewResource(ReviewService reviews) {
        this.reviews = reviews;
    }

    /** CDI'nin bu bean için proxy oluşturabilmesi için gerekli. */
    protected ReviewResource() {
        // doğrudan kullanılmaz
    }

    /** Başarılı olursa 201 ve yeni yorumun adresi ({@code Location}) döner. */
    @POST
    public Response create(@Valid @NotNull CreateReviewRequest request, @Context UriInfo uriInfo) {
        Review review = reviews.addReview(request.userId(), request.movieId(), request.rating(), request.comment());
        // UriBuilder yol sonundaki "/" ile çift "/" oluşmasını engeller
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(review.getId())).build();
        return Response.created(location).entity(ReviewMapper.toResponse(review)).build();
    }

    @GET
    @Path("/{id}")
    public ReviewResponse get(@PathParam("id") String id) {
        return ReviewMapper.toResponse(reviews.getReview(id));
    }

    @PUT
    @Path("/{id}")
    public ReviewResponse update(@PathParam("id") String id, @Valid @NotNull UpdateReviewRequest request) {
        return ReviewMapper.toResponse(reviews.updateReview(id, request.rating(), request.comment()));
    }

    /** Başarılı silmede gövdesiz 204 döner. */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        reviews.deleteReview(id);
        return Response.noContent().build();
    }
}
