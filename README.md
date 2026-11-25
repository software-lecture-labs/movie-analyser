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
- Jakarta EE 10 (JAX-RS, CDI, Bean Validation)
- Jakarta NoSQL (Eclipse JNoSQL)
- Payara Micro 6
- MongoDB 8 (Docker Compose ile, transaction desteği için tek düğümlü replica set)
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
│   │   │           ├── config/        # Uygulama yapılandırması (REST, veritabanı bağlantısı)
│   │   │           ├── controller/    # Sunum katmanı (JAX-RS REST endpoint'leri)
│   │   │           ├── service/       # İş mantığı arayüzleri
│   │   │           │   └── impl/      # İş mantığı uygulamaları
│   │   │           ├── repository/    # Veri erişim katmanı (Jakarta NoSQL)
│   │   │           ├── entity/        # MongoDB belge (document) sınıfları
│   │   │           ├── dto/           # Katmanlar arası veri transferi (record)
│   │   │           ├── mapper/        # Entity ve DTO dönüşümleri
│   │   │           └── exception/     # Hata sınıfları ve hata eşleyicileri
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── microprofile-config.properties  # Veritabanı bağlantı ayarları
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml            # Web uygulama yapılandırması
│   │       │   └── beans.xml          # CDI yapılandırması
│   │       ├── css/                   # Stil dosyaları
│   │       ├── js/                    # JavaScript dosyaları
│   │       └── images/                # Görseller
│   └── test/
│       └── java/                      # Test sınıfları
├── docker/
│   └── mongo/
│       └── init-user.js               # Uygulama kullanıcısını oluşturan betik
├── docker-compose.yml                 # Yerel MongoDB
├── .env.example                       # Ortam değişkenleri şablonu
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
4. **Entity Katmanı**: MongoDB koleksiyonlarındaki belgeleri temsil eden entity sınıfları.
5. **DTO ve Mapper**: Katmanlar arası veri transferi için kullanılan record'lar ve entity ile DTO arasındaki dönüşümler.

Bağımlılıklar constructor injection ile verilir.

## Kurulum

Gereksinimler (Windows ve macOS):

- JDK 21
- Maven 3.9+
- Docker Desktop
- Git

### 1. Ortam değişkenleri

`.env.example` dosyasını `.env` adıyla kopyalayın ve şifreleri değiştirin. `.env` dosyası repoya gönderilmez.

```bash
# macOS / Linux
cp .env.example .env
```

```powershell
# Windows (PowerShell)
Copy-Item .env.example .env
```

### 2. Veritabanı

MongoDB, Docker Compose ile tek düğümlü bir replica set olarak çalışır. Replica set, birden fazla belgeyi tek transaction içinde güncelleyebilmek için gereklidir.

```bash
docker compose up -d
docker compose ps    # STATUS sütununda "healthy" görünmeli
```

İlk çalıştırmada uygulama kullanıcısı (`DB_USER`) yalnızca `DB_NAME` veritabanına ve entegrasyon testlerinin kullandığı test veritabanına (`DB_NAME` + `_test`) erişim yetkisiyle otomatik oluşturulur.

> Bilgisayarınızda kurulu bir MongoDB servisi 27017 portunu kullanıyorsa `.env` içindeki `DB_PORT` değerini değiştirin (örneğin `27018`).

### 3. Ortam değişkenlerini kabuğa yükleme

Uygulama bağlantı bilgilerini ortam değişkenlerinden okur. Uygulamayı başlatacağınız terminalde:

```bash
# macOS / Linux
set -a; source .env; set +a
```

```powershell
# Windows (PowerShell)
Get-Content .env | Where-Object { $_ -match '^[A-Z_]+=' } | ForEach-Object { $k, $v = $_ -split '=', 2; Set-Item "env:$k" $v }
```

## Çalıştırma

Geliştirme aşamasında.

## API

Geliştirme aşamasında.

## Test

### Unit testler

```bash
mvn -B clean verify
```

Entegrasyon testleri (`*IT`) bu komutta atlanır; MongoDB gerekmez.

### Entegrasyon testleri

Entegrasyon testleri gerçek MongoDB replica set'ine karşı çalışır (transaction ve rollback senaryoları için gereklidir). MongoDB'yi başlatıp ortam değişkenlerini yükledikten sonra (bkz. Kurulum):

```bash
mvn -B verify -DskipITs=false
```

Testler uygulamanın veritabanını değil, ayrı bir test veritabanını (`DB_NAME` + `_test`, örneğin `movieanalyser_test`) kullanır. Her test kendi verisini oluşturup sonunda siler, yarıda kalan bir test de uygulama verisinde iz bırakmaz.

**Test veritabanı eklenmeden önce kurulmuş ortamlar için (tek seferlik):** Uygulama kullanıcısının test veritabanı yetkisi, MongoDB volume'u ilk oluşturulurken verilir. Daha önce `docker compose up` çalıştırdıysanız aşağıdaki iki komutu bir kez çalıştırın (Windows PowerShell ve macOS / Linux'ta aynı):

```bash
docker compose cp docker/mongo/grant-test-db.js mongodb:/tmp/grant-test-db.js
docker compose exec mongodb sh -c 'mongosh --quiet -u "$MONGO_INITDB_ROOT_USERNAME" -p "$MONGO_INITDB_ROOT_PASSWORD" --authenticationDatabase admin --file /tmp/grant-test-db.js'
```

Yerel veriyi korumanız gerekmiyorsa `docker compose down -v` ve ardından `docker compose up -d` ile veritabanını sıfırdan kurmak da aynı sonucu verir.

## Lisans ve Ders Bilgisi

Bu proje, Java Kurumsal Proje Geliştirme dersi kapsamında dönem projesi olarak geliştirilmektedir. Eğitim amaçlıdır.
