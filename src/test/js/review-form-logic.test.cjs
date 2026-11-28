// Puan/yorum formunun DOM'dan bağımsız mantığının testleri.
// Çalıştırma: node --test src/test/js/
const test = require('node:test');
const assert = require('node:assert/strict');
const logic = require('../../main/webapp/js/review-form-logic.js');

const USER_ID = '6b0a0000000000000000a001';
const MOVIE_ID = '6b0a0000000000000000b001';

test('geçerli girişte hata yok', () => {
  assert.deepEqual(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: 8, comment: 'Güzel' }), {});
});

test('kullanıcı id biçimi kontrol edilir', () => {
  assert.equal(logic.validate({ userId: 'abc', movieId: MOVIE_ID, rating: 8 }).userId,
    'Kullanıcı id 24 karakterlik onaltılık bir değer olmalı');
  assert.ok(logic.validate({ userId: '', movieId: MOVIE_ID, rating: 8 }).userId);
});

test('film seçilmeden gönderilemez', () => {
  assert.equal(logic.validate({ userId: USER_ID, movieId: '', rating: 8 }).movieId, 'Bir film seçin');
});

test('puan 1 ile 10 arasında bir tam sayı olmalı', () => {
  assert.ok(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: 0 }).rating);
  assert.ok(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: 11 }).rating);
  assert.ok(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: null }).rating);
  assert.equal(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: 1 }).rating, undefined);
  assert.equal(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: 10 }).rating, undefined);
});

test('yorum en fazla 1000 karakter', () => {
  assert.equal(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: 5, comment: 'a'.repeat(1000) }).comment,
    undefined);
  assert.equal(logic.validate({ userId: USER_ID, movieId: MOVIE_ID, rating: 5, comment: 'a'.repeat(1001) }).comment,
    'Yorum en fazla 1000 karakter olabilir');
});

test('istek gövdesi: yorum kırpılır, boş yorum gönderilmez', () => {
  assert.deepEqual(logic.buildPayload({ userId: ` ${USER_ID} `, movieId: MOVIE_ID, rating: '7', comment: '  süper ' }),
    { userId: USER_ID, movieId: MOVIE_ID, rating: 7, comment: 'süper' });
  assert.equal(logic.buildPayload({ userId: USER_ID, movieId: MOVIE_ID, rating: 7, comment: '   ' }).comment, null);
});

test('ortalama Türkçe biçimde gösterilir', () => {
  assert.deepEqual(logic.formatAverage(0, 0), { score: '-', detail: 'Henüz puan yok' });
  assert.deepEqual(logic.formatAverage(7, 2), { score: '7,0', detail: '2 değerlendirme' });
  assert.deepEqual(logic.formatAverage(8.333333, 3), { score: '8,3', detail: '3 değerlendirme' });
});

test('hata mesajı sunucunun gövdesinden alınır', () => {
  assert.equal(logic.describeError(409, { message: 'Bu kullanıcı bu filme zaten yorum yapmış' }),
    'Bu kullanıcı bu filme zaten yorum yapmış');
});

test('gövdesiz hata ve bağlantı hatası için anlaşılır mesaj', () => {
  assert.equal(logic.describeError(0, null), 'Sunucuya ulaşılamadı, bağlantınızı kontrol edin');
  assert.equal(logic.describeError(500, null), 'Beklenmeyen bir hata oluştu (500)');
});

test('kullanıcının bu filme yaptığı yorum listeden bulunur', () => {
  const page = { content: [{ id: 'r1', movieId: 'x' }, { id: 'r2', movieId: MOVIE_ID }] };
  assert.equal(logic.findReviewForMovie(page, MOVIE_ID).id, 'r2');
  assert.equal(logic.findReviewForMovie(page, 'yok'), null);
  assert.equal(logic.findReviewForMovie(null, MOVIE_ID), null);
});
