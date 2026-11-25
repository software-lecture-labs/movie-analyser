// Runs once, when the data volume is created for the first time.
// Uygulama kullanıcısını oluşturur; yalnızca uygulama veritabanında ve test veritabanında yetkisi vardır.
// Test veritabanı (<veritabanı>_test) entegrasyon testleri içindir; uygulama verisine dokunmazlar.
const dbName = process.env.MONGO_INITDB_DATABASE;

db.getSiblingDB(dbName).createUser({
  user: process.env.DB_USER,
  pwd: process.env.DB_PASSWORD,
  roles: [
    { role: 'readWrite', db: dbName },
    { role: 'readWrite', db: dbName + '_test' }
  ]
});
