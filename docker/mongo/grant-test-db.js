// Mevcut bir MongoDB volume'unda uygulama kullanıcısına test veritabanı yetkisini verir.
// init-user.js yalnızca volume ilk oluşturulurken çalıştığı için, test veritabanı
// eklenmeden önce kurulmuş ortamlarda bir kez çalıştırılır (bkz. README).
const dbName = process.env.MONGO_INITDB_DATABASE;

db.getSiblingDB(dbName).grantRolesToUser(process.env.DB_USER, [
  { role: 'readWrite', db: dbName + '_test' }
]);
print('readWrite yetkisi verildi: ' + dbName + '_test');
