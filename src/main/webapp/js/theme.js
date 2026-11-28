// Açık/koyu tema düğmesi ve fareyi takip eden arka plan ışığı.
// Saf fonksiyonlar Node testlerinde de kullanılır; DOM kısmı yalnızca tarayıcıda çalışır.
(function (root) {
  'use strict';

  const STORAGE_KEY = 'movie-analyser-theme';

  // Işığın renk tonu (HSL hue) fare soldan sağa giderken bu aralıkta kayar
  const GLOW_HUES = {
    light: { from: 14, to: -20 },   // turuncudan pembeye (kısa yoldan, 0'ın altına iner)
    dark: { from: 265, to: 200 }    // mordan maviye
  };

  /** Kayıtlı seçim geçerliyse onu, değilse sistemin tercihini kullanır. */
  function resolveInitialTheme(stored, prefersDark) {
    if (stored === 'light' || stored === 'dark') {
      return stored;
    }
    return prefersDark ? 'dark' : 'light';
  }

  function nextTheme(current) {
    return current === 'dark' ? 'light' : 'dark';
  }

  /** 0 (sol) ile 1 (sağ) arasındaki konum için temanın aralığında bir renk tonu. */
  function glowHue(xRatio, theme) {
    const range = GLOW_HUES[theme] || GLOW_HUES.light;
    const t = Math.min(1, Math.max(0, xRatio));
    const hue = Math.round(range.from + (range.to - range.from) * t);
    // Eksi tonlar çemberde karşılığına çevrilir (-20 -> 340)
    return ((hue % 360) + 360) % 360;
  }

  /** Fare konumunu ekranın oranına çevirir; boyut yoksa ortayı döner. */
  function pointerRatio(x, y, width, height) {
    if (!width || !height) {
      return { x: 0.5, y: 0.5 };
    }
    return { x: x / width, y: y / height };
  }

  const api = {
    STORAGE_KEY: STORAGE_KEY,
    GLOW_HUES: GLOW_HUES,
    resolveInitialTheme: resolveInitialTheme,
    nextTheme: nextTheme,
    glowHue: glowHue,
    pointerRatio: pointerRatio
  };

  if (typeof module !== 'undefined' && module.exports) {
    module.exports = api;
    return;
  }
  root.Theme = api;

  // ---- Tarayıcı kısmı ----
  const doc = root.document;
  const html = doc.documentElement;

  function readStored() {
    // Gizli pencere gibi durumlarda localStorage erişimi hata verebilir
    try {
      return root.localStorage.getItem(STORAGE_KEY);
    } catch (e) {
      return null;
    }
  }

  function store(theme) {
    try {
      root.localStorage.setItem(STORAGE_KEY, theme);
    } catch (e) {
      // kaydedilemezse yalnızca bu sayfa için geçerli olur
    }
  }

  function currentTheme() {
    return html.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
  }

  function applyTheme(theme) {
    html.setAttribute('data-theme', theme);
    const toggle = doc.getElementById('theme-toggle');
    if (toggle) {
      const dark = theme === 'dark';
      toggle.setAttribute('aria-pressed', String(dark));
      toggle.setAttribute('aria-label', dark ? 'Açık temaya geç' : 'Koyu temaya geç');
      toggle.title = toggle.getAttribute('aria-label');
    }
    setGlow(lastRatio.x, lastRatio.y);
  }

  let lastRatio = { x: 0.7, y: 0.35 };

  function setGlow(xRatio, yRatio) {
    lastRatio = { x: xRatio, y: yRatio };
    html.style.setProperty('--glow-x', (xRatio * 100).toFixed(2) + '%');
    html.style.setProperty('--glow-y', (yRatio * 100).toFixed(2) + '%');
    html.style.setProperty('--glow-hue', String(glowHue(xRatio, currentTheme())));
  }

  // Tema, sayfa çizilmeden önce uygulanır ki yanlış renk bir an görünmesin
  const prefersDark = root.matchMedia && root.matchMedia('(prefers-color-scheme: dark)').matches;
  applyTheme(resolveInitialTheme(readStored(), prefersDark));

  function bindToggle() {
    const toggle = doc.getElementById('theme-toggle');
    if (!toggle) {
      return;
    }
    applyTheme(currentTheme());
    toggle.addEventListener('click', function () {
      const theme = nextTheme(currentTheme());
      store(theme);
      applyTheme(theme);
    });
  }

  function bindGlow() {
    const reduceMotion = root.matchMedia && root.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reduceMotion) {
      return;
    }
    let pending = null;
    // Her fare olayında değil, ekran yenilenirken bir kez güncellenir
    root.addEventListener('pointermove', function (event) {
      if (event.pointerType !== 'mouse') {
        return;
      }
      pending = pointerRatio(event.clientX, event.clientY, root.innerWidth, root.innerHeight);
      root.requestAnimationFrame(function () {
        if (pending) {
          setGlow(pending.x, pending.y);
          pending = null;
        }
      });
    }, { passive: true });
  }

  doc.addEventListener('DOMContentLoaded', function () {
    bindToggle();
    bindGlow();
  });
}(typeof window !== 'undefined' ? window : this));
