package com.movieanalyser.repository;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.movieanalyser.entity.RatingSummary;
import com.movieanalyser.entity.Review;
import com.movieanalyser.exception.ConflictException;
import com.movieanalyser.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * Yorumları yazar ve filmin ortalama puanı ile puan sayısını tutarlı tutar;
 * her işlem tek bir MongoDB transaction'ı içinde yapılır.
 *
 * <p>Eşleme katmanının repository yazmaları sürücü oturumuna katılmaz; bu yüzden bu
 * sınıf sürücüyü doğrudan kullanır. Belgeler eşleme katmanının okuduğu biçimde yazılır:
 * _id olarak ObjectId, zaman damgaları için BSON tarihleri. İki transaction aynı filmi
 * aynı anda değiştirirse MongoDB yazma çakışması bildirir ve sürücü kaybeden
 * transaction'ı yeniden dener; böylece hiçbir güncelleme kaybolmaz.</p>
 */
@ApplicationScoped
public class ReviewTransactionRepository {

    private static final String ID = "_id";
    private static final String RATING = "rating";
    private static final String COMMENT = "comment";
    private static final String USER_ID = "userId";
    private static final String MOVIE_ID = "movieId";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String AVERAGE_RATING = "averageRating";
    private static final String RATING_COUNT = "ratingCount";

    private MongoClient client;
    private MongoCollection<Document> reviews;
    private MongoCollection<Document> movies;

    @Inject
    public ReviewTransactionRepository(MongoClient client, MongoDatabase database) {
        this.client = client;
        this.reviews = database.getCollection("reviews");
        this.movies = database.getCollection("movies");
    }

    /** Application scope'lu bu bean için proxy oluşturabilmesi adına CDI tarafından gereklidir. */
    protected ReviewTransactionRepository() {
        // doğrudan kullanılmaz
    }

    /**
     * Yeni bir yorumu kaydeder ve puanını filme ekler.
     *
     * @return kaydedilen yorumun id'si
     * @throws ConflictException kullanıcı bu filme zaten yorum yapmışsa
     * @throws ResourceNotFoundException film yoksa; hiçbir şey kaydedilmez
     */
    public String add(Review review) {
        ObjectId id = new ObjectId();
        Document document = new Document(ID, id)
                .append(RATING, review.getRating())
                .append(COMMENT, review.getComment())
                .append(USER_ID, review.getUserId())
                .append(MOVIE_ID, review.getMovieId())
                .append(CREATED_AT, Date.from(review.getCreatedAt()))
                .append(UPDATED_AT, Date.from(review.getUpdatedAt()));
        try {
            inTransaction(session -> {
                reviews.insertOne(session, document);
                // Bilerek önce eklenir: film yoksa transaction iptal edilir ve ekleme geri alınır
                changeRating(session, review.getMovieId(), summary -> summary.add(review.getRating()));
                return null;
            });
        } catch (MongoWriteException e) {
            if (ErrorCategory.fromErrorCode(e.getCode()) == ErrorCategory.DUPLICATE_KEY) {
                throw new ConflictException("Bu kullanıcı bu filme zaten yorum yapmış", e);
            }
            throw e;
        }
        return id.toHexString();
    }

    /**
     * Bir yorumun puanını ve metnini değiştirir; filmin ortalamasındaki eski puanı yenisiyle değiştirir.
     *
     * @throws ResourceNotFoundException yorum veya filmi yoksa
     */
    public void update(String reviewId, int rating, String comment, Instant updatedAt) {
        inTransaction(session -> {
            Document current = findReview(session, reviewId);
            int oldRating = current.getInteger(RATING);
            reviews.updateOne(session, byId(reviewId), Updates.combine(
                    Updates.set(RATING, rating),
                    Updates.set(COMMENT, comment),
                    Updates.set(UPDATED_AT, Date.from(updatedAt))));
            changeRating(session, current.getString(MOVIE_ID), summary -> summary.replace(oldRating, rating));
            return null;
        });
    }

    /**
     * Bir yorumu siler ve puanını filmin ortalamasından çıkarır.
     *
     * @throws ResourceNotFoundException yorum veya filmi yoksa
     */
    public void delete(String reviewId) {
        inTransaction(session -> {
            Document current = findReview(session, reviewId);
            reviews.deleteOne(session, byId(reviewId));
            changeRating(session, current.getString(MOVIE_ID),
                    summary -> summary.remove(current.getInteger(RATING)));
            return null;
        });
    }

    private void inTransaction(Function<ClientSession, Void> work) {
        try (ClientSession session = client.startSession()) {
            // work döndüğünde commit eder, hata fırlatırsa iptal eder, geçici hatalarda yeniden dener
            session.withTransaction(() -> work.apply(session));
        }
    }

    private Document findReview(ClientSession session, String reviewId) {
        Document review = reviews.find(session, byId(reviewId)).first();
        if (review == null) {
            throw new ResourceNotFoundException("Yorum bulunamadı: " + reviewId);
        }
        return review;
    }

    private void changeRating(ClientSession session, String movieId, Function<RatingSummary, RatingSummary> change) {
        Document movie = movies.find(session, byId(movieId)).first();
        if (movie == null) {
            throw new ResourceNotFoundException("Film bulunamadı: " + movieId);
        }
        RatingSummary next = change.apply(new RatingSummary(
                numberOrZero(movie.get(AVERAGE_RATING)).doubleValue(),
                numberOrZero(movie.get(RATING_COUNT)).intValue()));
        movies.updateOne(session, byId(movieId), Updates.combine(
                Updates.set(AVERAGE_RATING, next.average()),
                Updates.set(RATING_COUNT, next.count())));
    }

    private static Number numberOrZero(Object value) {
        return value instanceof Number number ? number : 0;
    }

    private static Bson byId(String id) {
        return Filters.eq(ID, new ObjectId(id));
    }
}
