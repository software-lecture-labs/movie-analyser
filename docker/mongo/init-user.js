// Veri birimi ilk kez oluşturulduğunda yalnızca bir kez çalışır.
// Yalnızca uygulama veritabanına erişimi olan uygulama kullanıcısını oluşturur.
const dbName = process.env.MONGO_INITDB_DATABASE;

db.getSiblingDB(dbName).createUser({
  user: process.env.DB_USER,
  pwd: process.env.DB_PASSWORD,
  roles: [{ role: 'readWrite', db: dbName }]
});
