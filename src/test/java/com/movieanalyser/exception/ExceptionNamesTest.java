package com.movieanalyser.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExceptionNamesTest {

    // Jakarta'da sık kullanılan exception paketleri; aynı adlı bir sınıf varsa
    // IDE otomatik import sırasında yanlış paketi seçebilir.
    private static final List<String> JAKARTA_PACKAGES = List.of(
            "jakarta.ws.rs", "jakarta.ws.rs.core", "jakarta.validation", "jakarta.persistence");

    // Yeni bir domain exception eklendiğinde bu listeye de eklenmeli.
    private static final List<Class<?>> DOMAIN_EXCEPTIONS = List.of(
            ResourceNotFoundException.class, ConflictException.class, BusinessRuleException.class);

    @Test
    void jakartaPackagesAreOnTheClasspath() {
        // Jakarta sınıfları yüklenemezse çakışma testi hiçbir şeyi kontrol etmeden geçerdi
        assertTrue(exists("jakarta.ws.rs.NotFoundException"));
        assertTrue(exists("jakarta.validation.ValidationException"));
    }

    @Test
    void domainExceptionNamesDoNotClashWithJakarta() {
        List<String> clashes = new ArrayList<>();
        for (Class<?> exception : DOMAIN_EXCEPTIONS) {
            for (String jakartaPackage : JAKARTA_PACKAGES) {
                String candidate = jakartaPackage + "." + exception.getSimpleName();
                if (exists(candidate)) {
                    clashes.add(candidate);
                }
            }
        }

        assertTrue(clashes.isEmpty(), "Jakarta ile aynı adı taşıyan exception'lar: " + clashes);
    }

    private static boolean exists(String className) {
        try {
            Class.forName(className, false, ExceptionNamesTest.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
