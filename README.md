# Movie Analyser

Film inceleme ve puanlama platformu projesi

## Proje Hakkında

Bu proje, kullanıcıların filmleri inceleyip puanlayabileceği, listeler oluşturabileceği ve diğer kullanıcıların değerlendirmelerini görebileceği bir web platformudur.

## Proje Yapısı

```
movie-analyser/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── movieanalyser/
│   │   │           ├── entity/        # Entity katmanı (Domain models)
│   │   │           ├── dao/           # Data Access Object katmanı
│   │   │           ├── service/       # İş mantığı katmanı
│   │   │           ├── controller/    # Sunum katmanı (Servlets/REST)
│   │   │           ├── dto/           # Data Transfer Objects
│   │   │           └── util/          # Yardımcı sınıflar
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml    # JPA yapılandırması
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml            # Web uygulama yapılandırması
│   │       │   └── beans.xml          # CDI yapılandırması
│   │       ├── css/                   # Stil dosyaları
│   │       ├── js/                    # JavaScript dosyaları
│   │       ├── images/                # Görseller
│   │       └── index.html             # Ana sayfa
│   └── test/
│       └── java/                      # Test sınıfları
└── pom.xml                            # Maven yapılandırması
```

## Mimari

Proje katmanlı mimari (Layered Architecture) kullanmaktadır:

1. **Entity Katmanı**: Veritabanı tablolarını temsil eden JPA entity sınıfları
2. **DAO Katmanı**: Veritabanı işlemlerini yöneten sınıflar
3. **Service Katmanı**: İş mantığının bulunduğu katman
4. **Controller Katmanı**: HTTP isteklerini karşılayan servlet/REST endpoint'ler
5. **DTO Katmanı**: Katmanlar arası veri transferi için kullanılan sınıflar
