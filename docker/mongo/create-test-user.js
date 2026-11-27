// Daha önce kurulmuş bir MongoDB volume'unda test kullanıcısını oluşturur ve
// uygulama kullanıcısına eskiden verilmiş test veritabanı yetkisini geri alır.
// init-user.js yalnızca volume ilk oluşturulurken çalıştığı için bir kez çalıştırılır (bkz. README).
const dbName = process.env.MONGO_INITDB_DATABASE;
const testDbName = dbName + '_test';
const testUser = process.env.DB_TEST_USER;

if (!testUser) {
  throw new Error('DB_TEST_USER tanımlı değil: .env dosyasına ekleyip "docker compose up -d" çalıştırın');
}

// Uygulama kullanıcısının test veritabanında yetkisi kalmasın
const appDb = db.getSiblingDB(dbName);
const appUser = appDb.getUser(process.env.DB_USER);
if (appUser && appUser.roles.some(r => r.db === testDbName)) {
  appDb.revokeRolesFromUser(process.env.DB_USER, [{ role: 'readWrite', db: testDbName }]);
  print('Uygulama kullanıcısından test veritabanı yetkisi geri alındı');
}

const testDb = db.getSiblingDB(testDbName);
if (testDb.getUser(testUser)) {
  testDb.updateUser(testUser, { pwd: process.env.DB_TEST_PASSWORD, roles: [{ role: 'readWrite', db: testDbName }] });
  print('Test kullanıcısı güncellendi: ' + testUser);
} else {
  testDb.createUser({ user: testUser, pwd: process.env.DB_TEST_PASSWORD, roles: [{ role: 'readWrite', db: testDbName }] });
  print('Test kullanıcısı oluşturuldu: ' + testUser);
}
