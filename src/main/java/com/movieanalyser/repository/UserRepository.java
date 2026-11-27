package com.movieanalyser.repository;

import com.movieanalyser.entity.User;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.util.Optional;

/**
 * Kullanıcılar için veri erişimi. Uygulama çalışma zamanında üretilir;
 * sorgular metot adından türetilir.
 *
 * <p>Kullanıcı adı ve e-posta benzersizdir; bu, {@code MongoIndexes} içindeki unique
 * index'lerle garanti edilir. Bu yüzden aramalar en fazla bir kullanıcı döndürür ve
 * ilgili index'ler sayesinde koleksiyonu taramaz. Eşleşme tam ve büyük/küçük harfe duyarlıdır.</p>
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {

    /**
     * Verilen e-posta adresine sahip kullanıcı, yoksa boş.
     */
    Optional<User> findByEmail(String email);

    /**
     * Verilen kullanıcı adına sahip kullanıcı, yoksa boş.
     */
    Optional<User> findByUsername(String username);
}
