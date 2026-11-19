# Movie Analyser

Film inceleme ve puanlama platformu projesi

## Proje Hakkında

Bu proje, kullanıcıların filmleri inceleyip puanlayabileceği ve diğer kullanıcıların değerlendirmelerini görebileceği bir web platformudur.

Her kullanıcı bir filme 1 ile 10 arasında puan verip yorum yazabilir. Filmin ortalama puanı ve değerlendirme sayısı, yorum eklendiğinde, güncellendiğinde veya silindiğinde tek bir transaction içinde güncellenir.

## Ekip

| İsim | GitHub |
|---|---|
| Şeyma Mavideniz | [@seymavideniz](https://github.com/seymavideniz) |
| Elanur Demir | [@elanurdmr](https://github.com/elanurdmr) |

## Teknolojiler

- Java 21
- Jakarta EE 10 (JAX-RS, CDI, JPA, Bean Validation, JTA)
- Payara Micro 6
- MySQL 8 (Docker Compose ile)
- Maven
- JUnit 5 ve Mockito

## Proje Yapısı

```
movie-analyser/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── movieanalyser/
│   │   │           ├── config/        # Uygulama yapılandırması (REST, veri kaynağı)
│   │   │           ├── controller/    # Sunum katmanı (JAX-RS REST endpoint'leri)
│   │   │           ├── service/       # İş mantığı arayüzleri
│   │   │           │   └── impl/      # İş mantığı uygulamaları
│   │   │           ├── repository/    # Veri erişim katmanı (JPA)
│   │   │           ├── entity/        # JPA entity sınıfları
│   │   │           ├── dto/           # Katmanlar arası veri transferi (record)
│   │   │           ├── mapper/        # Entity ve DTO dönüşümleri
│   │   │           └── exception/     # Hata sınıfları ve hata eşleyicileri
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml    # JPA yapılandırması
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml            # Web uygulama yapılandırması
│   │       │   └── beans.xml          # CDI yapılandırması
│   │       ├── css/                   # Stil dosyaları
│   │       ├── js/                    # JavaScript dosyaları
│   │       └── images/                # Görseller
│   └── test/
│       └── java/                      # Test sınıfları
└── pom.xml                            # Maven yapılandırması
```

## Mimari

Proje katmanlı mimari (Layered Architecture) kullanmaktadır:

```
Controller  ->  Service (arayüz + impl)  ->  Repository  ->  Entity
```

1. **Controller Katmanı**: HTTP isteklerini karşılayan JAX-RS REST endpoint'leri. İstemciye entity değil DTO döner.
2. **Service Katmanı**: İş mantığının bulunduğu katman. Arayüzler `service`, uygulamalar `service/impl` paketindedir. Transaction sınırları bu katmandadır.
3. **Repository Katmanı**: Veritabanı işlemlerini yöneten sınıflar. Ortak CRUD işlemleri generic bir temel sınıfta toplanır.
4. **Entity Katmanı**: Veritabanı tablolarını temsil eden JPA entity sınıfları.
5. **DTO ve Mapper**: Katmanlar arası veri transferi için kullanılan record'lar ve entity ile DTO arasındaki dönüşümler.

Bağımlılıklar constructor injection ile verilir.

## Kurulum

Gereksinimler (Windows ve macOS):

- JDK 21
- Maven 3.9+
- Docker Desktop
- Git

Kurulum adımları: Geliştirme aşamasında.

## Çalıştırma

Geliştirme aşamasında.

## API

Geliştirme aşamasında.

## Test

Geliştirme aşamasında.

## Lisans ve Ders Bilgisi

Bu proje, Java Kurumsal Proje Geliştirme dersi kapsamında dönem projesi olarak geliştirilmektedir. Eğitim amaçlıdır.
