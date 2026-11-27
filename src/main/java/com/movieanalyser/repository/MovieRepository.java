package com.movieanalyser.repository;

import com.movieanalyser.entity.Movie;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

/**
 * Filmler için veri erişimi. Uygulama çalışma zamanında üretilir;
 * sorgular ve sıralamaları metot adından türetilir.
 *
 * <p>Başlık araması büyük/küçük harf duyarsızdır: MongoDB sürücüsü "içerir" sorgusunu
 * büyük/küçük harfe duyarlı çalıştırdığı için arama, küçük harfe çevrilmiş
 * {@code titleLower} alanında yapılır. Arama metni {@link Movie#normalizeTitle(String)}
 * ile normalleştirilerek verilmelidir.</p>
 *
 * <p>İçerir araması bir index ile daraltılamaz; sorgu koleksiyondaki kayıtları tarar.
 * Film sayısı az olduğu için bu kabul edilmiştir. Sıralama, kayıtlar aynı başlığa
 * sahip olduğunda sayfaların kararlı kalması için id ile tamamlanır. Sıralama metot
 * adında verilir, çünkü mevcut eşleme katmanı türetilmiş sorgularda @OrderBy
 * anotasyonunu desteklemiyor.</p>
 */
@Repository
public interface MovieRepository extends CrudRepository<Movie, String> {

    /**
     * Başlığında verilen (normalleştirilmiş) metin geçen filmlerin bir sayfası; başlığa göre sıralı.
     */
    Page<Movie> findByTitleLowerContainsOrderByTitleLowerAscIdAsc(String normalizedTitle, PageRequest pageRequest);
}
