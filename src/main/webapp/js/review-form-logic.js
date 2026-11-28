// Puan/yorum formunun DOM'dan bağımsız mantığı: doğrulama, istek gövdesi,
// ortalamanın gösterimi ve hata mesajları. Tarayıcıda window.ReviewFormLogic olarak,
// testlerde (Node) require ile kullanılır.
(function (root) {
  'use strict';

  const MIN_RATING = 1;
  const MAX_RATING = 10;
  const MAX_COMMENT_LENGTH = 1000;
  // Sunucudaki id'ler MongoDB ObjectId biçiminde: 24 onaltılık karakter
  const ID_PATTERN = /^[0-9a-f]{24}$/i;

  /** Formdaki değerleri kontrol eder; alan adı -> mesaj eşlemesi döner (boşsa geçerli). */
  function validate(input) {
    const errors = {};
    const userId = (input.userId || '').trim();
    if (!ID_PATTERN.test(userId)) {
      errors.userId = 'Kullanıcı id 24 karakterlik onaltılık bir değer olmalı';
    }
    if (!input.movieId) {
      errors.movieId = 'Bir film seçin';
    }
    const rating = Number(input.rating);
    if (input.rating === null || input.rating === undefined || input.rating === ''
        || !Number.isInteger(rating) || rating < MIN_RATING || rating > MAX_RATING) {
      errors.rating = 'Puan ' + MIN_RATING + ' ile ' + MAX_RATING + ' arasında olmalı';
    }
    if ((input.comment || '').length > MAX_COMMENT_LENGTH) {
      errors.comment = 'Yorum en fazla ' + MAX_COMMENT_LENGTH + ' karakter olabilir';
    }
    return errors;
  }

  /** API'ye gidecek gövde; boş yorum hiç gönderilmez. */
  function buildPayload(input) {
    const comment = (input.comment || '').trim();
    return {
      userId: (input.userId || '').trim(),
      movieId: input.movieId,
      rating: Number(input.rating),
      comment: comment === '' ? null : comment
    };
  }

  /** Ortalamayı Türkçe ondalıkla (virgül) ve değerlendirme sayısıyla gösterir. */
  function formatAverage(average, count) {
    if (!count) {
      return { score: '-', detail: 'Henüz puan yok' };
    }
    return { score: average.toFixed(1).replace('.', ','), detail: count + ' değerlendirme' };
  }

  /** Sunucunun hata gövdesindeki mesajı; yoksa duruma göre anlaşılır bir metin. */
  function describeError(status, body) {
    if (body && body.message) {
      return body.message;
    }
    if (!status) {
      return 'Sunucuya ulaşılamadı, bağlantınızı kontrol edin';
    }
    return 'Beklenmeyen bir hata oluştu (' + status + ')';
  }

  /** Kullanıcının yorum sayfasında bu filme ait yorumu bulur (yoksa null). */
  function findReviewForMovie(page, movieId) {
    if (!page || !Array.isArray(page.content)) {
      return null;
    }
    return page.content.find(function (review) { return review.movieId === movieId; }) || null;
  }

  const api = {
    MAX_COMMENT_LENGTH: MAX_COMMENT_LENGTH,
    validate: validate,
    buildPayload: buildPayload,
    formatAverage: formatAverage,
    describeError: describeError,
    findReviewForMovie: findReviewForMovie
  };

  if (typeof module !== 'undefined' && module.exports) {
    module.exports = api;
  } else {
    root.ReviewFormLogic = api;
  }
}(typeof window !== 'undefined' ? window : this));
