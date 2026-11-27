// Tema (açık/koyu) ve fareyi takip eden ışığın DOM'dan bağımsız mantığının testleri.
// Çalıştırma: node --test src/test/js/theme.test.cjs
const test = require('node:test');
const assert = require('node:assert/strict');
const theme = require('../../main/webapp/js/theme.js');

test('kayıtlı seçim varsa o kullanılır', () => {
  assert.equal(theme.resolveInitialTheme('dark', false), 'dark');
  assert.equal(theme.resolveInitialTheme('light', true), 'light');
});

test('kayıtlı seçim yoksa sistem ayarına uyulur', () => {
  assert.equal(theme.resolveInitialTheme(null, true), 'dark');
  assert.equal(theme.resolveInitialTheme(null, false), 'light');
});

test('bozuk kayıtlı değer yok sayılır', () => {
  assert.equal(theme.resolveInitialTheme('mor', true), 'dark');
  assert.equal(theme.resolveInitialTheme('', false), 'light');
});

test('düğme temayı tersine çevirir', () => {
  assert.equal(theme.nextTheme('light'), 'dark');
  assert.equal(theme.nextTheme('dark'), 'light');
});

test('ışığın tonu fare yatayda ilerledikçe tema aralığında kayar', () => {
  const light = theme.GLOW_HUES.light;
  assert.equal(theme.glowHue(0, 'light'), light.from);
  assert.equal(theme.glowHue(1, 'light'), light.to + 360);
  assert.equal(theme.glowHue(0.5, 'light'), (Math.round((light.from + light.to) / 2) + 360) % 360);

  const dark = theme.GLOW_HUES.dark;
  assert.equal(theme.glowHue(0, 'dark'), dark.from);
  assert.equal(theme.glowHue(1, 'dark'), dark.to);
});

test('açık temada ışık turuncudan pembeye kısa yoldan geçer, sarı/yeşil/maviye uğramaz', () => {
  for (let i = 0; i <= 20; i++) {
    const hue = theme.glowHue(i / 20, 'light');
    assert.ok(hue >= 0 && hue < 360, `ton 0-359 aralığında olmalı: ${hue}`);
    assert.ok(hue <= 20 || hue >= 330, `sıcak tonların dışına çıktı: ${hue}`);
  }
});

test('ekran dışı konumlar aralığın içine sıkıştırılır', () => {
  assert.equal(theme.glowHue(-0.3, 'light'), theme.GLOW_HUES.light.from);
  assert.equal(theme.glowHue(1.7, 'light'), theme.GLOW_HUES.light.to + 360);
  assert.equal(theme.glowHue(1.7, 'dark'), theme.GLOW_HUES.dark.to);
});

test('fare konumu ekran oranına çevrilir', () => {
  assert.deepEqual(theme.pointerRatio(200, 150, 800, 600), { x: 0.25, y: 0.25 });
  // Genişliği sıfır bir pencerede bölme hatası olmaz, ışık ortada kalır
  assert.deepEqual(theme.pointerRatio(10, 10, 0, 0), { x: 0.5, y: 0.5 });
});
