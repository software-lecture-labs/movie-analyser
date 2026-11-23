// Runs once, when the data volume is created for the first time.
// Creates the application user with access to the application database only.
const dbName = process.env.MONGO_INITDB_DATABASE;

db.getSiblingDB(dbName).createUser({
  user: process.env.DB_USER,
  pwd: process.env.DB_PASSWORD,
  roles: [{ role: 'readWrite', db: dbName }]
});
