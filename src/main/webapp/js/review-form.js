// Puan/yorum sayfası: filmleri yükler, formu gönderir ve filmin ortalamasını
// sayfa yenilenmeden günceller. Kurallar review-form-logic.js'te.
(function () {
  'use strict';

  const logic = window.ReviewFormLogic;
  // Sayfa /<uygulama>/review.html altında; API aynı uygulamanın /api yolunda
  const API = 'api';
  const RECENT_REVIEW_COUNT = 5;

  const el = {
    movieSelect: document.getElementById('movie-select'),
    movieError: document.getElementById('movie-error'),
    summary: document.getElementById('movie-summary'),
    movieTitle: document.getElementById('movie-title'),
    movieMeta: document.getElementById('movie-meta'),
    scoreBox: document.getElementById('score-box'),
    scoreValue: document.getElementById('score-value'),
    scoreDetail: document.getElementById('score-detail'),
    form: document.getElementById('review-form'),
    userId: document.getElementById('user-id'),
    ratingOptions: document.getElementById('rating-options'),
    comment: document.getElementById('comment'),
    counter: document.getElementById('comment-counter'),
    submit: document.getElementById('submit-button'),
    update: document.getElementById('update-button'),
    message: document.getElementById('form-message'),
    reviewList: document.getElementById('review-list'),
    reviewsEmpty: document.getElementById('reviews-empty')
  };

  /** JSON isteği gönderir; başarısız yanıtta {status, body} ile hata fırlatır. */
  async function request(method, path, body) {
    let response;
    try {
      response = await fetch(API + path, {
        method: method,
        headers: body ? { 'Content-Type': 'application/json' } : {},
        body: body ? JSON.stringify(body) : undefined
      });
    } catch (networkError) {
      throw { status: 0, body: null };
    }
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    if (!response.ok) {
      throw { status: response.status, body: data };
    }
    return data;
  }

  function showMessage(text, kind) {
    el.message.textContent = text;
    el.message.className = 'message ' + (kind || '');
  }

  function showFieldErrors(errors) {
    ['userId', 'rating', 'comment'].forEach(function (field) {
      document.getElementById(field + '-error').textContent = errors[field] || '';
    });
    el.movieError.textContent = errors.movieId || '';
  }

  function buildRatingOptions() {
    for (let value = 1; value <= 10; value++) {
      const id = 'rating-' + value;
      const input = document.createElement('input');
      input.type = 'radio';
      input.name = 'rating';
      input.id = id;
      input.value = String(value);
      const label = document.createElement('label');
      label.htmlFor = id;
      label.textContent = String(value);
      el.ratingOptions.append(input, label);
    }
  }

  function selectedRating() {
    const checked = el.form.querySelector('input[name="rating"]:checked');
    return checked ? checked.value : null;
  }

  function readForm() {
    return {
      userId: el.userId.value,
      movieId: el.movieSelect.value,
      rating: selectedRating(),
      comment: el.comment.value
    };
  }

  async function loadMovies() {
    try {
      const page = await request('GET', '/movies?size=100&sort=title');
      el.movieSelect.textContent = '';
      el.movieSelect.append(new Option(page.content.length ? 'Film seçin' : 'Henüz film yok', ''));
      page.content.forEach(function (movie) {
        el.movieSelect.append(new Option(movie.title + ' (' + movie.releaseYear + ')', movie.id));
      });
    } catch (error) {
      el.movieError.textContent = logic.describeError(error.status, error.body);
    }
  }

  /** Filmin özetini gösterir; changed ise ortalama kutusu kısa bir vurguyla yenilenir. */
  function renderMovie(movie, changed) {
    el.summary.hidden = false;
    el.movieTitle.textContent = movie.title;
    el.movieMeta.textContent = movie.director + ' · ' + movie.releaseYear;
    const average = logic.formatAverage(movie.averageRating, movie.ratingCount);
    el.scoreValue.textContent = average.score;
    el.scoreDetail.textContent = average.detail;
    if (changed) {
      el.scoreBox.classList.remove('updated');
      // Animasyonun yeniden başlaması için yeniden çizim tetiklenir
      void el.scoreBox.offsetWidth;
      el.scoreBox.classList.add('updated');
    }
  }

  function renderReviews(page) {
    el.reviewList.textContent = '';
    page.content.forEach(function (review) {
      const item = document.createElement('li');
      const head = document.createElement('div');
      head.className = 'review-head';
      const rating = document.createElement('span');
      rating.className = 'review-rating';
      rating.textContent = review.rating + ' / 10';
      const date = document.createElement('span');
      date.textContent = new Date(review.updatedAt).toLocaleString('tr-TR');
      head.append(rating, date);
      item.append(head);
      if (review.comment) {
        // textContent: yorum metni HTML olarak yorumlanmaz
        const text = document.createElement('p');
        text.className = 'review-comment';
        text.textContent = review.comment;
        item.append(text);
      }
      el.reviewList.append(item);
    });
    el.reviewsEmpty.hidden = page.content.length > 0;
  }

  async function refreshMovie(movieId, changed) {
    const movie = await request('GET', '/movies/' + movieId);
    renderMovie(movie, changed);
    const reviews = await request('GET', '/movies/' + movieId + '/reviews?size=' + RECENT_REVIEW_COUNT);
    renderReviews(reviews);
  }

  async function onMovieChange() {
    el.update.hidden = true;
    showMessage('');
    el.movieError.textContent = '';
    if (!el.movieSelect.value) {
      el.summary.hidden = true;
      el.reviewList.textContent = '';
      el.reviewsEmpty.hidden = false;
      return;
    }
    try {
      await refreshMovie(el.movieSelect.value, false);
    } catch (error) {
      el.movieError.textContent = logic.describeError(error.status, error.body);
    }
  }

  function setBusy(busy) {
    el.submit.disabled = busy;
    el.update.disabled = busy;
  }

  async function onSubmit(event) {
    event.preventDefault();
    el.update.hidden = true;
    const input = readForm();
    const errors = logic.validate(input);
    showFieldErrors(errors);
    if (Object.keys(errors).length) {
      showMessage('Lütfen işaretli alanları düzeltin.', 'error');
      return;
    }
    setBusy(true);
    try {
      await request('POST', '/reviews', logic.buildPayload(input));
      showMessage('Yorumunuz kaydedildi.', 'success');
      await refreshMovie(input.movieId, true);
    } catch (error) {
      showMessage(logic.describeError(error.status, error.body), 'error');
      // Aynı filme ikinci yorum: mevcut yorumu güncelleme seçeneği sunulur
      el.update.hidden = error.status !== 409;
    } finally {
      setBusy(false);
    }
  }

  async function onUpdate() {
    const input = readForm();
    const errors = logic.validate(input);
    showFieldErrors(errors);
    if (Object.keys(errors).length) {
      return;
    }
    setBusy(true);
    try {
      const payload = logic.buildPayload(input);
      const mine = await request('GET', '/users/' + payload.userId + '/reviews?size=100');
      const existing = logic.findReviewForMovie(mine, payload.movieId);
      if (!existing) {
        showMessage('Bu filme ait yorumunuz bulunamadı.', 'error');
        return;
      }
      await request('PUT', '/reviews/' + existing.id, { rating: payload.rating, comment: payload.comment });
      el.update.hidden = true;
      showMessage('Yorumunuz güncellendi.', 'success');
      await refreshMovie(payload.movieId, true);
    } catch (error) {
      showMessage(logic.describeError(error.status, error.body), 'error');
    } finally {
      setBusy(false);
    }
  }

  function updateCounter() {
    el.counter.textContent = el.comment.value.length + ' / ' + logic.MAX_COMMENT_LENGTH;
  }

  buildRatingOptions();
  el.movieSelect.addEventListener('change', onMovieChange);
  el.form.addEventListener('submit', onSubmit);
  el.update.addEventListener('click', onUpdate);
  el.comment.addEventListener('input', updateCounter);
  loadMovies();
}());
