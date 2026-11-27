package com.movieanalyser.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.movieanalyser.entity.User;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Repository uygulaması çalışma zamanında üretildiği için burada yalnızca arayüzün
 * sözleşmesi doğrulanır; sorguların gerçek davranışı uygulama açılırken ayrıştırılır.
 */
class UserRepositoryTest {

    @Test
    void isARepositoryForUsersWithStringIds() {
        assertNotNull(UserRepository.class.getAnnotation(Repository.class));
        assertTrue(CrudRepository.class.isAssignableFrom(UserRepository.class));
    }

    @Test
    void findByEmailReturnsAnOptionalUser() throws NoSuchMethodException {
        Method method = UserRepository.class.getMethod("findByEmail", String.class);

        assertEquals(Optional.class, method.getReturnType());
    }

    @Test
    void findByUsernameReturnsAnOptionalUser() throws NoSuchMethodException {
        Method method = UserRepository.class.getMethod("findByUsername", String.class);

        assertEquals(Optional.class, method.getReturnType());
    }

    @Test
    void queryMethodNamesRefersToRealEntityFields() throws NoSuchFieldException {
        // Metot adları sorguya çevrilir; adların geçtiği alanlar entity'de bulunmalı
        assertNotNull(User.class.getDeclaredField("email"));
        assertNotNull(User.class.getDeclaredField("username"));
    }
}
