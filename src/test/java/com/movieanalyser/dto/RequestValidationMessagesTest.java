package com.movieanalyser.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Constraint;
import java.lang.annotation.Annotation;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * İstek DTO'larındaki doğrulama mesajları açıkça yazılmış olmalı.
 *
 * <p>Varsayılan mesajlar ({@code {jakarta.validation...}}) sunucunun diline göre değişir:
 * bir makinede Türkçe, CI'da İngilizce olur. API'nin dili sabit kalsın diye her kısıtın
 * kendi mesajı olmalı.</p>
 */
class RequestValidationMessagesTest {

    // Yeni bir istek DTO'su eklendiğinde bu listeye de eklenmeli.
    private static final List<Class<? extends Record>> REQUESTS = List.of(
            CreateMovieRequest.class, CreateReviewRequest.class, UpdateReviewRequest.class);

    @Test
    void everyConstraintHasExplicitMessage() throws ReflectiveOperationException {
        List<String> defaults = new ArrayList<>();
        for (Class<? extends Record> request : REQUESTS) {
            for (RecordComponent component : request.getRecordComponents()) {
                for (Annotation annotation : component.getAccessor().getDeclaringClass()
                        .getDeclaredField(component.getName()).getAnnotations()) {
                    if (isConstraint(annotation) && message(annotation).startsWith("{")) {
                        defaults.add(request.getSimpleName() + "." + component.getName()
                                + " @" + annotation.annotationType().getSimpleName());
                    }
                }
            }
        }

        assertTrue(defaults.isEmpty(), "Varsayılan mesaj kullanan kısıtlar: " + defaults);
    }

    private static boolean isConstraint(Annotation annotation) {
        return annotation.annotationType().isAnnotationPresent(Constraint.class);
    }

    private static String message(Annotation annotation) throws ReflectiveOperationException {
        return (String) annotation.annotationType().getMethod("message").invoke(annotation);
    }
}
