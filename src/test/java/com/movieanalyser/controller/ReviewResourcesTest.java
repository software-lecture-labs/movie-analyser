package com.movieanalyser.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.movieanalyser.dto.CreateReviewRequest;
import com.movieanalyser.dto.PageResponse;
import com.movieanalyser.dto.ReviewResponse;
import com.movieanalyser.dto.UpdateReviewRequest;
import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.BusinessRuleException;
import com.movieanalyser.service.ReviewService;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Yorum uç noktaları: /reviews, /movies/{id}/reviews, /users/{id}/reviews. */
@ExtendWith(MockitoExtension.class)
class ReviewResourcesTest {

    private static final String USER_ID = "6b060c259c09bf675bb8c53e";
    private static final String MOVIE_ID = "6b060c259c09bf675bb8c53f";
    private static final String REVIEW_ID = "6b060c259c09bf675bb8c540";
    private static final Instant NOW = Instant.parse("2026-11-28T10:00:00Z");

    @Mock
    private ReviewService reviews;

    private static Review review(int rating) {
        return Review.create(USER_ID, MOVIE_ID, rating, "yorum", NOW);
    }

    @Test
    void createReturns201WithBody() {
        when(reviews.addReview(USER_ID, MOVIE_ID, 8, "yorum")).thenReturn(review(8));
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost/api/reviews"));

        try (Response response = new ReviewResource(reviews)
                .create(new CreateReviewRequest(USER_ID, MOVIE_ID, 8, "yorum"), uriInfo)) {
            assertEquals(201, response.getStatus());
            assertEquals(8, ((ReviewResponse) response.getEntity()).rating());
        }
    }

    @Test
    void getReturnsMappedReview() {
        when(reviews.getReview(REVIEW_ID)).thenReturn(review(6));

        assertEquals(6, new ReviewResource(reviews).get(REVIEW_ID).rating());
    }

    @Test
    void updatePassesNewValues() {
        when(reviews.updateReview(REVIEW_ID, 9, "değişti")).thenReturn(review(9));

        assertEquals(9, new ReviewResource(reviews).update(REVIEW_ID, new UpdateReviewRequest(9, "değişti")).rating());
    }

    @Test
    void deleteReturns204() {
        try (Response response = new ReviewResource(reviews).delete(REVIEW_ID)) {
            assertEquals(204, response.getStatus());
        }
        verify(reviews).deleteReview(REVIEW_ID);
    }

    @Test
    @SuppressWarnings("unchecked")
    void movieReviewsListsReviews() {
        Page<Review> page = mock(Page.class);
        when(page.content()).thenReturn(List.of(review(7)));
        when(page.pageRequest()).thenReturn(PageRequest.ofPage(1).size(20));
        when(reviews.listByMovie(MOVIE_ID, 1, 20)).thenReturn(page);

        PageResponse<ReviewResponse> response = new MovieReviewsResource(reviews).list(MOVIE_ID, null, null);

        assertEquals(7, response.content().get(0).rating());
    }

    @Test
    void movieReviewsWithNonNumericSizeIs400() {
        MovieReviewsResource resource = new MovieReviewsResource(reviews);
        assertThrows(BusinessRuleException.class, () -> resource.list(MOVIE_ID, "1", "abc"));
        verifyNoInteractions(reviews);
    }

    @Test
    @SuppressWarnings("unchecked")
    void userReviewsListsReviews() {
        Page<Review> page = mock(Page.class);
        when(page.content()).thenReturn(List.of(review(5)));
        when(page.pageRequest()).thenReturn(PageRequest.ofPage(1).size(20));
        when(reviews.listByUser(USER_ID, 1, 20)).thenReturn(page);

        assertEquals(5, new UserReviewsResource(reviews).list(USER_ID, "1", "20").content().get(0).rating());
    }
}
