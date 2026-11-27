// Veri birimi ilk kez oluşturulduğunda yalnızca bir kez çalışır.
// Uygulama kullanıcısı yalnızca uygulama veritabanında yetkilidir.
// Entegrasyon testleri için ayrı bir test kullanıcısı, yalnızca test veritabanında (<veritabanı>_test) yetkilidir.
const dbName = process.env.MONGO_INITDB_DATABASE;
const testDbName = dbName + '_test';

db.getSiblingDB(dbName).createUser({
  user: process.env.DB_USER,
  pwd: process.env.DB_PASSWORD,
  roles: [{ role: 'readWrite', db: dbName }]
});

// Sunucu ortamında DB_TEST_USER boş bırakılır ve test kullanıcısı oluşturulmaz
if (process.env.DB_TEST_USER) {
  db.getSiblingDB(testDbName).createUser({
    user: process.env.DB_TEST_USER,
    pwd: process.env.DB_TEST_PASSWORD,
    roles: [{ role: 'readWrite', db: testDbName }]
  });
}
