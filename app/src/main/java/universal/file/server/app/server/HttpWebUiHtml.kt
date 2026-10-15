package universal.file.server.app.server

import universal.file.server.app.data.FtpSettings

object HttpWebUiHtml {

    const val APP_FAVICON_SVG: String = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1254 1254" width="100%" height="100%">
  <g fill="#02f5d2">
    <!-- فریم گوشی با برش هماهنگ (فاصله‌ی ثابت 30 واحد دور آیکون FTP) -->
    <path id="phone-frame" d="M 404 868.6 L 404 1098 C 404 1125.61 426.39 1148 454 1148 L 930 1148 C 957.61 1148 980 1125.61 980 1098 L 980 154 C 980 126.39 957.61 104 930 104 L 454 104 C 426.39 104 404 126.39 404 154 L 404 343 L 360 343 L 360 152 C 360 100.08 402.08 58 454 58 L 930 58 C 981.92 58 1024 100.08 1024 152 L 1024 1100 C 1024 1151.92 981.92 1194 930 1194 L 454 1194 C 402.08 1194 360 1151.92 360 1100 L 360 868.6 Z" />

    <!-- شیار اسپیکر بالای گوشی -->
    <path id="phone-notch" d="M 601 133 L 766 133 C 778.15 133 788 142.85 788 155 C 788 167.15 778.15 177 766 177 L 601 177 C 588.85 177 579 167.15 579 155 C 579 142.85 588.85 133 601 133 Z" />

    <!-- پوشه‌ی FTP (بدون متن) -->
    <g id="ftp-icon" transform="translate(540 625) scale(1.2) translate(-540 -625)">
    <!-- پوشه‌ی FTP: کاملاً پُر (بدنه + زائده‌ی بالا)؛ شیار کوچک داخل آن شفاف است -->
    <path id="ftp-folder" fill-rule="evenodd" d="M 384 415 L 490 415 A 16 16 0 0 1 501.31 419.69 L 536.62 455 L 696 455 A 54 54 0 0 1 750 509 L 750 636 A 54 54 0 0 1 696 690 L 384 690 A 54 54 0 0 1 330 636 L 330 469 A 54 54 0 0 1 384 415 Z M 392 466 L 484 466 A 14 14 0 0 1 484 494 L 392 494 A 14 14 0 0 1 392 466 Z" />

    <!-- شبکه‌ی زیر پوشه: ساقه، گره مربعی، خط و دو دایره‌ی انتهایی -->
    <path id="ftp-links" d="M 522 685 L 558 685 L 558 740 L 522 740 Z M 300 767 L 490 767 L 490 803 L 300 803 Z M 590 767 L 780 767 L 780 803 L 590 803 Z" />
    <path id="ftp-nodes" fill-rule="evenodd" d="M 514 735 L 566 735 A 24 24 0 0 1 590 759 L 590 811 A 24 24 0 0 1 566 835 L 514 835 A 24 24 0 0 1 490 811 L 490 759 A 24 24 0 0 1 514 735 Z M 518 763 L 562 763 L 562 807 L 518 807 Z M 216 785 A 46 46 0 1 0 308 785 A 46 46 0 1 0 216 785 Z M 244 785 A 18 18 0 1 1 280 785 A 18 18 0 1 1 244 785 Z M 772 785 A 46 46 0 1 0 864 785 A 46 46 0 1 0 772 785 Z M 800 785 A 18 18 0 1 1 836 785 A 18 18 0 1 1 800 785 Z" />
    </g>
  </g>
</svg>"""

    fun buildWebPortalHtml(settings: FtpSettings): String {
        val initialTheme = if (settings.themeMode.name == "LIGHT") "light" else "dark"
        return """
<!DOCTYPE html>
<html lang="en" data-theme="$initialTheme">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Universal File Server — Web Portal</title>
<link rel="icon" type="image/svg+xml" href="/favicon.svg">
<link rel="alternate icon" href="/favicon.ico">
<link rel="apple-touch-icon" href="/favicon.svg">
<script>
(function() {
  try {
    var saved = null;
    var target = 'ufs_theme=';
    var parts = (document.cookie || '').split(';');
    for (var i = 0; i < parts.length; i++) {
      var p = parts[i].trim();
      if (p.indexOf(target) === 0) {
        saved = decodeURIComponent(p.substring(target.length));
        break;
      }
    }
    if (!saved) {
      try { saved = localStorage.getItem('ufs_theme'); } catch (e) {}
    }
    if (saved !== 'light' && saved !== 'dark') {
      if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
        saved = 'light';
      } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        saved = 'dark';
      } else {
        saved = document.documentElement.getAttribute('data-theme') === 'light' ? 'light' : 'dark';
      }
      try {
        document.cookie = 'ufs_theme=' + encodeURIComponent(saved) + '; path=/; max-age=31536000; SameSite=Lax';
      } catch (e) {}
      try {
        localStorage.setItem('ufs_theme', saved);
      } catch (e) {}
    }
    document.documentElement.setAttribute('data-theme', saved);
  } catch (e) {}
})();
</script>
<style>
  :root[data-theme="dark"] {
    --bg: #0b1120;
    --surface: #151f32;
    --surface-hover: #1e2d47;
    --surface-selected: rgba(34, 211, 238, 0.14);
    --surface-alt: #111827;
    --border: #263552;
    --text: #f1f5f9;
    --text-muted: #94a3b8;
    --primary: #22d3ee;
    --primary-bg: rgba(34, 211, 238, 0.14);
    --emerald: #34d399;
    --emerald-bg: rgba(52, 211, 153, 0.14);
    --amber: #fbbf24;
    --amber-bg: rgba(251, 191, 36, 0.14);
    --danger: #f87171;
    --danger-bg: rgba(248, 113, 113, 0.14);
    --shadow: 0 10px 30px rgba(0, 0, 0, 0.45);
  }
  :root[data-theme="light"] {
    --bg: #f1f5f9;
    --surface: #ffffff;
    --surface-hover: #f8fafc;
    --surface-selected: rgba(2, 132, 199, 0.10);
    --surface-alt: #f8fafc;
    --border: #e2e8f0;
    --text: #0f172a;
    --text-muted: #64748b;
    --primary: #0284c7;
    --primary-bg: rgba(2, 132, 199, 0.1);
    --emerald: #059669;
    --emerald-bg: rgba(5, 150, 105, 0.1);
    --amber: #d97706;
    --amber-bg: rgba(217, 119, 6, 0.1);
    --danger: #dc2626;
    --danger-bg: rgba(220, 38, 38, 0.1);
    --shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
  }
  * { box-sizing: border-box; margin: 0; padding: 0; }
  html, body {
    width: 100%;
    min-height: 100vh;
  }
  body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Inter, sans-serif;
    background: var(--bg);
    color: var(--text);
    display: flex;
    flex-direction: column;
    transition: background 0.2s, color 0.2s;
  }
  .container {
    width: 100%;
    max-width: 100%;
    min-height: 100vh;
    margin: 0;
    padding: 12px 18px 16px 18px;
    display: flex;
    flex-direction: column;
    flex: 1;
  }
  /* Header */
  .header {
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    align-items: center;
    gap: 16px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 18px;
    padding: 8px 14px;
    box-shadow: var(--shadow);
    margin-bottom: 14px;
  }
  .brand {
    display: flex;
    align-items: center;
    gap: 10px;
    justify-self: start;
  }
  .brand-text {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 5px;
    text-align: center;
  }
  .brand-icon {
    width: 56px;
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .brand-icon svg {
    width: 100%;
    height: 100%;
    display: block;
  }
  .brand-title {
    font-size: 18px;
    font-weight: 700;
    line-height: 1.2;
    letter-spacing: -0.01em;
    white-space: nowrap;
  }
  .brand-sub {
    font-size: 12px;
    color: var(--text-muted);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    flex-wrap: wrap;
  }
  .header-center {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    justify-self: center;
    width: 260px;
    max-width: 100%;
  }
  .header-storage-badges {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    flex-wrap: wrap;
    text-align: center;
  }
  .header-actions {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
    flex-wrap: nowrap;
    justify-self: end;
  }
  .header-actions .btn {
    white-space: nowrap;
  }
  .badge {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    padding: 3px 9px;
    border-radius: 999px;
    font-size: 11px;
    font-weight: 600;
  }
  .badge-rw { background: var(--emerald-bg); color: var(--emerald); }
  .badge-ro { background: var(--amber-bg); color: var(--amber); }
  .badge-http { background: var(--primary-bg); color: var(--primary); }
  .badge-storage { background: var(--primary-bg); color: var(--primary); }
  /* Live Connection Status Pill */
  .live-conn-pill {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 11px;
    font-weight: 600;
    background: var(--emerald-bg);
    color: var(--emerald);
    border: 1px solid rgba(52, 211, 153, 0.32);
    transition: background 0.25s ease, color 0.25s ease, border-color 0.25s ease;
    user-select: none;
  }
  .live-conn-pill.offline {
    background: var(--danger-bg);
    color: var(--danger);
    border-color: rgba(248, 113, 113, 0.35);
  }
  .live-conn-dot-wrap {
    position: relative;
    width: 8px;
    height: 8px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }
  .live-conn-dot {
    width: 7px;
    height: 7px;
    border-radius: 50%;
    background: var(--emerald);
    position: relative;
    z-index: 2;
    transition: background 0.25s ease;
  }
  .live-conn-pill.offline .live-conn-dot {
    background: var(--danger);
  }
  .live-conn-dot-pulse {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    background: var(--emerald);
    opacity: 0.75;
    animation: liveConnPulse 1.8s infinite cubic-bezier(0.22, 0.61, 0.36, 1);
    z-index: 1;
  }
  .live-conn-pill.offline .live-conn-dot-pulse {
    display: none;
  }
  @keyframes liveConnPulse {
    0% { transform: scale(1); opacity: 0.75; }
    80%, 100% { transform: scale(2.5); opacity: 0; }
  }
  .live-conn-divider {
    opacity: 0.55;
    font-size: 10px;
  }
  .live-conn-count {
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
    font-weight: 700;
    display: inline-block;
    transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
  }
  .live-conn-count.bump {
    transform: scale(1.28);
  }
  .storage-box {
    display: flex;
    flex-direction: column;
    gap: 4px;
    width: 100%;
    min-width: 220px;
  }
  .storage-labels {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: var(--text-muted);
  }
  .progress-track {
    width: 100%;
    height: 7px;
    background: var(--border);
    border-radius: 999px;
    overflow: hidden;
  }
  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, var(--primary), var(--emerald));
    border-radius: 999px;
    transition: width 0.25s ease;
    animation: none;
    transform: none;
  }
  .progress-fill.indeterminate {
    width: 42% !important;
    animation: progressIndeterminate 1.15s infinite ease-in-out !important;
  }
  @keyframes progressIndeterminate {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(260%); }
  }
  /* Toolbar (3-Row Layout: 1. Nav Buttons + Address Bar, 2. Search + Scope, 3. Action Buttons) */
  .toolbar {
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 16px;
    padding: 12px 16px;
    margin-bottom: 12px;
  }
  .address-nav-row {
    display: flex;
    align-items: center;
    gap: 8px;
    width: 100%;
  }
  .nav-history-group {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    flex-shrink: 0;
  }
  .nav-history-btn {
    width: 38px;
    height: 38px;
    padding: 0;
    border-radius: 10px;
    font-size: 13px;
    flex-shrink: 0;
  }
  .address-bar-shell {
    flex: 1;
    min-width: 0;
    background: var(--surface-alt);
    border: 1.5px solid var(--border);
    border-radius: 11px;
    transition: border-color 0.15s, box-shadow 0.15s;
  }
  .address-bar-shell:focus-within {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px var(--primary-bg);
  }
  .address-bar-shell.shake {
    animation: modalShake 0.36s ease-in-out;
    border-color: var(--danger);
  }
  .breadcrumbs-view {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 6px;
    padding: 4px 8px;
    min-height: 38px;
  }
  .breadcrumbs {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 4px;
    font-size: 14px;
    font-weight: 600;
    flex: 1;
    min-width: 0;
    cursor: text;
    padding: 2px 0;
  }
  .crumb {
    padding: 4px 9px;
    border-radius: 7px;
    cursor: pointer;
    color: var(--primary);
    transition: all 0.15s;
    user-select: none;
    border: 1.5px solid transparent;
  }
  .crumb:hover { background: var(--primary-bg); }
  .crumb.active { color: var(--text); cursor: pointer; }
  .crumb.active:hover { background: var(--surface-hover); }
  .crumb-sep { color: var(--text-muted); font-size: 13px; user-select: none; }
  .address-bar-actions {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    flex-shrink: 0;
  }
  .address-action-btn {
    border: 1px solid var(--border);
    background: var(--surface);
    color: var(--text-muted);
    width: 30px;
    height: 30px;
    border-radius: 8px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 13px;
    transition: all 0.15s;
  }
  .address-action-btn:hover {
    color: var(--primary);
    border-color: var(--primary);
    background: var(--primary-bg);
  }
  .address-edit-view {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 4px 8px;
    min-height: 38px;
  }
  .address-path-input {
    flex: 1;
    min-width: 0;
    background: transparent;
    border: none;
    color: var(--text);
    font-family: monospace;
    font-size: 14px;
    font-weight: 600;
    outline: none;
    padding: 4px 6px;
  }
  /* Internal Drag & Drop Highlight Styles */
  tr.file-row.dragging-source,
  .grid-card.dragging-source {
    opacity: 0.45;
  }
  tr.file-row.drop-target-active td {
    background: var(--emerald-bg) !important;
    border-top: 1.5px dashed var(--emerald);
    border-bottom: 1.5px dashed var(--emerald);
  }
  .grid-card.drop-target-active {
    border-color: var(--emerald) !important;
    background: var(--emerald-bg) !important;
    box-shadow: 0 0 0 3px rgba(52, 211, 153, 0.25);
    transform: scale(1.02);
  }
  .crumb.drop-target-active {
    border-color: var(--emerald) !important;
    background: var(--emerald-bg) !important;
    color: var(--emerald) !important;
    transform: scale(1.05);
  }
  .toolbar-search-row {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;
    flex-wrap: nowrap;
  }
  .toolbar-actions-row {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    width: 100%;
    padding-top: 2px;
  }
  #writeActions {
    display: contents;
  }
  .toolbar-actions-row .btn {
    height: 36px;
    min-height: 36px;
    max-height: 36px;
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 7px;
    padding: 0 13px;
    font-size: 13px;
    font-weight: 600;
    line-height: 1;
    border-radius: 10px;
    white-space: nowrap;
    vertical-align: middle;
    flex-shrink: 0;
  }
  .btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 8px 13px;
    border-radius: 10px;
    border: 1px solid var(--border);
    background: var(--surface-alt);
    color: var(--text);
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.15s;
    text-decoration: none;
    white-space: nowrap;
  }
  .btn:hover:not(:disabled) { background: var(--surface-hover); border-color: var(--primary); }
  .btn:disabled { opacity: 0.45; cursor: not-allowed; }
  .btn.active-toggle {
    background: var(--primary-bg);
    color: var(--primary);
    border-color: var(--primary);
  }
  .btn-primary {
    background: var(--primary-bg);
    color: var(--primary);
    border-color: rgba(34, 211, 238, 0.35);
  }
  .btn-primary:hover:not(:disabled) { background: var(--primary); color: #0b1120; }
  .btn-emerald {
    background: var(--emerald-bg);
    color: var(--emerald);
    border-color: rgba(52, 211, 153, 0.35);
  }
  .btn-emerald:hover:not(:disabled) { background: var(--emerald); color: #0b1120; }
  .btn-amber {
    background: var(--amber-bg);
    color: var(--amber);
    border-color: rgba(251, 191, 36, 0.35);
  }
  .btn-amber:hover:not(:disabled) { background: var(--amber); color: #0b1120; }
  .btn-danger {
    background: var(--danger-bg);
    color: var(--danger);
    border-color: rgba(248, 113, 113, 0.35);
  }
  .btn-danger:hover:not(:disabled) { background: var(--danger); color: #fff; border-color: var(--danger); }
  .btn-sm {
    padding: 5px 9px;
    font-size: 12px;
    line-height: 1.2;
    border-radius: 8px;
  }
  .clipboard-bar .btn-sm,
  .header-actions .btn-sm {
    height: 31px;
    min-height: 31px;
    padding: 0 10px;
    line-height: 1;
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }
  .search-input, .select-input {
    padding: 7px 11px;
    border-radius: 10px;
    border: 1px solid var(--border);
    background: var(--surface-alt);
    color: var(--text);
    font-size: 13px;
    outline: none;
  }
  .search-input { min-width: 120px; }
  .search-input:focus, .select-input:focus { border-color: var(--primary); }
  .search-box-wrap {
    position: relative;
    display: flex;
    align-items: center;
    flex: 1 1 auto;
    min-width: 0;
  }
  .search-box-wrap .search-input {
    width: 100%;
    padding-right: 30px;
  }
  .search-clear-btn {
    display: none;
    position: absolute;
    right: 7px;
    background: transparent;
    border: none;
    color: var(--text-muted);
    cursor: pointer;
    font-size: 13px;
    padding: 3px 6px;
    border-radius: 6px;
    line-height: 1;
  }
  .search-clear-btn:hover {
    background: var(--surface-hover);
    color: var(--text);
  }
  .search-scope-group {
    display: inline-flex;
    align-items: center;
    background: var(--surface-alt);
    border: 1px solid var(--border);
    border-radius: 10px;
    padding: 2px;
    gap: 2px;
    flex-shrink: 0;
  }
  .search-scope-btn {
    border: none;
    background: transparent;
    color: var(--text-muted);
    padding: 5px 10px;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 5px;
    transition: all 0.15s;
    white-space: nowrap;
  }
  .search-scope-btn:hover {
    color: var(--text);
  }
  .search-scope-btn.active {
    background: var(--primary-bg);
    color: var(--primary);
  }
  .item-subpath-badge {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    margin-top: 3px;
    padding: 2px 7px;
    border-radius: 6px;
    background: var(--surface-alt);
    border: 1px solid var(--border);
    color: var(--text-muted);
    font-size: 11px;
    font-family: monospace;
    cursor: pointer;
    width: fit-content;
    max-width: 100%;
    word-break: break-all;
    transition: all 0.15s;
  }
  .item-subpath-badge:hover {
    border-color: var(--primary);
    color: var(--primary);
    background: var(--primary-bg);
  }
  /* View & Pagination Sub-bar */
  .view-bar {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 9px 14px;
    margin-bottom: 12px;
  }
  .view-bar-left, .view-bar-right {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
  }
  .view-dropdown-wrap {
    position: relative;
    display: inline-flex;
  }
  .view-dropdown-btn {
    display: inline-flex;
    align-items: center;
    gap: 7px;
    padding: 6px 12px;
    border-radius: 10px;
    border: 1px solid var(--border);
    background: var(--surface-alt);
    color: var(--text);
    font-size: 12.5px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.15s;
    user-select: none;
  }
  .view-dropdown-btn:hover,
  .view-dropdown-wrap.open .view-dropdown-btn {
    border-color: var(--primary);
    background: var(--primary-bg);
    color: var(--primary);
  }
  .view-dropdown-caret {
    font-size: 10px;
    opacity: 0.8;
    transition: transform 0.15s ease;
  }
  .view-dropdown-wrap.open .view-dropdown-caret {
    transform: rotate(180deg);
  }
  .view-dropdown-menu {
    display: none;
    position: absolute;
    top: calc(100% + 6px);
    left: 0;
    min-width: 225px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 6px;
    box-shadow: 0 14px 34px rgba(0, 0, 0, 0.5);
    z-index: 1250;
  }
  .view-dropdown-wrap.open .view-dropdown-menu {
    display: block;
    animation: menuFadeIn 0.12s ease-out;
  }
  .view-dropdown-option {
    width: 100%;
    border: none;
    background: transparent;
    color: var(--text);
    padding: 7px 10px;
    border-radius: 9px;
    font-size: 12.5px;
    font-weight: 500;
    text-align: left;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 9px;
    transition: background 0.12s, color 0.12s;
  }
  .view-dropdown-option:hover {
    background: var(--surface-hover);
    color: var(--primary);
  }
  .view-dropdown-option.active {
    background: var(--primary-bg);
    color: var(--primary);
    font-weight: 600;
  }
  /* Batch Selection Bar & Clipboard Bar */
  .batch-bar {
    display: none;
    flex-direction: column;
    align-items: stretch;
    gap: 7px;
    background: var(--surface);
    border: 1.5px solid var(--primary);
    border-radius: 14px;
    padding: 8px 12px;
    margin-bottom: 12px;
    box-shadow: var(--shadow);
  }
  .batch-bar.active { display: flex; }
  .batch-bar-row {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 6px;
    width: 100%;
  }
  #batchWriteButtons {
    display: contents;
  }
  .batch-counter-badge {
    height: 26px;
    min-height: 26px;
    max-height: 26px;
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 0 11px;
    font-size: 11.5px;
    font-weight: 700;
    line-height: 1;
    border-radius: 999px;
    white-space: nowrap;
    vertical-align: middle;
    flex-shrink: 0;
    background: var(--primary-bg);
    color: var(--primary);
    border: 1px solid rgba(34, 211, 238, 0.4);
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
    user-select: none;
    cursor: default;
  }
  .batch-bar .btn-sm {
    height: 30px;
    min-height: 30px;
    max-height: 30px;
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    padding: 0 9px;
    font-size: 11.5px;
    font-weight: 600;
    line-height: 28px;
    border: 1px solid var(--border);
    border-radius: 7px;
    white-space: nowrap;
    vertical-align: middle;
    flex-shrink: 0;
  }
  .batch-bar .btn-primary {
    border-color: rgba(34, 211, 238, 0.4);
  }
  .batch-bar .btn-emerald {
    border-color: rgba(52, 211, 153, 0.4);
  }
  .batch-bar .btn-amber {
    border-color: rgba(251, 191, 36, 0.4);
  }
  .batch-bar .btn-danger {
    border-color: rgba(248, 113, 113, 0.4);
  }
  .clipboard-bar {
    display: none;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    background: var(--surface);
    border: 1.5px solid var(--emerald);
    border-radius: 14px;
    padding: 10px 16px;
    margin-bottom: 12px;
    box-shadow: var(--shadow);
  }
  .clipboard-bar.active { display: flex; }
  .clipboard-bar { border-color: var(--emerald); }
  .bar-left {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
    font-size: 13px;
    font-weight: 600;
  }
  .bar-right {
    display: flex;
    align-items: center;
    gap: 7px;
    flex-wrap: wrap;
  }
  .chk {
    width: 17px;
    height: 17px;
    accent-color: var(--primary);
    cursor: pointer;
  }
  /* Upload Progress Card */
  .upload-panel {
    display: none;
    background: var(--surface);
    border: 1px solid var(--primary);
    border-radius: 14px;
    padding: 14px 18px;
    margin-bottom: 14px;
  }
  .upload-panel.active { display: block; }
  .upload-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    font-size: 13px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  .upload-meta-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: var(--text-muted);
    margin-top: 6px;
    gap: 10px;
    flex-wrap: wrap;
  }
  .upload-timer-counters {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
    font-size: 12px;
    font-weight: 600;
    font-variant-numeric: tabular-nums;
    white-space: nowrap;
  }
  .timer-badge {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 2px 7px;
    border-radius: 6px;
    background: rgba(255, 255, 255, 0.06);
  }
  [data-theme="light"] .timer-badge {
    background: rgba(0, 0, 0, 0.05);
  }
  /* Dropzone */
  .drop-overlay {
    display: none;
    position: fixed;
    inset: 16px;
    border: 3px dashed var(--primary);
    border-radius: 24px;
    background: rgba(11, 17, 32, 0.88);
    z-index: 999;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    gap: 12px;
    color: #fff;
    pointer-events: none;
  }
  .drop-overlay.active { display: flex; }
  /* File List Table */
  .file-card {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 18px;
    overflow: hidden;
    box-shadow: var(--shadow);
    min-height: 360px;
    flex: 1 1 auto;
    display: flex;
    flex-direction: column;
    position: relative;
  }
  table {
    width: 100%;
    border-collapse: collapse;
    text-align: left;
  }
  th {
    padding: 12px 14px;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    color: var(--text-muted);
    background: var(--surface-alt);
    border-bottom: 1px solid var(--border);
    cursor: pointer;
    user-select: none;
  }
  th:hover { color: var(--text); }
  td {
    padding: 10px 14px;
    border-bottom: 1px solid var(--border);
    font-size: 14px;
    vertical-align: middle;
  }
  tr:last-child td { border-bottom: none; }
  tr.file-row { user-select: none; -webkit-user-select: none; }
  tr.file-row:hover td { background: var(--surface-hover); }
  tr.file-row.selected td { background: var(--surface-selected); }
  .item-name-cell {
    display: flex;
    align-items: center;
    gap: 11px;
    cursor: pointer;
    font-weight: 500;
    word-break: break-all;
  }
  .item-name-cell:hover .item-title { color: var(--primary); text-decoration: underline; }
  .item-icon {
    width: 34px;
    height: 34px;
    border-radius: 9px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 17px;
    flex-shrink: 0;
  }
  .icon-dir { background: rgba(251, 191, 36, 0.15); color: #fbbf24; }
  .icon-img { background: rgba(52, 211, 153, 0.15); color: #34d399; }
  .icon-vid { background: rgba(244, 63, 94, 0.15); color: #fb7185; }
  .icon-aud { background: rgba(168, 85, 247, 0.15); color: #c084fc; }
  .icon-zip { background: rgba(249, 115, 22, 0.15); color: #fb923c; }
  .icon-file { background: var(--primary-bg); color: var(--primary); }
  .row-actions {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 5px;
    flex-wrap: nowrap;
  }
  /* Grid View Layout */
  .grid-container {
    display: none;
    grid-template-columns: repeat(auto-fill, minmax(205px, 1fr));
    gap: 14px;
    padding: 16px;
    flex: 1;
    align-content: start;
  }
  .grid-card {
    background: var(--surface-alt);
    border: 1.5px solid var(--border);
    border-radius: 16px;
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    transition: all 0.18s ease;
    position: relative;
    user-select: none;
    -webkit-user-select: none;
  }
  .grid-card:hover {
    border-color: var(--primary);
    background: var(--surface-hover);
    transform: translateY(-2px);
    box-shadow: var(--shadow);
  }
  .grid-card.selected {
    border-color: var(--primary);
    background: var(--surface-selected);
  }
  .grid-card-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 6px;
  }
  .grid-size-pill {
    font-size: 11px;
    color: var(--text-muted);
    font-family: monospace;
    background: var(--surface);
    border: 1px solid var(--border);
    padding: 2px 7px;
    border-radius: 999px;
  }
  /* Folder Size Button & Loading Indicator */
  .folder-size-btn {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 3px 9px;
    border-radius: 999px;
    border: 1px solid rgba(34, 211, 238, 0.35);
    background: var(--primary-bg);
    color: var(--primary);
    font-size: 11.5px;
    font-weight: 600;
    font-family: system-ui, -apple-system, sans-serif;
    cursor: pointer;
    transition: all 0.15s ease;
    white-space: nowrap;
  }
  .folder-size-btn:hover {
    background: var(--primary);
    color: #0b1120;
    border-color: var(--primary);
  }
  .folder-size-loading {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 3px 9px;
    border-radius: 999px;
    border: 1px solid rgba(251, 191, 36, 0.4);
    background: var(--amber-bg);
    color: var(--amber);
    font-size: 11.5px;
    font-weight: 600;
    font-family: system-ui, -apple-system, sans-serif;
    white-space: nowrap;
    animation: sizePulse 1.1s infinite ease-in-out;
  }
  @keyframes sizePulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.55; }
  }
  .folder-size-val {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-family: monospace;
    font-weight: 600;
    color: var(--emerald);
    white-space: nowrap;
  }
  #listViewWrapper {
    width: 100%;
    overflow-x: auto;
    flex: 1;
    display: flex;
    flex-direction: column;
  }
  /* Group By Section Headers (List & Grid Views) */
  tr.group-header-row td {
    background: var(--surface-alt);
    padding: 8px 14px;
    border-top: 1px solid var(--border);
    border-bottom: 1px solid var(--border);
    user-select: none;
    -webkit-user-select: none;
  }
  tr.group-header-row:first-child td {
    border-top: none;
  }
  .group-header-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
  }
  .group-header-title {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    font-size: 12.5px;
    font-weight: 700;
    color: var(--primary);
    letter-spacing: 0.02em;
  }
  .group-header-count {
    font-size: 11px;
    font-weight: 600;
    color: var(--text-muted);
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 999px;
    padding: 2px 9px;
    font-family: monospace;
  }
  .grid-group-header {
    grid-column: 1 / -1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    padding: 8px 12px;
    margin-top: 6px;
    background: var(--surface-alt);
    border: 1px solid var(--border);
    border-radius: 12px;
    user-select: none;
    -webkit-user-select: none;
  }
  .grid-group-header:first-child {
    margin-top: 0;
  }
  .grid-thumb-box {
    height: 108px;
    border-radius: 12px;
    background: var(--surface);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    cursor: pointer;
    border: 1px solid var(--border);
    position: relative;
  }
  .grid-thumb-box img,
  .grid-thumb-box video {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.25s ease;
  }
  .grid-card:hover .grid-thumb-box img,
  .grid-card:hover .grid-thumb-box video {
    transform: scale(1.04);
  }
  .grid-video-thumb-wrap {
    width: 100%;
    height: 100%;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .grid-video-play-icon {
    position: absolute;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: rgba(11, 17, 32, 0.76);
    border: 1.5px solid rgba(255, 255, 255, 0.75);
    color: #ffffff;
    font-size: 13px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding-left: 2px;
    backdrop-filter: blur(4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.45);
    pointer-events: none;
  }
  .grid-thumb-badge {
    position: absolute;
    bottom: 6px;
    right: 6px;
    background: rgba(11, 17, 32, 0.78);
    color: #f8fafc;
    font-size: 11px;
    font-weight: 600;
    padding: 3px 8px;
    border-radius: 999px;
    backdrop-filter: blur(4px);
    opacity: 0;
    transform: translateY(3px);
    transition: all 0.18s ease;
    pointer-events: none;
  }
  .grid-card:hover .grid-thumb-badge {
    opacity: 1;
    transform: translateY(0);
  }
  .grid-big-icon {
    font-size: 40px;
    transition: transform 0.2s ease;
  }
  .grid-card:hover .grid-big-icon {
    transform: scale(1.08);
  }
  .grid-info {
    display: flex;
    flex-direction: column;
    gap: 4px;
    flex: 1;
  }
  .grid-title {
    font-size: 13px;
    font-weight: 600;
    line-height: 1.35;
    min-height: 35px;
    word-break: break-all;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    cursor: pointer;
  }
  .grid-title:hover { color: var(--primary); text-decoration: underline; }
  .grid-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 4px;
    flex-wrap: nowrap;
    font-size: 10.5px;
    color: var(--text-muted);
    font-family: monospace;
    width: 100%;
  }
  .grid-meta-date {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    min-width: 0;
    letter-spacing: -0.3px;
  }
  .grid-meta-size {
    flex-shrink: 0;
    margin-left: auto;
    text-align: right;
    white-space: nowrap;
  }
  .grid-meta .folder-size-btn,
  .grid-meta .folder-size-loading {
    font-size: 10px;
    padding: 1.5px 6px;
    gap: 2px;
    letter-spacing: -0.2px;
    line-height: 1.25;
  }
  .grid-meta .folder-size-val {
    font-size: 10.5px;
  }
  .grid-actions {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 4px;
    padding-top: 8px;
    margin-top: auto;
    border-top: 1px solid var(--border);
    flex-wrap: nowrap;
    width: 100%;
  }
  .grid-actions .btn {
    flex: 1 1 0;
    min-width: 24px;
    height: 30px;
    padding: 0;
    font-size: 13px;
    border-radius: 8px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
  }
  /* 8 Windows Explorer Layout Variants (Extra large, Large, Medium, Small, List, Details, Tiles, Content) */
  #gridViewWrapper[data-view-mode="extra_large_icons"] {
    grid-template-columns: repeat(auto-fill, minmax(265px, 1fr));
    gap: 16px;
    padding: 18px;
  }
  #gridViewWrapper[data-view-mode="extra_large_icons"] .grid-thumb-box {
    height: 184px;
  }
  #gridViewWrapper[data-view-mode="extra_large_icons"] .grid-big-icon {
    font-size: 64px;
  }
  #gridViewWrapper[data-view-mode="extra_large_icons"] .grid-title {
    font-size: 14px;
  }
  #gridViewWrapper[data-view-mode="large_icons"] {
    grid-template-columns: repeat(auto-fill, minmax(205px, 1fr));
    gap: 14px;
    padding: 16px;
  }
  #gridViewWrapper[data-view-mode="large_icons"] .grid-thumb-box {
    height: 124px;
  }
  #gridViewWrapper[data-view-mode="large_icons"] .grid-big-icon {
    font-size: 46px;
  }
  #gridViewWrapper[data-view-mode="medium_icons"] {
    grid-template-columns: repeat(auto-fill, minmax(162px, 1fr));
    gap: 12px;
    padding: 14px;
  }
  #gridViewWrapper[data-view-mode="medium_icons"] .grid-card {
    padding: 10px;
    gap: 6px;
    border-radius: 14px;
  }
  #gridViewWrapper[data-view-mode="medium_icons"] .grid-thumb-box {
    height: 88px;
  }
  #gridViewWrapper[data-view-mode="medium_icons"] .grid-big-icon {
    font-size: 32px;
  }
  #gridViewWrapper[data-view-mode="medium_icons"] .grid-title {
    font-size: 12px;
    min-height: 32px;
  }
  /* Small Icons View */
  #gridViewWrapper[data-view-mode="small_icons"] {
    grid-template-columns: repeat(auto-fill, minmax(210px, 1fr));
    gap: 8px;
    padding: 14px;
  }
  .view-small-card {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 7px 10px;
    border-radius: 10px;
    background: var(--surface-alt);
    border: 1.5px solid var(--border);
    user-select: none;
    -webkit-user-select: none;
    transition: all 0.14s ease;
    min-width: 0;
  }
  .view-small-card:hover {
    border-color: var(--primary);
    background: var(--surface-hover);
  }
  .view-small-card.selected {
    border-color: var(--primary);
    background: var(--surface-selected);
  }
  .view-small-main {
    display: flex;
    align-items: center;
    gap: 8px;
    flex: 1;
    min-width: 0;
    cursor: pointer;
  }
  .view-small-icon {
    width: 26px;
    height: 26px;
    border-radius: 7px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    flex-shrink: 0;
  }
  .view-small-title {
    font-size: 12.5px;
    font-weight: 500;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    flex: 1;
    min-width: 0;
  }
  .view-small-main:hover .view-small-title {
    color: var(--primary);
    text-decoration: underline;
  }
  /* Compact List View (Windows Explorer List Mode) */
  #gridViewWrapper[data-view-mode="list"] {
    grid-template-columns: repeat(auto-fill, minmax(255px, 1fr));
    gap: 6px 12px;
    padding: 14px;
  }
  .view-list-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 10px;
    border-radius: 9px;
    background: transparent;
    border: 1px solid transparent;
    user-select: none;
    -webkit-user-select: none;
    transition: all 0.12s ease;
    min-width: 0;
  }
  .view-list-item:hover {
    background: var(--surface-hover);
    border-color: var(--border);
  }
  .view-list-item.selected {
    background: var(--surface-selected);
    border-color: var(--primary);
  }
  /* Tiles View (Windows Explorer Tiles Mode) */
  #gridViewWrapper[data-view-mode="tiles"] {
    grid-template-columns: repeat(auto-fill, minmax(285px, 1fr));
    gap: 12px;
    padding: 16px;
  }
  .view-tile-card {
    display: flex;
    align-items: center;
    gap: 11px;
    padding: 10px 12px;
    border-radius: 14px;
    background: var(--surface-alt);
    border: 1.5px solid var(--border);
    user-select: none;
    -webkit-user-select: none;
    transition: all 0.16s ease;
    min-width: 0;
  }
  .view-tile-card:hover {
    border-color: var(--primary);
    background: var(--surface-hover);
    transform: translateY(-1px);
  }
  .view-tile-card.selected {
    border-color: var(--primary);
    background: var(--surface-selected);
  }
  .view-tile-thumb {
    width: 54px;
    height: 54px;
    border-radius: 11px;
    background: var(--surface);
    border: 1px solid var(--border);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    flex-shrink: 0;
    cursor: pointer;
    font-size: 26px;
    position: relative;
  }
  .view-tile-thumb img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .view-tile-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
    flex: 1;
    min-width: 0;
  }
  .view-tile-title {
    font-size: 13px;
    font-weight: 600;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    cursor: pointer;
  }
  .view-tile-title:hover {
    color: var(--primary);
    text-decoration: underline;
  }
  .view-tile-sub {
    font-size: 11px;
    color: var(--text-muted);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  /* Content View (Windows Explorer Content Mode) */
  #gridViewWrapper[data-view-mode="content"] {
    grid-template-columns: 1fr;
    gap: 8px;
    padding: 14px 16px;
  }
  .view-content-row {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 10px 14px;
    border-radius: 13px;
    background: var(--surface-alt);
    border: 1.5px solid var(--border);
    user-select: none;
    -webkit-user-select: none;
    transition: all 0.15s ease;
    min-width: 0;
  }
  .view-content-row:hover {
    border-color: var(--primary);
    background: var(--surface-hover);
  }
  .view-content-row.selected {
    border-color: var(--primary);
    background: var(--surface-selected);
  }
  .view-content-thumb {
    width: 48px;
    height: 48px;
    border-radius: 10px;
    background: var(--surface);
    border: 1px solid var(--border);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    flex-shrink: 0;
    cursor: pointer;
    font-size: 24px;
    position: relative;
  }
  .view-content-thumb img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .view-content-main {
    display: flex;
    flex-direction: column;
    gap: 3px;
    flex: 1.4;
    min-width: 0;
  }
  .view-content-meta {
    display: flex;
    flex-direction: column;
    gap: 3px;
    flex: 1;
    min-width: 150px;
    font-size: 12px;
    color: var(--text-muted);
  }
  @media (max-width: 720px) {
    .view-content-row { flex-wrap: wrap; }
    .view-content-meta { min-width: 120px; }
  }
  .view-small-card.dragging-source,
  .view-list-item.dragging-source,
  .view-tile-card.dragging-source,
  .view-content-row.dragging-source {
    opacity: 0.45;
  }
  .view-small-card.drop-target-active,
  .view-list-item.drop-target-active,
  .view-tile-card.drop-target-active,
  .view-content-row.drop-target-active {
    border-color: var(--emerald) !important;
    background: var(--emerald-bg) !important;
  }
  /* Type-Ahead Keyboard Focus & Floating Pill */
  tr.file-row.typeahead-focus td {
    box-shadow: inset 0 2px 0 var(--emerald), inset 0 -2px 0 var(--emerald);
  }
  .grid-card.typeahead-focus,
  .view-small-card.typeahead-focus,
  .view-list-item.typeahead-focus,
  .view-tile-card.typeahead-focus,
  .view-content-row.typeahead-focus {
    outline: 2px solid var(--emerald);
    outline-offset: 1px;
  }
  .typeahead-indicator {
    display: none;
    position: fixed;
    bottom: 22px;
    left: 22px;
    z-index: 1550;
    align-items: center;
    gap: 8px;
    padding: 8px 14px;
    border-radius: 999px;
    background: var(--surface);
    border: 1.5px solid var(--emerald);
    color: var(--text);
    font-size: 13px;
    font-weight: 600;
    box-shadow: var(--shadow);
    pointer-events: none;
  }
  .typeahead-indicator.active {
    display: inline-flex;
    animation: modalPopIn 0.14s ease-out;
  }
  /* Mouse Marquee Rubber-Band Selection Box */
  .marquee-selection-box {
    display: none;
    position: fixed;
    z-index: 1180;
    border: 1.5px solid var(--primary);
    background: rgba(34, 211, 238, 0.16);
    box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.12);
    border-radius: 4px;
    pointer-events: none;
  }
  .marquee-selection-box.active {
    display: block;
  }
  /* Guide / How to Use Modal Tabs */
  .guide-tabs {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-wrap: wrap;
    padding: 12px 20px;
    background: var(--surface-alt);
    border-bottom: 1px solid var(--border);
  }
  .guide-tab-btn {
    border: 1px solid var(--border);
    background: var(--surface);
    color: var(--text-muted);
    padding: 6px 12px;
    border-radius: 9px;
    font-size: 12.5px;
    font-weight: 600;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    transition: all 0.15s;
  }
  .guide-tab-btn:hover {
    color: var(--text);
    border-color: var(--primary);
  }
  .guide-tab-btn.active {
    background: var(--primary-bg);
    color: var(--primary);
    border-color: var(--primary);
  }
  .guide-pane {
    display: none;
    flex-direction: column;
    gap: 14px;
  }
  .guide-pane.active {
    display: flex;
  }
  .guide-card-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
  @media (max-width: 680px) {
    .guide-card-grid { grid-template-columns: 1fr; }
  }
  .guide-feature-card {
    background: var(--surface-alt);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 13px 15px;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }
  .guide-feature-title {
    font-size: 13.5px;
    font-weight: 700;
    color: var(--primary);
    display: flex;
    align-items: center;
    gap: 7px;
  }
  .guide-feature-desc {
    font-size: 12.5px;
    color: var(--text);
    line-height: 1.5;
  }
  /* Floating Item & Background Action Context Menu */
  .context-menu {
    display: none;
    position: fixed;
    z-index: 1200;
    min-width: 215px;
    max-width: 270px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 6px;
    box-shadow: 0 14px 34px rgba(0, 0, 0, 0.5);
    overflow: visible;
    user-select: none;
    -webkit-user-select: none;
  }
  .context-menu.active {
    display: block;
    animation: menuFadeIn 0.12s ease-out;
  }
  @keyframes menuFadeIn {
    from { opacity: 0; transform: scale(0.96) translateY(-4px); }
    to { opacity: 1; transform: scale(1) translateY(0); }
  }
  .context-menu-header {
    padding: 7px 10px;
    font-size: 12px;
    font-weight: 700;
    color: var(--text-muted);
    border-bottom: 1px solid var(--border);
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .context-menu-item {
    width: 100%;
    border: none;
    background: transparent;
    color: var(--text);
    padding: 8px 10px;
    border-radius: 9px;
    font-size: 13px;
    font-weight: 500;
    text-align: left;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
    text-decoration: none;
    transition: background 0.12s, color 0.12s;
  }
  .context-menu-item:hover {
    background: var(--surface-hover);
    color: var(--primary);
  }
  .context-menu-item.disabled,
  .context-menu-item:disabled {
    opacity: 0.45;
    cursor: not-allowed;
    pointer-events: none;
  }
  .context-menu-item.danger {
    color: var(--danger);
  }
  .context-menu-item.danger:hover {
    background: var(--danger-bg);
    color: var(--danger);
  }
  .context-menu-divider {
    height: 1px;
    background: var(--border);
    margin: 4px 0;
  }
  .context-menu-kbd {
    margin-left: auto;
    padding-left: 12px;
    font-size: 10.5px;
    font-family: monospace;
    color: var(--text-muted);
    opacity: 0.85;
    flex-shrink: 0;
  }
  /* Cascading Flyout Submenus */
  .context-submenu-wrap {
    position: relative;
    width: 100%;
  }
  .context-submenu-arrow {
    margin-left: auto;
    padding-left: 12px;
    font-size: 11px;
    color: var(--text-muted);
    transition: transform 0.14s ease, color 0.14s ease;
    flex-shrink: 0;
  }
  .context-submenu-wrap:hover > .context-menu-item,
  .context-submenu-wrap.open > .context-menu-item {
    background: var(--surface-hover);
    color: var(--primary);
  }
  .context-submenu-wrap:hover > .context-menu-item .context-submenu-arrow,
  .context-submenu-wrap.open > .context-menu-item .context-submenu-arrow {
    color: var(--primary);
    transform: translateX(2px);
  }
  .context-submenu {
    display: none;
    position: absolute;
    top: -6px;
    left: calc(100% + 4px);
    min-width: 210px;
    max-width: 265px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 6px;
    box-shadow: 0 14px 34px rgba(0, 0, 0, 0.55);
    z-index: 1215;
  }
  .context-submenu::before {
    content: '';
    position: absolute;
    top: 0;
    bottom: 0;
    left: -10px;
    width: 10px;
  }
  .context-submenu.flip-left {
    left: auto;
    right: calc(100% + 4px);
  }
  .context-submenu.flip-left::before {
    left: auto;
    right: -10px;
  }
  .context-submenu.flip-up {
    top: auto;
    bottom: -6px;
  }
  .context-submenu-wrap:hover > .context-submenu,
  .context-submenu-wrap.open > .context-submenu {
    display: block;
    animation: menuFadeIn 0.12s ease-out;
  }
  .menu-check {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    font-size: 12px;
    font-weight: 800;
    color: var(--primary);
    flex-shrink: 0;
  }
  /* Pagination Footer */
  .pagination-bar {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    padding: 12px 16px;
    background: var(--surface-alt);
    border-top: 1px solid var(--border);
    font-size: 13px;
    margin-top: auto;
  }
  .pagination-bar-top {
    border-top: none;
    border-bottom: 1px solid var(--border);
    margin-top: 0;
  }
  .page-buttons {
    display: flex;
    align-items: center;
    gap: 5px;
    flex-wrap: wrap;
  }
  .empty-state {
    padding: 72px 20px;
    text-align: center;
    color: var(--text-muted);
    user-select: none;
    -webkit-user-select: none;
  }
  /* Modals & Custom Popups */
  .modal-backdrop {
    display: none;
    position: fixed;
    inset: 0;
    background: rgba(6, 10, 20, 0.76);
    backdrop-filter: blur(7px);
    -webkit-backdrop-filter: blur(7px);
    z-index: 1000;
    align-items: center;
    justify-content: center;
    padding: 16px;
  }
  .modal-backdrop.active {
    display: flex;
    animation: backdropFadeIn 0.16s ease-out;
  }
  #dialogModal { z-index: 1350; }
  #osPasteModal { z-index: 1365; }
  #batchRenameModal { z-index: 1380; }
  #propertiesModal { z-index: 1390; }
  #shortcutsModal { z-index: 1400; }
  #conflictModal { z-index: 1420; }
  #renameConflictModal { z-index: 1430; }
  #authModal { z-index: 1500; }
  @keyframes backdropFadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
  }
  @keyframes modalPopIn {
    from { opacity: 0; transform: scale(0.95) translateY(8px); }
    to { opacity: 1; transform: scale(1) translateY(0); }
  }
  @keyframes modalShake {
    0%, 100% { transform: translateX(0); }
    20%, 60% { transform: translateX(-7px); }
    40%, 80% { transform: translateX(7px); }
  }
  .modal-box {
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 20px;
    max-width: 820px;
    width: 100%;
    max-height: 90vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    box-shadow: var(--shadow);
    animation: modalPopIn 0.18s cubic-bezier(0.16, 1, 0.3, 1);
  }
  /* Floating Pop-Out Preview Window */
  .preview-floating-window {
    position: fixed;
    z-index: 999;
    display: none;
    flex-direction: column;
    background: var(--surface);
    border: 1.5px solid var(--border);
    border-radius: 18px;
    box-shadow: 0 16px 44px rgba(0, 0, 0, 0.45);
    overflow: hidden;
    min-width: 320px;
    min-height: 220px;
    width: 640px;
    height: 480px;
    right: 24px;
    bottom: 24px;
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    transition: box-shadow 0.2s ease, border-color 0.2s ease;
  }
  .preview-floating-window.active {
    display: flex;
  }
  .preview-floating-window:focus-within,
  .preview-floating-window.focused {
    border-color: var(--primary);
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.55), 0 0 0 2px var(--primary-glow, rgba(2, 245, 210, 0.25));
  }
  .preview-floating-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 14px;
    border-bottom: 1px solid var(--border);
    font-weight: 700;
    font-size: 13px;
    background: var(--surface-alt);
    cursor: move;
    user-select: none;
    -webkit-user-select: none;
    touch-action: none;
  }
  .preview-floating-header:active {
    cursor: grabbing;
  }
  .preview-floating-title-box {
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 0;
    overflow: hidden;
    flex: 1;
    margin-right: 10px;
  }
  .preview-floating-title {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 13px;
    font-weight: 700;
    color: var(--text);
  }
  .preview-floating-actions {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
  }
  .preview-floating-body {
    flex: 1;
    padding: 14px;
    overflow: auto;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--surface);
    position: relative;
    min-height: 0;
  }
  .preview-floating-body img,
  .preview-floating-body video {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
    border-radius: 8px;
  }
  .preview-floating-body audio {
    width: 100%;
    max-width: 440px;
  }
  .preview-floating-body pre {
    width: 100%;
    height: 100%;
    max-height: 100%;
    margin: 0;
    overflow: auto;
    background: var(--surface-alt);
    padding: 12px;
    border-radius: 8px;
    font-size: 12px;
    color: var(--text);
    white-space: pre-wrap;
    word-break: break-word;
  }
  /* Resize Handles for Floating Window */
  .preview-resize-handle {
    position: absolute;
    z-index: 10;
    touch-action: none;
  }
  .preview-resize-handle.handle-e {
    right: 0; top: 0; bottom: 0; width: 8px; cursor: e-resize;
  }
  .preview-resize-handle.handle-s {
    left: 0; right: 0; bottom: 0; height: 8px; cursor: s-resize;
  }
  .preview-resize-handle.handle-w {
    left: 0; top: 0; bottom: 0; width: 8px; cursor: w-resize;
  }
  .preview-resize-handle.handle-n {
    left: 0; right: 0; top: 0; height: 8px; cursor: n-resize;
  }
  .preview-resize-handle.handle-se {
    right: 0; bottom: 0; width: 14px; height: 14px; cursor: se-resize;
  }
  .preview-resize-handle.handle-sw {
    left: 0; bottom: 0; width: 14px; height: 14px; cursor: sw-resize;
  }
  .preview-resize-handle.handle-ne {
    right: 0; top: 0; width: 14px; height: 14px; cursor: ne-resize;
  }
  .preview-resize-handle.handle-nw {
    left: 0; top: 0; width: 14px; height: 14px; cursor: nw-resize;
  }
  .modal-box.shake {
    animation: modalShake 0.36s ease-in-out;
  }
  .modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 15px 20px;
    border-bottom: 1px solid var(--border);
    font-weight: 700;
    background: var(--surface-alt);
  }
  .modal-body {
    padding: 20px;
    overflow: auto;
    display: flex;
    flex-direction: column;
  }
  .modal-body.center-content {
    align-items: center;
    justify-content: center;
  }
  .modal-body img, .modal-body video {
    max-width: 100%;
    max-height: 70vh;
    border-radius: 10px;
  }
  .modal-body pre {
    width: 100%;
    max-height: 68vh;
    overflow: auto;
    background: var(--surface-alt);
    padding: 14px;
    border-radius: 10px;
    font-size: 13px;
    color: var(--text);
    white-space: pre-wrap;
    word-break: break-word;
  }
  /* Custom Styled Dialog & Auth Popups */
  .dialog-card {
    max-width: 450px;
  }
  .dialog-top {
    display: flex;
    align-items: flex-start;
    gap: 14px;
    margin-bottom: 14px;
  }
  .dialog-icon-badge {
    width: 46px;
    height: 46px;
    border-radius: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    flex-shrink: 0;
    border: 1px solid var(--border);
  }
  .icon-badge-primary {
    background: var(--primary-bg);
    color: var(--primary);
    border-color: rgba(34, 211, 238, 0.35);
  }
  .icon-badge-emerald {
    background: var(--emerald-bg);
    color: var(--emerald);
    border-color: rgba(52, 211, 153, 0.35);
  }
  .icon-badge-amber {
    background: var(--amber-bg);
    color: var(--amber);
    border-color: rgba(251, 191, 36, 0.35);
  }
  .icon-badge-danger {
    background: var(--danger-bg);
    color: var(--danger);
    border-color: rgba(248, 113, 113, 0.35);
  }
  /* Settings Modal Popup */
  .settings-modal-card {
    max-width: 440px;
    width: 94%;
    border-radius: 18px;
  }
  .settings-options-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  .settings-option-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 12px 14px;
    background: var(--bg-hover);
    border: 1px solid var(--border);
    border-radius: 12px;
    cursor: pointer;
    transition: background 0.15s ease, border-color 0.15s ease;
  }
  .settings-option-item:hover {
    background: var(--bg-active);
    border-color: var(--primary);
  }
  .settings-option-info {
    display: flex;
    align-items: center;
    gap: 10px;
    flex: 1;
    min-width: 0;
  }
  .settings-option-icon {
    font-size: 20px;
    flex-shrink: 0;
    line-height: 1;
  }
  .settings-option-text {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
  }
  .settings-option-title {
    font-size: 13.5px;
    font-weight: 600;
    color: var(--text);
  }
  .settings-option-desc {
    font-size: 11px;
    color: var(--text-muted);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .dialog-title {
    font-size: 17px;
    font-weight: 700;
    color: var(--text);
    line-height: 1.3;
  }
  .dialog-subtitle {
    font-size: 13px;
    color: var(--text-muted);
    margin-top: 4px;
    line-height: 1.45;
  }
  .dialog-target-box {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    background: var(--surface-alt);
    border: 1px solid var(--border);
    border-radius: 12px;
    padding: 9px 13px;
    margin-bottom: 14px;
    font-size: 12px;
  }
  .dialog-target-label {
    color: var(--text-muted);
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.04em;
    flex-shrink: 0;
  }
  .dialog-target-val {
    color: var(--primary);
    font-family: monospace;
    font-weight: 600;
    word-break: break-all;
    text-align: right;
  }
  .dialog-field-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
    margin-bottom: 14px;
  }
  .dialog-field-label {
    font-size: 12px;
    font-weight: 600;
    color: var(--text-muted);
  }
  .dialog-input-wrap {
    position: relative;
    display: flex;
    align-items: center;
  }
  .dialog-input {
    width: 100%;
    padding: 11px 14px;
    border-radius: 12px;
    border: 1.5px solid var(--border);
    background: var(--surface-alt);
    color: var(--text);
    font-size: 14px;
    outline: none;
    transition: border-color 0.15s, box-shadow 0.15s;
  }
  .dialog-input:focus {
    border-color: var(--primary);
    box-shadow: 0 0 0 3px var(--primary-bg);
  }
  .dialog-input-action {
    position: absolute;
    right: 8px;
    background: transparent;
    border: none;
    color: var(--text-muted);
    cursor: pointer;
    padding: 5px 8px;
    border-radius: 8px;
    font-size: 14px;
  }
  .dialog-input-action:hover {
    background: var(--surface-hover);
    color: var(--text);
  }
  .dialog-warning-banner {
    display: flex;
    align-items: center;
    gap: 9px;
    padding: 10px 13px;
    border-radius: 11px;
    background: var(--danger-bg);
    color: var(--danger);
    border: 1px solid rgba(248, 113, 113, 0.3);
    font-size: 12.5px;
    font-weight: 500;
    margin-bottom: 14px;
    line-height: 1.4;
  }
  .dialog-footer {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 10px;
    padding: 14px 20px;
    background: var(--surface-alt);
    border-top: 1px solid var(--border);
  }
  .conflict-card {
    max-width: 520px;
  }
  .conflict-compare-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 10px;
    margin-bottom: 14px;
  }
  @media (max-width: 520px) {
    .conflict-compare-grid {
      grid-template-columns: 1fr;
    }
  }
  .conflict-meta-box {
    background: var(--surface-alt);
    border: 1px solid var(--border);
    border-radius: 12px;
    padding: 10px 12px;
    font-size: 12px;
  }
  .conflict-meta-title {
    font-weight: 700;
    color: var(--text-muted);
    text-transform: uppercase;
    letter-spacing: 0.04em;
    font-size: 11px;
    margin-bottom: 4px;
  }
  .conflict-meta-val {
    color: var(--text);
    font-weight: 600;
    font-size: 13px;
  }
  .conflict-meta-sub {
    color: var(--text-muted);
    font-size: 11.5px;
    margin-top: 2px;
  }
  .conflict-apply-all {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 12.5px;
    color: var(--text-muted);
    cursor: pointer;
    user-select: none;
    margin-top: 4px;
  }
  .conflict-footer {
    display: flex;
    flex-direction: column;
    gap: 10px;
    padding: 16px 22px;
    background: var(--surface-alt);
    border-top: 1px solid var(--border);
  }
  .conflict-footer-row {
    display: flex;
    align-items: stretch;
    gap: 10px;
    width: 100%;
  }
  .conflict-footer-row .btn {
    flex: 1 1 0;
    justify-content: center;
    text-align: center;
    min-height: 42px;
    padding: 9px 12px;
    white-space: nowrap;
  }
  @media (max-width: 460px) {
    .conflict-footer-row {
      flex-direction: column;
    }
    .conflict-footer-row .btn {
      width: 100%;
    }
  }
  /* Batch Rename Modal Styles */
  .batch-rename-card {
    max-width: 580px;
  }
  .batch-rename-grid {
    display: grid;
    grid-template-columns: 1.4fr 1fr;
    gap: 10px;
    margin-bottom: 12px;
  }
  @media (max-width: 520px) {
    .batch-rename-grid {
      grid-template-columns: 1fr;
    }
  }
  .batch-preview-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
    margin-bottom: 6px;
    font-size: 12px;
    font-weight: 600;
    color: var(--text-muted);
  }
  .batch-preview-list {
    border: 1px solid var(--border);
    border-radius: 12px;
    background: var(--surface-alt);
    max-height: 240px;
    overflow-y: auto;
    divide-y: 1px solid var(--border);
  }
  .batch-preview-row {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    border-bottom: 1px solid var(--border);
    font-size: 12.5px;
    font-family: monospace;
  }
  .batch-preview-row:last-child {
    border-bottom: none;
  }
  .batch-preview-idx {
    font-size: 11px;
    font-weight: 700;
    color: var(--text-muted);
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 6px;
    padding: 2px 6px;
    flex-shrink: 0;
  }
  .batch-preview-old {
    color: var(--text-muted);
    flex: 1 1 0;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .batch-preview-arrow {
    color: var(--emerald);
    font-weight: 700;
    flex-shrink: 0;
    padding: 0 2px;
  }
  .batch-preview-new {
    color: var(--primary);
    font-weight: 700;
    flex: 1 1 0;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .batch-info-banner {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 9px 12px;
    border-radius: 11px;
    background: var(--primary-bg);
    color: var(--primary);
    border: 1px solid rgba(34, 211, 238, 0.3);
    font-size: 12px;
    font-weight: 500;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  /* Toast Notifications */
  .toast-container {
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 1600;
    display: flex;
    flex-direction: column;
    gap: 8px;
    pointer-events: none;
    max-width: min(390px, calc(100vw - 32px));
    width: auto;
  }
  .toast-item {
    pointer-events: auto;
    display: flex;
    align-items: center;
    gap: 10px;
    background: var(--surface);
    color: var(--text);
    border: 1.5px solid var(--primary);
    border-radius: 14px;
    padding: 11px 16px;
    font-size: 13px;
    font-weight: 600;
    box-shadow: var(--shadow);
    animation: modalPopIn 0.2s ease-out;
    max-width: 100%;
    box-sizing: border-box;
    overflow: hidden;
  }
  .toast-item.toast-emerald { border-color: var(--emerald); }
  .toast-item.toast-danger { border-color: var(--danger); }
  .toast-item.toast-amber { border-color: var(--amber); }
  .toast-text {
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-height: 1.4;
    word-break: break-word;
    overflow-wrap: anywhere;
  }
  .folder-picker-list {
    border: 1px solid var(--border);
    border-radius: 12px;
    max-height: 320px;
    overflow-y: auto;
    background: var(--surface-alt);
    margin: 10px 0 14px 0;
  }
  .folder-picker-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 14px;
    border-bottom: 1px solid var(--border);
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
  }
  .folder-picker-item:last-child { border-bottom: none; }
  .folder-picker-item:hover { background: var(--surface-hover); color: var(--primary); }
  /* Properties & Keyboard Shortcuts Modals */
  .kbd-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 2px 7px;
    border-radius: 6px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-bottom-width: 2px;
    font-family: monospace;
    font-size: 11px;
    font-weight: 700;
    color: var(--primary);
    white-space: nowrap;
    line-height: 1.3;
  }
  .prop-list {
    display: flex;
    flex-direction: column;
    border: 1px solid var(--border);
    border-radius: 12px;
    background: var(--surface-alt);
    overflow: hidden;
  }
  .prop-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 10px 14px;
    border-bottom: 1px solid var(--border);
    font-size: 13px;
  }
  .prop-row:last-child { border-bottom: none; }
  .prop-label {
    color: var(--text-muted);
    font-weight: 600;
    flex-shrink: 0;
  }
  .prop-val {
    color: var(--text);
    font-weight: 600;
    text-align: right;
    word-break: break-all;
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }
  .prop-val.mono {
    font-family: monospace;
    color: var(--primary);
  }
  .shortcuts-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 14px;
  }
  .shortcut-section {
    background: var(--surface-alt);
    border: 1px solid var(--border);
    border-radius: 14px;
    padding: 12px 14px;
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  .shortcut-section-title {
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    color: var(--primary);
    border-bottom: 1px solid var(--border);
    padding-bottom: 6px;
    margin-bottom: 2px;
  }
  .shortcut-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    font-size: 12.5px;
  }
  .shortcut-desc {
    color: var(--text);
    font-weight: 500;
  }
  .shortcut-keys {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    flex-shrink: 0;
    flex-wrap: wrap;
    justify-content: flex-end;
  }
  .footer-credit-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6px;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: 16px;
    padding: 12px 16px;
    margin-top: 14px;
    box-shadow: var(--shadow);
    text-align: center;
    font-size: 13px;
    color: var(--text-muted);
    line-height: 1.5;
  }
  .footer-credit-line {
    font-weight: 500;
  }
  .footer-credit-link {
    color: var(--primary);
    font-weight: 700;
    text-decoration: underline;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    vertical-align: baseline;
  }
  .footer-credit-link:has(svg) {
    text-decoration: none;
  }
  .footer-credit-link span {
    text-decoration: underline;
  }
  .footer-credit-link:hover {
    opacity: 0.85;
  }
  .footer-credit-link svg {
    width: 14px;
    height: 14px;
    fill: var(--text);
    flex-shrink: 0;
  }
  @media (max-width: 860px) {
    .header {
      grid-template-columns: 1fr;
      justify-items: center;
      text-align: center;
      padding: 10px 14px;
      gap: 12px;
    }
    .brand { justify-self: center; justify-content: center; }
    .header-center { justify-self: center; width: 100%; max-width: 320px; }
    .header-actions { justify-self: center; justify-content: center; flex-wrap: wrap; width: 100%; }
  }
  @media (max-width: 680px) {
    .hide-mobile { display: none; }
    .toolbar { flex-direction: column; align-items: stretch; }
    .toolbar-search-row { flex-wrap: wrap; }
    .search-box-wrap { flex: 1 1 180px; }
    .toolbar-actions-row { justify-content: flex-start; }
    .batch-bar-row { gap: 5px; }
    .grid-container { grid-template-columns: repeat(auto-fill, minmax(168px, 1fr)); padding: 10px; gap: 8px; }
    .shortcuts-grid { grid-template-columns: 1fr; }
  }
</style>
</head>
<body>
<div class="drop-overlay" id="dropOverlay">
  <div style="font-size:48px;">📂</div>
  <div style="font-size:22px;font-weight:700;">Drop files or folders here to upload</div>
  <div style="font-size:14px;color:#94a3b8;">Folder structures &amp; subdirectories will be preserved automatically</div>
</div>

<div class="container">
  <header class="header">
    <div class="brand">
      <div class="brand-icon">
        $APP_FAVICON_SVG
      </div>
      <div class="brand-text">
        <div class="brand-title">Universal File Server</div>
        <div class="brand-sub">
          <span class="badge badge-http">HTTP Web Portal</span>
          <span class="live-conn-pill" id="liveConnPill" title="Server Online • Active Connections">
            <span class="live-conn-dot-wrap">
              <span class="live-conn-dot-pulse" id="liveConnPulse"></span>
              <span class="live-conn-dot" id="liveConnDot"></span>
            </span>
            <span id="liveConnStatusText">Online</span>
            <span class="live-conn-divider">•</span>
            <span>Active: <strong class="live-conn-count" id="liveConnCount">1</strong></span>
          </span>
        </div>
      </div>
    </div>
    <div class="header-center">
      <div class="storage-box" id="storageBox">
        <div class="storage-labels">
          <span id="storageUsedText">Storage</span>
          <span id="storageFreeText">--</span>
        </div>
        <div class="progress-track">
          <div class="progress-fill" id="storageBar" style="width:0%"></div>
        </div>
      </div>
      <div class="header-storage-badges">
        <span id="accessBadge" class="badge badge-rw">Read &amp; Write</span>
        <span id="rootNameLabel" class="badge badge-storage">Internal Storage</span>
      </div>
    </div>
    <div class="header-actions">
      <button class="btn btn-sm" onclick="openShortcutsModal()" title="Comprehensive Web Portal Manual, Features &amp; Shortcuts (? or F1)">📑 Manual</button>
      <button class="btn btn-sm" id="btnOpenSettings" onclick="openSettingsModal()" title="Web Portal Settings">⚙️ Settings</button>
      <button class="btn btn-sm" id="logoutBtn" style="display:none;" onclick="logoutUser()" title="Sign out of current session">🔒 Sign Out</button>
    </div>
  </header>

  <div class="toolbar">
    <!-- Row 1: Navigation History Buttons (Back / Forward / Up) + Editable & Copyable Address Bar -->
    <div class="address-nav-row">
      <div class="nav-history-group">
        <button type="button" class="btn nav-history-btn" id="btnNavBack" onclick="navigateBack()" disabled title="Backward to Previous Folder (Alt + Left Arrow)">
          ◀
        </button>
        <button type="button" class="btn nav-history-btn" id="btnNavForward" onclick="navigateForward()" disabled title="Forward to Next Folder (Alt + Right Arrow)">
          ▶
        </button>
        <button type="button" class="btn nav-history-btn" id="btnNavUp" onclick="navigateUp()" disabled title="Up to Parent Folder (Alt + Up Arrow)">
          ▲
        </button>
      </div>

      <div class="address-bar-shell" id="addressBarShell">
        <div class="breadcrumbs-view" id="breadcrumbsView">
          <div class="breadcrumbs" id="breadcrumbs" onclick="onBreadcrumbsBackgroundClick(event)" title="Click empty space to edit path manually"></div>
          <div class="address-bar-actions">
            <button type="button" class="address-action-btn" id="btnEditAddress" onclick="enterAddressEditMode()" title="Edit path manually">
              ✏️
            </button>
            <button type="button" class="address-action-btn" id="btnCopyAddress" onclick="copyCurrentAddress()" title="Copy current path">
              📋
            </button>
          </div>
        </div>

        <div class="address-edit-view" id="addressEditView" style="display:none;">
          <span style="font-size:15px;padding-left:4px;">📂</span>
          <input type="text" id="addressPathInput" class="address-path-input" placeholder="/path/to/folder" autocomplete="off" spellcheck="false" onkeydown="onAddressInputKeyDown(event)" onblur="onAddressInputBlur(event)">
          <button type="button" class="btn btn-sm btn-primary" onclick="submitManualAddress()" title="Go to typed path">
            ↵ Go
          </button>
          <button type="button" class="btn btn-sm" onclick="exitAddressEditMode()" title="Cancel editing">
            ✕
          </button>
        </div>
      </div>
    </div>

    <!-- Row 2: Search Input + Scope Toggle in front of it -->
    <div class="toolbar-search-row">
      <div class="search-box-wrap">
        <input type="text" id="searchInput" class="search-input" placeholder="Search in current folder... (Ctrl+F or F3)" oninput="onSearchChanged()">
        <button type="button" class="search-clear-btn" id="clearSearchBtn" onclick="clearSearchInput()" title="Clear Search (Esc)">✕</button>
      </div>
      <div class="search-scope-group" id="searchScopeGroup" title="Choose whether to search only in the current folder or also inside all subfolders">
        <button type="button" class="search-scope-btn active" id="btnScopeCurrent" onclick="setSearchSubfolders(false)">
          📁 This Folder
        </button>
        <button type="button" class="search-scope-btn" id="btnScopeSubfolders" onclick="setSearchSubfolders(true)">
          🗂️ + Subfolders
        </button>
      </div>
    </div>

    <!-- Row 3: Action Buttons -->
    <div class="toolbar-actions-row" id="toolbarActionsRow">
      <button type="button" class="btn btn-primary" onclick="downloadCurrentDirZip()" title="Stream current folder as ZIP (0 device storage used)">
        📦 Download Folder ZIP
      </button>
      <div id="writeActions" class="write-actions-group">
        <button type="button" class="btn btn-emerald" onclick="document.getElementById('fileInput').click()">
          ⬆️ Upload Files
        </button>
        <button type="button" class="btn btn-emerald" onclick="document.getElementById('folderInput').click()">
          📁⬆️ Upload Folder
        </button>
        <button type="button" class="btn" onclick="promptNewFolder()" title="Create New Folder (Ctrl+Shift+N or Alt+N)">
          📁➕ New Folder
        </button>
      </div>
      <button type="button" class="btn" onclick="refreshCurrentDirectory()" title="Refresh File List (F5)">
        🔄 Refresh
      </button>
    </div>
  </div>

  <!-- Display Mode (7 Windows Explorer Views), Sort, Group & Items Per Page Controls -->
  <div class="view-bar">
    <div class="view-bar-left">
      <div class="view-dropdown-wrap" id="viewDropdownWrap" onclick="event.stopPropagation()">
        <button type="button" class="view-dropdown-btn" id="viewDropdownBtn" onclick="toggleViewDropdown(event)" title="Change Layout View Mode (Alt+Shift+6 or Alt+V)">
          <span id="viewDropdownIcon">☰</span>
          <span id="viewDropdownLabel">View: Details</span>
          <span class="view-dropdown-caret">▾</span>
        </button>
        <div class="view-dropdown-menu" id="viewDropdownMenu">
          <button type="button" class="view-dropdown-option" data-mode="extra_large_icons" onclick="setViewMode('extra_large_icons')">
            <span class="menu-check"></span><span>🖼️ Extra large icons</span>
          </button>
          <button type="button" class="view-dropdown-option" data-mode="large_icons" onclick="setViewMode('large_icons')">
            <span class="menu-check"></span><span>🖼️ Large icons</span>
          </button>
          <button type="button" class="view-dropdown-option" data-mode="medium_icons" onclick="setViewMode('medium_icons')">
            <span class="menu-check"></span><span>⊞ Medium icons</span>
          </button>
          <button type="button" class="view-dropdown-option" data-mode="small_icons" onclick="setViewMode('small_icons')">
            <span class="menu-check"></span><span>▪ Small icons</span>
          </button>
          <button type="button" class="view-dropdown-option" data-mode="list" onclick="setViewMode('list')">
            <span class="menu-check"></span><span>≣ List</span>
          </button>
          <button type="button" class="view-dropdown-option active" data-mode="details" onclick="setViewMode('details')">
            <span class="menu-check">✓</span><span>☰ Details</span>
          </button>
          <button type="button" class="view-dropdown-option" data-mode="tiles" onclick="setViewMode('tiles')">
            <span class="menu-check"></span><span>▤ Tiles</span>
          </button>
        </div>
      </div>

      <label style="font-size:12px;color:var(--text-muted);display:inline-flex;align-items:center;gap:6px;">
        Sort:
        <select id="sortSelect" class="select-input" onchange="onSortSelectChanged(this.value)">
          <option value="name_asc">Name (A → Z)</option>
          <option value="name_desc">Name (Z → A)</option>
          <option value="size_desc">Size (Largest)</option>
          <option value="size_asc">Size (Smallest)</option>
          <option value="date_desc">Date (Newest)</option>
          <option value="date_asc">Date (Oldest)</option>
        </select>
      </label>

      <label style="font-size:12px;color:var(--text-muted);display:inline-flex;align-items:center;gap:6px;">
        Group:
        <select id="groupSelect" class="select-input" onchange="onGroupSelectChanged(this.value)">
          <option value="none">None</option>
          <option value="name">Name</option>
          <option value="date">Date Modified</option>
          <option value="type">File Type</option>
          <option value="size">Size</option>
        </select>
      </label>
    </div>

    <div class="view-bar-right">
      <span id="itemRangeSummary" style="font-size:12px;color:var(--text-muted);font-weight:600;">0 items</span>
      <label style="font-size:12px;color:var(--text-muted);display:inline-flex;align-items:center;gap:6px;">
        Per Page:
        <select id="pageSizeSelect" class="select-input" onchange="onPageSizeChanged(this.value)">
          <option value="25">25</option>
          <option value="50" selected>50</option>
          <option value="100">100</option>
          <option value="200">200</option>
          <option value="500">500</option>
          <option value="all">All (Show All)</option>
        </select>
      </label>
    </div>
  </div>

  <!-- Batch Action Bar (appears when 1+ items are checked) -->
  <div class="batch-bar" id="batchBar">
    <!-- Row 1: Selection Controls -->
    <div class="batch-bar-row batch-bar-row-select" id="batchSelectRow">
      <span class="badge badge-http batch-counter-badge" id="selectedCountBadge">0 selected</span>
      <button type="button" class="btn btn-sm" onclick="selectCurrentPage(true)">Select Page</button>
      <button type="button" class="btn btn-sm" onclick="selectAllFiltered(true)" title="Select All (Ctrl+A)">Select All in Folder</button>
      <button type="button" class="btn btn-sm" onclick="invertCurrentPageSelection()" title="Invert selection on the current page">Invert Selection</button>
      <button type="button" class="btn btn-sm btn-danger" onclick="clearSelection()" title="Clear selection">Clear Selection</button>
    </div>
    <!-- Row 2: Actions on Selection -->
    <div class="batch-bar-row batch-bar-row-actions" id="batchActionsRow">
      <button type="button" class="btn btn-sm" onclick="openPropertiesForSelection()" title="View Item Properties (Alt+Enter)">
        ℹ️ Properties
      </button>
      <button type="button" class="btn btn-sm btn-primary" onclick="downloadSelectedZip()" title="Download all selected files &amp; folders as a single ZIP">
        📦 Download Selected (ZIP)
      </button>
      <div id="batchWriteButtons" class="batch-write-group">
        <button type="button" class="btn btn-sm btn-primary" onclick="openBatchRenameModal()" title="Rename selected items (F2)">
          ✏️ Batch Rename
        </button>
        <button type="button" class="btn btn-sm btn-emerald" onclick="stageClipboardFromSelection('copy')" title="Copy selected items (Ctrl+C)">
          📋 Copy
        </button>
        <button type="button" class="btn btn-sm btn-amber" onclick="stageClipboardFromSelection('move')" title="Cut / Move selected items (Ctrl+X)">
          ✂️ Move
        </button>
        <button type="button" class="btn btn-sm" onclick="openDestinationModalForSelection()" title="Choose destination folder to Copy or Move">
          📂 Copy / Move To...
        </button>
        <button type="button" class="btn btn-sm btn-danger" onclick="batchDeleteSelected()" title="Permanently delete selected items (Delete)">
          🗑️ Delete Selected
        </button>
      </div>
    </div>
  </div>

  <!-- Clipboard / Paste Bar (appears when items are staged for Copy or Move) -->
  <div class="clipboard-bar" id="clipboardBar">
    <div class="bar-left">
      <span id="clipboardStatusText">📋 0 item(s) ready</span>
    </div>
    <div class="bar-right">
      <button class="btn btn-sm btn-emerald" id="pasteHereBtn" onclick="executePasteTo(currentPath)" title="Paste into current folder (Ctrl+V)">
        📥 Paste Here
      </button>
      <button class="btn btn-sm btn-primary" onclick="openDestinationModalForClipboard()">
        📂 Pick Destination Folder...
      </button>
      <button class="btn btn-sm btn-danger" onclick="clearClipboard()">
        ✕ Cancel
      </button>
    </div>
  </div>

  <input type="file" id="fileInput" multiple style="display:none" onchange="handleFilesSelected(this.files)">
  <input type="file" id="folderInput" webkitdirectory directory multiple style="display:none" onchange="handleFolderSelected(this.files)">

  <div class="upload-panel" id="uploadPanel">
    <div class="upload-header">
      <div style="display:flex;align-items:center;gap:10px;min-width:0;flex:1;">
        <span id="uploadStatusText" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;">Uploading...</span>
      </div>
      <div style="display:flex;align-items:center;gap:10px;flex-shrink:0;">
        <span id="uploadPercentText" style="font-variant-numeric:tabular-nums;font-weight:700;">0%</span>
        <button type="button" class="btn btn-sm btn-danger" id="opCancelBtn" onclick="requestCancelCurrentOperation()" style="padding:3px 10px;font-size:12px;height:26px;display:none;align-items:center;gap:4px;" title="Cancel ongoing operation">
          <span>✕</span><span>Cancel</span>
        </button>
      </div>
    </div>
    <div class="progress-track">
      <div class="progress-fill" id="uploadProgressBar" style="width:0%"></div>
    </div>
    <div class="upload-meta-row">
      <div id="uploadSubText" style="overflow:hidden;text-overflow:ellipsis;white-space:nowrap;min-width:0;flex:1;"></div>
      <div class="upload-timer-counters" id="uploadTimerCounters" style="display:inline-flex;">
        <span class="timer-badge timer-elapsed" id="uploadTimeElapsed" title="Elapsed Time">
          <span style="opacity:0.75;font-size:11px;">⏱️</span><span id="uploadTimeElapsedText">00:00 elapsed</span>
        </span>
        <span style="opacity:0.4;">•</span>
        <span class="timer-badge timer-remaining" id="uploadTimeRemaining" style="color:var(--primary);" title="Estimated Remaining Time">
          <span style="opacity:0.85;font-size:11px;">⏳</span><span id="uploadTimeRemainingText">--:-- remaining</span>
        </span>
      </div>
    </div>
  </div>

  <div class="file-card" id="fileCardWorkspace" oncontextmenu="onWorkspaceContextMenu(event)">
    <!-- Pagination Bar (Top) -->
    <div class="pagination-bar pagination-bar-top" id="paginationBarTop" style="display:none;">
      <div id="paginationInfoTop" style="color:var(--text-muted);font-weight:500;">Page 1 of 1</div>
      <div class="page-buttons" id="paginationButtonsTop"></div>
    </div>

    <!-- List View Table -->
    <div id="listViewWrapper">
      <table>
        <thead>
          <tr>
            <th style="width:42px;text-align:center;" onclick="event.stopPropagation()">
              <input type="checkbox" id="masterCheckbox" class="chk" onchange="selectCurrentPage(this.checked)" title="Select Current Page">
            </th>
            <th onclick="setSort('name')">Name <span id="sort-name">▲</span></th>
            <th onclick="setSort('date')" style="width:165px;white-space:nowrap;">Modified <span id="sort-date"></span></th>
            <th onclick="setSort('size')" style="width:135px;white-space:nowrap;">Size <span id="sort-size"></span></th>
            <th style="text-align:right;width:250px;">Actions</th>
          </tr>
        </thead>
        <tbody id="fileTableBody">
          <tr><td colspan="5" class="empty-state">Loading directory...</td></tr>
        </tbody>
      </table>
    </div>

    <!-- Grid View Container -->
    <div class="grid-container" id="gridViewWrapper"></div>

    <!-- Pagination Bar -->
    <div class="pagination-bar" id="paginationBar" style="display:none;">
      <div id="paginationInfo" style="color:var(--text-muted);font-weight:500;">Page 1 of 1</div>
      <div class="page-buttons" id="paginationButtons"></div>
    </div>
  </div>

  <footer class="footer-credit-box">
    <div class="footer-credit-line">
      Designed &amp; Developed by "<a class="footer-credit-link" href="https://mehdisayyadnahed.ir" target="_blank" rel="noopener noreferrer">Mehdi SayyadNahed</a>"
    </div>
    <div class="footer-credit-line">
      Submit suggestions for the improvement and development of this App on <a class="footer-credit-link" href="https://github.com/mehdisayyadnahed/universal-ftp-server" target="_blank" rel="noopener noreferrer"><span>GitHub</span><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" aria-hidden="true"><path d="M12,2C6.477,2 2,6.484 2,12.017c0,4.425 2.865,8.18 6.839,9.504c0.5,0.092 0.682,-0.217 0.682,-0.483c0,-0.237 -0.008,-0.868 -0.013,-1.703c-2.782,0.605 -3.369,-1.343 -3.369,-1.343c-0.454,-1.158 -1.11,-1.466 -1.11,-1.466c-0.908,-0.62 0.069,-0.608 0.069,-0.608c1.003,0.07 1.53,1.032 1.53,1.032c0.892,1.53 2.341,1.088 2.91,0.832c0.092,-0.647 0.35,-1.088 0.636,-1.338c-2.22,-0.253 -4.555,-1.113 -4.555,-4.951c0,-1.093 0.39,-1.988 1.029,-2.688c-0.103,-0.253 -0.446,-1.272 0.098,-2.65c0,0 0.84,-0.27 2.75,1.026A9.564,9.564 0 0,1 12,6.844c0.85,0.004 1.705,0.115 2.504,0.337c1.909,-1.296 2.747,-1.027 2.747,-1.027c0.546,1.379 0.202,2.398 0.1,2.651c0.64,0.7 1.028,1.595 1.028,2.688c0,3.848 -2.339,4.695 -4.566,4.943c0.359,0.309 0.678,0.92 0.678,1.855c0,1.338 -0.012,2.419 -0.012,2.747c0,0.268 0.18,0.58 0.688,0.482A10.019,10.019 0 0,0 22,12.017C22,6.484 17.522,2 12,2z"/></svg></a>
    </div>
  </footer>
</div>

<!-- Media/Text Preview Modal -->
<div class="modal-backdrop" id="previewModal" onclick="if(event.target===this)closePreview()">
  <div class="modal-box">
    <div class="modal-header">
      <span id="previewTitle" style="word-break:break-all;">Preview</span>
      <div style="display:flex;gap:8px;align-items:center;">
        <a id="previewDownloadBtn" class="btn btn-sm btn-primary" href="#" download>⬇ Download</a>
        <button id="previewPopOutBtn" class="btn btn-sm" onclick="popOutPreview()" title="Open in persistent floating window (stays visible while browsing folders)">⧉ Pop Out</button>
        <button class="btn btn-sm" onclick="closePreview()">✕ Close</button>
      </div>
    </div>
    <div class="modal-body center-content" id="previewBody"></div>
  </div>
</div>

<!-- Persistent Floating / Pop-Out Preview Window -->
<div class="preview-floating-window" id="previewFloatingWindow" tabindex="-1">
  <div class="preview-floating-header" id="previewFloatingHeader">
    <div class="preview-floating-title-box">
      <span style="font-size:14px;line-height:1;">👁️</span>
      <span class="preview-floating-title" id="previewFloatingTitle">Preview</span>
    </div>
    <div class="preview-floating-actions">
      <a id="previewFloatingDownloadBtn" class="btn btn-sm btn-primary" href="#" download style="padding:4px 9px;font-size:11px;" title="Download File">⬇</a>
      <button class="btn btn-sm" onclick="popInPreview()" style="padding:4px 9px;font-size:11px;" title="Dock back into centered modal">🗗</button>
      <button class="btn btn-sm" onclick="closeFloatingPreview()" style="padding:4px 9px;font-size:11px;" title="Close Floating Window">✕</button>
    </div>
  </div>
  <div class="preview-floating-body" id="previewFloatingBody"></div>
  <!-- 8-Directional Resize Handles -->
  <div class="preview-resize-handle handle-e" data-resize-dir="e"></div>
  <div class="preview-resize-handle handle-s" data-resize-dir="s"></div>
  <div class="preview-resize-handle handle-w" data-resize-dir="w"></div>
  <div class="preview-resize-handle handle-n" data-resize-dir="n"></div>
  <div class="preview-resize-handle handle-se" data-resize-dir="se"></div>
  <div class="preview-resize-handle handle-sw" data-resize-dir="sw"></div>
  <div class="preview-resize-handle handle-ne" data-resize-dir="ne"></div>
  <div class="preview-resize-handle handle-nw" data-resize-dir="nw"></div>
</div>

<!-- Destination Folder Picker Modal for Copy / Move -->
<div class="modal-backdrop" id="destModal" onclick="if(event.target===this)closeDestModal()">
  <div class="modal-box" style="max-width:560px;">
    <div class="modal-header">
      <span id="destModalTitle">Select Destination Folder</span>
      <button class="btn btn-sm" onclick="closeDestModal()">✕ Close</button>
    </div>
    <div class="modal-body">
      <div style="font-size:13px;color:var(--text-muted);margin-bottom:6px;">
        Target Folder: <strong id="destModalCurrentPath" style="color:var(--primary);font-family:monospace;">/</strong>
      </div>
      <div class="folder-picker-list" id="destFolderList"></div>
      <div style="display:flex;justify-content:space-between;align-items:center;gap:8px;flex-wrap:wrap;">
        <button class="btn btn-sm" onclick="promptNewFolderInDestModal()">📁➕ New Subfolder</button>
        <div style="display:flex;gap:8px;">
          <button class="btn btn-emerald" onclick="confirmDestModalAction('copy')">📋 Copy Here</button>
          <button class="btn btn-amber" onclick="confirmDestModalAction('move')">✂️ Move Here</button>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Custom Styled Dialog Modal (Rename, New Folder, Delete Confirmation, Alerts) -->
<div class="modal-backdrop" id="dialogModal" onclick="if(event.target===this)resolveDialogModal(null)">
  <div class="modal-box dialog-card" id="dialogCard">
    <div class="modal-body" style="padding:22px 22px 16px 22px;">
      <div class="dialog-top">
        <div class="dialog-icon-badge icon-badge-primary" id="dialogIconBadge">✏️</div>
        <div style="flex:1;min-width:0;">
          <div class="dialog-title" id="dialogTitle">Dialog</div>
          <div class="dialog-subtitle" id="dialogSubtitle"></div>
        </div>
      </div>
      <div class="dialog-target-box" id="dialogTargetBox" style="display:none;">
        <span class="dialog-target-label" id="dialogTargetLabel">Item</span>
        <span class="dialog-target-val" id="dialogTargetVal"></span>
      </div>
      <div class="dialog-warning-banner" id="dialogWarningBanner" style="display:none;">
        <span>⚠️</span>
        <span id="dialogWarningText"></span>
      </div>
      <div class="dialog-field-group" id="dialogInputGroup" style="display:none;">
        <label class="dialog-field-label" id="dialogInputLabel" for="dialogInput">Value</label>
        <div class="dialog-input-wrap">
          <input type="text" id="dialogInput" class="dialog-input" autocomplete="off">
        </div>
      </div>
    </div>
    <div class="dialog-footer">
      <button type="button" class="btn" id="dialogCancelBtn" onclick="resolveDialogModal(null)">Cancel</button>
      <button type="button" class="btn btn-primary" id="dialogConfirmBtn" onclick="confirmDialogModal()">Confirm</button>
    </div>
  </div>
</div>

<!-- File Conflict Resolution Modal (Cancel / Skip / Overwrite / Rename) -->
<div class="modal-backdrop" id="conflictModal">
  <div class="modal-box dialog-card conflict-card" id="conflictCard">
    <div class="modal-body" style="padding:22px 22px 16px 22px;">
      <div class="dialog-top">
        <div class="dialog-icon-badge icon-badge-amber">⚠️</div>
        <div style="flex:1;min-width:0;">
          <div class="dialog-title" id="conflictTitle">File Already Exists</div>
          <div class="dialog-subtitle" id="conflictSubtitle">A file with the same name already exists in this folder on your device. Choose what you would like to do:</div>
        </div>
      </div>

      <div class="dialog-target-box" style="margin-bottom:10px;">
        <span class="dialog-target-label" id="conflictPathLabel">Conflicting File</span>
        <span class="dialog-target-val" id="conflictPathVal"></span>
      </div>

      <div class="conflict-compare-grid">
        <div class="conflict-meta-box">
          <div class="conflict-meta-title">📱 Existing on Phone</div>
          <div class="conflict-meta-val" id="conflictExistingSize">--</div>
          <div class="conflict-meta-sub" id="conflictExistingDate">--</div>
        </div>
        <div class="conflict-meta-box">
          <div class="conflict-meta-title" id="conflictIncomingLabel">📤 New File</div>
          <div class="conflict-meta-val" id="conflictNewSize">--</div>
          <div class="conflict-meta-sub" id="conflictNewDate">--</div>
        </div>
      </div>

      <div class="dialog-warning-banner" id="conflictErrorBanner" style="display:none;">
        <span>⚠️</span>
        <span id="conflictErrorText">Please enter a different file name.</span>
      </div>

      <div class="dialog-field-group" style="margin-bottom:10px;">
        <label class="dialog-field-label" for="conflictRenameInput">New File Name (if choosing Rename):</label>
        <div class="dialog-input-wrap">
          <input type="text" id="conflictRenameInput" class="dialog-input" style="padding-right:84px;" placeholder="Enter a new file name..." autocomplete="off">
          <button type="button" class="dialog-input-action" onclick="resetConflictSuggestedName()" title="Use Suggested Name" style="font-size:12px;font-weight:600;color:var(--primary);">Auto Name</button>
        </div>
      </div>

      <label class="conflict-apply-all" id="conflictApplyAllWrap" style="display:none;">
        <input type="checkbox" id="conflictApplyAllCheckbox" class="item-checkbox">
        <span id="conflictApplyAllLabel">Apply this choice to all remaining conflicts</span>
      </label>
    </div>

    <div class="conflict-footer">
      <div class="conflict-footer-row">
        <button type="button" class="btn btn-amber" id="conflictOverwriteBtn" onclick="submitConflictDecision('overwrite')" title="Replace the existing file on the device">🔄 Overwrite Existing</button>
        <button type="button" class="btn btn-primary" id="conflictRenameBtn" onclick="submitConflictDecision('rename')" title="Save the new file with the new name above">✏️ Save With New Name</button>
      </div>
      <div class="conflict-footer-row">
        <button type="button" class="btn" id="conflictSkipBtn" style="display:none;" onclick="submitConflictDecision('skip')" title="Skip only this file and continue">⏭️ Skip File</button>
        <button type="button" class="btn btn-danger" id="conflictCancelBtn" onclick="submitConflictDecision('cancel')" title="Abort the entire operation">✕ Cancel Process</button>
      </div>
    </div>
  </div>
</div>

<!-- Rename Conflict Resolution Modal (Cancel / Append Number / Enter New Name) -->
<div class="modal-backdrop" id="renameConflictModal">
  <div class="modal-box dialog-card conflict-card" id="renameConflictCard">
    <div class="modal-body" style="padding:22px 22px 16px 22px;">
      <div class="dialog-top">
        <div class="dialog-icon-badge icon-badge-amber">⚠️</div>
        <div style="flex:1;min-width:0;">
          <div class="dialog-title" id="renameConflictTitle">Name Already Exists</div>
          <div class="dialog-subtitle" id="renameConflictSubtitle">Another item with this name already exists in this folder. Choose how you would like to proceed:</div>
        </div>
      </div>

      <div class="conflict-compare-grid">
        <div class="conflict-meta-box">
          <div class="conflict-meta-title">🏷️ Current Name</div>
          <div class="conflict-meta-val" id="renameConflictOldName" style="word-break:break-all;">--</div>
          <div class="conflict-meta-sub" id="renameConflictFolderSub">--</div>
        </div>
        <div class="conflict-meta-box">
          <div class="conflict-meta-title">🔢 Auto-Numbered Option</div>
          <div class="conflict-meta-val" id="renameConflictSuggestedVal" style="color:var(--emerald);word-break:break-all;">--</div>
          <div class="conflict-meta-sub">Appends sequential number in parentheses</div>
        </div>
      </div>

      <div class="dialog-warning-banner" id="renameConflictErrorBanner" style="display:none;">
        <span>⚠️</span>
        <span id="renameConflictErrorText">Please enter a different name that does not already exist.</span>
      </div>

      <div class="dialog-field-group" style="margin-bottom:6px;">
        <label class="dialog-field-label" for="renameConflictInput">Or Enter a New Custom Name:</label>
        <div class="dialog-input-wrap">
          <input type="text" id="renameConflictInput" class="dialog-input" style="padding-right:92px;" placeholder="Enter a new name..." autocomplete="off">
          <button type="button" class="dialog-input-action" onclick="fillRenameConflictSuggested()" title="Fill with Auto-Numbered Name" style="font-size:12px;font-weight:600;color:var(--primary);">Use (1)</button>
        </div>
      </div>
    </div>

    <div class="conflict-footer">
      <div class="conflict-footer-row">
        <button type="button" class="btn btn-emerald" id="renameConflictAppendBtn" onclick="submitRenameConflictDecision('append')" title="Automatically append a number in parentheses to the end">🔢 Append Number</button>
        <button type="button" class="btn btn-primary" id="renameConflictCustomBtn" onclick="submitRenameConflictDecision('custom')" title="Rename using the custom name typed above">✏️ Save New Name</button>
      </div>
      <div class="conflict-footer-row">
        <button type="button" class="btn btn-danger" onclick="submitRenameConflictDecision('cancel')" title="Cancel renaming completely">✕ Cancel Process</button>
      </div>
    </div>
  </div>
</div>

<!-- OS Clipboard Paste Helper Modal (when Paste is clicked from context menu for OS files) -->
<div class="modal-backdrop" id="osPasteModal" onclick="if(event.target===this)closeOsPasteModal()">
  <div class="modal-box dialog-card" id="osPasteCard">
    <div class="modal-body" style="padding:22px 22px 16px 22px;">
      <div class="dialog-top">
        <div class="dialog-icon-badge icon-badge-primary">📋</div>
        <div style="flex:1;min-width:0;">
          <div class="dialog-title">Paste Files from OS Clipboard</div>
          <div class="dialog-subtitle">Press <kbd class="kbd-badge">Ctrl</kbd> + <kbd class="kbd-badge">V</kbd> on your keyboard right now to immediately upload the file(s) copied in Windows / your OS into <strong id="osPasteTargetFolder" style="color:var(--primary);font-family:monospace;">/</strong>.</div>
        </div>
      </div>
      <div class="batch-info-banner" style="margin-bottom:8px;">
        <span>💡</span>
        <span>Tip: You can press <strong>Ctrl + V</strong> anywhere on the page at any time to upload files copied from your computer!</span>
      </div>
    </div>
    <div class="dialog-footer" style="display:flex;gap:8px;flex-wrap:wrap;justify-content:flex-end;">
      <button type="button" class="btn btn-emerald" onclick="closeOsPasteModal();document.getElementById('fileInput').click()">📄 Browse Files</button>
      <button type="button" class="btn btn-primary" onclick="closeOsPasteModal();document.getElementById('folderInput').click()">📁 Upload Folder</button>
      <button type="button" class="btn" onclick="closeOsPasteModal()">Close</button>
    </div>
  </div>
</div>

<!-- Batch Rename Modal (Base Name + Sequential Numbering + Live Preview) -->
<div class="modal-backdrop" id="batchRenameModal" onclick="if(event.target===this)closeBatchRenameModal()">
  <div class="modal-box dialog-card batch-rename-card" id="batchRenameCard">
    <div class="modal-body" style="padding:22px 22px 16px 22px;">
      <div class="dialog-top">
        <div class="dialog-icon-badge icon-badge-primary">✏️</div>
        <div style="flex:1;min-width:0;">
          <div class="dialog-title">Batch Rename Selected Items</div>
          <div class="dialog-subtitle">Enter a base name and numbering style. Each file's original extension is preserved automatically.</div>
        </div>
      </div>

      <div class="dialog-warning-banner" id="batchRenameErrorBanner" style="display:none;">
        <span>⚠️</span>
        <span id="batchRenameErrorText">Please enter a valid base name.</span>
      </div>

      <div class=" dialog-field-group" style="margin-bottom:12px;">
        <label class="dialog-field-label" for="batchRenameBaseInput">Base Name:</label>
        <div class="dialog-input-wrap">
          <input type="text" id="batchRenameBaseInput" class="dialog-input" style="padding-right:42px;" placeholder="e.g. Holiday, Document, Track..." autocomplete="off" oninput="updateBatchRenamePreview()">
          <button type="button" class="dialog-input-action" onclick="clearBatchRenameBaseInput()" title="Clear">✕</button>
        </div>
      </div>

      <div class="batch-rename-grid">
        <div class="dialog-field-group" style="margin-bottom:0;">
          <label class="dialog-field-label" for="batchRenameFormatSelect">Numbering Format:</label>
          <select id="batchRenameFormatSelect" class="dialog-input" style="cursor:pointer;" onchange="updateBatchRenamePreview()">
            <option value="paren">Name (1), Name (2)</option>
            <option value="underscore">Name_1, Name_2</option>
            <option value="pad2">Name_01, Name_02</option>
            <option value="pad3">Name_001, Name_002</option>
            <option value="dash">Name-001, Name-002</option>
            <option value="space">Name 1, Name 2</option>
          </select>
        </div>
        <div class="dialog-field-group" style="margin-bottom:0;">
          <label class="dialog-field-label" for="batchRenameStartInput">Start Number:</label>
          <input type="number" id="batchRenameStartInput" class="dialog-input" min="1" max="999999" value="1" oninput="updateBatchRenamePreview()">
        </div>
      </div>

      <div class="batch-info-banner" id="batchRenameSkipNotice" style="display:none;">
        <span>ℹ️</span>
        <span id="batchRenameSkipNoticeText">Existing filenames in this folder were automatically skipped to avoid collisions.</span>
      </div>

      <div class="batch-preview-header">
        <span>Live Preview (Old Name → New Name)</span>
        <span class="badge badge-http" id="batchRenameCountBadge" style="font-size:11px;padding:3px 9px;">0 items</span>
      </div>
      <div class="batch-preview-list" id="batchRenamePreviewList"></div>
    </div>

    <div class="dialog-footer">
      <button type="button" class="btn" onclick="closeBatchRenameModal()">Cancel</button>
      <button type="button" class="btn btn-primary" id="batchRenameSubmitBtn" onclick="submitBatchRename()">✏️ Rename Items</button>
    </div>
  </div>
</div>

<!-- Custom Styled Authentication / Login Modal (Replaces Browser Basic Auth Popup) -->
<div class="modal-backdrop" id="authModal">
  <div class="modal-box dialog-card" id="authCard">
    <div class="modal-body" style="padding:24px 22px 0 22px;">
      <div class="dialog-top">
        <div class="dialog-icon-badge icon-badge-primary">🔐</div>
        <div style="flex:1;min-width:0;">
          <div class="dialog-title">Authentication Required</div>
          <div class="dialog-subtitle">Enter your HTTP server username and password to access files</div>
        </div>
      </div>
      <div class="dialog-warning-banner" id="authErrorBanner" style="display:none;">
        <span>🚫</span>
        <span id="authErrorText">Invalid username or password</span>
      </div>
      <form id="authForm" onsubmit="submitAuthLogin(event)" style="display:flex;flex-direction:column;gap:2px;">
        <div class="dialog-field-group">
          <label class="dialog-field-label" for="authUsernameInput">Username</label>
          <div class="dialog-input-wrap">
            <input type="text" id="authUsernameInput" class="dialog-input" placeholder="Enter username" autocomplete="username" required>
          </div>
        </div>
        <div class="dialog-field-group" style="margin-bottom:0;">
          <label class="dialog-field-label" for="authPasswordInput">Password</label>
          <div class="dialog-input-wrap">
            <input type="password" id="authPasswordInput" class="dialog-input" style="padding-right:42px;" placeholder="Enter password" autocomplete="current-password" required>
            <button type="button" class="dialog-input-action" id="togglePassBtn" onclick="toggleAuthPasswordVisibility()" title="Show / Hide Password">👁️</button>
          </div>
        </div>
        <div style="display:flex;align-items:center;margin-top:14px;margin-bottom:14px;">
          <label style="display:inline-flex;align-items:center;gap:8px;cursor:pointer;font-size:13px;font-weight:500;user-select:none;color:var(--text-secondary,#94a3b8);">
            <input type="checkbox" id="authRememberMe" style="width:16px;height:16px;cursor:pointer;accent-color:var(--primary,#3b82f6);border-radius:4px;">
            <span>Remember Me</span>
          </label>
        </div>
      </form>
    </div>
    <div class="dialog-footer">
      <button type="button" class="btn" onclick="toggleTheme()" style="margin-right:auto;" title="Switch Theme">🌓 Theme</button>
      <button type="submit" form="authForm" class="btn btn-primary" id="authSubmitBtn">🔓 Sign In</button>
    </div>
  </div>
</div>

<!-- Item / Selection Properties Modal (Alt+Enter) -->
<div class="modal-backdrop" id="propertiesModal" onclick="if(event.target===this)closePropertiesModal()">
  <div class="modal-box dialog-card" style="max-width:510px;">
    <div class="modal-body" style="padding:22px 22px 16px 22px;">
      <div class="dialog-top">
        <div class="dialog-icon-badge icon-badge-primary" id="propIconBadge">ℹ️</div>
        <div style="flex:1;min-width:0;">
          <div class="dialog-title" id="propTitle" style="word-break:break-all;">Properties</div>
          <div class="dialog-subtitle" id="propSubtitle">Item details &amp; storage metadata</div>
        </div>
      </div>
      <div class="prop-list" id="propListBody"></div>
    </div>
    <div class="dialog-footer">
      <button type="button" class="btn" id="propCopyPathBtn" onclick="copyPropertiesPath()" style="margin-right:auto;">📋 Copy Path</button>
      <button type="button" class="btn btn-primary" onclick="closePropertiesModal()">Done</button>
    </div>
  </div>
</div>

<!-- Web Portal Settings Modal Popup -->
<div class="modal-backdrop" id="settingsModal" onclick="if(event.target===this)closeSettingsModal()">
  <div class="modal-box dialog-card settings-modal-card">
    <div class="modal-header">
      <span style="display:flex;align-items:center;gap:8px;">⚙️ Settings</span>
      <button class="btn btn-sm" onclick="closeSettingsModal()" title="Close (Esc)">✕ Close</button>
    </div>
    <div class="modal-body" style="padding:16px 18px;gap:10px;">
      <div class="settings-options-list">
        <!-- Option 1: Theme -->
        <div class="settings-option-item" onclick="toggleTheme(); updateSettingsModalUi();" title="Toggle between Dark and Light mode">
          <div class="settings-option-info">
            <span class="settings-option-icon">🌓</span>
            <div class="settings-option-text">
              <span class="settings-option-title">Theme</span>
              <span class="settings-option-desc">Toggle Dark / Light appearance</span>
            </div>
          </div>
          <button type="button" class="btn btn-sm" id="btnModalToggleTheme" onclick="event.stopPropagation(); toggleTheme(); updateSettingsModalUi();" title="Switch Theme">🌓 Theme</button>
        </div>

        <!-- Option 2: Thumbnails -->
        <div class="settings-option-item" onclick="toggleMediaThumbnails(); updateSettingsModalUi();" title="Toggle image &amp; video thumbnails in icon and tile views">
          <div class="settings-option-info">
            <span class="settings-option-icon">🖼️</span>
            <div class="settings-option-text">
              <span class="settings-option-title">Thumbnails</span>
              <span class="settings-option-desc">Image &amp; video media previews</span>
            </div>
          </div>
          <button type="button" class="btn btn-sm" id="btnToggleThumbnails" onclick="event.stopPropagation(); toggleMediaThumbnails(); updateSettingsModalUi();" title="Show or hide image &amp; video thumbnails in icon/tile views (Default: Off)">🖼️ Thumbnails: Off</button>
        </div>

        <!-- Option 3: Reset Web Portal / Theme -->
        <div class="settings-option-item" onclick="closeSettingsModal(); promptResetWebSettings();" title="Reset all web portal appearance &amp; layout preferences to defaults">
          <div class="settings-option-info">
            <span class="settings-option-icon">↺</span>
            <div class="settings-option-text">
              <span class="settings-option-title">Reset Web Portal</span>
              <span class="settings-option-desc">Restore appearance &amp; layout to defaults</span>
            </div>
          </div>
          <button type="button" class="btn btn-sm btn-amber" id="btnResetSettings" onclick="event.stopPropagation(); closeSettingsModal(); promptResetWebSettings();" title="Reset all web portal appearance &amp; layout settings to defaults">↺ Reset Web Portal</button>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Comprehensive Web Portal Manual & How to Use Modal -->
<div class="modal-backdrop" id="shortcutsModal" onclick="if(event.target===this)closeShortcutsModal()">
  <div class="modal-box" style="max-width:860px;">
    <div class="modal-header">
      <span>📑 Universal File Server — Web Portal Manual &amp; How to Use</span>
      <button class="btn btn-sm" onclick="closeShortcutsModal()">✕ Close</button>
    </div>
    <div class="guide-tabs" id="guideTabsBar">
      <button type="button" class="guide-tab-btn active" data-tab="overview" onclick="switchGuideTab('overview')">🚀 Overview &amp; Layout</button>
      <button type="button" class="guide-tab-btn" data-tab="views" onclick="switchGuideTab('views')">👁️ 7 View Modes &amp; Media</button>
      <button type="button" class="guide-tab-btn" data-tab="navigation" onclick="switchGuideTab('navigation')">🧭 Navigation &amp; Type-Ahead</button>
      <button type="button" class="guide-tab-btn" data-tab="selection" onclick="switchGuideTab('selection')">🖱️ Selection &amp; Marquee</button>
      <button type="button" class="guide-tab-btn" data-tab="transfers" onclick="switchGuideTab('transfers')">📂 Transfers, Drag &amp; Clipboard</button>
      <button type="button" class="guide-tab-btn" data-tab="shortcuts" onclick="switchGuideTab('shortcuts')">⌨️ Keyboard Shortcuts</button>
    </div>
    <div class="modal-body" style="gap:14px;max-height:72vh;">
      <!-- Tab 1: Overview & Layout -->
      <div class="guide-pane active" id="guide-pane-overview">
        <div class="guide-card-grid">
          <div class="guide-feature-card">
            <div class="guide-feature-title">🖥️ Full-Screen Desktop Workspace</div>
            <div class="guide-feature-desc">The Web Portal spans 100% of your browser viewport, automatically matching your system's Dark/Light mode on first visit (<strong>🌓 Theme</strong>) and providing a one-click <strong>↺ Reset</strong> button to restore default layout settings.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">🟢 Live Connection &amp; Activity Monitor</div>
            <div class="guide-feature-desc">The header pill shows real-time server connectivity (<strong>Online / Offline</strong>) and the total count of active browser tabs plus ongoing uploads and downloads, synchronized with the Android app.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">💾 Storage Meter &amp; Access Badge</div>
            <div class="guide-feature-desc">Monitor used and free device storage at a glance. The badge indicates whether the server is running in <strong>Read &amp; Write</strong> mode (full management) or <strong>Read-Only</strong> mode (safe browsing/downloading).</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">🖱️ Context-Aware Right-Click Menus</div>
            <div class="guide-feature-desc">Right-click any file or folder for item-specific actions (Preview, Download, Copy, Move, Rename, Delete, Properties). Right-click empty workspace areas to change View, Sort, Group, Paste, or create new items.</div>
          </div>
        </div>
      </div>

      <!-- Tab 2: 7 View Modes & Media -->
      <div class="guide-pane" id="guide-pane-views">
        <div class="guide-card-grid">
          <div class="guide-feature-card">
            <div class="guide-feature-title">☰ Details &amp; ≣ List Views</div>
            <div class="guide-feature-desc"><strong>Details (Default)</strong> displays a full sortable table with Name, Modified Date, Size, and Actions. <strong>List</strong> arranges items in a sleek, multi-column compact list for high-density browsing.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">🖼️ Extra Large, Large, Medium &amp; Small Icons</div>
            <div class="guide-feature-desc">Choose from four icon densities in the <strong>View</strong> menu: <strong>Extra large icons</strong> (oversized previews), <strong>Large icons</strong>, <strong>Medium icons</strong>, or <strong>Small icons</strong> (compact horizontal chips).</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">▤ Tiles Layout</div>
            <div class="guide-feature-desc"><strong>Tiles</strong> renders side-by-side cards with a left icon/thumbnail and a 3-line summary (Name, Type, Size) for quick visual scanning alongside key file metadata.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">🖼️ Thumbnails Toggle &amp; Ctrl+Scroll Zoom</div>
            <div class="guide-feature-desc">Click <strong>🖼️ Thumbnails: Off/On</strong> in the header to toggle live image/video previews. Hold <kbd class="kbd-badge">Ctrl</kbd> + <strong>Mouse Wheel Scroll</strong> to smoothly zoom across all 7 view modes—and all your view, sort, group, theme, and page-size preferences are automatically saved in browser cookies.</div>
          </div>
        </div>
      </div>

      <!-- Tab 3: Navigation, Search & Type-Ahead -->
      <div class="guide-pane" id="guide-pane-navigation">
        <div class="guide-card-grid">
          <div class="guide-feature-card">
            <div class="guide-feature-title">🔤 Keyboard Type-Ahead Jumping</div>
            <div class="guide-feature-desc">Just like in Windows Explorer, type any letter(s) on your keyboard while browsing to jump straight to the matching file or folder. Type quickly (e.g. <code>au</code>) to match prefixes, or press the same letter repeatedly to cycle through matches—even across pages!</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">📂 Breadcrumbs &amp; Editable Address Bar</div>
            <div class="guide-feature-desc">Click any breadcrumb segment to jump to that parent folder, or click the empty address bar / <strong>✏️ Edit</strong> button to type a folder path manually. Use <strong>◀ Back</strong>, <strong>▶ Forward</strong>, and <strong>▲ Up</strong> for history navigation.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">🔍 Instant &amp; Recursive Subfolder Search</div>
            <div class="guide-feature-desc">Use the search box (<kbd class="kbd-badge">Ctrl+F</kbd> or <kbd class="kbd-badge">F3</kbd>) to filter the current folder, or switch to <strong>🗂️ + Subfolders</strong> to search recursively across all nested directories.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">🗂️ Sort, Group By &amp; On-Demand Folder Size</div>
            <div class="guide-feature-desc">Sort by Name, Size, or Date, and group items into collapsible sections by <strong>Name</strong>, <strong>Date Modified</strong>, <strong>File Type</strong>, or <strong>Size</strong>. Click <strong>Calculate Size</strong> on any folder to compute its recursive size.</div>
          </div>
        </div>
      </div>

      <!-- Tab 4: Selection & Mouse Marquee -->
      <div class="guide-pane" id="guide-pane-selection">
        <div class="guide-card-grid">
          <div class="guide-feature-card">
            <div class="guide-feature-title">🖱️ Mouse Drag-Box (Marquee) Selection</div>
            <div class="guide-feature-desc">Click and hold the left mouse button on any empty space in the file workspace (or non-button background area) and drag across items to draw a selection rectangle. Hold <kbd class="kbd-badge">Ctrl</kbd> or <kbd class="kbd-badge">Shift</kbd> while dragging to add to your existing selection.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">⇄ Invert Selection, Select Page &amp; Clear Selection</div>
            <div class="guide-feature-desc">Use <strong>Select Page</strong> to check all items on the current page, <strong>Select All in Folder</strong> (<kbd class="kbd-badge">Ctrl+A</kbd>) to select every item in the folder, <strong>Invert Selection</strong> to flip selected and unselected items on the current page, or <strong>Clear Selection</strong> to deselect all items.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">⌨️ Ctrl+Click &amp; Shift+Click Range Selection</div>
            <div class="guide-feature-desc">Hold <kbd class="kbd-badge">Ctrl</kbd> and click any item to toggle its selection state individually. Hold <kbd class="kbd-badge">Shift</kbd> and click another item to select the entire contiguous range between the two items.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">✏️ Batch Operations &amp; Sequential Rename</div>
            <div class="guide-feature-desc">When items are selected, the Batch Bar unlocks one-click <strong>Download Selected (ZIP)</strong>, <strong>Batch Rename</strong> (with live preview &amp; numbering formats), <strong>Copy / Move</strong>, and <strong>Batch Delete</strong>.</div>
          </div>
        </div>
      </div>

      <!-- Tab 5: Transfers, Drag & Drop & Clipboard -->
      <div class="guide-pane" id="guide-pane-transfers">
        <div class="guide-card-grid">
          <div class="guide-feature-card">
            <div class="guide-feature-title">📋 Paste Files from OS Clipboard (Ctrl+V)</div>
            <div class="guide-feature-desc">Copy any file, folder, or screenshot on your computer (<kbd class="kbd-badge">Ctrl+C</kbd> in Windows Explorer) and press <kbd class="kbd-badge">Ctrl+V</kbd> (or right-click → <strong>Paste</strong>) inside the Web Portal to upload it directly into the current folder.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">📤 Drag &amp; Drop from Browser to Windows</div>
            <div class="guide-feature-desc">Drag any file from the Web Portal onto your Windows Desktop or File Explorer to transfer it directly (folders and multi-item selections automatically stream as a single <code>.zip</code> archive). In Chrome/Edge it writes directly to the target folder; in Firefox dropping outside the browser automatically downloads the file/ZIP.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">📥 Drag &amp; Drop Upload &amp; Internal Move</div>
            <div class="guide-feature-desc">Drag files or entire folder trees from your computer into the browser window to upload them with full subdirectory preservation. Drag items inside the portal onto any subfolder or breadcrumb to move them.</div>
          </div>
          <div class="guide-feature-card">
            <div class="guide-feature-title">⚠️ Smart Rename &amp; Upload Conflict Dialogs</div>
            <div class="guide-feature-desc">If a renamed item conflicts with an existing name, a dialog lets you <strong>Cancel Process</strong>, click <strong>Append Number: file (1).ext</strong>, or type a custom name. Upload and Copy/Move conflicts also offer Overwrite, Skip, or Rename.</div>
          </div>
        </div>
      </div>

      <!-- Tab 6: Keyboard & Mouse Shortcuts -->
      <div class="guide-pane" id="guide-pane-shortcuts">
        <div class="shortcuts-grid">
          <div class="shortcut-section">
            <div class="shortcut-section-title">🖱️ Selection &amp; Mouse</div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Rubber-band box selection</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Click &amp; Drag</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Toggle single item selection</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Ctrl</kbd> + <kbd class="kbd-badge">Click</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Select range between items</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Shift</kbd> + <kbd class="kbd-badge">Click</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Select all items in folder</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Ctrl</kbd> + <kbd class="kbd-badge">A</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Clear selection / Close modal</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Click Empty Space</kbd> / <kbd class="kbd-badge">Esc</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Zoom across 7 View Modes</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Ctrl</kbd> + <kbd class="kbd-badge">Mouse Scroll</kbd></span>
            </div>
          </div>

          <div class="shortcut-section">
            <div class="shortcut-section-title">🧭 Navigation &amp; Type-Ahead</div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Jump to item starting with letter</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">A</kbd>–<kbd class="kbd-badge">Z</kbd> / <kbd class="kbd-badge">0</kbd>–<kbd class="kbd-badge">9</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Previous / Next folder</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Alt</kbd> + <kbd class="kbd-badge">←</kbd> / <kbd class="kbd-badge">→</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Parent folder (One level up)</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Alt</kbd> + <kbd class="kbd-badge">↑</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Refresh current folder</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">F5</kbd></span>
            </div>
          </div>

          <div class="shortcut-section">
            <div class="shortcut-section-title">🛠️ File &amp; Clipboard Actions</div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Create new folder</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Alt</kbd>+<kbd class="kbd-badge">N</kbd> / <kbd class="kbd-badge">Ctrl</kbd>+<kbd class="kbd-badge">Shift</kbd>+<kbd class="kbd-badge">N</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Copy / Cut selected items</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Ctrl</kbd> + <kbd class="kbd-badge">C</kbd> / <kbd class="kbd-badge">X</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Paste (OS files or internal items)</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Ctrl</kbd> + <kbd class="kbd-badge">V</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Rename / Batch Rename</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">F2</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Delete selected item(s)</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Delete</kbd></span>
            </div>
          </div>

          <div class="shortcut-section">
            <div class="shortcut-section-title">👁️ Views, Inspector &amp; Help</div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Cycle through 7 View Modes</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Alt</kbd>+<kbd class="kbd-badge">V</kbd> / <kbd class="kbd-badge">Alt</kbd>+<kbd class="kbd-badge">Shift</kbd>+<kbd class="kbd-badge">6</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Item / Folder Properties</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Alt</kbd> + <kbd class="kbd-badge">Enter</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Toggle file Preview</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Alt</kbd> + <kbd class="kbd-badge">P</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Focus search input</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">Ctrl</kbd>+<kbd class="kbd-badge">F</kbd> / <kbd class="kbd-badge">F3</kbd></span>
            </div>
            <div class="shortcut-row">
              <span class="shortcut-desc">Open this Manual &amp; Help</span>
              <span class="shortcut-keys"><kbd class="kbd-badge">?</kbd> / <kbd class="kbd-badge">F1</kbd></span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="dialog-footer">
      <span style="font-size:12px;color:var(--text-muted);margin-right:auto;">Tip: Press <kbd class="kbd-badge">?</kbd> or <kbd class="kbd-badge">F1</kbd> anytime to open this Manual.</span>
      <button type="button" class="btn btn-primary" onclick="closeShortcutsModal()">Got It</button>
    </div>
  </div>
</div>

<!-- Mouse Marquee Rubber-Band Selection Rectangle -->
<div class="marquee-selection-box" id="marqueeSelectionBox"></div>

<!-- Type-Ahead Keyboard Jump Floating Pill -->
<div class="typeahead-indicator" id="typeaheadIndicator"></div>

<!-- Floating Context Menu for Grid & Right-Click Actions -->
<div class="context-menu" id="itemContextMenu" onclick="event.stopPropagation()"></div>

<!-- Non-blocking Styled Toast Container -->
<div class="toast-container" id="toastContainer"></div>

<script>
let currentPath = '/';
let currentItems = [];
let isReadOnly = false;
let sortField = 'name';
let sortAsc = true;
const serverRequiresAuth = ${!settings.isAnonymous};
let authToken = '';

// View mode (7 Windows Explorer modes), Media Thumbnails, Grouping, Search Scope & Pagination state
const VIEW_MODE_ORDER = [
  'extra_large_icons',
  'large_icons',
  'medium_icons',
  'small_icons',
  'list',
  'details',
  'tiles'
];
const VIEW_MODE_META = {
  extra_large_icons: { label: 'Extra large icons', icon: '🖼️', supportsThumbnails: true },
  large_icons:       { label: 'Large icons',       icon: '🖼️', supportsThumbnails: true },
  medium_icons:      { label: 'Medium icons',      icon: '⊞',  supportsThumbnails: true },
  small_icons:       { label: 'Small icons',       icon: '▪',  supportsThumbnails: false },
  list:              { label: 'List',              icon: '≣',  supportsThumbnails: false },
  details:           { label: 'Details',           icon: '☰',  supportsThumbnails: false },
  tiles:             { label: 'Tiles',             icon: '▤',  supportsThumbnails: true }
};
let viewMode = 'details'; // Default: Details view
let isViewModeMenuOpen = false;
let showMediaThumbnails = false; // Default false: show 🖼️ and 🎬 emojis unless toggled on in header
let groupByField = 'none'; // 'none', 'name', 'date', 'type', 'size'
let groupByAsc = true;
let pageSize = 50; // number or 'all'
let currentPage = 1;
let searchSubfolders = false; // false = current folder only, true = current folder + subfolders
let recursiveSearchResults = null; // null when not active, Array when active
let isSearchingSubfolders = false;
let searchDebounceTimer = null;
let searchReqCounter = 0;

// Multi-select & Clipboard state
let selectedPaths = new Set();
let lastSelectedPath = null; // Anchor item path for Shift+Click range selection
let clipboardItems = [];
let clipboardMode = null; // 'copy' or 'move'
let destModalPath = '/';
let destModalItems = [];
let activePropertiesTarget = null; // Tracks open Properties modal context

// Type-Ahead Keyboard Jump state
let typeaheadBuffer = '';
let typeaheadResetTimer = null;
let typeaheadIndicatorTimer = null;
let typeaheadLastChar = '';
let typeaheadCycleIndex = -1;
let typeaheadFocusedPath = null;

// Mouse Marquee (Rubber-Band) Selection state
let marqueeState = {
  isDown: false,
  isDragging: false,
  startX: 0,
  startY: 0,
  startDocX: 0,
  startDocY: 0,
  curX: 0,
  curY: 0,
  additive: false,
  baseSelected: new Set(),
  itemRects: [],
  suppressClickUntil: 0,
  autoScrollRaf: null,
  autoScrollSpeedY: 0
};
let lastCtrlWheelTime = 0;

// Active custom dialog state
let activeDialogResolver = null;
let activeDialogMode = null; // 'prompt', 'confirm', 'alert'

// Navigation History & Address Bar state
let navHistory = ['/'];
let navHistoryIndex = 0;
let isAddressEditMode = false;

// Internal Drag & Drop + Cross-Browser OS Drag-Out state
let isInternalDragActive = false;
let draggedItemPaths = [];
let activeDragDownloadUrl = '';
let activeDragDownloadName = '';
let lastDragOverInsideWindow = 0;

// On-demand Folder Size Cache: fullPath -> { state: 'loading' | 'done' | 'error', size: number, fileCount: number }
let folderSizeCache = new Map();

// Unified Operation Progress Bar state & Safe Cancellation Manager
let opProgressHideTimer = null;
let currentActiveOperation = null; // 'upload' | 'copy' | 'move' | 'batch-delete' | 'batch-rename' | 'scan' | null
let isOpCancellationRequested = false;
let activeUploadXhr = null;
let activeUploadCurrentTarget = null;
let activeOpAbortController = null;
let opStartTime = null;
let opTimerInterval = null;
let opLastPercent = 0;
let opCustomEtaSec = null;

function formatDigitalClock(totalSeconds) {
  if (totalSeconds == null || isNaN(totalSeconds) || totalSeconds < 0) {
    return '--:--';
  }
  const s = Math.floor(totalSeconds);
  const hrs = Math.floor(s / 3600);
  const mins = Math.floor((s % 3600) / 60);
  const secs = s % 60;
  const pad = (n) => (n < 10 ? '0' + n : '' + n);
  if (hrs > 0) {
    return pad(hrs) + ':' + pad(mins) + ':' + pad(secs);
  }
  return pad(mins) + ':' + pad(secs);
}

function updateOperationTimers(elapsedSec, remainingSec) {
  const elText = document.getElementById('uploadTimeElapsedText');
  const remText = document.getElementById('uploadTimeRemainingText');
  const container = document.getElementById('uploadTimerCounters');
  if (container) container.style.display = 'inline-flex';
  if (elText) {
    elText.textContent = formatDigitalClock(elapsedSec) + ' elapsed';
  }
  if (remText) {
    if (remainingSec != null && !isNaN(remainingSec) && remainingSec >= 0) {
      remText.textContent = formatDigitalClock(remainingSec) + ' remaining';
    } else {
      remText.textContent = '--:-- remaining';
    }
  }
}

function startOperationTimer() {
  if (opTimerInterval) clearInterval(opTimerInterval);
  if (!opStartTime) opStartTime = Date.now();
  opTimerInterval = setInterval(() => {
    if (!opStartTime) return;
    const elapsedSec = Math.max(0, (Date.now() - opStartTime) / 1000);
    let remainingSec = opCustomEtaSec;
    if (remainingSec == null) {
      if (opLastPercent > 0 && opLastPercent < 100) {
        remainingSec = Math.max(0, Math.round((elapsedSec / opLastPercent) * (100 - opLastPercent)));
      } else if (opLastPercent >= 100) {
        remainingSec = 0;
      }
    }
    updateOperationTimers(elapsedSec, remainingSec);
  }, 500);
}

function stopOperationTimer() {
  if (opTimerInterval) {
    clearInterval(opTimerInterval);
    opTimerInterval = null;
  }
}

function setOperationCancellable(isCancellable, opType) {
  currentActiveOperation = isCancellable ? (opType || 'operation') : null;
  isOpCancellationRequested = false;
  const btn = document.getElementById('opCancelBtn');
  if (btn) {
    btn.style.display = isCancellable ? 'inline-flex' : 'none';
    btn.disabled = false;
    btn.innerHTML = '<span>✕</span><span>Cancel</span>';
  }
}

async function requestCancelCurrentOperation() {
  if (isOpCancellationRequested) return;
  const currentTitle = document.getElementById('uploadStatusText')?.textContent || 'Current operation';
  const confirmed = await showConfirmDialog({
    icon: '⏹️',
    badgeClass: 'icon-badge-danger',
    title: 'Cancel Operation?',
    subtitle: 'Are you sure you want to stop this ongoing operation?',
    targetLabel: 'Active Process',
    targetValue: currentTitle,
    warningText: 'Any partially transferred or incomplete file will be safely deleted to avoid file corruption.',
    confirmText: '✕ Yes, Cancel Operation',
    confirmBtnClass: 'btn-danger'
  });
  if (!confirmed) return;

  isOpCancellationRequested = true;
  const btn = document.getElementById('opCancelBtn');
  if (btn) {
    btn.disabled = true;
    btn.innerHTML = '<span>⏳</span><span>Cancelling...</span>';
  }
  const statusEl = document.getElementById('uploadStatusText');
  if (statusEl) statusEl.textContent = '⏹️ Cancelling safely...';

  // 1. Abort active upload XHR if in flight
  if (activeUploadXhr) {
    try {
      activeUploadXhr.abort();
    } catch (e) {}
  }
  // 2. Abort fetch controller if in flight
  if (activeOpAbortController) {
    try {
      activeOpAbortController.abort();
    } catch (e) {}
  }
  // 3. Clean up active target file on server if uploading
  if (activeUploadCurrentTarget) {
    const toDelete = activeUploadCurrentTarget;
    try {
      await apiFetch('/api/delete?path=' + encodeURIComponent(toDelete), { method: 'POST' });
    } catch (e) {}
  }
}

function showOperationProgress(statusTitle, percent, subText, isIndeterminate) {
  if (opProgressHideTimer) {
    clearTimeout(opProgressHideTimer);
    opProgressHideTimer = null;
  }
  const panel = document.getElementById('uploadPanel');
  const statusEl = document.getElementById('uploadStatusText');
  const pctEl = document.getElementById('uploadPercentText');
  const barEl = document.getElementById('uploadProgressBar');
  const subEl = document.getElementById('uploadSubText');
  const cancelBtn = document.getElementById('opCancelBtn');
  if (!panel) return;

  panel.classList.add('active');
  if (!opStartTime) {
    opStartTime = Date.now();
  }
  startOperationTimer();

  if (cancelBtn && currentActiveOperation && !cancelBtn.disabled) {
    cancelBtn.style.display = 'inline-flex';
  }
  if (statusEl) statusEl.textContent = statusTitle || 'Processing...';

  const elapsedSec = Math.max(0, (Date.now() - opStartTime) / 1000);

  if (isIndeterminate) {
    opLastPercent = 0;
    opCustomEtaSec = null;
    if (pctEl) pctEl.textContent = '⏳';
    if (barEl) {
      barEl.classList.add('indeterminate');
      barEl.style.animation = '';
      barEl.style.transform = '';
      barEl.style.width = '42%';
    }
    updateOperationTimers(elapsedSec, null);
  } else {
    const clamped = Math.max(0, Math.min(100, Math.round(percent || 0)));
    opLastPercent = clamped;
    if (pctEl) pctEl.textContent = clamped + '%';
    if (barEl) {
      barEl.classList.remove('indeterminate');
      barEl.style.animation = 'none';
      barEl.style.transform = 'none';
      barEl.style.width = clamped + '%';
    }
    let remainingSec = opCustomEtaSec;
    if (remainingSec == null) {
      if (clamped > 0 && clamped < 100) {
        remainingSec = Math.max(0, Math.round((elapsedSec / clamped) * (100 - clamped)));
      } else if (clamped >= 100) {
        remainingSec = 0;
      }
    }
    updateOperationTimers(elapsedSec, remainingSec);
  }
  if (subEl) subEl.textContent = subText || '';
}

function completeOperationProgress(doneTitle, subText, delayMs) {
  const cancelBtn = document.getElementById('opCancelBtn');
  if (cancelBtn) cancelBtn.style.display = 'none';
  currentActiveOperation = null;
  isOpCancellationRequested = false;
  stopOperationTimer();
  const finalElapsedSec = opStartTime ? Math.max(0, (Date.now() - opStartTime) / 1000) : 0;
  updateOperationTimers(finalElapsedSec, 0);
  showOperationProgress(doneTitle || 'Completed', 100, subText || '', false);
  opProgressHideTimer = setTimeout(() => {
    hideOperationProgress();
  }, typeof delayMs === 'number' ? delayMs : 1800);
}

function hideOperationProgress() {
  if (opProgressHideTimer) {
    clearTimeout(opProgressHideTimer);
    opProgressHideTimer = null;
  }
  stopOperationTimer();
  const panel = document.getElementById('uploadPanel');
  const barEl = document.getElementById('uploadProgressBar');
  const cancelBtn = document.getElementById('opCancelBtn');
  if (cancelBtn) cancelBtn.style.display = 'none';
  if (barEl) {
    barEl.classList.remove('indeterminate');
    barEl.style.animation = 'none';
    barEl.style.transform = 'none';
    barEl.style.width = '0%';
  }
  if (panel) panel.classList.remove('active');
  opStartTime = null;
  opLastPercent = 0;
  opCustomEtaSec = null;
  currentActiveOperation = null;
  isOpCancellationRequested = false;
  updateOperationTimers(0, null);
}

const clientSessionId = 'tab_' + Date.now().toString(36) + '_' + Math.random().toString(36).substring(2, 10);
let lastDisplayedConnCount = 1;

function updateLiveConnectionBadge(isOnline, activeCount, openTabs, activeTransfers) {
  const pill = document.getElementById('liveConnPill');
  const statusEl = document.getElementById('liveConnStatusText');
  const countEl = document.getElementById('liveConnCount');
  if (!pill || !statusEl || !countEl) return;

  if (!isOnline) {
    pill.classList.add('offline');
    statusEl.textContent = 'Offline';
    countEl.textContent = '0';
    lastDisplayedConnCount = 0;
    pill.title = 'Server is currently offline or unreachable';
    return;
  }

  pill.classList.remove('offline');
  statusEl.textContent = 'Online';
  const safeCount = Math.max(1, typeof activeCount === 'number' ? activeCount : 1);
  if (lastDisplayedConnCount !== safeCount) {
    lastDisplayedConnCount = safeCount;
    countEl.textContent = String(safeCount);
    countEl.classList.remove('bump');
    void countEl.offsetWidth;
    countEl.classList.add('bump');
    setTimeout(() => { countEl.classList.remove('bump'); }, 220);
  } else {
    countEl.textContent = String(safeCount);
  }

  if (typeof openTabs === 'number' && typeof activeTransfers === 'number') {
    pill.title = 'Server Online • ' + safeCount + ' Active (' + openTabs + ' open tab' + (openTabs === 1 ? '' : 's') +
      (activeTransfers > 0 ? ', ' + activeTransfers + ' active transfer' + (activeTransfers === 1 ? '' : 's') : '') + ')';
  } else {
    pill.title = 'Server Online • ' + safeCount + ' Active Connection' + (safeCount === 1 ? '' : 's');
  }
}

async function sendSessionHeartbeat() {
  try {
    const res = await fetch('/api/heartbeat?sid=' + encodeURIComponent(clientSessionId) + '&t=' + Date.now(), {
      method: 'GET',
      cache: 'no-store',
      headers: { 'X-UFS-Session': clientSessionId }
    });
    if (res.ok) {
      const data = await res.json();
      if (data && data.ok) {
        updateLiveConnectionBadge(
          data.online !== false,
          data.activeConnections,
          data.openTabs,
          data.activeTransfers
        );
        return;
      }
    }
    updateLiveConnectionBadge(false, 0);
  } catch (e) {
    updateLiveConnectionBadge(false, 0);
  }
}

function notifySessionLeave() {
  const leaveUrl = '/api/heartbeat?sid=' + encodeURIComponent(clientSessionId) + '&action=leave';
  try {
    if (navigator.sendBeacon) {
      navigator.sendBeacon(leaveUrl);
      return;
    }
  } catch (e) {}
  try {
    fetch(leaveUrl, { method: 'POST', keepalive: true });
  } catch (e) {}
}

window.addEventListener('pagehide', notifySessionLeave);
window.addEventListener('beforeunload', notifySessionLeave);
document.addEventListener('visibilitychange', () => {
  if (document.visibilityState === 'visible') {
    sendSessionHeartbeat();
  }
});
window.addEventListener('focus', () => {
  sendSessionHeartbeat();
});

try {
  const isRemembered = localStorage.getItem('ufs_remember_me') === '1';
  if (isRemembered) {
    const permToken = localStorage.getItem('ufs_auth_token');
    if (permToken) {
      authToken = permToken;
      try { sessionStorage.setItem('ufs_auth_token', permToken); } catch(e) {}
    }
  } else {
    // If Remember Me was not set, ensure permanent storage is clean so opening a new tab requires fresh login
    try { localStorage.removeItem('ufs_auth_token'); } catch(e) {}
    try { localStorage.removeItem('ufs_remember_me'); } catch(e) {}
    const sessToken = sessionStorage.getItem('ufs_auth_token');
    if (sessToken) authToken = sessToken;
  }
} catch(e) {}

function withAuthUrl(url) {
  let result = url;
  if (authToken) {
    const sep = result.includes('?') ? '&' : '?';
    result = result + sep + 'auth=' + encodeURIComponent(authToken);
  }
  return result;
}

async function apiFetch(url, options) {
  const opts = Object.assign({}, options || {});
  const headers = Object.assign({}, opts.headers || {});
  headers['X-UFS-Session'] = clientSessionId;
  if (authToken) {
    headers['X-UFS-Auth'] = authToken;
  }
  opts.headers = headers;
  const res = await fetch(url, opts);
  if (res.status === 401) {
    openAuthModal();
  }
  return res;
}

function formatToastText(rawMessage) {
  if (!rawMessage || typeof rawMessage !== 'string') return '';
  // Truncate long quoted segments (filenames or foldernames) with middle-ellipsis
  let formatted = rawMessage.replace(/"([^"]+)"/g, (match, inner) => {
    if (inner.length > 34) {
      return '"' + inner.slice(0, 18) + '...' + inner.slice(-12) + '"';
    }
    return match;
  });
  // If the entire message is still very long, truncate with ellipsis
  if (formatted.length > 90) {
    formatted = formatted.slice(0, 87) + '...';
  }
  return formatted;
}

function showToast(message, variant) {
  const container = document.getElementById('toastContainer');
  if (!container) return;
  const fullText = (message == null) ? '' : String(message);
  const displayText = formatToastText(fullText);
  const el = document.createElement('div');
  el.className = 'toast-item' + (variant ? ' toast-' + variant : '');
  el.title = fullText;
  const textSpan = document.createElement('span');
  textSpan.className = 'toast-text';
  textSpan.textContent = displayText;
  el.appendChild(textSpan);
  container.appendChild(el);
  setTimeout(() => {
    el.style.opacity = '0';
    el.style.transition = 'opacity 0.25s ease';
    setTimeout(() => { if (el.parentNode) el.parentNode.removeChild(el); }, 260);
  }, 3000);
}

/* Authentication Modal Logic */
function openAuthModal(errorMessage) {
  const modal = document.getElementById('authModal');
  const errBanner = document.getElementById('authErrorBanner');
  const errText = document.getElementById('authErrorText');
  const userInp = document.getElementById('authUsernameInput');
  const passInp = document.getElementById('authPasswordInput');

  if (errorMessage) {
    errText.textContent = errorMessage;
    errBanner.style.display = 'flex';
  } else {
    errBanner.style.display = 'none';
  }

  modal.classList.add('active');
  setTimeout(() => {
    if (!userInp.value) userInp.focus();
    else passInp.focus();
  }, 50);
}

function closeAuthModal() {
  document.getElementById('authModal').classList.remove('active');
  document.getElementById('authErrorBanner').style.display = 'none';
}

function toggleAuthPasswordVisibility() {
  const inp = document.getElementById('authPasswordInput');
  const btn = document.getElementById('togglePassBtn');
  if (inp.type === 'password') {
    inp.type = 'text';
    btn.textContent = '🙈';
  } else {
    inp.type = 'password';
    btn.textContent = '👁️';
  }
  inp.focus();
}

async function submitAuthLogin(e) {
  if (e) e.preventDefault();
  const userInp = document.getElementById('authUsernameInput');
  const passInp = document.getElementById('authPasswordInput');
  const rememberInp = document.getElementById('authRememberMe');
  const submitBtn = document.getElementById('authSubmitBtn');
  const card = document.getElementById('authCard');
  const username = (userInp.value || '').trim();
  const password = passInp.value || '';
  const rememberMe = rememberInp ? rememberInp.checked : false;

  submitBtn.disabled = true;
  submitBtn.textContent = '⏳ Signing In...';

  try {
    const res = await fetch('/api/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username: username, password: password, rememberMe: rememberMe })
    });
    const data = await res.json();
    if (res.ok && data.ok) {
      authToken = data.token || '';
      try { sessionStorage.setItem('ufs_auth_token', authToken); } catch(err) {}
      if (rememberMe) {
        try {
          localStorage.setItem('ufs_auth_token', authToken);
          localStorage.setItem('ufs_remember_me', '1');
        } catch(err) {}
      } else {
        try {
          localStorage.removeItem('ufs_auth_token');
          localStorage.removeItem('ufs_remember_me');
        } catch(err) {}
      }
      passInp.value = '';
      closeAuthModal();
      updateLogoutButtonVisibility(true);
      showToast('🔓 Signed in successfully', 'emerald');
      await loadDirectory(currentPath || '/');
    } else {
      const msg = (data && data.error) ? data.error : 'Invalid username or password';
      openAuthModal(msg);
      card.classList.remove('shake');
      void card.offsetWidth;
      card.classList.add('shake');
    }
  } catch (err) {
    openAuthModal('Connection error: ' + err.message);
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = '🔓 Sign In';
  }
}

async function logoutUser() {
  try {
    await apiFetch('/api/logout', { method: 'POST' });
  } catch(e) {}
  authToken = '';
  try { sessionStorage.removeItem('ufs_auth_token'); } catch(e) {}
  try { localStorage.removeItem('ufs_auth_token'); } catch(e) {}
  try { localStorage.removeItem('ufs_remember_me'); } catch(e) {}
  updateLogoutButtonVisibility(false);
  openAuthModal();
}

function updateLogoutButtonVisibility(isAuthenticated) {
  const btn = document.getElementById('logoutBtn');
  if (!btn) return;
  btn.style.display = (serverRequiresAuth && isAuthenticated) ? 'inline-flex' : 'none';
}

/* Unified Styled Prompt / Confirm / Alert Dialog Modal */
function openCustomDialog(opts) {
  return new Promise((resolve) => {
    if (activeDialogResolver) {
      const prev = activeDialogResolver;
      activeDialogResolver = null;
      prev(null);
    }
    activeDialogResolver = resolve;
    activeDialogMode = opts.mode || 'alert';

    const modal = document.getElementById('dialogModal');
    const badge = document.getElementById('dialogIconBadge');
    const titleEl = document.getElementById('dialogTitle');
    const subEl = document.getElementById('dialogSubtitle');
    const targetBox = document.getElementById('dialogTargetBox');
    const targetLabel = document.getElementById('dialogTargetLabel');
    const targetVal = document.getElementById('dialogTargetVal');
    const warnBanner = document.getElementById('dialogWarningBanner');
    const warnText = document.getElementById('dialogWarningText');
    const inputGroup = document.getElementById('dialogInputGroup');
    const inputLabel = document.getElementById('dialogInputLabel');
    const inputEl = document.getElementById('dialogInput');
    const cancelBtn = document.getElementById('dialogCancelBtn');
    const confirmBtn = document.getElementById('dialogConfirmBtn');

    badge.textContent = opts.icon || '💬';
    badge.className = 'dialog-icon-badge ' + (opts.badgeClass || 'icon-badge-primary');
    titleEl.textContent = opts.title || 'Notice';
    subEl.textContent = opts.subtitle || '';

    if (opts.targetValue) {
      targetBox.style.display = 'flex';
      targetLabel.textContent = opts.targetLabel || 'Target';
      targetVal.textContent = opts.targetValue;
    } else {
      targetBox.style.display = 'none';
    }

    if (opts.warningText) {
      warnBanner.style.display = 'flex';
      warnText.textContent = opts.warningText;
    } else {
      warnBanner.style.display = 'none';
    }

    if (activeDialogMode === 'prompt') {
      inputGroup.style.display = 'flex';
      inputLabel.textContent = opts.inputLabel || 'Name';
      inputEl.placeholder = opts.placeholder || '';
      inputEl.value = opts.defaultValue !== undefined ? opts.defaultValue : '';
    } else {
      inputGroup.style.display = 'none';
      inputEl.value = '';
    }

    if (activeDialogMode === 'alert') {
      cancelBtn.style.display = 'none';
    } else {
      cancelBtn.style.display = 'inline-flex';
      cancelBtn.textContent = opts.cancelText || 'Cancel';
    }

    confirmBtn.textContent = opts.confirmText || 'OK';
    confirmBtn.className = 'btn ' + (opts.confirmBtnClass || 'btn-primary');

    modal.classList.add('active');

    setTimeout(() => {
      if (activeDialogMode === 'prompt') {
        inputEl.focus();
        const val = inputEl.value;
        if (opts.selectBaseName && val) {
          const dotIdx = val.lastIndexOf('.');
          if (dotIdx > 0) {
            inputEl.setSelectionRange(0, dotIdx);
          } else {
            inputEl.select();
          }
        } else {
          inputEl.select();
        }
      } else {
        confirmBtn.focus();
      }
    }, 35);
  });
}

function confirmDialogModal() {
  if (!activeDialogResolver) return;
  const resolve = activeDialogResolver;
  const mode = activeDialogMode;
  const inputEl = document.getElementById('dialogInput');
  const card = document.getElementById('dialogCard');

  if (mode === 'prompt') {
    const val = (inputEl.value || '').trim();
    if (!val) {
      card.classList.remove('shake');
      void card.offsetWidth;
      card.classList.add('shake');
      inputEl.focus();
      return;
    }
    activeDialogResolver = null;
    document.getElementById('dialogModal').classList.remove('active');
    resolve(val);
  } else {
    activeDialogResolver = null;
    document.getElementById('dialogModal').classList.remove('active');
    resolve(true);
  }
}

function resolveDialogModal(result) {
  document.getElementById('dialogModal').classList.remove('active');
  if (!activeDialogResolver) return;
  const resolve = activeDialogResolver;
  activeDialogResolver = null;
  resolve(result);
}

function showPromptDialog(opts) {
  return openCustomDialog(Object.assign({ mode: 'prompt' }, opts));
}

function showConfirmDialog(opts) {
  return openCustomDialog(Object.assign({ mode: 'confirm' }, opts)).then(res => res === true);
}

function showAlertDialog(opts) {
  if (typeof opts === 'string') {
    return openCustomDialog({
      mode: 'alert',
      icon: '⚠️',
      badgeClass: 'icon-badge-danger',
      title: 'Action Notice',
      subtitle: opts,
      confirmText: 'OK',
      confirmBtnClass: 'btn-primary'
    });
  }
  return openCustomDialog(Object.assign({ mode: 'alert' }, opts));
}

/* File Conflict Resolution Modal Logic */
let activeConflictResolver = null;
let activeConflictContext = null;

function openConflictModal(opts) {
  return new Promise((resolve) => {
    if (activeConflictResolver) {
      const prev = activeConflictResolver;
      activeConflictResolver = null;
      prev({ action: 'cancel', applyAll: false });
    }
    activeConflictResolver = resolve;
    activeConflictContext = opts;

    const modal = document.getElementById('conflictModal');
    const titleEl = document.getElementById('conflictTitle');
    const subEl = document.getElementById('conflictSubtitle');
    const pathLabel = document.getElementById('conflictPathLabel');
    const pathVal = document.getElementById('conflictPathVal');
    const exSize = document.getElementById('conflictExistingSize');
    const exDate = document.getElementById('conflictExistingDate');
    const incLabel = document.getElementById('conflictIncomingLabel');
    const newSize = document.getElementById('conflictNewSize');
    const newDate = document.getElementById('conflictNewDate');
    const errBanner = document.getElementById('conflictErrorBanner');
    const renameInp = document.getElementById('conflictRenameInput');
    const applyWrap = document.getElementById('conflictApplyAllWrap');
    const applyChk = document.getElementById('conflictApplyAllCheckbox');
    const applyLbl = document.getElementById('conflictApplyAllLabel');
    const skipBtn = document.getElementById('conflictSkipBtn');

    const totalCount = opts.totalFiles || 1;
    const currentIdx = opts.currentIndex || 1;
    const hasMore = totalCount > 1 && currentIdx < totalCount;

    titleEl.textContent = opts.title || (totalCount > 1
      ? ('File Already Exists (' + currentIdx + ' of ' + totalCount + ')')
      : 'File Already Exists');
    subEl.textContent = opts.subtitle || 'A file with this name already exists on your device. Choose whether to cancel, overwrite the existing file, or save with a new name:';
    pathLabel.textContent = opts.targetDir ? ('In ' + opts.targetDir) : 'Conflicting File';
    pathVal.textContent = opts.fileName || '';

    exSize.textContent = opts.existingIsDir ? 'Folder' : ('Size: ' + formatBytes(opts.existingSize));
    exDate.textContent = 'Modified: ' + formatDate(opts.existingModified);

    incLabel.textContent = opts.incomingLabel || '📤 New File to Upload';
    newSize.textContent = opts.newIsDir ? 'Folder' : ('Size: ' + formatBytes(opts.newSize));
    newDate.textContent = 'Modified: ' + formatDate(opts.newModified);

    errBanner.style.display = 'none';
    renameInp.value = opts.suggestedName || ('Copy of ' + (opts.fileName || 'file'));

    applyChk.checked = false;
    if (hasMore) {
      applyWrap.style.display = 'flex';
      const rem = totalCount - currentIdx;
      applyLbl.textContent = 'Apply this choice to remaining conflicts (' + rem + ' more file' + (rem === 1 ? '' : 's') + ' in queue)';
    } else {
      applyWrap.style.display = 'none';
    }

    if (totalCount > 1) {
      skipBtn.style.display = 'inline-flex';
    } else {
      skipBtn.style.display = 'none';
    }

    modal.classList.add('active');

    setTimeout(() => {
      renameInp.focus();
      const val = renameInp.value;
      const dotIdx = val.lastIndexOf('.');
      if (dotIdx > 0) {
        renameInp.setSelectionRange(0, dotIdx);
      } else {
        renameInp.select();
      }
    }, 40);
  });
}

function resetConflictSuggestedName() {
  if (!activeConflictContext) return;
  const renameInp = document.getElementById('conflictRenameInput');
  renameInp.value = activeConflictContext.suggestedName || '';
  document.getElementById('conflictErrorBanner').style.display = 'none';
  renameInp.focus();
}

async function submitConflictDecision(action) {
  if (!activeConflictResolver) return;
  const ctx = activeConflictContext || {};
  const card = document.getElementById('conflictCard');
  const errBanner = document.getElementById('conflictErrorBanner');
  const errText = document.getElementById('conflictErrorText');
  const renameInp = document.getElementById('conflictRenameInput');
  const applyAll = !!document.getElementById('conflictApplyAllCheckbox').checked;

  if (action === 'rename') {
    const newName = (renameInp.value || '').trim();
    if (!newName || newName.includes('/') || newName.includes('\\') || newName === '.' || newName === '..') {
      errText.textContent = 'Please enter a valid file name without slashes.';
      errBanner.style.display = 'flex';
      card.classList.remove('shake');
      void card.offsetWidth;
      card.classList.add('shake');
      renameInp.focus();
      return;
    }
    if (newName === ctx.fileName) {
      errText.textContent = 'The new name is identical to the existing file name. Enter a different name or click "Overwrite Existing".';
      errBanner.style.display = 'flex';
      card.classList.remove('shake');
      void card.offsetWidth;
      card.classList.add('shake');
      renameInp.focus();
      return;
    }
    // Verify that the new name does not also exist in the target folder
    const targetDir = ctx.targetDir || currentPath || '/';
    const candidatePath = joinPath(targetDir, newName);
    try {
      const checkRes = await apiFetch('/api/exists?path=' + encodeURIComponent(candidatePath));
      if (checkRes.ok) {
        const checkData = await checkRes.json();
        if (checkData.ok && checkData.exists) {
          errText.textContent = 'A file named "' + newName + '" also already exists in this folder! Please choose another name.';
          errBanner.style.display = 'flex';
          if (checkData.suggestedName) {
            ctx.suggestedName = checkData.suggestedName;
          }
          card.classList.remove('shake');
          void card.offsetWidth;
          card.classList.add('shake');
          renameInp.focus();
          return;
        }
      }
    } catch (e) {}

    const resolve = activeConflictResolver;
    activeConflictResolver = null;
    activeConflictContext = null;
    document.getElementById('conflictModal').classList.remove('active');
    resolve({ action: 'rename', newName: newName, applyAll: applyAll });
    return;
  }

  const resolve = activeConflictResolver;
  activeConflictResolver = null;
  activeConflictContext = null;
  document.getElementById('conflictModal').classList.remove('active');
  resolve({ action: action, applyAll: applyAll });
}

/* Rename Conflict Resolution Modal Logic (Cancel / Append (n) / Custom Name) */
let activeRenameConflictResolver = null;
let activeRenameConflictContext = null;

function openRenameConflictModal(opts) {
  return new Promise((resolve) => {
    if (activeRenameConflictResolver) {
      const prev = activeRenameConflictResolver;
      activeRenameConflictResolver = null;
      prev({ action: 'cancel' });
    }
    activeRenameConflictResolver = resolve;
    activeRenameConflictContext = opts || {};

    const modal = document.getElementById('renameConflictModal');
    const titleEl = document.getElementById('renameConflictTitle');
    const subEl = document.getElementById('renameConflictSubtitle');
    const oldNameEl = document.getElementById('renameConflictOldName');
    const folderSubEl = document.getElementById('renameConflictFolderSub');
    const suggestedEl = document.getElementById('renameConflictSuggestedVal');
    const appendBtn = document.getElementById('renameConflictAppendBtn');
    const errBanner = document.getElementById('renameConflictErrorBanner');
    const inp = document.getElementById('renameConflictInput');

    const attemptedName = opts.attemptedName || '';
    const oldName = opts.oldName || '';
    const parentDir = opts.parentDir || currentPath || '/';
    const suggestedName = opts.suggestedName || attemptedName;

    titleEl.textContent = 'Name Already Exists';
    subEl.textContent = 'An item named "' + attemptedName + '" already exists in this folder. Choose whether to cancel, append a number in parentheses, or enter a new name:';
    oldNameEl.textContent = oldName;
    folderSubEl.textContent = 'In ' + parentDir;
    suggestedEl.textContent = suggestedName;
    appendBtn.textContent = '🔢 Append Number: ' + suggestedName;
    errBanner.style.display = 'none';
    inp.value = attemptedName;

    modal.classList.add('active');
    setTimeout(() => {
      inp.focus();
      const val = inp.value;
      const dotIdx = val.lastIndexOf('.');
      if (dotIdx > 0) {
        inp.setSelectionRange(0, dotIdx);
      } else {
        inp.select();
      }
    }, 35);
  });
}

function fillRenameConflictSuggested() {
  if (!activeRenameConflictContext) return;
  const inp = document.getElementById('renameConflictInput');
  inp.value = activeRenameConflictContext.suggestedName || '';
  document.getElementById('renameConflictErrorBanner').style.display = 'none';
  inp.focus();
}

async function submitRenameConflictDecision(action) {
  if (!activeRenameConflictResolver) return;
  const ctx = activeRenameConflictContext || {};
  const card = document.getElementById('renameConflictCard');
  const errBanner = document.getElementById('renameConflictErrorBanner');
  const errText = document.getElementById('renameConflictErrorText');
  const inp = document.getElementById('renameConflictInput');
  const parentDir = ctx.parentDir || currentPath || '/';
  const oldName = ctx.oldName || '';

  if (action === 'cancel') {
    const resolve = activeRenameConflictResolver;
    activeRenameConflictResolver = null;
    activeRenameConflictContext = null;
    document.getElementById('renameConflictModal').classList.remove('active');
    resolve({ action: 'cancel' });
    return;
  }

  if (action === 'append') {
    const targetName = (ctx.suggestedName || '').trim();
    const resolve = activeRenameConflictResolver;
    activeRenameConflictResolver = null;
    activeRenameConflictContext = null;
    document.getElementById('renameConflictModal').classList.remove('active');
    resolve({ action: 'append', newName: targetName });
    return;
  }

  if (action === 'custom') {
    const customName = (inp.value || '').trim();
    if (!customName || customName.includes('/') || customName.includes('\\') || customName === '.' || customName === '..') {
      errText.textContent = 'Please enter a valid name without slashes (/ or \\).';
      errBanner.style.display = 'flex';
      card.classList.remove('shake');
      void card.offsetWidth;
      card.classList.add('shake');
      inp.focus();
      return;
    }
    if (customName === oldName) {
      const resolve = activeRenameConflictResolver;
      activeRenameConflictResolver = null;
      activeRenameConflictContext = null;
      document.getElementById('renameConflictModal').classList.remove('active');
      resolve({ action: 'cancel' });
      return;
    }
    // Case-only rename of the same item is allowed
    const isCaseOnlySameItem = customName.toLowerCase() === oldName.toLowerCase();
    if (!isCaseOnlySameItem) {
      const candidatePath = joinPath(parentDir, customName);
      try {
        const checkRes = await apiFetch('/api/exists?path=' + encodeURIComponent(candidatePath));
        if (checkRes.ok) {
          const checkData = await checkRes.json();
          if (checkData && checkData.ok && checkData.exists) {
            errText.textContent = 'An item named "' + customName + '" also already exists in this folder! Enter another name or click "Append Number".';
            errBanner.style.display = 'flex';
            if (checkData.suggestedName) {
              ctx.suggestedName = checkData.suggestedName;
              document.getElementById('renameConflictSuggestedVal').textContent = checkData.suggestedName;
              document.getElementById('renameConflictAppendBtn').textContent = '🔢 Append Number: ' + checkData.suggestedName;
            }
            card.classList.remove('shake');
            void card.offsetWidth;
            card.classList.add('shake');
            inp.focus();
            return;
          }
        }
      } catch (e) {}
    }

    const resolve = activeRenameConflictResolver;
    activeRenameConflictResolver = null;
    activeRenameConflictContext = null;
    document.getElementById('renameConflictModal').classList.remove('active');
    resolve({ action: 'custom', newName: customName });
  }
}

function saveUserPref(key, value) {
  const strVal = String(value);
  try {
    document.cookie = encodeURIComponent(key) + '=' + encodeURIComponent(strVal) + '; path=/; max-age=31536000; SameSite=Lax';
  } catch (e) {}
  try {
    localStorage.setItem(key, strVal);
  } catch (e) {}
}

function loadUserPref(key, defaultValue) {
  try {
    const target = encodeURIComponent(key) + '=';
    const parts = (document.cookie || '').split(';');
    for (let i = 0; i < parts.length; i++) {
      const part = parts[i].trim();
      if (part.indexOf(target) === 0) {
        return decodeURIComponent(part.substring(target.length));
      }
    }
  } catch (e) {}
  try {
    const lsVal = localStorage.getItem(key);
    if (lsVal !== null && lsVal !== undefined) {
      try {
        document.cookie = encodeURIComponent(key) + '=' + encodeURIComponent(lsVal) + '; path=/; max-age=31536000; SameSite=Lax';
      } catch (err) {}
      return lsVal;
    }
  } catch (e) {}
  return defaultValue;
}

function clearUserPref(key) {
  try {
    document.cookie = encodeURIComponent(key) + '=; path=/; max-age=0; SameSite=Lax';
  } catch (e) {}
  try {
    localStorage.removeItem(key);
  } catch (e) {}
}

function detectBrowserSystemTheme() {
  try {
    if (window.matchMedia) {
      if (window.matchMedia('(prefers-color-scheme: light)').matches) return 'light';
      if (window.matchMedia('(prefers-color-scheme: dark)').matches) return 'dark';
    }
  } catch (e) {}
  return document.documentElement.getAttribute('data-theme') === 'light' ? 'light' : 'dark';
}

function toggleTheme() {
  const html = document.documentElement;
  const next = html.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
  html.setAttribute('data-theme', next);
  saveUserPref('ufs_theme', next);
  updateSettingsModalUi();
}

async function promptResetWebSettings() {
  const confirmed = await showConfirmDialog({
    icon: '↺',
    badgeClass: 'icon-badge-amber',
    title: 'Reset Web Portal Settings?',
    subtitle: 'Are you sure you want to reset all appearance and layout preferences (View Mode, Thumbnails, Sort Order, Group By, Items Per Page, Search Scope, and Theme) back to their initial and system defaults?',
    confirmText: '↺ Reset Settings',
    cancelText: 'Cancel',
    confirmBtnClass: 'btn-amber'
  });
  if (!confirmed) return;
  resetAllWebSettingsToDefaults();
}

function resetAllWebSettingsToDefaults() {
  const prefKeys = [
    'ufs_theme',
    'ufs_view_mode',
    'ufs_thumbnails',
    'ufs_sort_field',
    'ufs_sort_asc',
    'ufs_group_by',
    'ufs_group_by_asc',
    'ufs_page_size',
    'ufs_search_subfolders'
  ];
  for (let i = 0; i < prefKeys.length; i++) {
    clearUserPref(prefKeys[i]);
  }

  // Re-detect system/browser theme once and store in cookie
  const autoTheme = detectBrowserSystemTheme();
  document.documentElement.setAttribute('data-theme', autoTheme);
  saveUserPref('ufs_theme', autoTheme);

  // Restore default layout, sorting, grouping, pagination, and search scope
  viewMode = 'details';
  showMediaThumbnails = false;
  sortField = 'name';
  sortAsc = true;
  groupByField = 'none';
  groupByAsc = true;
  pageSize = 50;
  currentPage = 1;
  searchSubfolders = false;

  const grpSel = document.getElementById('groupSelect');
  if (grpSel) grpSel.value = 'none';
  const pageSel = document.getElementById('pageSizeSelect');
  if (pageSel) pageSel.value = '50';

  if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
  isSearchingSubfolders = false;
  recursiveSearchResults = null;

  closeViewDropdown();
  closeItemMenu();
  updateSearchScopeUi();
  updateMediaThumbnailsBtnUi();
  updateViewModeUi();
  updateSortIndicators();
  renderTable();
  showToast('↺ Web portal settings reset to defaults', 'emerald');
}

// Restore saved user preferences from browser cookies (with localStorage fallback)
try {
  const savedTheme = loadUserPref('ufs_theme', null);
  if (savedTheme === 'light' || savedTheme === 'dark') {
    document.documentElement.setAttribute('data-theme', savedTheme);
  } else {
    const initialAutoTheme = detectBrowserSystemTheme();
    document.documentElement.setAttribute('data-theme', initialAutoTheme);
    saveUserPref('ufs_theme', initialAutoTheme);
  }
  const savedView = loadUserPref('ufs_view_mode', null);
  if (savedView === 'grid') viewMode = 'medium_icons';
  else if (savedView && VIEW_MODE_ORDER.includes(savedView)) viewMode = savedView;
  else viewMode = 'details';

  const savedThumbs = loadUserPref('ufs_thumbnails', null);
  if (savedThumbs === '1') showMediaThumbnails = true;
  else if (savedThumbs === '0') showMediaThumbnails = false;

  const savedSortField = loadUserPref('ufs_sort_field', null);
  if (savedSortField && ['name', 'date', 'size'].includes(savedSortField)) {
    sortField = savedSortField;
  }
  const savedSortAsc = loadUserPref('ufs_sort_asc', null);
  if (savedSortAsc === '0') sortAsc = false;
  else if (savedSortAsc === '1') sortAsc = true;

  const savedGroup = loadUserPref('ufs_group_by', null);
  if (savedGroup && ['none', 'name', 'date', 'type', 'size'].includes(savedGroup)) {
    groupByField = savedGroup;
    const grpSel = document.getElementById('groupSelect');
    if (grpSel) grpSel.value = groupByField;
  }
  const savedGroupAsc = loadUserPref('ufs_group_by_asc', null);
  if (savedGroupAsc === '0') groupByAsc = false;
  else if (savedGroupAsc === '1') groupByAsc = true;

  const savedPageSize = loadUserPref('ufs_page_size', null);
  if (savedPageSize) {
    pageSize = savedPageSize === 'all' ? 'all' : (parseInt(savedPageSize, 10) || 50);
    const sel = document.getElementById('pageSizeSelect');
    if (sel) sel.value = String(pageSize);
  }
  const savedScope = loadUserPref('ufs_search_subfolders', null);
  if (savedScope === '1') {
    searchSubfolders = true;
  } else if (savedScope === '0') {
    searchSubfolders = false;
  }
} catch(e) {}

function updateSearchScopeUi() {
  const btnCur = document.getElementById('btnScopeCurrent');
  const btnSub = document.getElementById('btnScopeSubfolders');
  const inp = document.getElementById('searchInput');
  const clearBtn = document.getElementById('clearSearchBtn');
  if (btnCur) btnCur.className = 'search-scope-btn' + (!searchSubfolders ? ' active' : '');
  if (btnSub) btnSub.className = 'search-scope-btn' + (searchSubfolders ? ' active' : '');
  if (inp) {
    inp.placeholder = searchSubfolders
      ? 'Search in current folder & subfolders...'
      : 'Search in current folder...';
    if (clearBtn) {
      clearBtn.style.display = (inp.value && inp.value.length > 0) ? 'block' : 'none';
    }
  }
}
updateSearchScopeUi();

function setSearchSubfolders(includeSubfolders) {
  searchSubfolders = !!includeSubfolders;
  saveUserPref('ufs_search_subfolders', searchSubfolders ? '1' : '0');
  updateSearchScopeUi();
  currentPage = 1;
  const query = (document.getElementById('searchInput').value || '').trim();
  if (!query) {
    recursiveSearchResults = null;
    isSearchingSubfolders = false;
    renderTable();
    return;
  }
  if (searchSubfolders) {
    triggerRecursiveSearch(query, true);
  } else {
    if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
    isSearchingSubfolders = false;
    recursiveSearchResults = null;
    renderTable();
  }
}

function clearSearchInput() {
  const inp = document.getElementById('searchInput');
  if (!inp) return;
  inp.value = '';
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
  isSearchingSubfolders = false;
  recursiveSearchResults = null;
  currentPage = 1;
  updateSearchScopeUi();
  renderTable();
}

function triggerRecursiveSearch(query, immediate) {
  if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
  const cleanQuery = (query || '').trim();
  if (!cleanQuery) {
    isSearchingSubfolders = false;
    recursiveSearchResults = null;
    hideOperationProgress();
    renderTable();
    return;
  }
  isSearchingSubfolders = true;
  renderTable();
  const reqId = ++searchReqCounter;
  const delay = immediate ? 10 : 220;
  searchDebounceTimer = setTimeout(async () => {
    showOperationProgress(
      '🔍 Searching in ' + currentPath + ' and subfolders...',
      0,
      'Query: "' + cleanQuery + '"',
      true
    );
    try {
      const url = '/api/search?path=' + encodeURIComponent(currentPath) +
        '&q=' + encodeURIComponent(cleanQuery) + '&recursive=1';
      const res = await apiFetch(url);
      if (reqId !== searchReqCounter) return;
      if (res.status === 401) {
        isSearchingSubfolders = false;
        hideOperationProgress();
        return;
      }
      const data = await res.json();
      if (reqId !== searchReqCounter) return;
      recursiveSearchResults = (data && data.ok && Array.isArray(data.items)) ? data.items : [];
      isSearchingSubfolders = false;
      completeOperationProgress(
        '🔍 Search completed (' + recursiveSearchResults.length + ' match' + (recursiveSearchResults.length === 1 ? '' : 'es') + ')',
        'In ' + currentPath + ' & subfolders',
        1000
      );
      currentPage = 1;
      renderTable();
      updateBatchAndClipboardBars();
    } catch (e) {
      if (reqId !== searchReqCounter) return;
      isSearchingSubfolders = false;
      recursiveSearchResults = [];
      hideOperationProgress();
      renderTable();
    }
  }, delay);
}

function updateMediaThumbnailsBtnUi() {
  const btn = document.getElementById('btnToggleThumbnails');
  if (!btn) return;
  if (showMediaThumbnails) {
    btn.className = 'btn btn-sm active-toggle';
    btn.textContent = '🖼️ Thumbnails: On';
    btn.title = 'Image & video thumbnails are ON in icon/tile views (Click to turn Off and show emojis)';
  } else {
    btn.className = 'btn btn-sm';
    btn.textContent = '🖼️ Thumbnails: Off';
    btn.title = 'Image & video thumbnails are OFF (Showing emojis — Click to turn On for icon/tile views)';
  }
}
updateMediaThumbnailsBtnUi();

function toggleMediaThumbnails() {
  showMediaThumbnails = !showMediaThumbnails;
  saveUserPref('ufs_thumbnails', showMediaThumbnails ? '1' : '0');
  updateMediaThumbnailsBtnUi();
  const meta = VIEW_MODE_META[viewMode] || VIEW_MODE_META.details;
  if (showMediaThumbnails && !meta.supportsThumbnails) {
    showToast('🖼️ Thumbnails enabled (Switch to Extra Large / Large / Medium Icons or Tiles view to see previews)', 'emerald');
  } else if (showMediaThumbnails) {
    showToast('🖼️ Image & video thumbnails enabled in ' + meta.label + ' view', 'emerald');
  } else {
    showToast('🖼️ Media thumbnails turned off (showing emojis)');
  }
  renderTable();
}

function toggleViewDropdown(e) {
  if (e) {
    e.preventDefault();
    e.stopPropagation();
  }
  const wrap = document.getElementById('viewDropdownWrap');
  if (!wrap) return;
  isViewModeMenuOpen = !wrap.classList.contains('open');
  if (isViewModeMenuOpen) {
    closeItemMenu();
    wrap.classList.add('open');
  } else {
    wrap.classList.remove('open');
  }
}

function toggleViewModeMenu(e) {
  toggleViewDropdown(e);
}

function closeViewDropdown() {
  isViewModeMenuOpen = false;
  const wrap = document.getElementById('viewDropdownWrap');
  if (wrap) wrap.classList.remove('open');
}

function closeViewModeMenu() {
  closeViewDropdown();
}

function selectViewModeFromDropdown(mode, e) {
  if (e) {
    e.preventDefault();
    e.stopPropagation();
  }
  closeViewDropdown();
  setViewMode(mode);
}

function updateViewModeUi() {
  const meta = VIEW_MODE_META[viewMode] || VIEW_MODE_META.details;
  const iconEl = document.getElementById('viewDropdownIcon');
  const labelEl = document.getElementById('viewDropdownLabel');
  if (iconEl) iconEl.textContent = meta.icon;
  if (labelEl) labelEl.textContent = 'View: ' + meta.label;

  document.querySelectorAll('#viewDropdownMenu .view-dropdown-option').forEach(btn => {
    const m = btn.getAttribute('data-mode');
    const chkEl = btn.querySelector('.menu-check');
    if (m === viewMode) {
      btn.classList.add('active');
      if (chkEl) chkEl.textContent = '✓';
    } else {
      btn.classList.remove('active');
      if (chkEl) chkEl.textContent = '';
    }
  });

  const listWrap = document.getElementById('listViewWrapper');
  const gridWrap = document.getElementById('gridViewWrapper');
  if (viewMode === 'details') {
    if (listWrap) listWrap.style.display = 'flex';
    if (gridWrap) gridWrap.style.display = 'none';
  } else {
    if (listWrap) listWrap.style.display = 'none';
    if (gridWrap) {
      gridWrap.style.display = 'grid';
      gridWrap.className = 'grid-container';
      gridWrap.setAttribute('data-view-mode', viewMode);
    }
  }
}

function setViewMode(mode) {
  let normalized = mode;
  if (normalized === 'grid') normalized = 'medium_icons';
  if (!VIEW_MODE_ORDER.includes(normalized)) normalized = 'details';
  viewMode = normalized;
  saveUserPref('ufs_view_mode', viewMode);
  closeViewDropdown();
  updateViewModeUi();
  closeItemMenu();
  renderTable();
}
updateViewModeUi();

function onPageSizeChanged(val) {
  pageSize = val === 'all' ? 'all' : (parseInt(val, 10) || 50);
  currentPage = 1;
  saveUserPref('ufs_page_size', String(pageSize));
  renderTable();
}

function onSearchChanged() {
  currentPage = 1;
  updateSearchScopeUi();
  const query = (document.getElementById('searchInput').value || '').trim();
  if (!query) {
    if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
    isSearchingSubfolders = false;
    recursiveSearchResults = null;
    renderTable();
    return;
  }
  if (searchSubfolders) {
    triggerRecursiveSearch(query, false);
  } else {
    isSearchingSubfolders = false;
    recursiveSearchResults = null;
    renderTable();
  }
}

function onSortSelectChanged(val) {
  const parts = val.split('_');
  sortField = parts[0] || 'name';
  sortAsc = parts[1] !== 'desc';
  saveUserPref('ufs_sort_field', sortField);
  saveUserPref('ufs_sort_asc', sortAsc ? '1' : '0');
  updateSortIndicators();
  currentPage = 1;
  renderTable();
}

function setSortFromMenu(field) {
  if (sortField === field) {
    sortAsc = !sortAsc;
  } else {
    sortField = field;
    sortAsc = (field === 'name');
  }
  saveUserPref('ufs_sort_field', sortField);
  saveUserPref('ufs_sort_asc', sortAsc ? '1' : '0');
  updateSortIndicators();
  currentPage = 1;
  closeItemMenu();
  renderTable();
}

function setSortDirection(asc) {
  sortAsc = !!asc;
  saveUserPref('ufs_sort_asc', sortAsc ? '1' : '0');
  updateSortIndicators();
  currentPage = 1;
  closeItemMenu();
  renderTable();
}

function onGroupSelectChanged(val) {
  setGroupBy(val, true);
}

function setGroupBy(field, keepDirection) {
  const valid = ['none', 'name', 'date', 'type', 'size'];
  const nextField = valid.includes(field) ? field : 'none';
  if (groupByField === nextField && nextField !== 'none' && !keepDirection) {
    groupByAsc = !groupByAsc;
  } else {
    groupByField = nextField;
    if (!keepDirection) {
      groupByAsc = true;
    }
  }
  saveUserPref('ufs_group_by', groupByField);
  saveUserPref('ufs_group_by_asc', groupByAsc ? '1' : '0');
  const grpSel = document.getElementById('groupSelect');
  if (grpSel) grpSel.value = groupByField;
  currentPage = 1;
  closeItemMenu();
  renderTable();
}

function setGroupByDirection(asc) {
  groupByAsc = !!asc;
  saveUserPref('ufs_group_by_asc', groupByAsc ? '1' : '0');
  currentPage = 1;
  closeItemMenu();
  renderTable();
}

function updateSortIndicators() {
  ['name', 'size', 'date'].forEach(f => {
    const el = document.getElementById('sort-' + f);
    if (el) el.textContent = (f === sortField) ? (sortAsc ? '▲' : '▼') : '';
  });
  const sel = document.getElementById('sortSelect');
  if (sel) sel.value = sortField + '_' + (sortAsc ? 'asc' : 'desc');
  const grpSel = document.getElementById('groupSelect');
  if (grpSel) grpSel.value = groupByField;
}
updateSortIndicators();

function formatBytes(bytes) {
  if (bytes === undefined || bytes === null || bytes < 0) return '--';
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(i > 1 ? 2 : 1)) + ' ' + sizes[i];
}

function formatDate(ms) {
  if (!ms || ms <= 0) return '--';
  const d = new Date(ms);
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
}

function joinPath(base, name) {
  if (base === '/' || !base) return '/' + name.replace(/^\/+/, '');
  return base.replace(/\/+$/, '') + '/' + name.replace(/^\/+/, '');
}

function getParentPath(p) {
  if (!p || p === '/') return '/';
  const idx = p.lastIndexOf('/');
  return idx <= 0 ? '/' : p.substring(0, idx);
}

function getItemFullPath(item) {
  return item.path ? item.path : joinPath(currentPath, item.name);
}

function navigateToFolder(path, options) {
  const inp = document.getElementById('searchInput');
  if (inp && inp.value) {
    inp.value = '';
    if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
    isSearchingSubfolders = false;
    recursiveSearchResults = null;
    updateSearchScopeUi();
  }
  loadDirectory(path, options);
}

function updateNavHistoryButtons() {
  const backBtn = document.getElementById('btnNavBack');
  const fwdBtn = document.getElementById('btnNavForward');
  const upBtn = document.getElementById('btnNavUp');
  if (backBtn) backBtn.disabled = navHistoryIndex <= 0;
  if (fwdBtn) fwdBtn.disabled = navHistoryIndex >= navHistory.length - 1;
  if (upBtn) upBtn.disabled = !currentPath || currentPath === '/';
}

function navigateBack() {
  if (navHistoryIndex <= 0) return;
  navHistoryIndex--;
  updateNavHistoryButtons();
  navigateToFolder(navHistory[navHistoryIndex], { skipHistory: true });
}

function navigateForward() {
  if (navHistoryIndex >= navHistory.length - 1) return;
  navHistoryIndex++;
  updateNavHistoryButtons();
  navigateToFolder(navHistory[navHistoryIndex], { skipHistory: true });
}

function navigateUp() {
  if (!currentPath || currentPath === '/') return;
  const parent = getParentPath(currentPath);
  navigateToFolder(parent);
}

function refreshCurrentDirectory() {
  loadDirectory(currentPath, { skipHistory: true });
  showToast('🔄 Refreshed file list');
}

function onBreadcrumbsBackgroundClick(e) {
  if (e.target && e.target.id === 'breadcrumbs') {
    enterAddressEditMode();
  }
}

function enterAddressEditMode() {
  isAddressEditMode = true;
  const crumbsView = document.getElementById('breadcrumbsView');
  const editView = document.getElementById('addressEditView');
  const inp = document.getElementById('addressPathInput');
  if (crumbsView) crumbsView.style.display = 'none';
  if (editView) editView.style.display = 'flex';
  if (inp) {
    inp.value = currentPath || '/';
    setTimeout(() => {
      inp.focus();
      inp.select();
    }, 20);
  }
}

function exitAddressEditMode() {
  isAddressEditMode = false;
  const crumbsView = document.getElementById('breadcrumbsView');
  const editView = document.getElementById('addressEditView');
  const inp = document.getElementById('addressPathInput');
  if (inp) inp.value = currentPath || '/';
  if (editView) editView.style.display = 'none';
  if (crumbsView) crumbsView.style.display = 'flex';
}

function handleAddressBarOutsideInteraction(e) {
  if (!isAddressEditMode) return;
  const shell = document.getElementById('addressBarShell');
  if (!shell) return;
  if (!e || !e.target || !shell.contains(e.target)) {
    exitAddressEditMode();
  }
}
document.addEventListener('pointerdown', handleAddressBarOutsideInteraction, true);
document.addEventListener('click', handleAddressBarOutsideInteraction, true);

function onAddressInputBlur(e) {
  if (!isAddressEditMode) return;
  const shell = document.getElementById('addressBarShell');
  if (e && e.relatedTarget && shell && !shell.contains(e.relatedTarget)) {
    exitAddressEditMode();
  }
}

function onAddressInputKeyDown(e) {
  if (e.key === 'Enter') {
    e.preventDefault();
    submitManualAddress();
  } else if (e.key === 'Escape') {
    e.preventDefault();
    exitAddressEditMode();
  }
}

function normalizeTypedPath(raw) {
  let clean = (raw || '').trim().replace(/\\+/g, '/');
  if (!clean || clean === '/') return '/';
  if (!clean.startsWith('/')) clean = '/' + clean;
  clean = clean.replace(/\/+/g, '/').replace(/\/+$/, '');
  return clean || '/';
}

async function submitManualAddress() {
  const inp = document.getElementById('addressPathInput');
  const shell = document.getElementById('addressBarShell');
  const targetPath = normalizeTypedPath(inp ? inp.value : '/');

  if (targetPath === '/') {
    exitAddressEditMode();
    navigateToFolder('/');
    return;
  }

  try {
    const res = await apiFetch('/api/exists?path=' + encodeURIComponent(targetPath));
    if (res.status === 401) return;
    if (res.ok) {
      const data = await res.json();
      if (data && data.ok && data.exists) {
        if (data.isDirectory) {
          exitAddressEditMode();
          navigateToFolder(targetPath);
          return;
        } else {
          if (shell) {
            shell.classList.remove('shake');
            void shell.offsetWidth;
            shell.classList.add('shake');
          }
          showToast('⚠️ Path is a file, not a folder: ' + targetPath, 'danger');
          return;
        }
      }
    }
    if (shell) {
      shell.classList.remove('shake');
      void shell.offsetWidth;
      shell.classList.add('shake');
    }
    showToast('⚠️ Folder not found: ' + targetPath, 'danger');
  } catch (e) {
    showToast('⚠️ Could not open path: ' + e.message, 'danger');
  }
}

async function copyCurrentAddress() {
  const pathText = currentPath || '/';
  let copied = false;
  if (navigator.clipboard && navigator.clipboard.writeText) {
    try {
      await navigator.clipboard.writeText(pathText);
      copied = true;
    } catch (e) {}
  }
  if (!copied) {
    try {
      const ta = document.createElement('textarea');
      ta.value = pathText;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.focus();
      ta.select();
      copied = document.execCommand('copy');
      document.body.removeChild(ta);
    } catch (e) {}
  }
  showToast('📋 Path copied: ' + pathText, 'emerald');
}

/* Internal Drag & Drop (Single & Multi-Item) into Folders & Breadcrumbs */
function cleanupInternalDragState() {
  isInternalDragActive = false;
  draggedItemPaths = [];
  activeDragDownloadUrl = '';
  activeDragDownloadName = '';
  document.querySelectorAll('.dragging-source, .drop-target-active').forEach(el => {
    el.classList.remove('dragging-source');
    el.classList.remove('drop-target-active');
  });
}

function getMimeTypeForFilename(name) {
  const ext = (name.includes('.') ? (name.split('.').pop() || '') : '').toLowerCase();
  const map = {
    'jpg': 'image/jpeg', 'jpeg': 'image/jpeg', 'png': 'image/png', 'gif': 'image/gif',
    'webp': 'image/webp', 'svg': 'image/svg+xml', 'bmp': 'image/bmp',
    'mp4': 'video/mp4', 'webm': 'video/webm', 'mkv': 'video/x-matroska', 'mov': 'video/quicktime', 'avi': 'video/x-msvideo',
    'mp3': 'audio/mpeg', 'wav': 'audio/wav', 'ogg': 'audio/ogg', 'm4a': 'audio/mp4', 'flac': 'audio/flac', 'aac': 'audio/aac',
    'pdf': 'application/pdf', 'zip': 'application/zip', 'apk': 'application/vnd.android.package-archive',
    'txt': 'text/plain', 'json': 'application/json', 'html': 'text/html', 'css': 'text/css', 'js': 'application/javascript', 'xml': 'application/xml'
  };
  return map[ext] || 'application/octet-stream';
}

function sanitizeDownloadFilename(name, fallback) {
  const cleaned = String(name || fallback || 'download').replace(/[:\/\\*?"<>|]/g, '_').trim();
  return cleaned || (fallback || 'download');
}

function triggerDirectBrowserDownload(url, fileName) {
  if (!url) return;
  try {
    const a = document.createElement('a');
    a.href = url;
    if (fileName) a.setAttribute('download', fileName);
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
    setTimeout(() => {
      if (a.parentNode) a.parentNode.removeChild(a);
    }, 400);
  } catch (e) {
    window.location.href = url;
  }
}

function onItemDragStart(e, fullPath) {
  closeItemMenu();
  closeViewModeMenu();
  isInternalDragActive = true;
  lastDragOverInsideWindow = Date.now();
  if (selectedPaths.has(fullPath) && selectedPaths.size > 0) {
    draggedItemPaths = Array.from(selectedPaths);
  } else {
    draggedItemPaths = [fullPath];
  }
  if (e.currentTarget) {
    e.currentTarget.classList.add('dragging-source');
  }
  if (!e.dataTransfer) return;

  try {
    e.dataTransfer.effectAllowed = 'copyMove';
    e.dataTransfer.setData('application/x-ufs-items', JSON.stringify(draggedItemPaths));

    // Configure Cross-Browser Native OS Drag-Out (Browser -> Windows Explorer / Desktop)
    // Single file -> direct file download; Folder or Multiple items -> ZIP archive
    const origin = window.location.origin;
    let absUrl = '';
    let dlFileName = '';
    let dlMime = 'application/octet-stream';

    if (draggedItemPaths.length === 1) {
      const singlePath = draggedItemPaths[0];
      const itemObj = findItemByFullPath(singlePath);
      const rawName = itemObj ? itemObj.name : (singlePath.split('/').pop() || 'file');
      const isDir = itemObj ? !!itemObj.isDirectory : false;
      if (!isDir) {
        dlFileName = sanitizeDownloadFilename(rawName, 'file');
        dlMime = getMimeTypeForFilename(dlFileName);
        const dlPathUrl = withAuthUrl('/api/download/' + encodeURIComponent(dlFileName) + '?path=' + encodeURIComponent(singlePath));
        absUrl = origin + dlPathUrl;
      } else {
        dlFileName = sanitizeDownloadFilename(rawName, 'folder') + '.zip';
        dlMime = 'application/zip';
        const zipPathUrl = withAuthUrl('/api/zip/' + encodeURIComponent(dlFileName) + '?path=' + encodeURIComponent(singlePath));
        absUrl = origin + zipPathUrl;
      }
    } else if (draggedItemPaths.length > 1) {
      const folderBase = currentPath === '/' ? 'files' : (currentPath.split('/').pop() || 'files');
      dlFileName = sanitizeDownloadFilename(folderBase + '_' + draggedItemPaths.length + '_items', 'selected_files') + '.zip';
      dlMime = 'application/zip';
      const zipPathUrl = withAuthUrl('/api/zip/' + encodeURIComponent(dlFileName) + '?items=' + encodeURIComponent(JSON.stringify(draggedItemPaths)));
      absUrl = origin + zipPathUrl;
    }

    activeDragDownloadUrl = absUrl;
    activeDragDownloadName = dlFileName;

    if (absUrl && dlFileName) {
      // Chromium / Edge native file stream to Windows Explorer
      e.dataTransfer.setData('DownloadURL', dlMime + ':' + dlFileName + ':' + absUrl);
      // Standard URI & Mozilla URL payloads for Firefox / OS drop targets
      e.dataTransfer.setData('text/uri-list', absUrl);
      e.dataTransfer.setData('text/x-moz-url', absUrl + '\n' + dlFileName);
      e.dataTransfer.setData('text/plain', absUrl);
    }
  } catch (err) {}
}

function onItemDragEnd(e) {
  const wasInternalActive = isInternalDragActive;
  const dlUrl = activeDragDownloadUrl;
  const dlName = activeDragDownloadName;
  const lastInWindow = lastDragOverInsideWindow;
  cleanupInternalDragState();

  // Firefox fallback: Firefox does not implement Chromium's non-standard DownloadURL CF_HDROP stream
  // when dragging onto Windows Explorer, so if the user releases an uncancelled drag outside the
  // browser viewport in Firefox, trigger the direct file/ZIP download immediately.
  if (wasInternalActive && dlUrl && /Firefox\//i.test(navigator.userAgent || '')) {
    const userCancelled = !!(e && e.mozUserCancelled === true);
    if (!userCancelled) {
      const cx = e && typeof e.clientX === 'number' ? e.clientX : 0;
      const cy = e && typeof e.clientY === 'number' ? e.clientY : 0;
      const outByCoords = (cx <= 0 || cy <= 0 || cx >= window.innerWidth || cy >= window.innerHeight);
      const outByTimer = (Date.now() - lastInWindow) > 120;
      if (outByCoords || outByTimer) {
        triggerDirectBrowserDownload(dlUrl, dlName);
        showToast('⬇️ Downloading "' + (dlName || 'item') + '"', 'emerald');
      }
    }
  }
}

function canDropIntoFolder(targetFolderPath) {
  if (!isInternalDragActive || isReadOnly || !targetFolderPath || draggedItemPaths.length === 0) {
    return false;
  }
  let hasValidItem = false;
  for (const src of draggedItemPaths) {
    if (src === targetFolderPath) return false;
    if (targetFolderPath.startsWith(src + '/')) return false;
    if (getParentPath(src) !== targetFolderPath) {
      hasValidItem = true;
    }
  }
  return hasValidItem;
}

function onFolderDragOver(e, targetFolderPath) {
  if (!canDropIntoFolder(targetFolderPath)) return;
  e.preventDefault();
  e.stopPropagation();
  try { e.dataTransfer.dropEffect = 'move'; } catch (err) {}
  if (e.currentTarget && !e.currentTarget.classList.contains('drop-target-active')) {
    e.currentTarget.classList.add('drop-target-active');
  }
}

function onFolderDragLeave(e) {
  if (!e.currentTarget) return;
  const rel = e.relatedTarget;
  if (rel && e.currentTarget.contains(rel)) return;
  e.currentTarget.classList.remove('drop-target-active');
}

async function onFolderDrop(e, targetFolderPath) {
  if (!isInternalDragActive) return;
  e.preventDefault();
  e.stopPropagation();
  const rawDragged = draggedItemPaths.slice();
  cleanupInternalDragState();

  const validItems = rawDragged.filter(src =>
    src !== targetFolderPath &&
    !targetFolderPath.startsWith(src + '/') &&
    getParentPath(src) !== targetFolderPath
  );
  if (validItems.length === 0) return;

  const firstItemName = validItems[0].split('/').pop() || validItems[0];
  const itemSummary = validItems.length === 1
    ? ('"' + firstItemName + '"')
    : (validItems.length + ' items (' + validItems.slice(0, 3).map(p => p.split('/').pop()).join(', ') + (validItems.length > 3 ? ', ...' : '') + ')');

  const confirmed = await showConfirmDialog({
    icon: '✂️',
    badgeClass: 'icon-badge-amber',
    title: validItems.length === 1 ? 'Move Item to Folder?' : ('Move ' + validItems.length + ' Items to Folder?'),
    subtitle: 'Are you sure you want to move ' + itemSummary + ' to the selected folder?',
    targetLabel: 'Destination Folder',
    targetValue: targetFolderPath,
    confirmText: '✂️ Move ' + (validItems.length === 1 ? 'Item' : (validItems.length + ' Items')),
    confirmBtnClass: 'btn-amber'
  });

  if (!confirmed) return;
  await executePasteTo(targetFolderPath, 'move', validItems);
}

async function loadDirectory(path, options) {
  closeItemMenu();
  if (isAddressEditMode) exitAddressEditMode();
  try {
    const res = await apiFetch('/api/list?path=' + encodeURIComponent(path));
    if (res.status === 401) {
      document.getElementById('fileTableBody').innerHTML =
        '<tr><td colspan="5" class="empty-state">🔐 Please sign in to view files</td></tr>';
      return;
    }
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const data = await res.json();
    if (data && data.authToken && !authToken) {
      authToken = data.authToken;
    }
    updateLogoutButtonVisibility(true);
    const nextPath = data.path || '/';
    const pathChanged = currentPath !== nextPath;
    if (pathChanged) {
      selectedPaths.clear();
      lastSelectedPath = null;
      typeaheadFocusedPath = null;
      currentPage = 1;
    }
    currentPath = nextPath;
    if (!options || !options.skipHistory) {
      if (navHistory[navHistoryIndex] !== nextPath) {
        navHistory = navHistory.slice(0, navHistoryIndex + 1);
        navHistory.push(nextPath);
        navHistoryIndex = navHistory.length - 1;
      }
    }
    updateNavHistoryButtons();
    currentItems = data.items || [];
    isReadOnly = !!data.readOnly;
    if (typeof data.activeConnections === 'number') {
      updateLiveConnectionBadge(true, data.activeConnections);
    }

    document.getElementById('rootNameLabel').textContent = data.rootDisplayName || 'Storage';
    const badge = document.getElementById('accessBadge');
    const writeActions = document.getElementById('writeActions');
    const batchWriteButtons = document.getElementById('batchWriteButtons');
    if (isReadOnly) {
      badge.textContent = 'Read-Only';
      badge.className = 'badge badge-ro';
      if (writeActions) writeActions.style.display = 'none';
      if (batchWriteButtons) batchWriteButtons.style.display = 'none';
    } else {
      badge.textContent = 'Read & Write';
      badge.className = 'badge badge-rw';
      if (writeActions) writeActions.style.display = 'contents';
      if (batchWriteButtons) batchWriteButtons.style.display = 'contents';
    }

    if (data.totalSpace > 0) {
      const used = Math.max(0, data.totalSpace - data.freeSpace);
      const pct = Math.min(100, Math.round((used / data.totalSpace) * 100));
      document.getElementById('storageUsedText').textContent = 'Used: ' + formatBytes(used) + ' (' + pct + '%)';
      document.getElementById('storageFreeText').textContent = 'Free: ' + formatBytes(data.freeSpace);
      const sBar = document.getElementById('storageBar');
      if (sBar) {
        sBar.classList.remove('indeterminate');
        sBar.style.animation = 'none';
        sBar.style.transform = 'none';
        sBar.style.width = pct + '%';
      }
    }

    renderBreadcrumbs();
    const activeQuery = (document.getElementById('searchInput').value || '').trim();
    if (searchSubfolders && activeQuery) {
      triggerRecursiveSearch(activeQuery, true);
    } else {
      const existingSet = new Set(currentItems.map(it => getItemFullPath(it)));
      Array.from(selectedPaths).forEach(p => {
        if (!existingSet.has(p)) selectedPaths.delete(p);
      });
      setViewMode(viewMode);
      updateBatchAndClipboardBars();
    }
  } catch (err) {
    document.getElementById('fileTableBody').innerHTML =
      '<tr><td colspan="5" class="empty-state">Failed to load directory: ' + err.message + '</td></tr>';
  }
}

function renderBreadcrumbs() {
  const container = document.getElementById('breadcrumbs');
  container.innerHTML = '';
  const parts = currentPath.split('/').filter(Boolean);

  const rootCrumb = document.createElement('span');
  rootCrumb.className = 'crumb' + (parts.length === 0 ? ' active' : '');
  rootCrumb.textContent = '🏠 Root';
  rootCrumb.onclick = (e) => { e.stopPropagation(); navigateToFolder('/'); };
  rootCrumb.ondragover = (e) => onFolderDragOver(e, '/');
  rootCrumb.ondragleave = (e) => onFolderDragLeave(e);
  rootCrumb.ondrop = (e) => onFolderDrop(e, '/');
  container.appendChild(rootCrumb);

  let built = '';
  parts.forEach((part, idx) => {
    const sep = document.createElement('span');
    sep.className = 'crumb-sep';
    sep.textContent = '/';
    container.appendChild(sep);

    built += '/' + part;
    const target = built;
    const c = document.createElement('span');
    c.className = 'crumb' + (idx === parts.length - 1 ? ' active' : '');
    c.textContent = part;
    c.onclick = (e) => { e.stopPropagation(); navigateToFolder(target); };
    c.ondragover = (e) => onFolderDragOver(e, target);
    c.ondragleave = (e) => onFolderDragLeave(e);
    c.ondrop = (e) => onFolderDrop(e, target);
    container.appendChild(c);
  });
}

function setSort(field) {
  if (sortField === field) {
    sortAsc = !sortAsc;
  } else {
    sortField = field;
    sortAsc = true;
  }
  saveUserPref('ufs_sort_field', sortField);
  saveUserPref('ufs_sort_asc', sortAsc ? '1' : '0');
  updateSortIndicators();
  renderTable();
}

function getFileCategory(name, isDir) {
  if (isDir) return { cls: 'icon-dir', icon: '📁', preview: null };
  const ext = (name.split('.').pop() || '').toLowerCase();
  if (['jpg','jpeg','png','gif','webp','svg','bmp'].includes(ext))
    return { cls: 'icon-img', icon: '🖼️', preview: 'image' };
  if (['mp4','webm','mkv','mov','avi'].includes(ext))
    return { cls: 'icon-vid', icon: '🎬', preview: 'video' };
  if (['mp3','wav','ogg','m4a','flac','aac'].includes(ext))
    return { cls: 'icon-aud', icon: '🎵', preview: 'audio' };
  if (['zip','rar','7z','tar','gz','apk'].includes(ext))
    return { cls: 'icon-zip', icon: '📦', preview: null };
  if (['txt','md','json','xml','log','csv','html','css','js','kt','java','py','sh','yml','yaml','ini','conf'].includes(ext))
    return { cls: 'icon-file', icon: '📄', preview: 'text' };
  return { cls: 'icon-file', icon: '📄', preview: null };
}

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
}

function getFilteredSortedItems() {
  const query = (document.getElementById('searchInput').value || '').trim().toLowerCase();
  let filtered;
  if (query && searchSubfolders && Array.isArray(recursiveSearchResults)) {
    filtered = recursiveSearchResults.slice();
  } else {
    filtered = currentItems.filter(it => !query || it.name.toLowerCase().includes(query));
  }
  filtered.sort((a, b) => {
    if (a.isDirectory !== b.isDirectory) return a.isDirectory ? -1 : 1;
    let cmp = 0;
    if (sortField === 'size') {
      const sizeA = a.isDirectory
        ? (folderSizeCache.has(getItemFullPath(a)) && folderSizeCache.get(getItemFullPath(a)).state === 'done'
            ? folderSizeCache.get(getItemFullPath(a)).size
            : -1)
        : (a.size || 0);
      const sizeB = b.isDirectory
        ? (folderSizeCache.has(getItemFullPath(b)) && folderSizeCache.get(getItemFullPath(b)).state === 'done'
            ? folderSizeCache.get(getItemFullPath(b)).size
            : -1)
        : (b.size || 0);
      cmp = sizeA - sizeB;
    } else if (sortField === 'date') {
      cmp = (a.lastModified || 0) - (b.lastModified || 0);
    } else {
      cmp = a.name.toLowerCase().localeCompare(b.name.toLowerCase());
    }
    return sortAsc ? cmp : -cmp;
  });
  return filtered;
}

function renderFolderSizeSlotHtml(fullPath) {
  const safePath = escapeHtml(fullPath);
  const entry = folderSizeCache.get(fullPath);
  if (!entry) {
    return '<button type="button" class="folder-size-btn" onclick="calculateFolderSize(event, \'' + safePath + '\')" title="Calculate folder size">' +
      'Calculate Size</button>';
  }
  if (entry.state === 'loading') {
    return '<span class="folder-size-loading" title="Calculating folder size...">Calculating...</span>';
  }
  if (entry.state === 'error') {
    return '<button type="button" class="folder-size-btn" onclick="calculateFolderSize(event, \'' + safePath + '\')" title="Retry folder size calculation">' +
      'Retry Size</button>';
  }
  const filesNote = typeof entry.fileCount === 'number'
    ? (entry.fileCount + ' file' + (entry.fileCount === 1 ? '' : 's') +
       (typeof entry.folderCount === 'number' && entry.folderCount > 0
         ? (', ' + entry.folderCount + ' folder' + (entry.folderCount === 1 ? '' : 's'))
         : ''))
    : '';
  return '<span class="folder-size-val" title="' + escapeHtml(filesNote) + '">' + formatBytes(entry.size) + '</span>';
}

async function calculateFolderSize(e, fullPath, forceLoading = false) {
  if (e) {
    e.preventDefault();
    e.stopPropagation();
  }
  const existing = folderSizeCache.get(fullPath);
  if (!forceLoading && existing && existing.state === 'loading') return;

  folderSizeCache.set(fullPath, { state: 'loading', size: -1, fileCount: 0, folderCount: 0 });
  renderTable();
  refreshOpenPropertiesModal();

  try {
    const res = await apiFetch('/api/folder-size?path=' + encodeURIComponent(fullPath));
    if (res.status === 401) {
      folderSizeCache.delete(fullPath);
      renderTable();
      refreshOpenPropertiesModal();
      return;
    }
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const data = await res.json();
    if (data && data.ok) {
      const computedSize = typeof data.size === 'number' ? data.size : 0;
      const computedCount = typeof data.fileCount === 'number' ? data.fileCount : 0;
      const computedFolderCount = typeof data.folderCount === 'number' ? data.folderCount : 0;
      folderSizeCache.set(fullPath, {
        state: 'done',
        size: computedSize,
        fileCount: computedCount,
        folderCount: computedFolderCount
      });
      for (const it of currentItems) {
        if (getItemFullPath(it) === fullPath) {
          it.size = computedSize;
        }
      }
      if (Array.isArray(recursiveSearchResults)) {
        for (const it of recursiveSearchResults) {
          if (getItemFullPath(it) === fullPath) {
            it.size = computedSize;
          }
        }
      }
    } else {
      folderSizeCache.set(fullPath, { state: 'error', size: -1, fileCount: 0, folderCount: 0 });
    }
  } catch (err) {
    folderSizeCache.set(fullPath, { state: 'error', size: -1, fileCount: 0, folderCount: 0 });
  }
  renderTable();
  refreshOpenPropertiesModal();
}

function getPaginationSlice(filtered) {
  const total = filtered.length;
  if (pageSize === 'all' || total === 0) {
    currentPage = 1;
    return {
      pageItems: filtered,
      total: total,
      totalPages: 1,
      startIdx: total > 0 ? 1 : 0,
      endIdx: total
    };
  }
  const limit = Number(pageSize) || 50;
  const totalPages = Math.max(1, Math.ceil(total / limit));
  if (currentPage > totalPages) currentPage = totalPages;
  if (currentPage < 1) currentPage = 1;
  const start = (currentPage - 1) * limit;
  const end = Math.min(total, start + limit);
  return {
    pageItems: filtered.slice(start, end),
    total: total,
    totalPages: totalPages,
    startIdx: start + 1,
    endIdx: end
  };
}

function goToPage(p) {
  currentPage = p;
  closeItemMenu();
  renderTable();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function renderPaginationControls(sliceInfo) {
  const summaryEl = document.getElementById('itemRangeSummary');
  const barEl = document.getElementById('paginationBar');
  const infoEl = document.getElementById('paginationInfo');
  const btnsEl = document.getElementById('paginationButtons');
  const barTopEl = document.getElementById('paginationBarTop');
  const infoTopEl = document.getElementById('paginationInfoTop');
  const btnsTopEl = document.getElementById('paginationButtonsTop');
  const query = (document.getElementById('searchInput').value || '').trim();
  const scopeSuffix = query ? (searchSubfolders ? ' (in folder & subfolders)' : ' (in current folder)') : '';

  if (sliceInfo.total === 0) {
    summaryEl.textContent = (isSearchingSubfolders ? 'Searching subfolders...' : ('0 items' + scopeSuffix));
    if (barEl) barEl.style.display = 'none';
    if (barTopEl) barTopEl.style.display = 'none';
    return;
  }

  summaryEl.textContent = 'Showing ' + sliceInfo.startIdx + '–' + sliceInfo.endIdx + ' of ' + sliceInfo.total + ' items' + scopeSuffix;

  if (sliceInfo.totalPages <= 1) {
    if (barEl) barEl.style.display = 'none';
    if (barTopEl) barTopEl.style.display = 'none';
    return;
  }

  if (barEl) barEl.style.display = 'flex';
  if (barTopEl) barTopEl.style.display = 'flex';
  const infoText = 'Page ' + currentPage + ' of ' + sliceInfo.totalPages + ' (' + sliceInfo.total + ' total items)';
  if (infoEl) infoEl.textContent = infoText;
  if (infoTopEl) infoTopEl.textContent = infoText;

  let btnsHtml = '';
  const prevDisabled = currentPage <= 1 ? 'disabled' : '';
  const nextDisabled = currentPage >= sliceInfo.totalPages ? 'disabled' : '';

  btnsHtml += '<button class="btn btn-sm" ' + prevDisabled + ' onclick="goToPage(1)" title="First Page">⟪</button>';
  btnsHtml += '<button class="btn btn-sm" ' + prevDisabled + ' onclick="goToPage(' + (currentPage - 1) + ')">‹ Prev</button>';

  const maxVisiblePages = 5;
  let startPage = Math.max(1, currentPage - 2);
  let endPage = Math.min(sliceInfo.totalPages, startPage + maxVisiblePages - 1);
  if (endPage - startPage < maxVisiblePages - 1) {
    startPage = Math.max(1, endPage - maxVisiblePages + 1);
  }

  for (let p = startPage; p <= endPage; p++) {
    const activeCls = p === currentPage ? ' active-toggle' : '';
    btnsHtml += '<button class="btn btn-sm' + activeCls + '" onclick="goToPage(' + p + ')">' + p + '</button>';
  }

  btnsHtml += '<button class="btn btn-sm" ' + nextDisabled + ' onclick="goToPage(' + (currentPage + 1) + ')">Next ›</button>';
  btnsHtml += '<button class="btn btn-sm" ' + nextDisabled + ' onclick="goToPage(' + sliceInfo.totalPages + ')" title="Last Page">⟫</button>';

  if (btnsEl) btnsEl.innerHTML = btnsHtml;
  if (btnsTopEl) btnsTopEl.innerHTML = btnsHtml;
}

function getItemGroupInfo(item) {
  if (groupByField === 'name') {
    const rawName = (item.name || '').trim();
    const firstChar = rawName ? rawName.charAt(0).toUpperCase() : '#';
    if (firstChar >= 'A' && firstChar <= 'Z') {
      return { key: 'letter_' + firstChar, label: firstChar, icon: '🔤', order: firstChar.charCodeAt(0) };
    }
    if (firstChar >= '0' && firstChar <= '9') {
      return { key: 'num_0_9', label: '0 – 9 (Numbers)', icon: '🔢', order: 48 };
    }
    if (/[\u0600-\u06FF\u00C0-\u024F\u0400-\u04FF]/.test(firstChar)) {
      return { key: 'uni_' + firstChar, label: firstChar, icon: '🔤', order: 200 + firstChar.charCodeAt(0) };
    }
    return { key: 'sym_other', label: '# (Symbols & Other)', icon: '🔣', order: 1 };
  }

  if (groupByField === 'date') {
    const ms = item.lastModified || 0;
    if (!ms || ms <= 0) {
      return { key: 'date_unknown', label: 'Unknown Date', icon: '🕒', order: 70 };
    }
    const now = new Date();
    const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
    const startOfYesterday = startOfToday - 86400000;
    const startOfWeek = startOfToday - 6 * 86400000;
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1).getTime();
    const startOfYear = new Date(now.getFullYear(), 0, 1).getTime();

    if (ms >= startOfToday) {
      return { key: 'date_today', label: 'Today', icon: '🕒', order: 10 };
    }
    if (ms >= startOfYesterday) {
      return { key: 'date_yesterday', label: 'Yesterday', icon: '🕒', order: 20 };
    }
    if (ms >= startOfWeek) {
      return { key: 'date_week', label: 'Earlier This Week', icon: '📅', order: 30 };
    }
    if (ms >= startOfMonth) {
      return { key: 'date_month', label: 'Earlier This Month', icon: '📅', order: 40 };
    }
    if (ms >= startOfYear) {
      return { key: 'date_year', label: 'Earlier This Year', icon: '📆', order: 50 };
    }
    return { key: 'date_older', label: 'Older', icon: '🗄️', order: 60 };
  }

  if (groupByField === 'type') {
    if (item.isDirectory) {
      return { key: 'type_folder', label: 'Folders', icon: '📁', order: 10 };
    }
    const name = item.name || '';
    const ext = (name.includes('.') ? (name.split('.').pop() || '') : '').toLowerCase();
    const cat = getFileCategory(name, false);
    if (cat.preview === 'image') {
      return { key: 'type_image', label: 'Images', icon: '🖼️', order: 20 };
    }
    if (cat.preview === 'video') {
      return { key: 'type_video', label: 'Videos', icon: '🎬', order: 30 };
    }
    if (cat.preview === 'audio') {
      return { key: 'type_audio', label: 'Audio', icon: '🎵', order: 40 };
    }
    if (cat.preview === 'text' || ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'odt', 'rtf', 'epub'].includes(ext)) {
      return { key: 'type_doc', label: 'Documents & Text', icon: '📄', order: 50 };
    }
    if (cat.cls === 'icon-zip') {
      return { key: 'type_archive', label: 'Archives & Packages', icon: '📦', order: 60 };
    }
    return { key: 'type_other', label: 'Other Files', icon: '📄', order: 70 };
  }

  if (groupByField === 'size') {
    if (item.isDirectory) {
      const fullPath = getItemFullPath(item);
      const cached = folderSizeCache.get(fullPath);
      if (!cached || cached.state !== 'done' || cached.size < 0) {
        return { key: 'size_folder', label: 'Folders', icon: '📁', order: 5 };
      }
      const sz = cached.size;
      if (sz === 0) return { key: 'size_empty', label: 'Empty (0 B)', icon: '⚪', order: 10 };
      if (sz < 100 * 1024) return { key: 'size_tiny', label: 'Tiny (< 100 KB)', icon: '🔹', order: 20 };
      if (sz < 1024 * 1024) return { key: 'size_small', label: 'Small (100 KB – 1 MB)', icon: '🟦', order: 30 };
      if (sz < 100 * 1024 * 1024) return { key: 'size_medium', label: 'Medium (1 MB – 100 MB)', icon: '🟩', order: 40 };
      if (sz < 1024 * 1024 * 1024) return { key: 'size_large', label: 'Large (100 MB – 1 GB)', icon: '🟧', order: 50 };
      return { key: 'size_huge', label: 'Huge (> 1 GB)', icon: '🟥', order: 60 };
    }
    const sz = typeof item.size === 'number' && item.size >= 0 ? item.size : 0;
    if (sz === 0) return { key: 'size_empty', label: 'Empty (0 B)', icon: '⚪', order: 10 };
    if (sz < 100 * 1024) return { key: 'size_tiny', label: 'Tiny (< 100 KB)', icon: '🔹', order: 20 };
    if (sz < 1024 * 1024) return { key: 'size_small', label: 'Small (100 KB – 1 MB)', icon: '🟦', order: 30 };
    if (sz < 100 * 1024 * 1024) return { key: 'size_medium', label: 'Medium (1 MB – 100 MB)', icon: '🟩', order: 40 };
    if (sz < 1024 * 1024 * 1024) return { key: 'size_large', label: 'Large (100 MB – 1 GB)', icon: '🟧', order: 50 };
    return { key: 'size_huge', label: 'Huge (> 1 GB)', icon: '🟥', order: 60 };
  }

  return null;
}

function partitionItemsIntoGroups(items) {
  if (!groupByField || groupByField === 'none') {
    return [{ key: '__all__', label: '', icon: '', order: 0, items: items }];
  }
  const bucketMap = new Map();
  for (const item of items) {
    const info = getItemGroupInfo(item) || { key: 'other', label: 'Other', icon: '📁', order: 999 };
    if (!bucketMap.has(info.key)) {
      bucketMap.set(info.key, {
        key: info.key,
        label: info.label,
        icon: info.icon,
        order: info.order,
        items: []
      });
    }
    bucketMap.get(info.key).items.push(item);
  }
  const groups = Array.from(bucketMap.values());
  groups.sort((a, b) => {
    const diff = a.order - b.order;
    return groupByAsc ? diff : -diff;
  });
  return groups;
}

function renderTable() {
  const tbody = document.getElementById('fileTableBody');
  const gridWrapper = document.getElementById('gridViewWrapper');
  const query = (document.getElementById('searchInput').value || '').trim();

  if (query && searchSubfolders && isSearchingSubfolders) {
    renderPaginationControls({ pageItems: [], total: 0, totalPages: 1, startIdx: 0, endIdx: 0 });
    tbody.innerHTML = '<tr><td colspan="5" class="empty-state">🔍 Searching in ' + escapeHtml(currentPath) + ' and its subfolders...</td></tr>';
    gridWrapper.innerHTML = '<div class="empty-state" style="grid-column:1/-1;">🔍 Searching in ' + escapeHtml(currentPath) + ' and its subfolders...</div>';
    document.getElementById('masterCheckbox').checked = false;
    return;
  }

  const filtered = getFilteredSortedItems();
  const sliceInfo = getPaginationSlice(filtered);
  const pageItems = sliceInfo.pageItems;

  renderPaginationControls(sliceInfo);

  let rowsHtml = '';
  let gridHtml = '';

  if (pageItems.length === 0) {
    const emptyMsg = query
      ? (searchSubfolders
          ? 'No matching files or folders found in <strong>' + escapeHtml(currentPath) + '</strong> or its subfolders'
          : '<div>No matching files or folders in <strong>' + escapeHtml(currentPath) + '</strong></div>' +
            '<div style="margin-top:12px;"><button type="button" class="btn btn-sm btn-primary" onclick="setSearchSubfolders(true)">🗂️ Search in Subfolders Instead</button></div>')
      : 'This folder is empty';
    rowsHtml += '<tr><td colspan="5" class="empty-state">' + emptyMsg + '</td></tr>';
    gridHtml += '<div class="empty-state" style="grid-column:1/-1;">' + emptyMsg + '</div>';
    tbody.innerHTML = rowsHtml;
    gridWrapper.innerHTML = gridHtml;
    document.getElementById('masterCheckbox').checked = false;
    return;
  }

  const groups = partitionItemsIntoGroups(pageItems);
  const showGroupHeaders = groupByField && groupByField !== 'none';
  let allPageSelected = true;

  for (const grp of groups) {
    if (showGroupHeaders) {
      const countLabel = grp.items.length + ' item' + (grp.items.length === 1 ? '' : 's');
      const safeGrpLabel = escapeHtml(grp.label);
      rowsHtml += '<tr class="group-header-row"><td colspan="5">' +
        '<div class="group-header-inner">' +
        '<span class="group-header-title"><span>' + grp.icon + '</span><span>' + safeGrpLabel + '</span></span>' +
        '<span class="group-header-count">' + countLabel + '</span>' +
        '</div></td></tr>';
      gridHtml += '<div class="grid-group-header">' +
        '<span class="group-header-title"><span>' + grp.icon + '</span><span>' + safeGrpLabel + '</span></span>' +
        '<span class="group-header-count">' + countLabel + '</span>' +
        '</div>';
    }

    for (const item of grp.items) {
      const fullPath = getItemFullPath(item);
      const parentDir = item.parentPath || getParentPath(fullPath);
      const isSubfolderResult = parentDir !== currentPath;
      const safeParentDir = escapeHtml(parentDir);
      const isChecked = selectedPaths.has(fullPath);
      if (!isChecked) allPageSelected = false;

      const cat = getFileCategory(item.name, item.isDirectory);
      const encodedPath = encodeURIComponent(fullPath);
      const safeName = escapeHtml(item.name);
      const safeFullPath = escapeHtml(fullPath);
      const previewTypeStr = cat.preview ? cat.preview : '';
      const dlHref = withAuthUrl('/api/download?path=' + encodedPath);
      const zipHref = withAuthUrl('/api/zip?path=' + encodedPath);
      const inlineHref = withAuthUrl('/api/download?path=' + encodedPath + '&inline=1');

      let subPathBadgeHtml = '';
      if (isSubfolderResult) {
        let relDir = parentDir;
        if (currentPath !== '/' && parentDir.startsWith(currentPath + '/')) {
          relDir = parentDir.substring(currentPath.length + 1);
        } else if (currentPath === '/' && parentDir.startsWith('/')) {
          relDir = parentDir.substring(1);
        }
        subPathBadgeHtml = '<div class="item-subpath-badge" onclick="event.stopPropagation();navigateToFolder(\'' + safeParentDir + '\')" title="Open containing folder: ' + safeParentDir + '">' +
          '📂 ' + escapeHtml(relDir || '/') + '</div>';
      }

      let actionsHtml = '';
      if (isSubfolderResult) {
        actionsHtml += '<button type="button" class="btn btn-sm" onclick="navigateToFolder(\'' + safeParentDir + '\')" title="Open Containing Folder (' + safeParentDir + ')">📍</button>';
      }
      if (item.isDirectory) {
        actionsHtml += '<a class="btn btn-sm btn-primary" href="' + zipHref + '" title="Download Folder as ZIP">📦</a>';
      } else {
        if (cat.preview) {
          actionsHtml += '<button type="button" class="btn btn-sm" onclick="openPreview(\'' + safeFullPath + '\', \'' + cat.preview + '\', \'' + safeName + '\')" title="Preview">👁️</button>';
        }
        actionsHtml += '<a class="btn btn-sm btn-primary" href="' + dlHref + '" download title="Download">⬇️</a>';
      }

      if (!isReadOnly) {
        actionsHtml += '<button type="button" class="btn btn-sm" onclick="stageSingleItemClipboard(\'' + safeFullPath + '\', \'copy\')" title="Copy">📋</button>';
        actionsHtml += '<button type="button" class="btn btn-sm" onclick="stageSingleItemClipboard(\'' + safeFullPath + '\', \'move\')" title="Cut / Move">✂️</button>';
        actionsHtml += '<button type="button" class="btn btn-sm" onclick="promptRename(\'' + safeFullPath + '\', \'' + safeName + '\')" title="Rename">✏️</button>';
        actionsHtml += '<button type="button" class="btn btn-sm btn-danger" onclick="confirmDelete(\'' + safeFullPath + '\', \'' + safeName + '\', ' + item.isDirectory + ')" title="Delete">🗑️</button>';
      }

      const gridActionsHtml = actionsHtml;

      const isFocused = (typeaheadFocusedPath === fullPath);
      const stateClasses = (isChecked ? ' selected' : '') + (isFocused ? ' typeahead-focus' : '');

      const primaryActionCall = 'onItemPrimaryAction(event, \'' + safeFullPath + '\', ' + item.isDirectory + ', \'' + previewTypeStr + '\', \'' + safeName + '\')';
      const containerClickCall = 'onItemContainerClick(event, \'' + safeFullPath + '\')';
      const checkboxClickCall = 'onItemCheckboxClick(event, \'' + safeFullPath + '\', this.checked)';

      const contextMenuCall = 'openItemMenu(event, \'' + safeFullPath + '\', \'' + safeName + '\', ' + item.isDirectory + ', \'' + previewTypeStr + '\')';

      const dragSourceAttrs = ' draggable="true" ondragstart="onItemDragStart(event, \'' + safeFullPath + '\')" ondragend="onItemDragEnd(event)"';
      const dropTargetAttrs = (!isReadOnly && item.isDirectory)
        ? (' ondragover="onFolderDragOver(event, \'' + safeFullPath + '\')" ondragleave="onFolderDragLeave(event)" ondrop="onFolderDrop(event, \'' + safeFullPath + '\')"')
        : '';

      const listSizeDisplayHtml = item.isDirectory
        ? renderFolderSizeSlotHtml(fullPath)
        : ('<span style="font-family:monospace;">' + formatBytes(item.size) + '</span>');
      const gridSizeDisplayHtml = listSizeDisplayHtml;
      const readableType = escapeHtml(getReadableFileType(item.name, item.isDirectory));

      // 1. Details View Table Row HTML (Always shows lightweight emoji icon for images 🖼️ and videos 🎬)
      rowsHtml += '<tr class="file-row' + stateClasses + '" data-full-path="' + safeFullPath + '" onclick="' + containerClickCall + '" oncontextmenu="' + contextMenuCall + '"' + dragSourceAttrs + dropTargetAttrs + '>' +
        '<td style="text-align:center;" onclick="event.stopPropagation()">' +
        '<input type="checkbox" class="chk" ' + (isChecked ? 'checked' : '') + ' onclick="' + checkboxClickCall + '">' +
        '</td>' +
        '<td><div class="item-name-cell" onclick="' + primaryActionCall + '">' +
        '<div class="item-icon ' + cat.cls + '">' + cat.icon + '</div>' +
        '<div style="display:flex;flex-direction:column;"><span class="item-title">' + safeName + '</span>' + subPathBadgeHtml + '</div>' +
        '</div></td>' +
        '<td style="color:var(--text-muted);font-size:13px;white-space:nowrap;">' + formatDate(item.lastModified) + '</td>' +
        '<td style="color:var(--text-muted);font-family:monospace;white-space:nowrap;" onclick="event.stopPropagation()">' + listSizeDisplayHtml + '</td>' +
        '<td onclick="event.stopPropagation()"><div class="row-actions">' + actionsHtml + '</div></td>' +
        '</tr>';

      // 2. Non-Details Layouts (Extra large, Large, Medium, Small icons, List, Tiles, Content)
      const thumbBadgeLabel = item.isDirectory ? '📂 Open' : (cat.preview ? '👁️ Preview' : '⬇️ Download');
      const thumbHref = withAuthUrl('/api/thumbnail?path=' + encodedPath);
      const modeMeta = VIEW_MODE_META[viewMode] || VIEW_MODE_META.details;
      let mediaVisualHtml = '<span class="grid-big-icon">' + cat.icon + '</span>';
      let compactThumbHtml = '<span>' + cat.icon + '</span>';
      if (showMediaThumbnails && modeMeta.supportsThumbnails) {
        if (cat.preview === 'image') {
          mediaVisualHtml = '<img src="' + thumbHref + '" loading="lazy" draggable="false" alt="' + safeName + '" onerror="this.onerror=null;this.src=\'' + inlineHref + '\';">';
          compactThumbHtml = mediaVisualHtml;
        } else if (cat.preview === 'video') {
          mediaVisualHtml = '<div class="grid-video-thumb-wrap">' +
            '<img src="' + thumbHref + '" loading="lazy" draggable="false" alt="' + safeName + '" onerror="this.parentNode.outerHTML=\'<span class=&quot;grid-big-icon&quot;>🎬</span>\';">' +
            '<span class="grid-video-play-icon">▶</span>' +
            '</div>';
          compactThumbHtml = mediaVisualHtml;
        }
      }
      const thumbContent = mediaVisualHtml + '<span class="grid-thumb-badge">' + thumbBadgeLabel + '</span>';

      if (viewMode === 'small_icons') {
        gridHtml += '<div class="view-small-card' + stateClasses + '" data-full-path="' + safeFullPath + '" onclick="' + containerClickCall + '" oncontextmenu="' + contextMenuCall + '"' + dragSourceAttrs + dropTargetAttrs + '>' +
          '<input type="checkbox" class="chk" ' + (isChecked ? 'checked' : '') + ' onclick="' + checkboxClickCall + '">' +
          '<div class="view-small-main" onclick="' + primaryActionCall + '" title="' + safeName + '">' +
            '<div class="view-small-icon ' + cat.cls + '">' + cat.icon + '</div>' +
            '<span class="view-small-title">' + safeName + '</span>' +
          '</div>' +
          '<button type="button" class="btn btn-sm" style="padding:2px 6px;" onclick="' + contextMenuCall + '" title="Item Actions">⋮</button>' +
        '</div>';
      } else if (viewMode === 'list') {
        gridHtml += '<div class="view-list-item' + stateClasses + '" data-full-path="' + safeFullPath + '" onclick="' + containerClickCall + '" oncontextmenu="' + contextMenuCall + '"' + dragSourceAttrs + dropTargetAttrs + '>' +
          '<input type="checkbox" class="chk" ' + (isChecked ? 'checked' : '') + ' onclick="' + checkboxClickCall + '">' +
          '<div class="view-small-main" onclick="' + primaryActionCall + '" title="' + safeName + '">' +
            '<div class="view-small-icon ' + cat.cls + '">' + cat.icon + '</div>' +
            '<span class="view-small-title">' + safeName + '</span>' +
          '</div>' +
          '<button type="button" class="btn btn-sm" style="padding:2px 6px;" onclick="' + contextMenuCall + '" title="Item Actions">⋮</button>' +
        '</div>';
      } else if (viewMode === 'tiles') {
        gridHtml += '<div class="view-tile-card' + stateClasses + '" data-full-path="' + safeFullPath + '" onclick="' + containerClickCall + '" oncontextmenu="' + contextMenuCall + '"' + dragSourceAttrs + dropTargetAttrs + '>' +
          '<input type="checkbox" class="chk" ' + (isChecked ? 'checked' : '') + ' onclick="' + checkboxClickCall + '">' +
          '<div class="view-tile-thumb" onclick="' + primaryActionCall + '">' + compactThumbHtml + '</div>' +
          '<div class="view-tile-info">' +
            '<div class="view-tile-title" onclick="' + primaryActionCall + '" title="' + safeName + '">' + safeName + '</div>' +
            '<div class="view-tile-sub">' + readableType + '</div>' +
            '<div class="view-tile-sub" onclick="event.stopPropagation()">' + gridSizeDisplayHtml + '</div>' +
          '</div>' +
          '<button type="button" class="btn btn-sm" style="padding:4px 7px;" onclick="' + contextMenuCall + '" title="Item Actions">⋮</button>' +
        '</div>';
      } else {
        // extra_large_icons, large_icons, medium_icons
        gridHtml += '<div class="grid-card' + stateClasses + '" data-full-path="' + safeFullPath + '" onclick="' + containerClickCall + '" oncontextmenu="' + contextMenuCall + '"' + dragSourceAttrs + dropTargetAttrs + '>' +
          '<div class="grid-card-top" onclick="event.stopPropagation()">' +
            '<input type="checkbox" class="chk" ' + (isChecked ? 'checked' : '') + ' onclick="' + checkboxClickCall + '" title="Select item (Ctrl+Click or Shift+Click)">' +
            '<span class="grid-size-pill">' + (item.isDirectory ? '📁 Folder' : '📄 File') + '</span>' +
          '</div>' +
          '<div class="grid-thumb-box" onclick="' + primaryActionCall + '">' + thumbContent + '</div>' +
          '<div class="grid-info">' +
            '<div class="grid-title" onclick="' + primaryActionCall + '" title="' + safeName + '">' + safeName + '</div>' +
            subPathBadgeHtml +
            '<div class="grid-meta" onclick="event.stopPropagation()">' +
              '<span class="grid-meta-date" title="' + escapeHtml(formatDate(item.lastModified)) + '">' + formatDate(item.lastModified) + '</span>' +
              '<span class="grid-meta-size">' + gridSizeDisplayHtml + '</span>' +
            '</div>' +
          '</div>' +
          '<div class="grid-actions" onclick="event.stopPropagation()">' + gridActionsHtml + '</div>' +
          '</div>';
      }
    }
  }

  tbody.innerHTML = rowsHtml;
  gridWrapper.innerHTML = gridHtml;
  document.getElementById('masterCheckbox').checked = pageItems.length > 0 && allPageSelected;
}

/* Modifier Click (Ctrl / Shift) & Primary Item Interaction */
function clearBrowserTextSelection() {
  if (window.getSelection) {
    try { window.getSelection().removeAllRanges(); } catch (e) {}
  }
}

function applyShiftRangeSelection(targetPath, desiredState) {
  const items = getFilteredSortedItems();
  const targetIdx = items.findIndex(it => getItemFullPath(it) === targetPath);
  const anchorIdx = lastSelectedPath ? items.findIndex(it => getItemFullPath(it) === lastSelectedPath) : -1;

  if (targetIdx === -1) return;
  if (anchorIdx === -1) {
    if (desiredState) selectedPaths.add(targetPath);
    else selectedPaths.delete(targetPath);
    lastSelectedPath = targetPath;
    return;
  }

  const start = Math.min(anchorIdx, targetIdx);
  const end = Math.max(anchorIdx, targetIdx);
  for (let i = start; i <= end; i++) {
    const p = getItemFullPath(items[i]);
    if (desiredState) selectedPaths.add(p);
    else selectedPaths.delete(p);
  }
}

function handleModifierSelectionClick(e, fullPath) {
  e.preventDefault();
  e.stopPropagation();
  clearBrowserTextSelection();

  if (e.shiftKey) {
    if (!e.ctrlKey && !e.metaKey) {
      selectedPaths.clear();
    }
    applyShiftRangeSelection(fullPath, true);
    if (!lastSelectedPath) lastSelectedPath = fullPath;
  } else if (e.ctrlKey || e.metaKey) {
    if (selectedPaths.has(fullPath)) {
      selectedPaths.delete(fullPath);
    } else {
      selectedPaths.add(fullPath);
    }
    lastSelectedPath = fullPath;
  }

  renderTable();
  updateBatchAndClipboardBars();
}

function onItemContainerClick(e, fullPath) {
  if (Date.now() < marqueeState.suppressClickUntil) return;
  if (e && (e.ctrlKey || e.metaKey || e.shiftKey)) {
    handleModifierSelectionClick(e, fullPath);
  }
}

function onItemPrimaryAction(e, fullPath, isDir, previewType, name) {
  if (Date.now() < marqueeState.suppressClickUntil) {
    if (e) e.stopPropagation();
    return;
  }
  if (e && (e.ctrlKey || e.metaKey || e.shiftKey)) {
    handleModifierSelectionClick(e, fullPath);
    return;
  }
  if (e) e.stopPropagation();
  lastSelectedPath = fullPath;
  if (isDir) {
    navigateToFolder(fullPath);
  } else if (previewType) {
    openPreview(fullPath, previewType, name);
  } else {
    window.location.href = withAuthUrl('/api/download?path=' + encodeURIComponent(fullPath));
  }
}

function onItemCheckboxClick(e, fullPath, checked) {
  if (e) e.stopPropagation();
  if (e && e.shiftKey && lastSelectedPath) {
    clearBrowserTextSelection();
    applyShiftRangeSelection(fullPath, checked);
  } else {
    if (checked) selectedPaths.add(fullPath);
    else selectedPaths.delete(fullPath);
    lastSelectedPath = fullPath;
  }
  renderTable();
  updateBatchAndClipboardBars();
}

/* Floating Item Context Menu (⋮ button & Right-Click) */
function openItemMenu(e, fullPath, name, isDir, previewType) {
  e.preventDefault();
  e.stopPropagation();

  const menu = document.getElementById('itemContextMenu');
  const encodedPath = encodeURIComponent(fullPath);
  const safeName = escapeHtml(name);
  const safeFullPath = escapeHtml(fullPath);
  const parentDir = getParentPath(fullPath);
  const safeParentDir = escapeHtml(parentDir);
  const cat = getFileCategory(name, isDir);
  const isSelected = selectedPaths.has(fullPath);
  const dlHref = withAuthUrl('/api/download?path=' + encodedPath);
  const zipHref = withAuthUrl('/api/zip?path=' + encodedPath);

  const isMultiSelection = isSelected && selectedPaths.size > 1;

  let html = '<div class="context-menu-header">' + cat.icon + ' ' + safeName + (isMultiSelection ? ' (' + selectedPaths.size + ' Selected)' : '') + '</div>';

  if (isMultiSelection) {
    html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();downloadSelectedZip()">📦 Download Selected (ZIP)</button>';
    html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();toggleSelectItem(\'' + safeFullPath + '\', false)">' +
      '<span>⬜ Deselect This Item</span><span class="context-menu-kbd">Ctrl+Click</span></button>';
    html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();clearSelection()">' +
      '<span>🧹 Clear Selection (' + selectedPaths.size + ')</span></button>';

    if (!isReadOnly) {
      html += '<div class="context-menu-divider"></div>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();stageClipboardFromSelection(\'copy\')"><span>📋 Copy (' + selectedPaths.size + ' items)</span><span class="context-menu-kbd">Ctrl+C</span></button>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();stageClipboardFromSelection(\'move\')"><span>✂️ Move (Cut) (' + selectedPaths.size + ' items)</span><span class="context-menu-kbd">Ctrl+X</span></button>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();openDestinationModalForSelection()">📂 Copy / Move To...</button>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();openBatchRenameModal()"><span>✏️ Batch Rename (' + selectedPaths.size + ' items)</span><span class="context-menu-kbd">F2</span></button>';
      html += '<div class="context-menu-divider"></div>';
      html += '<button type="button" class="context-menu-item danger" onclick="closeItemMenu();batchDeleteSelected()"><span>🗑️ Delete Selected (' + selectedPaths.size + ')</span><span class="context-menu-kbd">Delete</span></button>';
    }

    html += '<div class="context-menu-divider"></div>';
    html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();openPropertiesForSelection()">' +
      '<span>ℹ️ Properties (' + selectedPaths.size + ' Items)</span><span class="context-menu-kbd">Alt+Enter</span></button>';
  } else {
    if (isDir) {
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();navigateToFolder(\'' + safeFullPath + '\')">📂 Open Folder</button>';
      html += '<a class="context-menu-item" href="' + zipHref + '" onclick="closeItemMenu()">📦 Download Folder as ZIP</a>';
    } else {
      if (previewType) {
        html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();openPreview(\'' + safeFullPath + '\', \'' + previewType + '\', \'' + safeName + '\')"><span>👁️ Preview File</span><span class="context-menu-kbd">Alt+P</span></button>';
      }
      html += '<a class="context-menu-item" href="' + dlHref + '" download onclick="closeItemMenu()">⬇️ Download File</a>';
    }

    if (parentDir !== currentPath) {
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();navigateToFolder(\'' + safeParentDir + '\')">📍 Open Containing Folder</button>';
    }

    html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();toggleSelectItem(\'' + safeFullPath + '\', ' + (!isSelected) + ')">' +
      '<span>' + (isSelected ? '☑️ Deselect Item' : '⬜ Select Item') + '</span><span class="context-menu-kbd">Ctrl+Click</span></button>';

    if (!isReadOnly) {
      html += '<div class="context-menu-divider"></div>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();stageSingleItemClipboard(\'' + safeFullPath + '\', \'copy\')"><span>📋 Copy</span><span class="context-menu-kbd">Ctrl+C</span></button>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();stageSingleItemClipboard(\'' + safeFullPath + '\', \'move\')"><span>✂️ Move (Cut)</span><span class="context-menu-kbd">Ctrl+X</span></button>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();openDestinationModalForSingleItem(\'' + safeFullPath + '\')">📂 Copy / Move To...</button>';
      html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();promptRename(\'' + safeFullPath + '\', \'' + safeName + '\')"><span>✏️ Rename</span><span class="context-menu-kbd">F2</span></button>';
      html += '<div class="context-menu-divider"></div>';
      html += '<button type="button" class="context-menu-item danger" onclick="closeItemMenu();confirmDelete(\'' + safeFullPath + '\', \'' + safeName + '\', ' + isDir + ')"><span>🗑️ Delete</span><span class="context-menu-kbd">Delete</span></button>';
    }

    html += '<div class="context-menu-divider"></div>';
    html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();openPropertiesForSingleItem(\'' + safeFullPath + '\')">' +
      '<span>ℹ️ Properties</span><span class="context-menu-kbd">Alt+Enter</span></button>';
  }

  menu.innerHTML = html;
  menu.classList.add('active');

  const rect = menu.getBoundingClientRect();
  let x = e.clientX || 16;
  let y = e.clientY || 16;
  if (e.type === 'click' && e.currentTarget && e.currentTarget.getBoundingClientRect) {
    const btnRect = e.currentTarget.getBoundingClientRect();
    x = btnRect.right - rect.width;
    y = btnRect.bottom + 6;
    if (y + rect.height > window.innerHeight - 12) {
      y = Math.max(12, btnRect.top - rect.height - 6);
    }
  } else {
    if (y + rect.height > window.innerHeight - 12) {
      y = Math.max(12, window.innerHeight - rect.height - 12);
    }
  }
  if (x + rect.width > window.innerWidth - 12) {
    x = Math.max(12, window.innerWidth - rect.width - 12);
  }
  if (x < 12) x = 12;
  menu.style.left = x + 'px';
  menu.style.top = y + 'px';
}

function positionSubmenu(wrapEl) {
  if (!wrapEl) return;
  const sub = wrapEl.querySelector('.context-submenu');
  if (!sub) return;
  sub.classList.remove('flip-left', 'flip-up');
  const wrapRect = wrapEl.getBoundingClientRect();
  const subWidth = sub.offsetWidth || 220;
  const subHeight = sub.offsetHeight || 220;
  if (wrapRect.right + subWidth + 12 > window.innerWidth) {
    sub.classList.add('flip-left');
  }
  if (wrapRect.top + subHeight + 12 > window.innerHeight) {
    sub.classList.add('flip-up');
  }
}

function toggleSubmenuClick(e, wrapEl) {
  if (e) e.stopPropagation();
  if (!wrapEl) return;
  const wasOpen = wrapEl.classList.contains('open');
  document.querySelectorAll('#itemContextMenu .context-submenu-wrap.open').forEach(el => {
    if (el !== wrapEl) el.classList.remove('open');
  });
  if (wasOpen) {
    wrapEl.classList.remove('open');
  } else {
    wrapEl.classList.add('open');
    positionSubmenu(wrapEl);
  }
}

function isInteractiveOrItemElement(target) {
  if (!target || !target.closest) return false;
  return !!target.closest('.file-row, .grid-card, .view-small-card, .view-list-item, .view-tile-card, .view-content-row, button, a, input, select, textarea, label, th');
}

function onWorkspaceContextMenu(e) {
  if (!e) return;
  if (isBlockingModalOpen()) return;
  if (isInteractiveOrItemElement(e.target)) return;
  openBackgroundMenu(e);
}

function openBackgroundMenu(e) {
  e.preventDefault();
  e.stopPropagation();

  const menu = document.getElementById('itemContextMenu');
  const dirLabel = currentPath === '/'
    ? ((document.getElementById('rootNameLabel') && document.getElementById('rootNameLabel').textContent) || 'Root Folder')
    : (currentPath.split('/').pop() || currentPath);
  const safeDirLabel = escapeHtml(dirLabel);

  const chk = (cond) => '<span class="menu-check">' + (cond ? '✓' : '') + '</span>';
  const hasInternalClipboard = clipboardItems.length > 0 && !!clipboardMode;
  const pasteCountSuffix = hasInternalClipboard
    ? (' (' + clipboardItems.length + ')')
    : '';

  let html = '<div class="context-menu-header">📂 ' + safeDirLabel + '</div>';

  // 1. View submenu (All 8 Windows Explorer View Modes + Media Thumbnails toggle)
  let viewSubItemsHtml = '';
  for (const m of VIEW_MODE_ORDER) {
    const meta = VIEW_MODE_META[m];
    viewSubItemsHtml += '<button type="button" class="context-menu-item" onclick="setViewMode(\'' + m + '\')">' +
      chk(viewMode === m) + '<span>' + meta.icon + ' ' + escapeHtml(meta.label) + '</span>' +
    '</button>';
  }
  viewSubItemsHtml += '<div class="context-menu-divider"></div>' +
    '<button type="button" class="context-menu-item" onclick="closeItemMenu();toggleMediaThumbnails()">' +
      chk(showMediaThumbnails) + '<span>🖼️ Media Thumbnails</span>' +
    '</button>';

  html += '<div class="context-submenu-wrap" onmouseenter="positionSubmenu(this)">' +
    '<button type="button" class="context-menu-item" onclick="toggleSubmenuClick(event, this.parentElement)">' +
      '<span>👁️ View</span><span class="context-submenu-arrow">▸</span>' +
    '</button>' +
    '<div class="context-submenu">' + viewSubItemsHtml + '</div>' +
  '</div>';

  // 2. Sort by submenu
  html += '<div class="context-submenu-wrap" onmouseenter="positionSubmenu(this)">' +
    '<button type="button" class="context-menu-item" onclick="toggleSubmenuClick(event, this.parentElement)">' +
      '<span>⇅ Sort by</span><span class="context-submenu-arrow">▸</span>' +
    '</button>' +
    '<div class="context-submenu">' +
      '<button type="button" class="context-menu-item" onclick="setSortFromMenu(\'name\')">' +
        chk(sortField === 'name') + '<span>🔤 Name</span>' +
      '</button>' +
      '<button type="button" class="context-menu-item" onclick="setSortFromMenu(\'date\')">' +
        chk(sortField === 'date') + '<span>🕒 Date Modified</span>' +
      '</button>' +
      '<button type="button" class="context-menu-item" onclick="setSortFromMenu(\'size\')">' +
        chk(sortField === 'size') + '<span>📊 Size</span>' +
      '</button>' +
      '<div class="context-menu-divider"></div>' +
      '<button type="button" class="context-menu-item" onclick="setSortDirection(true)">' +
        chk(sortAsc === true) + '<span>↑ Ascending</span>' +
      '</button>' +
      '<button type="button" class="context-menu-item" onclick="setSortDirection(false)">' +
        chk(sortAsc === false) + '<span>↓ Descending</span>' +
      '</button>' +
    '</div>' +
  '</div>';

  // 3. Group by submenu
  html += '<div class="context-submenu-wrap" onmouseenter="positionSubmenu(this)">' +
    '<button type="button" class="context-menu-item" onclick="toggleSubmenuClick(event, this.parentElement)">' +
      '<span>🗂️ Group by</span><span class="context-submenu-arrow">▸</span>' +
    '</button>' +
    '<div class="context-submenu">' +
      '<button type="button" class="context-menu-item" onclick="setGroupBy(\'none\', true)">' +
        chk(groupByField === 'none') + '<span>⊘ None</span>' +
      '</button>' +
      '<div class="context-menu-divider"></div>' +
      '<button type="button" class="context-menu-item" onclick="setGroupBy(\'name\', true)">' +
        chk(groupByField === 'name') + '<span>🔤 Name</span>' +
      '</button>' +
      '<button type="button" class="context-menu-item" onclick="setGroupBy(\'date\', true)">' +
        chk(groupByField === 'date') + '<span>🕒 Date Modified</span>' +
      '</button>' +
      '<button type="button" class="context-menu-item" onclick="setGroupBy(\'type\', true)">' +
        chk(groupByField === 'type') + '<span>🏷️ File Type</span>' +
      '</button>' +
      '<button type="button" class="context-menu-item" onclick="setGroupBy(\'size\', true)">' +
        chk(groupByField === 'size') + '<span>📊 Size</span>' +
      '</button>' +
      '<div class="context-menu-divider"></div>' +
      '<button type="button" class="context-menu-item' + (groupByField === 'none' ? ' disabled' : '') + '" ' + (groupByField === 'none' ? 'disabled ' : '') + 'onclick="setGroupByDirection(true)">' +
        chk(groupByField !== 'none' && groupByAsc === true) + '<span>↑ Ascending</span>' +
      '</button>' +
      '<button type="button" class="context-menu-item' + (groupByField === 'none' ? ' disabled' : '') + '" ' + (groupByField === 'none' ? 'disabled ' : '') + 'onclick="setGroupByDirection(false)">' +
        chk(groupByField !== 'none' && groupByAsc === false) + '<span>↓ Descending</span>' +
      '</button>' +
    '</div>' +
  '</div>';

  // 4. Refresh
  html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();refreshCurrentDirectory()">' +
    '<span>🔄 Refresh</span><span class="context-menu-kbd">F5</span>' +
  '</button>';

  html += '<div class="context-menu-divider"></div>';

  // 5. Paste (Always active in Read & Write mode to support both internal clipboard and OS file clipboard)
  if (!isReadOnly) {
    html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();handleContextMenuPaste()">' +
      '<span>📋 Paste' + pasteCountSuffix + '</span><span class="context-menu-kbd">Ctrl+V</span>' +
    '</button>';
  } else {
    html += '<button type="button" class="context-menu-item disabled" disabled>' +
      '<span>📋 Paste (Read-Only)</span><span class="context-menu-kbd">Ctrl+V</span>' +
    '</button>';
  }

  // 6. New submenu
  if (isReadOnly) {
    html += '<button type="button" class="context-menu-item disabled" disabled>' +
      '<span>➕ New (Read-Only)</span><span class="context-submenu-arrow">▸</span>' +
    '</button>';
  } else {
    html += '<div class="context-submenu-wrap" onmouseenter="positionSubmenu(this)">' +
      '<button type="button" class="context-menu-item" onclick="toggleSubmenuClick(event, this.parentElement)">' +
        '<span>➕ New</span><span class="context-submenu-arrow">▸</span>' +
      '</button>' +
      '<div class="context-submenu">' +
        '<button type="button" class="context-menu-item" onclick="closeItemMenu();promptNewFolder()">' +
          '<span>📁 New Folder</span><span class="context-menu-kbd">Alt+N</span>' +
        '</button>' +
        '<button type="button" class="context-menu-item" onclick="closeItemMenu();promptNewFile()">' +
          '<span>📄 New Text / Empty File</span>' +
        '</button>' +
        '<div class="context-menu-divider"></div>' +
        '<button type="button" class="context-menu-item" onclick="closeItemMenu();document.getElementById(\'fileInput\').click()">' +
          '<span>⬆️ Upload Files</span>' +
        '</button>' +
        '<button type="button" class="context-menu-item" onclick="closeItemMenu();document.getElementById(\'folderInput\').click()">' +
          '<span>📁⬆️ Upload Folder</span>' +
        '</button>' +
      '</div>' +
    '</div>';
  }

  html += '<div class="context-menu-divider"></div>';

  // 7. Properties
  html += '<button type="button" class="context-menu-item" onclick="closeItemMenu();openPropertiesForCurrentDirectory()">' +
    '<span>ℹ️ Properties</span><span class="context-menu-kbd">Alt+Enter</span>' +
  '</button>';

  menu.innerHTML = html;
  menu.classList.add('active');

  const rect = menu.getBoundingClientRect();
  let x = e.clientX || 16;
  let y = e.clientY || 16;
  if (y + rect.height > window.innerHeight - 12) {
    y = Math.max(12, window.innerHeight - rect.height - 12);
  }
  if (x + rect.width > window.innerWidth - 12) {
    x = Math.max(12, window.innerWidth - rect.width - 12);
  }
  if (x < 12) x = 12;
  menu.style.left = x + 'px';
  menu.style.top = y + 'px';
}

function closeItemMenu() {
  const menu = document.getElementById('itemContextMenu');
  if (menu) menu.classList.remove('active');
}

function openDestinationModalForSingleItem(fullPath) {
  destModalItems = [fullPath];
  openDestModal(currentPath);
}

/* Item & Selection Properties Modal (Alt+Enter & Context Menu) */
function findItemByFullPath(fullPath) {
  for (const it of currentItems) {
    if (getItemFullPath(it) === fullPath) return it;
  }
  if (Array.isArray(recursiveSearchResults)) {
    for (const it of recursiveSearchResults) {
      if (getItemFullPath(it) === fullPath) return it;
    }
  }
  return null;
}

function getReadableFileType(name, isDir) {
  if (isDir) return 'Folder (Directory)';
  const ext = (name.includes('.') ? (name.split('.').pop() || '') : '').toLowerCase();
  const cat = getFileCategory(name, false);
  if (cat.preview === 'image') return (ext ? ext.toUpperCase() + ' ' : '') + 'Image File';
  if (cat.preview === 'video') return (ext ? ext.toUpperCase() + ' ' : '') + 'Video File';
  if (cat.preview === 'audio') return (ext ? ext.toUpperCase() + ' ' : '') + 'Audio File';
  if (cat.preview === 'text') return (ext ? ext.toUpperCase() + ' ' : '') + 'Text / Code Document';
  if (cat.cls === 'icon-zip') return (ext ? ext.toUpperCase() + ' ' : '') + 'Archive / Package';
  return ext ? (ext.toUpperCase() + ' File') : 'File';
}

function openPropertiesForSingleItem(fullPath) {
  const item = findItemByFullPath(fullPath);
  if (item) {
    activePropertiesTarget = { mode: 'single', fullPath: fullPath };
  } else {
    activePropertiesTarget = { mode: 'path', fullPath: fullPath };
  }
  renderPropertiesModalContent();
  document.getElementById('propertiesModal').classList.add('active');
  const isDirectory = (item && item.isDirectory) || (!item && (fullPath === '/' || !fullPath.includes('.')));
  if (isDirectory) {
    const cached = folderSizeCache.get(fullPath);
    if (!cached || cached.state !== 'done') {
      calculateFolderSize(null, fullPath, true);
    }
  }
}

function openPropertiesForSelection() {
  const modal = document.getElementById('propertiesModal');
  if (modal.classList.contains('active')) {
    closePropertiesModal();
    return;
  }
  if (selectedPaths.size === 1) {
    const onlyPath = Array.from(selectedPaths)[0];
    openPropertiesForSingleItem(onlyPath);
    return;
  }
  if (selectedPaths.size > 1) {
    const paths = Array.from(selectedPaths);
    activePropertiesTarget = { mode: 'multi', paths: paths };
    renderPropertiesModalContent();
    modal.classList.add('active');
    calculateAllSelectedFoldersInProperties();
    return;
  }
  // If nothing is selected, show properties for the current folder
  openPropertiesForCurrentDirectory();
}

function openPropertiesForCurrentDirectory() {
  const modal = document.getElementById('propertiesModal');
  activePropertiesTarget = { mode: 'current_dir', fullPath: currentPath };
  renderPropertiesModalContent();
  modal.classList.add('active');
  const cached = folderSizeCache.get(currentPath);
  if (!cached || cached.state !== 'done') {
    calculateFolderSize(null, currentPath, true);
  }
}

function refreshOpenPropertiesModal() {
  const modal = document.getElementById('propertiesModal');
  if (!modal || !modal.classList.contains('active') || !activePropertiesTarget) return;
  renderPropertiesModalContent();
}

function renderPropertiesModalContent() {
  if (!activePropertiesTarget) return;
  const badge = document.getElementById('propIconBadge');
  const titleEl = document.getElementById('propTitle');
  const subEl = document.getElementById('propSubtitle');
  const listEl = document.getElementById('propListBody');
  const copyBtn = document.getElementById('propCopyPathBtn');

  if (activePropertiesTarget.mode === 'multi') {
    const paths = activePropertiesTarget.paths || [];
    let dirCount = 0;
    let fileCount = 0;
    let knownFileBytes = 0;
    let knownDirBytes = 0;
    let totalContainedFiles = 0;
    let totalContainedFolders = 0;
    let uncalculatedDirs = [];
    let loadingDirs = [];

    for (const p of paths) {
      const it = findItemByFullPath(p);
      if (it && it.isDirectory) {
        dirCount++;
        const cached = folderSizeCache.get(p);
        if (cached && cached.state === 'done' && cached.size >= 0) {
          knownDirBytes += cached.size;
          totalContainedFiles += (typeof cached.fileCount === 'number' ? cached.fileCount : 0);
          totalContainedFolders += (typeof cached.folderCount === 'number' ? cached.folderCount : 0);
        } else if (cached && cached.state === 'loading') {
          loadingDirs.push(p);
        } else {
          uncalculatedDirs.push(p);
        }
      } else if (it) {
        fileCount++;
        knownFileBytes += (it.size || 0);
        totalContainedFiles++;
      } else {
        fileCount++;
        totalContainedFiles++;
      }
    }

    badge.textContent = '📑';
    titleEl.textContent = paths.length + ' Selected Items';
    subEl.textContent = fileCount + ' file' + (fileCount === 1 ? '' : 's') + ', ' + dirCount + ' folder' + (dirCount === 1 ? '' : 's');
    copyBtn.style.display = 'inline-flex';
    copyBtn.textContent = '📋 Copy Paths';

    let sizeValHtml = '';
    let containedFilesHtml = '';
    let containedFoldersHtml = '';
    const hasUncalculatedOrLoading = dirCount > 0 && (uncalculatedDirs.length > 0 || loadingDirs.length > 0);

    if (hasUncalculatedOrLoading) {
      const knownBytes = knownFileBytes + knownDirBytes;
      sizeValHtml = '<span>' + (knownBytes > 0 ? formatBytes(knownBytes) : '0 B') + '</span>';
      if (loadingDirs.length > 0) {
        sizeValHtml += ' <span class="folder-size-loading" title="Calculating folder size...">Calculating (' + (dirCount - loadingDirs.length) + '/' + dirCount + ')...</span>';
        containedFilesHtml = totalContainedFiles > 0
          ? (totalContainedFiles.toLocaleString() + ' file(s) <span class="folder-size-loading">...</span>')
          : '<span class="folder-size-loading" title="Calculating...">Calculating...</span>';
        containedFoldersHtml = totalContainedFolders > 0
          ? (totalContainedFolders.toLocaleString() + ' folder(s) <span class="folder-size-loading">...</span>')
          : '<span class="folder-size-loading" title="Calculating...">Calculating...</span>';
      } else if (uncalculatedDirs.length > 0) {
        sizeValHtml += ' <button type="button" class="folder-size-btn" onclick="calculateAllSelectedFoldersInProperties()">Calculate ' + uncalculatedDirs.length + ' Folder' + (uncalculatedDirs.length === 1 ? '' : 's') + '</button>';
        containedFilesHtml = '<span style="color:var(--text-muted);">--</span>';
        containedFoldersHtml = '<span style="color:var(--text-muted);">--</span>';
      }
    } else {
      // All folders calculated (or only files selected)
      const totalBytes = knownFileBytes + knownDirBytes;
      sizeValHtml = '<span>' + formatBytes(totalBytes) + ' (' + totalBytes.toLocaleString() + ' B)</span>';
      containedFilesHtml = totalContainedFiles.toLocaleString() + ' file' + (totalContainedFiles === 1 ? '' : 's');
      containedFoldersHtml = totalContainedFolders.toLocaleString() + ' folder' + (totalContainedFolders === 1 ? '' : 's');
    }

    listEl.innerHTML =
      '<div class="prop-row"><span class="prop-label">Selection Breakdown</span><span class="prop-val">' + fileCount + ' File(s), ' + dirCount + ' Folder(s)</span></div>' +
      '<div class="prop-row"><span class="prop-label">Location</span><span class="prop-val mono">' + escapeHtml(currentPath) + '</span></div>' +
      '<div class="prop-row"><span class="prop-label">Combined Size</span><span class="prop-val">' + sizeValHtml + '</span></div>' +
      '<div class="prop-row"><span class="prop-label">Contained Files</span><span class="prop-val">' + containedFilesHtml + '</span></div>' +
      '<div class="prop-row"><span class="prop-label">Contained Folders</span><span class="prop-val">' + containedFoldersHtml + '</span></div>' +
      '<div class="prop-row"><span class="prop-label">Permissions</span><span class="prop-val">' + (isReadOnly ? '🔒 Read-Only' : '✏️ Read &amp; Write') + '</span></div>';
    return;
  }

  if (activePropertiesTarget.mode === 'current_dir') {
    const dirPath = activePropertiesTarget.fullPath || '/';
    const dirName = dirPath === '/' ? (document.getElementById('rootNameLabel').textContent || 'Root Folder') : (dirPath.split('/').pop() || dirPath);
    const dirCount = currentItems.filter(i => i.isDirectory).length;
    const fileCount = currentItems.filter(i => !i.isDirectory).length;

    badge.textContent = '📂';
    titleEl.textContent = dirName;
    subEl.textContent = 'Current Folder Properties';
    copyBtn.style.display = 'inline-flex';
    copyBtn.textContent = '📋 Copy Path';

    const cacheEntry = folderSizeCache.get(dirPath);
    let sizeHtml = renderFolderSizeSlotHtml(dirPath);
    let totalFilesHtml = (fileCount + ' file(s), ' + dirCount + ' subfolder(s) directly inside');
    let recursiveBreakdownHtml = '';
    if (cacheEntry && cacheEntry.state === 'done') {
      sizeHtml = formatBytes(cacheEntry.size) + ' (' + cacheEntry.size.toLocaleString() + ' B)';
      const recFolderCount = typeof cacheEntry.folderCount === 'number' ? cacheEntry.folderCount : 0;
      totalFilesHtml += ' • ' + cacheEntry.fileCount + ' total files, ' + recFolderCount + ' total folders recursively';
      recursiveBreakdownHtml =
        '<div class="prop-row"><span class="prop-label">Contained Files</span><span class="prop-val">' + cacheEntry.fileCount.toLocaleString() + ' file' + (cacheEntry.fileCount === 1 ? '' : 's') + '</span></div>' +
        '<div class="prop-row"><span class="prop-label">Contained Folders</span><span class="prop-val">' + recFolderCount.toLocaleString() + ' folder' + (recFolderCount === 1 ? '' : 's') + '</span></div>';
    } else if (cacheEntry && cacheEntry.state === 'loading') {
      recursiveBreakdownHtml =
        '<div class="prop-row"><span class="prop-label">Contained Files</span><span class="prop-val"><span class="folder-size-loading" title="Calculating...">Calculating...</span></span></div>' +
        '<div class="prop-row"><span class="prop-label">Contained Folders</span><span class="prop-val"><span class="folder-size-loading" title="Calculating...">Calculating...</span></span></div>';
    }

    listEl.innerHTML =
      '<div class="prop-row"><span class="prop-label">Type</span><span class="prop-val">Folder (Current Directory)</span></div>' +
      '<div class="prop-row"><span class="prop-label">Full Path</span><span class="prop-val mono">' + escapeHtml(dirPath) + '</span></div>' +
      '<div class="prop-row"><span class="prop-label">Total Size</span><span class="prop-val">' + sizeHtml + '</span></div>' +
      recursiveBreakdownHtml +
      '<div class="prop-row"><span class="prop-label">Contents</span><span class="prop-val">' + escapeHtml(totalFilesHtml) + '</span></div>' +
      '<div class="prop-row"><span class="prop-label">Access Mode</span><span class="prop-val">' + (isReadOnly ? '🔒 Read-Only' : '✏️ Read &amp; Write') + '</span></div>';
    return;
  }

  // Single item mode
  const fullPath = activePropertiesTarget.fullPath;
  const item = findItemByFullPath(fullPath);
  const name = item ? item.name : (fullPath.split('/').pop() || fullPath);
  const isDir = item ? !!item.isDirectory : false;
  const parentDir = item ? (item.parentPath || getParentPath(fullPath)) : getParentPath(fullPath);
  const cat = getFileCategory(name, isDir);

  badge.textContent = cat.icon;
  titleEl.textContent = name;
  subEl.textContent = getReadableFileType(name, isDir);
  copyBtn.style.display = 'inline-flex';
  copyBtn.textContent = '📋 Copy Path';

  let sizeRowHtml = '';
  if (isDir) {
    const entry = folderSizeCache.get(fullPath);
    if (entry && entry.state === 'done') {
      const recFolderCount = typeof entry.folderCount === 'number' ? entry.folderCount : 0;
      sizeRowHtml = '<div class="prop-row"><span class="prop-label">Folder Size</span><span class="prop-val">' +
        formatBytes(entry.size) + ' (' + entry.size.toLocaleString() + ' B)</span></div>' +
        '<div class="prop-row"><span class="prop-label">Contained Files</span><span class="prop-val">' +
        entry.fileCount.toLocaleString() + ' file' + (entry.fileCount === 1 ? '' : 's') + '</span></div>' +
        '<div class="prop-row"><span class="prop-label">Contained Folders</span><span class="prop-val">' +
        recFolderCount.toLocaleString() + ' folder' + (recFolderCount === 1 ? '' : 's') + '</span></div>';
    } else if (entry && entry.state === 'loading') {
      sizeRowHtml = '<div class="prop-row"><span class="prop-label">Folder Size</span><span class="prop-val">' +
        renderFolderSizeSlotHtml(fullPath) + '</span></div>' +
        '<div class="prop-row"><span class="prop-label">Contained Files</span><span class="prop-val"><span class="folder-size-loading" title="Calculating...">Calculating...</span></span></div>' +
        '<div class="prop-row"><span class="prop-label">Contained Folders</span><span class="prop-val"><span class="folder-size-loading" title="Calculating...">Calculating...</span></span></div>';
    } else {
      sizeRowHtml = '<div class="prop-row"><span class="prop-label">Folder Size</span><span class="prop-val">' +
        renderFolderSizeSlotHtml(fullPath) + '</span></div>';
    }
  } else {
    const rawSize = item && typeof item.size === 'number' ? item.size : 0;
    sizeRowHtml = '<div class="prop-row"><span class="prop-label">File Size</span><span class="prop-val">' +
      formatBytes(rawSize) + ' (' + rawSize.toLocaleString() + ' B)</span></div>';
  }

  const modStr = item ? formatDate(item.lastModified) : '--';
  listEl.innerHTML =
    '<div class="prop-row"><span class="prop-label">Name</span><span class="prop-val">' + escapeHtml(name) + '</span></div>' +
    '<div class="prop-row"><span class="prop-label">Type</span><span class="prop-val">' + escapeHtml(getReadableFileType(name, isDir)) + '</span></div>' +
    '<div class="prop-row"><span class="prop-label">Full Path</span><span class="prop-val mono">' + escapeHtml(fullPath) + '</span></div>' +
    '<div class="prop-row"><span class="prop-label">Parent Folder</span><span class="prop-val mono">' + escapeHtml(parentDir) + '</span></div>' +
    sizeRowHtml +
    '<div class="prop-row"><span class="prop-label">Last Modified</span><span class="prop-val">' + escapeHtml(modStr) + '</span></div>' +
    '<div class="prop-row"><span class="prop-label">Access Mode</span><span class="prop-val">' + (isReadOnly ? '🔒 Read-Only' : '✏️ Read &amp; Write') + '</span></div>';
}

async function calculateAllSelectedFoldersInProperties() {
  if (!activePropertiesTarget || activePropertiesTarget.mode !== 'multi') return;
  const paths = activePropertiesTarget.paths || [];
  const dirsToCalculate = [];
  for (const p of paths) {
    const it = findItemByFullPath(p);
    if (it && it.isDirectory) {
      const cached = folderSizeCache.get(p);
      if (!cached || cached.state !== 'done') {
        dirsToCalculate.push(p);
      }
    }
  }
  if (dirsToCalculate.length === 0) return;

  // Immediately mark all targeted folders as loading so the UI displays "Calculating..." badge instantly
  for (const p of dirsToCalculate) {
    folderSizeCache.set(p, { state: 'loading', size: -1, fileCount: 0, folderCount: 0 });
  }
  renderTable();
  refreshOpenPropertiesModal();

  // Execute folder size calculations in parallel with forceLoading = true
  await Promise.all(dirsToCalculate.map(p => calculateFolderSize(null, p, true)));
}

async function copyPropertiesPath() {
  if (!activePropertiesTarget) return;
  let textToCopy = '';
  if (activePropertiesTarget.mode === 'multi') {
    textToCopy = (activePropertiesTarget.paths || []).join('\n');
  } else {
    textToCopy = activePropertiesTarget.fullPath || currentPath;
  }
  await copyTextToClipboard(textToCopy, '📋 Path copied to clipboard');
}

async function copyTextToClipboard(text, toastMsg) {
  let copied = false;
  if (navigator.clipboard && navigator.clipboard.writeText) {
    try {
      await navigator.clipboard.writeText(text);
      copied = true;
    } catch (e) {}
  }
  if (!copied) {
    try {
      const ta = document.createElement('textarea');
      ta.value = text;
      ta.style.position = 'fixed';
      ta.style.opacity = '0';
      document.body.appendChild(ta);
      ta.focus();
      ta.select();
      copied = document.execCommand('copy');
      document.body.removeChild(ta);
    } catch (e) {}
  }
  showToast(toastMsg || '📋 Copied to clipboard', 'emerald');
}

function closePropertiesModal() {
  const modal = document.getElementById('propertiesModal');
  if (modal) modal.classList.remove('active');
  activePropertiesTarget = null;
}

/* Guide & How to Use Modal */
function switchGuideTab(tabId) {
  document.querySelectorAll('#guideTabsBar .guide-tab-btn').forEach(btn => {
    if (btn.getAttribute('data-tab') === tabId) btn.classList.add('active');
    else btn.classList.remove('active');
  });
  document.querySelectorAll('#shortcutsModal .guide-pane').forEach(pane => {
    if (pane.id === 'guide-pane-' + tabId) pane.classList.add('active');
    else pane.classList.remove('active');
  });
}

function openSettingsModal() {
  updateSettingsModalUi();
  const modal = document.getElementById('settingsModal');
  if (modal) modal.classList.add('active');
}

function closeSettingsModal() {
  const modal = document.getElementById('settingsModal');
  if (modal) modal.classList.remove('active');
}

function updateSettingsModalUi() {
  updateMediaThumbnailsBtnUi();
  const themeBtn = document.getElementById('btnModalToggleTheme');
  if (themeBtn) {
    const isDark = (document.documentElement.getAttribute('data-theme') || 'dark') === 'dark';
    themeBtn.textContent = '🌓 Theme (' + (isDark ? 'Dark' : 'Light') + ')';
  }
}

function openShortcutsModal() {
  const modal = document.getElementById('shortcutsModal');
  if (modal) modal.classList.add('active');
}

function closeShortcutsModal() {
  const modal = document.getElementById('shortcutsModal');
  if (modal) modal.classList.remove('active');
}

/* Keyboard Shortcut Action Helpers */
function isBlockingModalOpen() {
  return ['authModal', 'conflictModal', 'renameConflictModal', 'dialogModal', 'batchRenameModal', 'destModal'].some(id => {
    const el = document.getElementById(id);
    return el && el.classList.contains('active');
  });
}

function isAnyModalOpen() {
  return ['authModal', 'conflictModal', 'renameConflictModal', 'dialogModal', 'batchRenameModal', 'destModal', 'propertiesModal', 'shortcutsModal', 'previewModal', 'osPasteModal'].some(id => {
    const el = document.getElementById(id);
    return el && el.classList.contains('active');
  });
}

function isTypingInTextInput(e) {
  const el = (e && e.target) ? e.target : document.activeElement;
  if (!el || !el.tagName) return false;
  const tag = el.tagName.toUpperCase();
  if (tag === 'TEXTAREA' || tag === 'SELECT' || el.isContentEditable) return true;
  if (tag === 'INPUT') {
    const type = (el.type || 'text').toLowerCase();
    return type !== 'checkbox' && type !== 'radio' && type !== 'button';
  }
  return false;
}

function hasActiveTextSelection() {
  try {
    const activeEl = document.activeElement;
    if (activeEl && (activeEl.tagName === 'INPUT' || activeEl.tagName === 'TEXTAREA')) {
      if (typeof activeEl.selectionStart === 'number' && typeof activeEl.selectionEnd === 'number') {
        if (activeEl.selectionEnd > activeEl.selectionStart) return true;
      }
    }
    if (window.getSelection) {
      const sel = window.getSelection();
      if (sel && !sel.isCollapsed && sel.toString().length > 0) {
        return true;
      }
    }
  } catch (e) {}
  return false;
}

function focusSearchInput() {
  const inp = document.getElementById('searchInput');
  if (!inp) return;
  inp.focus();
  inp.select();
}

function triggerRenameForSelection() {
  if (isReadOnly) {
    showToast('🔒 Rename is disabled in Read-Only mode', 'danger');
    return;
  }
  if (selectedPaths.size === 0) {
    showToast('ℹ️ Select a file or folder first to rename (F2)');
    return;
  }
  if (selectedPaths.size === 1) {
    const fullPath = Array.from(selectedPaths)[0];
    const item = findItemByFullPath(fullPath);
    const name = item ? item.name : (fullPath.split('/').pop() || '');
    if (name) promptRename(fullPath, name);
  } else {
    openBatchRenameModal();
  }
}

function triggerDeleteForSelection() {
  if (isReadOnly) {
    showToast('🔒 Delete is disabled in Read-Only mode', 'danger');
    return;
  }
  if (selectedPaths.size === 0) {
    showToast('ℹ️ Select item(s) first to delete (Delete)');
    return;
  }
  if (selectedPaths.size === 1) {
    const fullPath = Array.from(selectedPaths)[0];
    const item = findItemByFullPath(fullPath);
    const name = item ? item.name : (fullPath.split('/').pop() || fullPath);
    const isDir = item ? !!item.isDirectory : false;
    confirmDelete(fullPath, name, isDir);
  } else {
    batchDeleteSelected();
  }
}

function togglePreviewForSelection() {
  const previewModal = document.getElementById('previewModal');
  if (previewModal && previewModal.classList.contains('active')) {
    closePreview();
    return;
  }
  if (selectedPaths.size === 0) {
    showToast('ℹ️ Select a file first to preview (Alt+P)');
    return;
  }
  const targetPath = (lastSelectedPath && selectedPaths.has(lastSelectedPath))
    ? lastSelectedPath
    : Array.from(selectedPaths)[0];
  const item = findItemByFullPath(targetPath);
  if (!item) return;
  if (item.isDirectory) {
    showToast('ℹ️ Preview is only available for files, not folders');
    return;
  }
  const cat = getFileCategory(item.name, false);
  if (cat.preview) {
    openPreview(targetPath, cat.preview, item.name);
  } else {
    showToast('ℹ️ No inline preview available for "' + item.name + '"');
  }
}

function toggleViewModeShortcut() {
  const idx = VIEW_MODE_ORDER.indexOf(viewMode);
  const nextIdx = (idx === -1) ? 0 : ((idx + 1) % VIEW_MODE_ORDER.length);
  const nextMode = VIEW_MODE_ORDER[nextIdx];
  setViewMode(nextMode);
  const meta = VIEW_MODE_META[nextMode] || VIEW_MODE_META.details;
  showToast(meta.icon + ' View: ' + meta.label);
}

/* Windows Explorer-Style Keyboard Type-Ahead Jump Navigation */
function showTypeaheadIndicator(bufferStr, matchedName, found) {
  const el = document.getElementById('typeaheadIndicator');
  if (!el) return;
  if (typeaheadIndicatorTimer) clearTimeout(typeaheadIndicatorTimer);
  if (found) {
    el.innerHTML = '<span>🔤</span><span class="typeahead-key">"' + escapeHtml(bufferStr) + '"</span><span>→ ' + escapeHtml(matchedName) + '</span>';
  } else {
    el.innerHTML = '<span>🔤</span><span class="typeahead-key">"' + escapeHtml(bufferStr) + '"</span><span style="opacity:0.8;">(No match)</span>';
  }
  el.classList.add('active');
  typeaheadIndicatorTimer = setTimeout(() => {
    el.classList.remove('active');
  }, 1500);
}

function handleTypeAheadKey(charStr) {
  const filtered = getFilteredSortedItems();
  if (!filtered || filtered.length === 0) return;

  if (typeaheadResetTimer) clearTimeout(typeaheadResetTimer);
  const wasActiveWindow = typeaheadBuffer.length > 0;
  const lowerChar = charStr.toLowerCase();

  let matchedIndex = -1;

  // If the user rapidly presses the same single character (e.g. 's', 's', 's'), cycle through items starting with 's'
  const isRepeatingSameChar = wasActiveWindow &&
    typeaheadBuffer.length >= 1 &&
    typeaheadBuffer.split('').every(c => c.toLowerCase() === lowerChar);

  typeaheadBuffer += charStr;
  typeaheadResetTimer = setTimeout(() => {
    typeaheadBuffer = '';
  }, 1000);

  if (isRepeatingSameChar && typeaheadBuffer.length > 1) {
    // First check if any item actually starts with the repeated string (e.g. "ss"); if not, cycle single char
    const exactRepeatIdx = filtered.findIndex(it => (it.name || '').toLowerCase().startsWith(typeaheadBuffer.toLowerCase()));
    if (exactRepeatIdx !== -1) {
      matchedIndex = exactRepeatIdx;
    } else {
      const matchingIndices = [];
      for (let i = 0; i < filtered.length; i++) {
        if ((filtered[i].name || '').toLowerCase().startsWith(lowerChar)) {
          matchingIndices.push(i);
        }
      }
      if (matchingIndices.length > 0) {
        if (typeaheadLastChar !== lowerChar) {
          typeaheadLastChar = lowerChar;
          typeaheadCycleIndex = 0;
        } else {
          typeaheadCycleIndex = (typeaheadCycleIndex + 1) % matchingIndices.length;
        }
        matchedIndex = matchingIndices[typeaheadCycleIndex];
      }
    }
  } else {
    const prefixLower = typeaheadBuffer.toLowerCase();
    matchedIndex = filtered.findIndex(it => (it.name || '').toLowerCase().startsWith(prefixLower));
    if (matchedIndex !== -1 && typeaheadBuffer.length === 1) {
      const matchingIndices = [];
      for (let i = 0; i < filtered.length; i++) {
        if ((filtered[i].name || '').toLowerCase().startsWith(lowerChar)) {
          matchingIndices.push(i);
        }
      }
      typeaheadLastChar = lowerChar;
      typeaheadCycleIndex = Math.max(0, matchingIndices.indexOf(matchedIndex));
    }
  }

  if (matchedIndex === -1) {
    showTypeaheadIndicator(typeaheadBuffer, '', false);
    return;
  }

  const matchedItem = filtered[matchedIndex];
  const fullPath = getItemFullPath(matchedItem);

  // Switch page automatically if the matched item is on another pagination page
  if (pageSize !== 'all') {
    const limit = Number(pageSize) || 50;
    const targetPage = Math.floor(matchedIndex / limit) + 1;
    if (currentPage !== targetPage) {
      currentPage = targetPage;
    }
  }

  selectedPaths.clear();
  selectedPaths.add(fullPath);
  lastSelectedPath = fullPath;
  typeaheadFocusedPath = fullPath;

  renderTable();
  updateBatchAndClipboardBars();
  showTypeaheadIndicator(typeaheadBuffer, matchedItem.name, true);

  setTimeout(() => {
    const containerSelector = (viewMode === 'details') ? '#listViewWrapper' : '#gridViewWrapper';
    const candidates = document.querySelectorAll(containerSelector + ' [data-full-path]');
    for (let i = 0; i < candidates.length; i++) {
      if (candidates[i].getAttribute('data-full-path') === fullPath) {
        candidates[i].scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        break;
      }
    }
  }, 15);
}

function matchKeyCodeOrChar(e, codeName, charLower) {
  if (e.code === codeName) return true;
  return (e.key || '').toLowerCase() === charLower;
}

window.addEventListener('click', (e) => {
  closeItemMenu();
  if (e && e.target && e.target.closest && !e.target.closest('#viewDropdownWrap')) {
    closeViewDropdown();
  }
  // Deselect items on normal left-click anywhere in empty space (outside files & operational controls)
  if (e && e.button === 0 && !e.defaultPrevented && !e.ctrlKey && !e.shiftKey && !e.metaKey) {
    if (Date.now() >= marqueeState.suppressClickUntil && !isAnyModalOpen() && !hasActiveTextSelection()) {
      const target = e.target;
      if (target && target.closest) {
        const hitFileOrControl = target.closest(
          '.file-row, .grid-card, .view-small-card, .view-list-item, .view-tile-card, .view-content-row, ' +
          'button, a, input, select, textarea, label, th, ' +
          '.batch-bar, .clipboard-bar, .context-menu, .view-dropdown-wrap, .modal-backdrop, #previewFloatingWindow, .address-bar-shell, .search-box, .upload-panel'
        );
        if (!hitFileOrControl && (selectedPaths.size > 0 || typeaheadFocusedPath !== null)) {
          clearSelection();
        }
      }
    }
  }
});
window.addEventListener('scroll', () => {
  closeItemMenu();
  if (marqueeState.isDragging) {
    marqueeState.itemRects = snapshotVisibleItemRects();
    updateMarqueeBoxAndSelection();
  }
}, true);
window.addEventListener('wheel', (e) => {
  if (!e || !(e.ctrlKey || e.metaKey)) return;
  if (isAnyModalOpen()) return;
  e.preventDefault();
  if (e.deltaY === 0) return;
  const now = Date.now();
  if (now - lastCtrlWheelTime < 65) return;
  lastCtrlWheelTime = now;

  const curIdx = VIEW_MODE_ORDER.indexOf(viewMode);
  const baseIdx = curIdx >= 0 ? curIdx : VIEW_MODE_ORDER.indexOf('details');
  // Scroll Up (deltaY < 0) steps toward larger icons (index 0); Scroll Down (deltaY > 0) steps toward compact/tiles
  const step = e.deltaY < 0 ? -1 : 1;
  const nextIdx = (baseIdx + step + VIEW_MODE_ORDER.length) % VIEW_MODE_ORDER.length;
  const nextMode = VIEW_MODE_ORDER[nextIdx];
  if (nextMode && nextMode !== viewMode) {
    setViewMode(nextMode);
    const meta = VIEW_MODE_META[nextMode] || VIEW_MODE_META.details;
    showToast(meta.icon + ' View: ' + meta.label);
  }
}, { passive: false });
window.addEventListener('resize', () => {
  closeItemMenu();
  closeViewDropdown();
});
window.addEventListener('contextmenu', (e) => {
  if (e.defaultPrevented || isBlockingModalOpen()) return;
  const target = e.target;
  if (!target || !target.closest) return;
  if (target.closest('#itemContextMenu, #viewDropdownWrap, .modal-backdrop, #previewFloatingWindow, .header, .toolbar, .view-bar, .batch-bar, .clipboard-bar, .footer-credit-box')) return;
  if (isInteractiveOrItemElement(target)) return;
  closeViewDropdown();
  openBackgroundMenu(e);
});
window.addEventListener('keydown', (e) => {
  // 1. Escape key handling across modals, menus, search, and selection
  if (e.key === 'Escape') {
    if (isViewModeMenuOpen) {
      closeViewModeMenu();
      return;
    }
    if (document.getElementById('renameConflictModal').classList.contains('active')) {
      submitRenameConflictDecision('cancel');
      return;
    }
    if (document.getElementById('osPasteModal').classList.contains('active')) {
      closeOsPasteModal();
      return;
    }
    if (document.getElementById('conflictModal').classList.contains('active')) {
      submitConflictDecision('cancel');
      return;
    }
    if (document.getElementById('dialogModal').classList.contains('active')) {
      resolveDialogModal(null);
      return;
    }
    if (document.getElementById('batchRenameModal').classList.contains('active')) {
      closeBatchRenameModal();
      return;
    }
    if (document.getElementById('propertiesModal').classList.contains('active')) {
      closePropertiesModal();
      return;
    }
    if (document.getElementById('settingsModal') && document.getElementById('settingsModal').classList.contains('active')) {
      closeSettingsModal();
      return;
    }
    if (document.getElementById('shortcutsModal').classList.contains('active')) {
      closeShortcutsModal();
      return;
    }
    if (document.getElementById('previewModal').classList.contains('active')) {
      closePreview();
      return;
    }
    if (document.getElementById('destModal').classList.contains('active')) {
      closeDestModal();
      return;
    }
    if (isAddressEditMode) {
      exitAddressEditMode();
      return;
    }
    if (document.activeElement && document.activeElement.id === 'searchInput') {
      document.activeElement.blur();
      return;
    }
    closeItemMenu();
    if (selectedPaths.size > 0 || typeaheadFocusedPath) {
      clearSelection();
    }
    return;
  }

  // 2. Enter key inside active modals
  if (e.key === 'Enter' && document.getElementById('renameConflictModal').classList.contains('active')) {
    if (document.activeElement && document.activeElement.id === 'renameConflictInput') {
      e.preventDefault();
      submitRenameConflictDecision('custom');
    }
    return;
  }
  if (e.key === 'Enter' && document.getElementById('conflictModal').classList.contains('active')) {
    if (document.activeElement && document.activeElement.id === 'conflictRenameInput') {
      e.preventDefault();
      submitConflictDecision('rename');
    }
    return;
  }
  if (e.key === 'Enter' && document.getElementById('batchRenameModal').classList.contains('active')) {
    const activeId = document.activeElement ? document.activeElement.id : '';
    if (activeId === 'batchRenameBaseInput' || activeId === 'batchRenameStartInput') {
      e.preventDefault();
      submitBatchRename();
    }
    return;
  }
  if (e.key === 'Enter' && document.getElementById('dialogModal').classList.contains('active')) {
    if (document.activeElement && document.activeElement.id === 'dialogCancelBtn') return;
    e.preventDefault();
    confirmDialogModal();
    return;
  }

  // Do not run background shortcuts while a blocking modal dialog is open
  if (isBlockingModalOpen()) return;

  const ctrlOrMeta = e.ctrlKey || e.metaKey;

  // 3. Alt + P: Toggle Preview Modal even if Preview Modal is currently open
  if (e.altKey && !ctrlOrMeta && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyP', 'p')) {
    e.preventDefault();
    togglePreviewForSelection();
    return;
  }

  // 4. Alt + Enter: Toggle Properties Modal
  if (e.altKey && !ctrlOrMeta && !e.shiftKey && e.key === 'Enter') {
    e.preventDefault();
    openPropertiesForSelection();
    return;
  }

  // 5. Ctrl + F or F3: Focus Search Bar (works even if already in search input)
  if ((ctrlOrMeta && !e.altKey && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyF', 'f')) ||
      (!ctrlOrMeta && !e.altKey && !e.shiftKey && e.key === 'F3')) {
    e.preventDefault();
    focusSearchInput();
    return;
  }

  // 6. F5: Refresh Directory List
  if (!ctrlOrMeta && !e.altKey && !e.shiftKey && e.key === 'F5') {
    e.preventDefault();
    refreshCurrentDirectory();
    return;
  }

  // 6b. F1: Open Guide & How to Use Modal
  if (!ctrlOrMeta && !e.altKey && !e.shiftKey && e.key === 'F1') {
    e.preventDefault();
    if (document.getElementById('shortcutsModal').classList.contains('active')) closeShortcutsModal();
    else openShortcutsModal();
    return;
  }

  // Guard remaining shortcuts when typing in text inputs
  if (isTypingInTextInput(e)) return;

  // If Preview, Properties, or Guide modal is open, ignore background file shortcuts
  if (document.getElementById('previewModal').classList.contains('active') ||
      document.getElementById('propertiesModal').classList.contains('active') ||
      document.getElementById('shortcutsModal').classList.contains('active')) {
    if (!ctrlOrMeta && !e.altKey && e.key === '?') {
      e.preventDefault();
      if (document.getElementById('shortcutsModal').classList.contains('active')) closeShortcutsModal();
      else openShortcutsModal();
    }
    return;
  }

  // 7. Ctrl + Shift + N or Alt + N: New Folder
  if ((ctrlOrMeta && e.shiftKey && !e.altKey && matchKeyCodeOrChar(e, 'KeyN', 'n')) ||
      (e.altKey && !ctrlOrMeta && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyN', 'n'))) {
    e.preventDefault();
    if (isReadOnly) {
      showToast('🔒 Cannot create folder in Read-Only mode', 'danger');
    } else {
      promptNewFolder();
    }
    return;
  }

  // 8. Alt + Left / Right / Up Arrow: Folder Navigation
  if (e.altKey && !ctrlOrMeta && !e.shiftKey) {
    if (e.key === 'ArrowLeft') {
      e.preventDefault();
      navigateBack();
      return;
    }
    if (e.key === 'ArrowRight') {
      e.preventDefault();
      navigateForward();
      return;
    }
    if (e.key === 'ArrowUp') {
      e.preventDefault();
      navigateUp();
      return;
    }
  }

  // 9. Alt + Shift + 6 (or Alt + V / Ctrl + Shift + 6): Cycle through the 8 View Modes
  if ((e.altKey && e.shiftKey && !ctrlOrMeta && (e.code === 'Digit6' || e.key === '6' || e.key === '^')) ||
      (ctrlOrMeta && e.shiftKey && !e.altKey && (e.code === 'Digit6' || e.key === '6' || e.key === '^')) ||
      (e.altKey && !ctrlOrMeta && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyV', 'v'))) {
    e.preventDefault();
    toggleViewModeShortcut();
    return;
  }

  // 10. Ctrl + A: Select All items in current view
  if (ctrlOrMeta && !e.altKey && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyA', 'a')) {
    e.preventDefault();
    selectAllFiltered(true);
    if (selectedPaths.size > 0) {
      showToast('☑️ Selected all ' + selectedPaths.size + ' item' + (selectedPaths.size === 1 ? '' : 's'));
    }
    return;
  }

  // 11. Ctrl + C: Copy selected items (only when no text is highlighted on the page)
  if (ctrlOrMeta && !e.altKey && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyC', 'c')) {
    if (hasActiveTextSelection()) {
      return; // Allow native browser text copy
    }
    if (selectedPaths.size > 0 && !isReadOnly) {
      e.preventDefault();
      stageClipboardFromSelection('copy');
    }
    return;
  }

  // 12. Ctrl + X: Cut / Move selected items (only when no text is highlighted on the page)
  if (ctrlOrMeta && !e.altKey && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyX', 'x')) {
    if (hasActiveTextSelection()) {
      return; // Allow native browser text cut/copy
    }
    if (selectedPaths.size > 0 && !isReadOnly) {
      e.preventDefault();
      stageClipboardFromSelection('move');
    }
    return;
  }

  // 13. Ctrl + V: Allow native paste event to fire so OS clipboard files can be uploaded;
  // fallback to internal clipboard paste if browser did not fire a paste event within 180ms.
  if (ctrlOrMeta && !e.altKey && !e.shiftKey && matchKeyCodeOrChar(e, 'KeyV', 'v')) {
    const keydownTime = Date.now();
    setTimeout(() => {
      if (lastPasteEventTimestamp < keydownTime && clipboardItems.length > 0 && clipboardMode && !isReadOnly) {
        executePasteTo(currentPath);
      }
    }, 180);
    return;
  }

  // 14. F2: Rename selected item(s)
  if (!ctrlOrMeta && !e.altKey && !e.shiftKey && e.key === 'F2') {
    e.preventDefault();
    triggerRenameForSelection();
    return;
  }

  // 15. Delete: Permanently delete selected item(s)
  if (!ctrlOrMeta && !e.altKey && e.key === 'Delete') {
    e.preventDefault();
    triggerDeleteForSelection();
    return;
  }

  // 16. ? (Shift + /): Open Guide & How to Use Modal
  if (!ctrlOrMeta && !e.altKey && e.key === '?') {
    e.preventDefault();
    openShortcutsModal();
    return;
  }

  // 17. Windows Explorer Type-Ahead Jump: Any printable character (excluding '?' which opens Guide)
  if (!ctrlOrMeta && !e.altKey && e.key && e.key.length === 1 && e.key !== '?') {
    // Ignore spacebar when nothing has been typed yet to prevent accidental page scroll override unless buffer is active
    if (e.key === ' ' && typeaheadBuffer.length === 0) return;
    e.preventDefault();
    handleTypeAheadKey(e.key);
  }
});

/* Selection & Batch Operations */
function toggleSelectItem(fullPath, checked) {
  if (checked) selectedPaths.add(fullPath);
  else selectedPaths.delete(fullPath);
  lastSelectedPath = fullPath;
  renderTable();
  updateBatchAndClipboardBars();
}

function selectCurrentPage(checked) {
  const filtered = getFilteredSortedItems();
  const sliceInfo = getPaginationSlice(filtered);
  if (checked) {
    selectedPaths.clear();
    for (const item of sliceInfo.pageItems) {
      selectedPaths.add(getItemFullPath(item));
    }
  } else {
    for (const item of sliceInfo.pageItems) {
      selectedPaths.delete(getItemFullPath(item));
    }
  }
  renderTable();
  updateBatchAndClipboardBars();
}

function invertCurrentPageSelection() {
  const filtered = getFilteredSortedItems();
  const sliceInfo = getPaginationSlice(filtered);
  if (!sliceInfo.pageItems || sliceInfo.pageItems.length === 0) return;
  let nowSelectedOnPage = 0;
  for (const item of sliceInfo.pageItems) {
    const p = getItemFullPath(item);
    if (selectedPaths.has(p)) {
      selectedPaths.delete(p);
    } else {
      selectedPaths.add(p);
      lastSelectedPath = p;
      nowSelectedOnPage++;
    }
  }
  renderTable();
  updateBatchAndClipboardBars();
  showToast('🔄 Inverted selection on page (' + nowSelectedOnPage + ' of ' + sliceInfo.pageItems.length + ' selected)');
}

function selectAllFiltered(checked) {
  const filtered = getFilteredSortedItems();
  for (const item of filtered) {
    const p = getItemFullPath(item);
    if (checked) selectedPaths.add(p);
    else selectedPaths.delete(p);
  }
  renderTable();
  updateBatchAndClipboardBars();
}

function clearSelection() {
  selectedPaths.clear();
  lastSelectedPath = null;
  typeaheadFocusedPath = null;
  renderTable();
  updateBatchAndClipboardBars();
}

/* Mouse Marquee (Rubber-Band) Drag Selection */
function canStartMarqueeFromTarget(target) {
  if (!target || !target.closest) return false;
  if (isAnyModalOpen()) return false;
  // Do not start marquee on header/toolbar/bars/menus or interactive controls or draggable item names/icons
  if (target.closest('.header, .toolbar, .batch-bar, .clipboard-bar, .context-menu, .view-dropdown-wrap, .modal-backdrop, #previewFloatingWindow, .upload-panel, button, a, input, select, textarea, label, th, .pagination-bar, .view-bar, .item-name-cell, .grid-thumb-box, .grid-title, .view-small-main, .view-tile-thumb, .view-tile-title, .view-content-thumb, .row-actions, .grid-actions, .item-subpath-badge')) {
    return false;
  }
  return true;
}

function snapshotVisibleItemRects() {
  const containerSelector = (viewMode === 'details') ? '#listViewWrapper' : '#gridViewWrapper';
  const els = document.querySelectorAll(containerSelector + ' [data-full-path]');
  const rects = [];
  for (let i = 0; i < els.length; i++) {
    const el = els[i];
    const p = el.getAttribute('data-full-path');
    if (!p) continue;
    const r = el.getBoundingClientRect();
    if (r.width <= 0 || r.height <= 0) continue;
    rects.push({
      path: p,
      el: el,
      left: r.left + window.scrollX,
      top: r.top + window.scrollY,
      right: r.right + window.scrollX,
      bottom: r.bottom + window.scrollY
    });
  }
  return rects;
}

function updateMarqueeBoxAndSelection() {
  const boxEl = document.getElementById('marqueeSelectionBox');
  if (!boxEl) return;

  // Compute selection rectangle in document coordinates so scrolling up/down never loses the start anchor
  const curDocX = marqueeState.curX + window.scrollX;
  const curDocY = marqueeState.curY + window.scrollY;
  const docLeft = Math.min(marqueeState.startDocX, curDocX);
  const docTop = Math.min(marqueeState.startDocY, curDocY);
  const docRight = Math.max(marqueeState.startDocX, curDocX);
  const docBottom = Math.max(marqueeState.startDocY, curDocY);

  // Project document rectangle onto visible viewport for the fixed visual selection box
  const viewLeft = Math.max(0, Math.min(window.innerWidth, docLeft - window.scrollX));
  const viewTop = Math.max(0, Math.min(window.innerHeight, docTop - window.scrollY));
  const viewRight = Math.max(0, Math.min(window.innerWidth, docRight - window.scrollX));
  const viewBottom = Math.max(0, Math.min(window.innerHeight, docBottom - window.scrollY));

  boxEl.style.left = viewLeft + 'px';
  boxEl.style.top = viewTop + 'px';
  boxEl.style.width = Math.max(1, viewRight - viewLeft) + 'px';
  boxEl.style.height = Math.max(1, viewBottom - viewTop) + 'px';

  const nextSelected = new Set(marqueeState.additive ? marqueeState.baseSelected : []);
  let lastHitPath = null;

  for (let i = 0; i < marqueeState.itemRects.length; i++) {
    const item = marqueeState.itemRects[i];
    const intersects = !(
      item.right < docLeft ||
      item.left > docRight ||
      item.bottom < docTop ||
      item.top > docBottom
    );
    if (intersects) {
      nextSelected.add(item.path);
      lastHitPath = item.path;
    }
    const isNowSelected = nextSelected.has(item.path);
    if (item.el) {
      if (isNowSelected) item.el.classList.add('selected');
      else item.el.classList.remove('selected');
      const chk = item.el.querySelector('input.chk');
      if (chk) chk.checked = isNowSelected;
    }
  }

  selectedPaths = nextSelected;
  if (lastHitPath) lastSelectedPath = lastHitPath;

  const masterChk = document.getElementById('masterCheckbox');
  if (masterChk && marqueeState.itemRects.length > 0) {
    masterChk.checked = marqueeState.itemRects.every(it => selectedPaths.has(it.path));
  }
  updateBatchAndClipboardBars();
}

function stopMarqueeAutoScroll() {
  if (marqueeState.autoScrollRaf) {
    cancelAnimationFrame(marqueeState.autoScrollRaf);
    marqueeState.autoScrollRaf = null;
  }
  marqueeState.autoScrollSpeedY = 0;
}

function stepMarqueeAutoScroll() {
  if (!marqueeState.isDragging || marqueeState.autoScrollSpeedY === 0) {
    marqueeState.autoScrollRaf = null;
    return;
  }
  const prevScrollY = window.scrollY;
  window.scrollBy(0, marqueeState.autoScrollSpeedY);
  if (window.scrollY !== prevScrollY) {
    marqueeState.itemRects = snapshotVisibleItemRects();
  }
  updateMarqueeBoxAndSelection();
  marqueeState.autoScrollRaf = requestAnimationFrame(stepMarqueeAutoScroll);
}

window.addEventListener('mousedown', (e) => {
  if (!e || e.button !== 0) return; // Only primary left click
  if (isInternalDragActive) return;
  if (!canStartMarqueeFromTarget(e.target)) return;

  marqueeState.isDown = true;
  marqueeState.isDragging = false;
  marqueeState.startX = e.clientX;
  marqueeState.startY = e.clientY;
  marqueeState.startDocX = e.clientX + window.scrollX;
  marqueeState.startDocY = e.clientY + window.scrollY;
  marqueeState.curX = e.clientX;
  marqueeState.curY = e.clientY;
  marqueeState.additive = !!(e.ctrlKey || e.metaKey || e.shiftKey);
  marqueeState.baseSelected = new Set(selectedPaths);
});

window.addEventListener('mousemove', (e) => {
  if (!marqueeState.isDown) return;
  if (isInternalDragActive) {
    marqueeState.isDown = false;
    marqueeState.isDragging = false;
    stopMarqueeAutoScroll();
    return;
  }

  marqueeState.curX = e.clientX;
  marqueeState.curY = e.clientY;
  const dx = Math.abs((marqueeState.curX + window.scrollX) - marqueeState.startDocX);
  const dy = Math.abs((marqueeState.curY + window.scrollY) - marqueeState.startDocY);

  if (!marqueeState.isDragging) {
    if (dx < 6 && dy < 6) return;
    marqueeState.isDragging = true;
    closeItemMenu();
    closeViewModeMenu();
    clearBrowserTextSelection();
    document.body.classList.add('marquee-selecting');
    marqueeState.itemRects = snapshotVisibleItemRects();
    const boxEl = document.getElementById('marqueeSelectionBox');
    if (boxEl) boxEl.classList.add('active');
  }

  clearBrowserTextSelection();
  updateMarqueeBoxAndSelection();

  // Auto-scroll when dragging near top or bottom of viewport
  const edgeThreshold = 48;
  if (e.clientY < edgeThreshold) {
    marqueeState.autoScrollSpeedY = -Math.max(4, Math.round((edgeThreshold - e.clientY) * 0.35));
    if (!marqueeState.autoScrollRaf) {
      marqueeState.autoScrollRaf = requestAnimationFrame(stepMarqueeAutoScroll);
    }
  } else if (e.clientY > window.innerHeight - edgeThreshold) {
    marqueeState.autoScrollSpeedY = Math.max(4, Math.round((e.clientY - (window.innerHeight - edgeThreshold)) * 0.35));
    if (!marqueeState.autoScrollRaf) {
      marqueeState.autoScrollRaf = requestAnimationFrame(stepMarqueeAutoScroll);
    }
  } else {
    stopMarqueeAutoScroll();
  }
});

window.addEventListener('mouseup', () => {
  if (!marqueeState.isDown) return;
  const wasDragging = marqueeState.isDragging;
  marqueeState.isDown = false;
  marqueeState.isDragging = false;
  stopMarqueeAutoScroll();
  document.body.classList.remove('marquee-selecting');

  const boxEl = document.getElementById('marqueeSelectionBox');
  if (boxEl) boxEl.classList.remove('active');

  if (wasDragging) {
    marqueeState.suppressClickUntil = Date.now() + 140;
    renderTable();
    updateBatchAndClipboardBars();
  }
});

function updateBatchAndClipboardBars() {
  const batchBar = document.getElementById('batchBar');
  const countBadge = document.getElementById('selectedCountBadge');
  const count = selectedPaths.size;
  if (count > 0) {
    batchBar.classList.add('active');
    countBadge.textContent = count + ' item' + (count === 1 ? '' : 's') + ' selected';
  } else {
    batchBar.classList.remove('active');
  }

  const clipBar = document.getElementById('clipboardBar');
  const clipText = document.getElementById('clipboardStatusText');
  const pasteBtn = document.getElementById('pasteHereBtn');
  if (clipboardItems.length > 0 && clipboardMode && !isReadOnly) {
    clipBar.classList.add('active');
    const actionLabel = clipboardMode === 'move' ? '✂️ Ready to Move ' : '📋 Ready to Copy ';
    clipText.textContent = actionLabel + clipboardItems.length + ' item' + (clipboardItems.length === 1 ? '' : 's');
    pasteBtn.textContent = (clipboardMode === 'move' ? '📥 Move Here (' : '📥 Paste Here (') + currentPath + ')';
  } else {
    clipBar.classList.remove('active');
  }
}

function downloadSelectedZip() {
  if (selectedPaths.size === 0) return;
  const arr = Array.from(selectedPaths);
  window.location.href = withAuthUrl('/api/zip?items=' + encodeURIComponent(JSON.stringify(arr)));
}

function stageClipboardFromSelection(mode) {
  if (selectedPaths.size === 0) return;
  clipboardItems = Array.from(selectedPaths);
  clipboardMode = mode;
  updateBatchAndClipboardBars();
  showToast((mode === 'move' ? '✂️ Staged ' : '📋 Copied ') + clipboardItems.length + ' item(s) to clipboard');
}

function stageSingleItemClipboard(fullPath, mode) {
  clipboardItems = [fullPath];
  clipboardMode = mode;
  updateBatchAndClipboardBars();
  const itemName = fullPath.split('/').pop() || fullPath;
  showToast((mode === 'move' ? '✂️ Ready to move: ' : '📋 Copied: ') + itemName);
}

function clearClipboard() {
  clipboardItems = [];
  clipboardMode = null;
  updateBatchAndClipboardBars();
}

async function executePasteTo(targetDir, explicitMode, customItems) {
  const mode = explicitMode || clipboardMode;
  const rawItems = Array.isArray(customItems)
    ? customItems.slice()
    : (clipboardItems.length > 0 ? clipboardItems.slice() : Array.from(selectedPaths));
  if (!mode || rawItems.length === 0) return;

  const itemsToSend = [];
  const targetNames = {};
  let batchConflictAction = null; // 'overwrite', 'skip', 'rename'
  setOperationCancellable(true, mode);

  for (let i = 0; i < rawItems.length; i++) {
    if (isOpCancellationRequested) {
      hideOperationProgress();
      setOperationCancellable(false, null);
      return;
    }
    const srcPath = rawItems[i];
    const srcParent = getParentPath(srcPath);
    const itemName = srcPath.split('/').pop() || '';
    if (!itemName) continue;

    if (rawItems.length > 1) {
      const checkPct = Math.round((i / rawItems.length) * 25);
      showOperationProgress(
        '🔍 Preparing ' + (mode === 'move' ? 'Move' : 'Copy') + ' (' + (i + 1) + ' / ' + rawItems.length + ')',
        checkPct,
        'Checking "' + itemName + '" in ' + targetDir,
        false
      );
    }

    // Copying into the same directory automatically creates "(Copy)" on the server
    if (srcParent === targetDir && mode === 'copy') {
      itemsToSend.push(srcPath);
      continue;
    }
    if (srcParent === targetDir && mode === 'move') {
      continue;
    }

    const destCandidate = joinPath(targetDir, itemName);
    let existsData = null;
    try {
      const checkRes = await apiFetch('/api/exists?path=' + encodeURIComponent(destCandidate));
      if (checkRes.status === 401) {
        hideOperationProgress();
        return;
      }
      if (checkRes.ok) {
        const parsed = await checkRes.json();
        if (parsed && parsed.ok && parsed.exists) {
          existsData = parsed;
        }
      }
    } catch (e) {}

    if (!existsData) {
      itemsToSend.push(srcPath);
      continue;
    }

    // Conflict detected in destination folder
    let srcSize = 0;
    let srcMod = 0;
    let srcIsDir = false;
    try {
      const srcCheck = await apiFetch('/api/exists?path=' + encodeURIComponent(srcPath));
      if (srcCheck.ok) {
        const sData = await srcCheck.json();
        if (sData && sData.ok && sData.exists) {
          srcSize = sData.size || 0;
          srcMod = sData.lastModified || 0;
          srcIsDir = !!sData.isDirectory;
        }
      }
    } catch (e) {}

    if (batchConflictAction === 'skip') {
      continue;
    } else if (batchConflictAction === 'overwrite') {
      itemsToSend.push(srcPath);
      continue;
    } else if (batchConflictAction === 'rename') {
      itemsToSend.push(srcPath);
      targetNames[srcPath] = existsData.suggestedName || ('Copy of ' + itemName);
      continue;
    }

    const decision = await openConflictModal({
      title: rawItems.length > 1
        ? ('File Already Exists (' + (i + 1) + ' of ' + rawItems.length + ')')
        : 'File Already Exists',
      subtitle: 'An item named "' + itemName + '" already exists in "' + targetDir + '". What would you like to do?',
      fileName: itemName,
      targetDir: targetDir,
      existingSize: existsData.size || 0,
      existingModified: existsData.lastModified || 0,
      existingIsDir: !!existsData.isDirectory,
      incomingLabel: mode === 'move' ? '✂️ Item Being Moved' : '📋 Item Being Copied',
      newSize: srcSize,
      newModified: srcMod,
      newIsDir: srcIsDir,
      suggestedName: existsData.suggestedName || ('Copy of ' + itemName),
      currentIndex: i + 1,
      totalFiles: rawItems.length
    });

    if (!decision || decision.action === 'cancel') {
      hideOperationProgress();
      showToast('⏹️ ' + (mode === 'move' ? 'Move' : 'Copy') + ' cancelled');
      return;
    }
    if (decision.applyAll) {
      batchConflictAction = decision.action;
    }
    if (decision.action === 'skip') {
      continue;
    } else if (decision.action === 'overwrite') {
      itemsToSend.push(srcPath);
    } else if (decision.action === 'rename' && decision.newName) {
      itemsToSend.push(srcPath);
      targetNames[srcPath] = decision.newName;
    }
  }

  if (itemsToSend.length === 0) {
    hideOperationProgress();
    setOperationCancellable(false, null);
    showToast('⏭️ No items were ' + (mode === 'move' ? 'moved' : 'copied'));
    return;
  }

  const endpoint = mode === 'move' ? '/api/batch-move' : '/api/batch-copy';
  const verbLabel = mode === 'move' ? 'Moving' : 'Copying';
  let succeededCount = 0;
  let lastErrorMsg = '';

  try {
    for (let idx = 0; idx < itemsToSend.length; idx++) {
      if (isOpCancellationRequested) break;
      const itemPath = itemsToSend[idx];
      const itemName = itemPath.split('/').pop() || itemPath;
      const pct = itemsToSend.length === 1
        ? 50
        : Math.round(((idx) / itemsToSend.length) * 100);
      showOperationProgress(
        (mode === 'move' ? '✂️ ' : '📋 ') + verbLabel + ' (' + (idx + 1) + ' / ' + itemsToSend.length + '): ' + itemName,
        pct,
        'Destination: ' + targetDir,
        false
      );

      const singleTargetNames = {};
      if (targetNames[itemPath]) {
        singleTargetNames[itemPath] = targetNames[itemPath];
      }

      activeOpAbortController = new AbortController();
      const res = await apiFetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ items: [itemPath], targetDir: targetDir, targetNames: singleTargetNames }),
        signal: activeOpAbortController.signal
      });
      activeOpAbortController = null;

      if (res.status === 401) {
        hideOperationProgress();
        setOperationCancellable(false, null);
        return;
      }
      const data = await res.json();
      if (data && data.ok) {
        succeededCount++;
      } else if (data && data.error) {
        lastErrorMsg = data.error;
      }

      if (isOpCancellationRequested) break;

      const donePct = Math.round(((idx + 1) / itemsToSend.length) * 100);
      showOperationProgress(
        (mode === 'move' ? '✂️ ' : '📋 ') + verbLabel + ' (' + (idx + 1) + ' / ' + itemsToSend.length + '): ' + itemName,
        donePct,
        'Destination: ' + targetDir,
        false
      );
    }

    if (isOpCancellationRequested) {
      showToast('⏹️ ' + (mode === 'move' ? 'Move' : 'Copy') + ' cancelled. ' + succeededCount + ' of ' + itemsToSend.length + ' item(s) completed safely.', 'amber');
      completeOperationProgress(
        '⏹️ ' + (mode === 'move' ? 'Move' : 'Copy') + ' Cancelled (' + succeededCount + ' of ' + itemsToSend.length + ' saved)',
        'Operation safely stopped',
        2200
      );
      clearClipboard();
      setOperationCancellable(false, null);
      loadDirectory(currentPath, { skipHistory: true });
      return;
    }

    setOperationCancellable(false, null);

    if (succeededCount === 0) {
      hideOperationProgress();
      await showAlertDialog({
        icon: '⚠️',
        badgeClass: 'icon-badge-danger',
        title: (mode === 'move' ? 'Move' : 'Copy') + ' Failed',
        subtitle: lastErrorMsg || ('Failed to ' + mode + ' selected items'),
        targetLabel: 'Destination',
        targetValue: targetDir,
        confirmText: 'OK',
        confirmBtnClass: 'btn-primary'
      });
    } else {
      completeOperationProgress(
        (mode === 'move' ? '✂️ Moved ' : '📋 Copied ') + succeededCount + ' of ' + itemsToSend.length + ' item(s)',
        'Destination: ' + targetDir,
        1800
      );
      clearClipboard();
      selectedPaths.clear();
      showToast((mode === 'move' ? '✂️ Moved ' : '📋 Copied ') + succeededCount + ' item(s)', 'emerald');
    }
  } catch (e) {
    hideOperationProgress();
    setOperationCancellable(false, null);
    if (!isOpCancellationRequested) {
      await showAlertDialog('Operation failed: ' + e.message);
    }
  }
  loadDirectory(currentPath, { skipHistory: true });
}

async function batchDeleteSelected() {
  const itemsArray = Array.from(selectedPaths);
  const count = itemsArray.length;
  if (count === 0) return;
  const confirmed = await showConfirmDialog({
    icon: '🗑️',
    badgeClass: 'icon-badge-danger',
    title: 'Delete Selected Items?',
    subtitle: 'Are you sure you want to permanently delete the selected items from storage?',
    targetLabel: 'Selection',
    targetValue: count + ' item' + (count === 1 ? '' : 's') + ' in ' + currentPath,
    warningText: 'This action will permanently remove all selected files and folders and cannot be undone.',
    confirmText: '🗑️ Delete ' + count + ' Item' + (count === 1 ? '' : 's'),
    confirmBtnClass: 'btn-danger'
  });
  if (!confirmed) return;

  setOperationCancellable(true, 'batch-delete');
  let deletedTotal = 0;
  try {
    for (let i = 0; i < itemsArray.length; i++) {
      if (isOpCancellationRequested) break;
      const itemPath = itemsArray[i];
      const itemName = itemPath.split('/').pop() || itemPath;
      const pct = Math.round((i / itemsArray.length) * 100);
      showOperationProgress(
        '🗑️ Deleting (' + (i + 1) + ' / ' + itemsArray.length + '): ' + itemName,
        pct,
        'Location: ' + currentPath,
        false
      );
      const res = await apiFetch('/api/batch-delete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ items: [itemPath] })
      });
      if (res.status === 401) {
        hideOperationProgress();
        setOperationCancellable(false, null);
        return;
      }
      const data = await res.json();
      if (data && data.ok) {
        deletedTotal++;
      }
      if (isOpCancellationRequested) break;
      const afterPct = Math.round(((i + 1) / itemsArray.length) * 100);
      showOperationProgress(
        '🗑️ Deleting (' + (i + 1) + ' / ' + itemsArray.length + '): ' + itemName,
        afterPct,
        'Location: ' + currentPath,
        false
      );
    }

    if (isOpCancellationRequested) {
      showToast('⏹️ Deletion cancelled. ' + deletedTotal + ' of ' + count + ' item(s) deleted.', 'amber');
      completeOperationProgress('⏹️ Deletion Cancelled', deletedTotal + ' of ' + count + ' items deleted, remaining items kept safe', 2200);
      selectedPaths.clear();
      setOperationCancellable(false, null);
      loadDirectory(currentPath, { skipHistory: true });
      return;
    }

    setOperationCancellable(false, null);

    if (deletedTotal === 0) {
      hideOperationProgress();
      await showAlertDialog('Failed to delete selected items');
    } else {
      completeOperationProgress('🗑️ Deleted ' + deletedTotal + ' item(s)', 'Location: ' + currentPath, 1600);
      showToast('🗑️ Deleted ' + deletedTotal + ' item(s)', 'emerald');
    }
    selectedPaths.clear();
  } catch (e) {
    hideOperationProgress();
    setOperationCancellable(false, null);
    if (!isOpCancellationRequested) {
      await showAlertDialog('Delete failed: ' + e.message);
    }
  }
  loadDirectory(currentPath, { skipHistory: true });
}

/* Batch Rename Modal & Logic */
let batchRenameItems = [];
let batchRenameComputedPlan = [];

function splitFileNameAndExt(name, isDirectory) {
  if (isDirectory) {
    return { stem: name, ext: '' };
  }
  const lastDot = name.lastIndexOf('.');
  if (lastDot > 0 && lastDot < name.length - 1) {
    return {
      stem: name.substring(0, lastDot),
      ext: name.substring(lastDot)
    };
  }
  return { stem: name, ext: '' };
}

function formatBatchNumberedNameJs(baseName, format, number, ext) {
  const numStr = String(number);
  const pad2 = numStr.padStart(2, '0');
  const pad3 = numStr.padStart(3, '0');
  switch (format) {
    case 'underscore': return baseName + '_' + numStr + ext;
    case 'pad2': return baseName + '_' + pad2 + ext;
    case 'pad3': return baseName + '_' + pad3 + ext;
    case 'dash': return baseName + '-' + pad3 + ext;
    case 'space': return baseName + ' ' + numStr + ext;
    case 'paren':
    default:
      return baseName + ' (' + numStr + ')' + ext;
  }
}

function getOrderedSelectedItemsForBatchRename() {
  const sortedVisible = getFilteredSortedItems();
  const visibleMap = new Map();
  for (const it of sortedVisible) {
    visibleMap.set(getItemFullPath(it), it);
  }
  const currentMap = new Map();
  for (const it of currentItems) {
    currentMap.set(getItemFullPath(it), it);
  }

  const ordered = [];
  const addedPaths = new Set();

  for (const it of sortedVisible) {
    const fullPath = getItemFullPath(it);
    if (selectedPaths.has(fullPath)) {
      ordered.push({
        fullPath: fullPath,
        parentDir: getParentPath(fullPath),
        name: it.name,
        isDirectory: !!it.isDirectory
      });
      addedPaths.add(fullPath);
    }
  }

  for (const fullPath of selectedPaths) {
    if (addedPaths.has(fullPath)) continue;
    const known = visibleMap.get(fullPath) || currentMap.get(fullPath);
    const name = known ? known.name : (fullPath.split('/').pop() || '');
    if (!name) continue;
    ordered.push({
      fullPath: fullPath,
      parentDir: getParentPath(fullPath),
      name: name,
      isDirectory: known ? !!known.isDirectory : false
    });
  }

  return ordered;
}

function openBatchRenameModal() {
  if (selectedPaths.size === 0 || isReadOnly) return;
  batchRenameItems = getOrderedSelectedItemsForBatchRename();
  if (batchRenameItems.length === 0) return;

  const firstItem = batchRenameItems[0];
  const splitFirst = splitFileNameAndExt(firstItem.name, firstItem.isDirectory);
  // Strip trailing " (1)" or "_01" if the file was already numbered so re-numbering is clean
  const cleanInitialStem = splitFirst.stem.replace(/\s*\(\d+\)$/, '').trim() || splitFirst.stem;

  const baseInp = document.getElementById('batchRenameBaseInput');
  const startInp = document.getElementById('batchRenameStartInput');
  const errBanner = document.getElementById('batchRenameErrorBanner');
  const countBadge = document.getElementById('batchRenameCountBadge');
  const submitBtn = document.getElementById('batchRenameSubmitBtn');

  baseInp.value = cleanInitialStem;
  startInp.value = '1';
  errBanner.style.display = 'none';
  countBadge.textContent = batchRenameItems.length + ' item' + (batchRenameItems.length === 1 ? '' : 's');
  submitBtn.disabled = false;
  submitBtn.textContent = '✏️ Rename ' + batchRenameItems.length + ' Item' + (batchRenameItems.length === 1 ? '' : 's');

  updateBatchRenamePreview();
  document.getElementById('batchRenameModal').classList.add('active');
  setTimeout(() => {
    baseInp.focus();
    baseInp.select();
  }, 40);
}

function closeBatchRenameModal() {
  document.getElementById('batchRenameModal').classList.remove('active');
}

function clearBatchRenameBaseInput() {
  const baseInp = document.getElementById('batchRenameBaseInput');
  baseInp.value = '';
  updateBatchRenamePreview();
  baseInp.focus();
}

function updateBatchRenamePreview() {
  const baseInp = document.getElementById('batchRenameBaseInput');
  const formatSel = document.getElementById('batchRenameFormatSelect');
  const startInp = document.getElementById('batchRenameStartInput');
  const errBanner = document.getElementById('batchRenameErrorBanner');
  const skipNotice = document.getElementById('batchRenameSkipNotice');
  const skipNoticeText = document.getElementById('batchRenameSkipNoticeText');
  const listEl = document.getElementById('batchRenamePreviewList');

  errBanner.style.display = 'none';

  const rawBase = (baseInp.value || '').trim();
  const format = formatSel.value || 'paren';
  let startNum = parseInt(startInp.value, 10);
  if (isNaN(startNum) || startNum < 1) startNum = 1;

  // Build set of unselected existing paths (lowercased) to detect collisions in real time
  const selectedLowerSet = new Set(batchRenameItems.map(it => it.fullPath.toLowerCase()));
  const unselectedExistingLowerSet = new Set();
  for (const it of currentItems) {
    const p = getItemFullPath(it).toLowerCase();
    if (!selectedLowerSet.has(p)) {
      unselectedExistingLowerSet.add(p);
    }
  }
  if (Array.isArray(recursiveSearchResults)) {
    for (const it of recursiveSearchResults) {
      const p = getItemFullPath(it).toLowerCase();
      if (!selectedLowerSet.has(p)) {
        unselectedExistingLowerSet.add(p);
      }
    }
  }

  const claimedTargetsLower = new Set();
  batchRenameComputedPlan = [];
  let nextCounter = startNum;
  let skippedConflictsCount = 0;

  let html = '';
  for (let i = 0; i < batchRenameItems.length; i++) {
    const item = batchRenameItems[i];
    const parts = splitFileNameAndExt(item.name, item.isDirectory);
    const effectiveBase = rawBase || parts.stem;

    let assignedNum = nextCounter;
    let candidateName = formatBatchNumberedNameJs(effectiveBase, format, assignedNum, parts.ext);
    let candidateFullPath = joinPath(item.parentDir, candidateName).toLowerCase();

    while (unselectedExistingLowerSet.has(candidateFullPath) || claimedTargetsLower.has(candidateFullPath)) {
      skippedConflictsCount++;
      assignedNum++;
      candidateName = formatBatchNumberedNameJs(effectiveBase, format, assignedNum, parts.ext);
      candidateFullPath = joinPath(item.parentDir, candidateName).toLowerCase();
      if (assignedNum - nextCounter > 5000) break;
    }

    claimedTargetsLower.add(candidateFullPath);
    nextCounter = assignedNum + 1;

    batchRenameComputedPlan.push({
      from: item.fullPath,
      oldName: item.name,
      toName: candidateName,
      baseName: effectiveBase,
      format: format,
      ext: parts.ext,
      index: assignedNum,
      isDirectory: item.isDirectory
    });

    const cat = getFileCategory(item.name, item.isDirectory);
    html += '<div class="batch-preview-row">'
      + '<span class="batch-preview-idx">#' + (i + 1) + '</span>'
      + '<span style="flex-shrink:0;">' + cat.icon + '</span>'
      + '<span class="batch-preview-old" title="' + escapeHtml(item.name) + '">' + escapeHtml(item.name) + '</span>'
      + '<span class="batch-preview-arrow">→</span>'
      + '<span class="batch-preview-new" title="' + escapeHtml(candidateName) + '">' + escapeHtml(candidateName) + '</span>'
      + '</div>';
  }

  listEl.innerHTML = html || '<div style="padding:14px;text-align:center;color:var(--text-muted);">No items selected</div>';

  if (skippedConflictsCount > 0) {
    skipNoticeText.textContent = skippedConflictsCount + ' existing filename' + (skippedConflictsCount === 1 ? '' : 's') + ' in this folder were automatically skipped to avoid overwriting.';
    skipNotice.style.display = 'flex';
  } else {
    skipNotice.style.display = 'none';
  }
}

async function submitBatchRename() {
  if (batchRenameItems.length === 0) return;
  const baseInp = document.getElementById('batchRenameBaseInput');
  const errBanner = document.getElementById('batchRenameErrorBanner');
  const errText = document.getElementById('batchRenameErrorText');
  const card = document.getElementById('batchRenameCard');
  const submitBtn = document.getElementById('batchRenameSubmitBtn');

  const rawBase = (baseInp.value || '').trim();
  if (!rawBase) {
    errText.textContent = 'Please enter a base name for batch renaming.';
    errBanner.style.display = 'flex';
    card.classList.remove('shake');
    void card.offsetWidth;
    card.classList.add('shake');
    baseInp.focus();
    return;
  }

  if (/[\/\\:*?"<>|]/.test(rawBase) || rawBase === '.' || rawBase === '..') {
    errText.textContent = 'Base name cannot contain / \\ : * ? " < > |';
    errBanner.style.display = 'flex';
    card.classList.remove('shake');
    void card.offsetWidth;
    card.classList.add('shake');
    baseInp.focus();
    return;
  }

  updateBatchRenamePreview();

  const totalCount = batchRenameComputedPlan.length;
  submitBtn.disabled = true;
  submitBtn.textContent = '⏳ Renaming...';
  showOperationProgress(
    '✏️ Batch Renaming ' + totalCount + ' item' + (totalCount === 1 ? '' : 's') + '...',
    50,
    'Base name: "' + rawBase + '"',
    false
  );

  try {
    const res = await apiFetch('/api/batch-rename', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ operations: batchRenameComputedPlan })
    });
    if (res.status === 401) {
      hideOperationProgress();
      submitBtn.disabled = false;
      submitBtn.textContent = '✏️ Rename ' + totalCount + ' Item' + (totalCount === 1 ? '' : 's');
      return;
    }
    const data = await res.json();
    if (!data.ok) {
      hideOperationProgress();
      errText.textContent = data.error || 'Could not rename selected items.';
      errBanner.style.display = 'flex';
      submitBtn.disabled = false;
      submitBtn.textContent = '✏️ Rename ' + totalCount + ' Item' + (totalCount === 1 ? '' : 's');
      return;
    }
    closeBatchRenameModal();
    selectedPaths.clear();
    const renamedCount = data.renamed || totalCount;
    completeOperationProgress(
      '✏️ Batch Renamed ' + renamedCount + ' item' + (renamedCount === 1 ? '' : 's'),
      'Base name: "' + rawBase + '"',
      1600
    );
    showToast('✏️ Renamed ' + renamedCount + ' item' + (renamedCount === 1 ? '' : 's'), 'emerald');
    loadDirectory(currentPath, { skipHistory: true });
  } catch (e) {
    hideOperationProgress();
    errText.textContent = 'Batch rename failed: ' + e.message;
    errBanner.style.display = 'flex';
    submitBtn.disabled = false;
    submitBtn.textContent = '✏️ Rename ' + totalCount + ' Item' + (totalCount === 1 ? '' : 's');
  }
}

/* Destination Folder Picker Modal */
function openDestinationModalForSelection() {
  if (selectedPaths.size === 0) return;
  destModalItems = Array.from(selectedPaths);
  openDestModal(currentPath);
}

function openDestinationModalForClipboard() {
  if (clipboardItems.length === 0) return;
  destModalItems = clipboardItems.slice();
  openDestModal(currentPath);
}

async function openDestModal(startPath) {
  document.getElementById('destModal').classList.add('active');
  document.getElementById('destModalTitle').textContent = 'Copy / Move ' + destModalItems.length + ' Item(s) To...';
  await loadDestModalFolder(startPath || '/');
}

function closeDestModal() {
  document.getElementById('destModal').classList.remove('active');
}

async function loadDestModalFolder(path) {
  destModalPath = path || '/';
  document.getElementById('destModalCurrentPath').textContent = destModalPath;
  const listEl = document.getElementById('destFolderList');
  listEl.innerHTML = '<div style="padding:16px;color:var(--text-muted);font-size:13px;">Loading folders...</div>';
  try {
    const res = await apiFetch('/api/list?path=' + encodeURIComponent(destModalPath));
    if (res.status === 401) return;
    const data = await res.json();
    const dirs = (data.items || []).filter(it => it.isDirectory).sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()));
    let html = '';
    if (destModalPath !== '/') {
      const parent = getParentPath(destModalPath);
      html += '<div class="folder-picker-item" onclick="loadDestModalFolder(\'' + escapeHtml(parent) + '\')">↩️ .. (Parent Folder)</div>';
    }
    if (dirs.length === 0) {
      html += '<div style="padding:16px;color:var(--text-muted);font-size:13px;text-align:center;">No subfolders here — click Copy Here or Move Here below</div>';
    } else {
      for (const d of dirs) {
        const subPath = joinPath(destModalPath, d.name);
        html += '<div class="folder-picker-item" onclick="loadDestModalFolder(\'' + escapeHtml(subPath) + '\')">📁 ' + escapeHtml(d.name) + '</div>';
      }
    }
    listEl.innerHTML = html;
  } catch (e) {
    listEl.innerHTML = '<div style="padding:16px;color:var(--danger);">Failed to load folders</div>';
  }
}

async function promptNewFolderInDestModal() {
  const name = await showPromptDialog({
    icon: '📁',
    badgeClass: 'icon-badge-emerald',
    title: 'Create New Subfolder',
    subtitle: 'Enter a name for the new destination subfolder',
    targetLabel: 'Inside Folder',
    targetValue: destModalPath,
    inputLabel: 'Subfolder Name',
    placeholder: 'e.g. New Folder',
    defaultValue: '',
    confirmText: '➕ Create Subfolder',
    confirmBtnClass: 'btn-emerald'
  });
  if (!name || !name.trim()) return;
  const target = joinPath(destModalPath, name.trim());
  const res = await apiFetch('/api/mkdir?path=' + encodeURIComponent(target), { method: 'POST' });
  if (res.status === 401) return;
  const data = await res.json();
  if (data.ok) {
    showToast('📁 Created folder "' + name.trim() + '"', 'emerald');
    await loadDestModalFolder(target);
  } else {
    await showAlertDialog(data.error || 'Failed to create folder');
  }
}

async function confirmDestModalAction(mode) {
  if (!destModalItems || destModalItems.length === 0) return;
  clipboardItems = destModalItems.slice();
  clipboardMode = mode;
  closeDestModal();
  await executePasteTo(destModalPath, mode);
}

function downloadCurrentDirZip() {
  window.location.href = withAuthUrl('/api/zip?path=' + encodeURIComponent(currentPath));
}

async function promptNewFolder() {
  const name = await showPromptDialog({
    icon: '📁',
    badgeClass: 'icon-badge-emerald',
    title: 'Create New Folder',
    subtitle: 'Enter a name for the new folder',
    targetLabel: 'Location',
    targetValue: currentPath,
    inputLabel: 'Folder Name',
    placeholder: 'e.g. Documents',
    defaultValue: '',
    confirmText: '➕ Create Folder',
    confirmBtnClass: 'btn-emerald'
  });
  if (!name || !name.trim()) return;
  const target = joinPath(currentPath, name.trim());
  const res = await apiFetch('/api/mkdir?path=' + encodeURIComponent(target), { method: 'POST' });
  if (res.status === 401) return;
  const data = await res.json();
  if (!data.ok) {
    await showAlertDialog(data.error || 'Failed to create folder');
  } else {
    showToast('📁 Created folder "' + name.trim() + '"', 'emerald');
  }
  loadDirectory(currentPath);
}

function getSuggestedNewFileName() {
  const existingLower = new Set(currentItems.map(it => (it.name || '').toLowerCase()));
  const base = 'New Text Document';
  const ext = '.txt';
  let candidate = base + ext;
  let counter = 2;
  while (existingLower.has(candidate.toLowerCase()) && counter < 1000) {
    candidate = base + ' (' + counter + ')' + ext;
    counter++;
  }
  return candidate;
}

async function promptNewFile() {
  if (isReadOnly) {
    showToast('🔒 Creating files is disabled in Read-Only mode', 'danger');
    return;
  }
  const defaultFileName = getSuggestedNewFileName();
  const rawName = await showPromptDialog({
    icon: '📄',
    badgeClass: 'icon-badge-primary',
    title: 'Create New File',
    subtitle: 'Enter a file name (with extension) to create an empty file in the current folder',
    targetLabel: 'Location',
    targetValue: currentPath,
    inputLabel: 'File Name',
    placeholder: 'e.g. notes.txt, README.md, data.json',
    defaultValue: defaultFileName,
    selectBaseName: true,
    confirmText: '➕ Create File',
    confirmBtnClass: 'btn-primary'
  });
  if (!rawName || !rawName.trim()) return;
  const cleanName = rawName.trim();
  if (cleanName.includes('/') || cleanName.includes('\\') || cleanName === '.' || cleanName === '..') {
    await showAlertDialog('File name cannot contain slashes (/ or \\).');
    return;
  }

  const candidatePath = joinPath(currentPath, cleanName);
  try {
    const checkRes = await apiFetch('/api/exists?path=' + encodeURIComponent(candidatePath));
    if (checkRes.status === 401) return;
    if (checkRes.ok) {
      const checkData = await checkRes.json();
      if (checkData && checkData.ok && checkData.exists) {
        if (checkData.isDirectory) {
          await showAlertDialog('A folder named "' + cleanName + '" already exists in this location.');
          return;
        }
        const overwrite = await showConfirmDialog({
          icon: '⚠️',
          badgeClass: 'icon-badge-amber',
          title: 'Overwrite Existing File?',
          subtitle: 'A file named "' + cleanName + '" already exists in this folder. Replace it with a new empty file?',
          targetLabel: 'Existing File',
          targetValue: candidatePath,
          warningText: 'Replacing this file will clear its current contents.',
          confirmText: '🔄 Overwrite File',
          confirmBtnClass: 'btn-amber'
        });
        if (!overwrite) return;
      }
    }
  } catch (e) {}

  try {
    const emptyBlob = new Blob([''], { type: 'text/plain;charset=utf-8' });
    const uploadUrl = '/api/upload?path=' + encodeURIComponent(currentPath) + '&relativePath=' + encodeURIComponent(cleanName);
    const res = await apiFetch(uploadUrl, {
      method: 'POST',
      body: emptyBlob
    });
    if (res.status === 401) return;
    const data = await res.json();
    if (!data || !data.ok) {
      await showAlertDialog((data && data.error) ? data.error : 'Failed to create file');
    } else {
      showToast('📄 Created file "' + cleanName + '"', 'emerald');
    }
  } catch (e) {
    await showAlertDialog('Failed to create file: ' + e.message);
  }
  loadDirectory(currentPath, { skipHistory: true });
}

async function promptRename(fullPath, oldName) {
  const rawNewName = await showPromptDialog({
    icon: '✏️',
    badgeClass: 'icon-badge-primary',
    title: 'Rename Item',
    subtitle: 'Enter a new name for this file or folder',
    targetLabel: 'Current Name',
    targetValue: oldName,
    inputLabel: 'New Name',
    placeholder: 'Enter new name...',
    defaultValue: oldName,
    selectBaseName: true,
    confirmText: '💾 Save Name',
    confirmBtnClass: 'btn-primary'
  });
  if (!rawNewName || !rawNewName.trim()) return;
  let finalNewName = rawNewName.trim();
  if (finalNewName === oldName) return;
  if (finalNewName.includes('/') || finalNewName.includes('\\') || finalNewName === '.' || finalNewName === '..') {
    await showAlertDialog('Item name cannot contain slashes (/ or \\).');
    return;
  }

  const parent = getParentPath(fullPath);
  const isCaseOnlySameItem = finalNewName.toLowerCase() === oldName.toLowerCase();

  if (!isCaseOnlySameItem) {
    const candidatePath = joinPath(parent, finalNewName);
    let existsData = null;
    try {
      const checkRes = await apiFetch('/api/exists?path=' + encodeURIComponent(candidatePath));
      if (checkRes.status === 401) return;
      if (checkRes.ok) {
        const parsed = await checkRes.json();
        if (parsed && parsed.ok && parsed.exists) {
          existsData = parsed;
        }
      }
    } catch (e) {}

    if (existsData) {
      const suggested = existsData.suggestedName || ('Copy of ' + finalNewName);
      const decision = await openRenameConflictModal({
        oldName: oldName,
        attemptedName: finalNewName,
        suggestedName: suggested,
        parentDir: parent
      });
      if (!decision || decision.action === 'cancel' || !decision.newName) {
        showToast('⏹️ Rename cancelled');
        return;
      }
      finalNewName = decision.newName.trim();
      if (!finalNewName || finalNewName === oldName) return;
    }
  }

  const toPath = joinPath(parent, finalNewName);
  const res = await apiFetch('/api/rename?from=' + encodeURIComponent(fullPath) + '&to=' + encodeURIComponent(toPath), { method: 'POST' });
  if (res.status === 401) return;
  const data = await res.json();
  if (!data.ok) {
    await showAlertDialog(data.error || 'Failed to rename item');
  } else {
    showToast('✏️ Renamed to "' + finalNewName + '"', 'emerald');
  }
  loadDirectory(currentPath, { skipHistory: true });
}

async function confirmDelete(fullPath, name, isDir) {
  const confirmed = await showConfirmDialog({
    icon: '🗑️',
    badgeClass: 'icon-badge-danger',
    title: isDir ? 'Delete Folder?' : 'Delete File?',
    subtitle: isDir
      ? 'Are you sure you want to permanently delete this folder and all of its contents?'
      : 'Are you sure you want to permanently delete this file?',
    targetLabel: isDir ? 'Folder' : 'File',
    targetValue: name,
    warningText: 'This item will be permanently removed from storage and cannot be recovered.',
    confirmText: '🗑️ Delete Permanently',
    confirmBtnClass: 'btn-danger'
  });
  if (!confirmed) return;
  const res = await apiFetch('/api/delete?path=' + encodeURIComponent(fullPath), { method: 'POST' });
  if (res.status === 401) return;
  const data = await res.json();
  if (!data.ok) {
    await showAlertDialog(data.error || 'Failed to delete item');
  } else {
    showToast('🗑️ Deleted "' + name + '"', 'emerald');
  }
  loadDirectory(currentPath);
}

/* Preview Modal & Floating Pop-Out Window */
let activePreviewState = {
  fullPath: '',
  type: '',
  name: '',
  isFloating: false
};

let floatingWinState = {
  isDragging: false,
  dragStartX: 0,
  dragStartY: 0,
  initialLeft: 0,
  initialTop: 0,
  isResizing: false,
  resizeDir: '',
  resizeStartX: 0,
  resizeStartY: 0,
  initialW: 0,
  initialH: 0
};

async function openPreview(fullPath, type, name) {
  activePreviewState.fullPath = fullPath;
  activePreviewState.type = type;
  activePreviewState.name = name;

  // If floating preview window is already open, update its content directly
  if (activePreviewState.isFloating) {
    const floatingWin = document.getElementById('previewFloatingWindow');
    if (floatingWin && floatingWin.classList.contains('active')) {
      const flTitle = document.getElementById('previewFloatingTitle');
      const flDl = document.getElementById('previewFloatingDownloadBtn');
      const flBody = document.getElementById('previewFloatingBody');
      const url = withAuthUrl('/api/download?path=' + encodeURIComponent(fullPath) + '&inline=1');

      flTitle.textContent = name;
      flDl.href = withAuthUrl('/api/download?path=' + encodeURIComponent(fullPath));
      flBody.innerHTML = 'Loading preview...';

      if (type === 'image') {
        flBody.innerHTML = '<img src="' + url + '" alt="' + escapeHtml(name) + '">';
      } else if (type === 'video') {
        flBody.innerHTML = '<video src="' + url + '" controls autoplay></video>';
      } else if (type === 'audio') {
        flBody.innerHTML = '<audio src="' + url + '" controls autoplay style="width:100%;max-width:480px;"></audio>';
      } else if (type === 'text') {
        try {
          const res = await apiFetch('/api/download?path=' + encodeURIComponent(fullPath) + '&inline=1');
          if (res.status === 401) { closeFloatingPreview(); return; }
          const txt = await res.text();
          flBody.innerHTML = '<pre>' + escapeHtml(txt.slice(0, 200000)) + '</pre>';
        } catch (e) {
          flBody.innerHTML = 'Failed to load text preview.';
        }
      }
      return;
    }
  }

  // Otherwise, open standard modal preview
  const modal = document.getElementById('previewModal');
  const body = document.getElementById('previewBody');
  const title = document.getElementById('previewTitle');
  const dlBtn = document.getElementById('previewDownloadBtn');
  const url = withAuthUrl('/api/download?path=' + encodeURIComponent(fullPath) + '&inline=1');

  title.textContent = name;
  dlBtn.href = withAuthUrl('/api/download?path=' + encodeURIComponent(fullPath));
  body.innerHTML = 'Loading preview...';
  modal.classList.add('active');

  if (type === 'image') {
    body.innerHTML = '<img src="' + url + '" alt="' + escapeHtml(name) + '">';
  } else if (type === 'video') {
    body.innerHTML = '<video src="' + url + '" controls autoplay></video>';
  } else if (type === 'audio') {
    body.innerHTML = '<audio src="' + url + '" controls autoplay style="width:100%;max-width:480px;"></audio>';
  } else if (type === 'text') {
    try {
      const res = await apiFetch('/api/download?path=' + encodeURIComponent(fullPath) + '&inline=1');
      if (res.status === 401) { closePreview(); return; }
      const txt = await res.text();
      body.innerHTML = '<pre>' + escapeHtml(txt.slice(0, 200000)) + '</pre>';
    } catch (e) {
      body.innerHTML = 'Failed to load text preview.';
    }
  }
}

function closePreview() {
  const modal = document.getElementById('previewModal');
  const body = document.getElementById('previewBody');
  modal.classList.remove('active');
  body.innerHTML = '';
  if (!activePreviewState.isFloating) {
    activePreviewState.fullPath = '';
    activePreviewState.type = '';
    activePreviewState.name = '';
  }
}

function popOutPreview() {
  const modal = document.getElementById('previewModal');
  const modalBody = document.getElementById('previewBody');
  const floatingWin = document.getElementById('previewFloatingWindow');
  const flTitle = document.getElementById('previewFloatingTitle');
  const flDl = document.getElementById('previewFloatingDownloadBtn');
  const flBody = document.getElementById('previewFloatingBody');

  if (!activePreviewState.fullPath) return;

  activePreviewState.isFloating = true;
  flTitle.textContent = activePreviewState.name;
  flDl.href = withAuthUrl('/api/download?path=' + encodeURIComponent(activePreviewState.fullPath));

  // Move content without reloading media
  flBody.innerHTML = '';
  while (modalBody.firstChild) {
    flBody.appendChild(modalBody.firstChild);
  }

  // Close modal backdrop
  modal.classList.remove('active');

  // Position floating window if not previously moved
  if (!floatingWin.style.top && !floatingWin.style.left) {
    const ww = window.innerWidth;
    const wh = window.innerHeight;
    const initialWidth = Math.min(640, Math.max(320, ww - 60));
    const initialHeight = Math.min(480, Math.max(240, wh - 100));
    const left = Math.max(16, ww - initialWidth - 28);
    const top = Math.max(60, wh - initialHeight - 32);
    floatingWin.style.width = initialWidth + 'px';
    floatingWin.style.height = initialHeight + 'px';
    floatingWin.style.left = left + 'px';
    floatingWin.style.top = top + 'px';
    floatingWin.style.right = 'auto';
    floatingWin.style.bottom = 'auto';
  }

  floatingWin.classList.add('active');
  initFloatingPreviewInteractions();
}

function popInPreview() {
  const modal = document.getElementById('previewModal');
  const modalBody = document.getElementById('previewBody');
  const title = document.getElementById('previewTitle');
  const dlBtn = document.getElementById('previewDownloadBtn');
  const floatingWin = document.getElementById('previewFloatingWindow');
  const flBody = document.getElementById('previewFloatingBody');

  if (!activePreviewState.fullPath) return;

  activePreviewState.isFloating = false;
  title.textContent = activePreviewState.name;
  dlBtn.href = withAuthUrl('/api/download?path=' + encodeURIComponent(activePreviewState.fullPath));

  modalBody.innerHTML = '';
  while (flBody.firstChild) {
    modalBody.appendChild(flBody.firstChild);
  }

  floatingWin.classList.remove('active');
  modal.classList.add('active');
}

function closeFloatingPreview() {
  const floatingWin = document.getElementById('previewFloatingWindow');
  const flBody = document.getElementById('previewFloatingBody');
  floatingWin.classList.remove('active');
  flBody.innerHTML = '';
  activePreviewState.isFloating = false;
  activePreviewState.fullPath = '';
  activePreviewState.type = '';
  activePreviewState.name = '';
}

let floatingInteractionsInitialized = false;
function initFloatingPreviewInteractions() {
  if (floatingInteractionsInitialized) return;
  floatingInteractionsInitialized = true;

  const floatingWin = document.getElementById('previewFloatingWindow');
  const header = document.getElementById('previewFloatingHeader');

  // Bring window to front on mousedown
  floatingWin.addEventListener('mousedown', () => {
    floatingWin.style.zIndex = '1001';
  });

  // Dragging by header
  header.addEventListener('mousedown', (e) => {
    if (e.button !== 0) return;
    if (e.target.closest('button, a')) return;
    e.preventDefault();

    const rect = floatingWin.getBoundingClientRect();
    floatingWinState.isDragging = true;
    floatingWinState.dragStartX = e.clientX;
    floatingWinState.dragStartY = e.clientY;
    floatingWinState.initialLeft = rect.left;
    floatingWinState.initialTop = rect.top;

    floatingWin.style.left = rect.left + 'px';
    floatingWin.style.top = rect.top + 'px';
    floatingWin.style.right = 'auto';
    floatingWin.style.bottom = 'auto';
    floatingWin.classList.add('focused');

    function onMouseMove(moveEvent) {
      if (!floatingWinState.isDragging) return;
      const dx = moveEvent.clientX - floatingWinState.dragStartX;
      const dy = moveEvent.clientY - floatingWinState.dragStartY;
      const newLeft = Math.max(0, Math.min(window.innerWidth - 80, floatingWinState.initialLeft + dx));
      const newTop = Math.max(0, Math.min(window.innerHeight - 50, floatingWinState.initialTop + dy));
      floatingWin.style.left = newLeft + 'px';
      floatingWin.style.top = newTop + 'px';
    }

    function onMouseUp() {
      floatingWinState.isDragging = false;
      floatingWin.classList.remove('focused');
      window.removeEventListener('mousemove', onMouseMove);
      window.removeEventListener('mouseup', onMouseUp);
    }

    window.addEventListener('mousemove', onMouseMove);
    window.addEventListener('mouseup', onMouseUp);
  });

  // Resizing via handles
  const handles = floatingWin.querySelectorAll('.preview-resize-handle');
  handles.forEach(handle => {
    handle.addEventListener('mousedown', (e) => {
      if (e.button !== 0) return;
      e.preventDefault();
      e.stopPropagation();

      const dir = handle.getAttribute('data-resize-dir') || 'se';
      const rect = floatingWin.getBoundingClientRect();

      floatingWinState.isResizing = true;
      floatingWinState.resizeDir = dir;
      floatingWinState.resizeStartX = e.clientX;
      floatingWinState.resizeStartY = e.clientY;
      floatingWinState.initialLeft = rect.left;
      floatingWinState.initialTop = rect.top;
      floatingWinState.initialW = rect.width;
      floatingWinState.initialH = rect.height;

      floatingWin.style.left = rect.left + 'px';
      floatingWin.style.top = rect.top + 'px';
      floatingWin.style.width = rect.width + 'px';
      floatingWin.style.height = rect.height + 'px';
      floatingWin.style.right = 'auto';
      floatingWin.style.bottom = 'auto';
      floatingWin.classList.add('focused');

      function onResizeMove(moveEvent) {
        if (!floatingWinState.isResizing) return;
        const dx = moveEvent.clientX - floatingWinState.resizeStartX;
        const dy = moveEvent.clientY - floatingWinState.resizeStartY;
        const minW = 280;
        const minH = 180;
        const maxW = window.innerWidth - 20;
        const maxH = window.innerHeight - 20;

        let newW = floatingWinState.initialW;
        let newH = floatingWinState.initialH;
        let newLeft = floatingWinState.initialLeft;
        let newTop = floatingWinState.initialTop;

        if (dir.includes('e')) {
          newW = Math.max(minW, Math.min(maxW, floatingWinState.initialW + dx));
        }
        if (dir.includes('s')) {
          newH = Math.max(minH, Math.min(maxH, floatingWinState.initialH + dy));
        }
        if (dir.includes('w')) {
          const proposedW = floatingWinState.initialW - dx;
          if (proposedW >= minW && proposedW <= maxW) {
            newW = proposedW;
            newLeft = floatingWinState.initialLeft + dx;
          }
        }
        if (dir.includes('n')) {
          const proposedH = floatingWinState.initialH - dy;
          if (proposedH >= minH && proposedH <= maxH) {
            newH = proposedH;
            newTop = floatingWinState.initialTop + dy;
          }
        }

        floatingWin.style.width = newW + 'px';
        floatingWin.style.height = newH + 'px';
        floatingWin.style.left = newLeft + 'px';
        floatingWin.style.top = newTop + 'px';
      }

      function onResizeUp() {
        floatingWinState.isResizing = false;
        floatingWin.classList.remove('focused');
        window.removeEventListener('mousemove', onResizeMove);
        window.removeEventListener('mouseup', onResizeUp);
      }

      window.addEventListener('mousemove', onResizeMove);
      window.addEventListener('mouseup', onResizeUp);
    });
  });
}

/* File & Folder Upload Queue */
function handleFilesSelected(fileList) {
  if (!fileList || fileList.length === 0) return;
  const queue = [];
  for (let i = 0; i < fileList.length; i++) {
    queue.push({ file: fileList[i], relativePath: fileList[i].name });
  }
  uploadQueue(queue, []);
  document.getElementById('fileInput').value = '';
}

function handleFolderSelected(fileList) {
  if (!fileList || fileList.length === 0) return;
  const queue = [];
  for (let i = 0; i < fileList.length; i++) {
    const f = fileList[i];
    const rel = f.webkitRelativePath || f.name;
    queue.push({ file: f, relativePath: rel });
  }
  uploadQueue(queue, []);
  document.getElementById('folderInput').value = '';
}

async function uploadQueue(fileEntries, emptyDirs) {
  if (isReadOnly) {
    await showAlertDialog({
      icon: '🔒',
      badgeClass: 'icon-badge-amber',
      title: 'Read-Only Mode',
      subtitle: 'The server is currently in Read-Only mode. Uploading files or folders is disabled.',
      confirmText: 'Got It',
      confirmBtnClass: 'btn-primary'
    });
    return;
  }
  const panel = document.getElementById('uploadPanel');
  const statusText = document.getElementById('uploadStatusText');
  const pctText = document.getElementById('uploadPercentText');
  const bar = document.getElementById('uploadProgressBar');
  const subText = document.getElementById('uploadSubText');

  for (const dirRel of emptyDirs) {
    const targetDir = joinPath(currentPath, dirRel);
    try {
      await apiFetch('/api/mkdir?path=' + encodeURIComponent(targetDir), { method: 'POST' });
    } catch (e) {}
  }

  const totalFiles = fileEntries.length;
  if (totalFiles === 0) {
    loadDirectory(currentPath);
    return;
  }

  setOperationCancellable(true, 'upload');
  let totalBytes = fileEntries.reduce((acc, it) => acc + (it.file.size || 0), 0);
  let uploadedBytesSoFar = 0;
  let uploadedCount = 0;
  let skippedCount = 0;
  let batchConflictAction = null; // 'overwrite', 'skip', 'rename'
  const startTime = Date.now();
  opStartTime = startTime;
  opCustomEtaSec = null;
  startOperationTimer();
  updateOperationTimers(0, null);

  if (bar) {
    bar.classList.remove('indeterminate');
    bar.style.animation = 'none';
    bar.style.transform = 'none';
    bar.style.width = '0%';
  }
  if (pctText) pctText.textContent = '0%';

  for (let i = 0; i < totalFiles; i++) {
    if (isOpCancellationRequested) break;
    const item = fileEntries[i];
    let effectiveRelPath = item.relativePath;
    const targetVirtualPath = joinPath(currentPath, effectiveRelPath);
    const targetParentDir = getParentPath(targetVirtualPath);
    const originalFileName = effectiveRelPath.split('/').pop() || item.file.name;

    // Check if a file with the same name already exists on the phone
    let existsData = null;
    try {
      const checkRes = await apiFetch('/api/exists?path=' + encodeURIComponent(targetVirtualPath));
      if (checkRes.status === 401) {
        panel.classList.remove('active');
        setOperationCancellable(false, null);
        return;
      }
      if (checkRes.ok) {
        const parsed = await checkRes.json();
        if (parsed && parsed.ok && parsed.exists) {
          existsData = parsed;
        }
      }
    } catch (e) {}

    if (isOpCancellationRequested) break;

    if (existsData) {
      let chosenAction = batchConflictAction;
      let chosenNewName = existsData.suggestedName || ('Copy of ' + originalFileName);

      if (!chosenAction) {
        const decision = await openConflictModal({
          title: totalFiles > 1
            ? ('File Already Exists (' + (i + 1) + ' of ' + totalFiles + ')')
            : 'File Already Exists',
          subtitle: 'A file named "' + originalFileName + '" already exists on your phone in "' + targetParentDir + '". What would you like to do?',
          fileName: originalFileName,
          targetDir: targetParentDir,
          existingSize: existsData.size || 0,
          existingModified: existsData.lastModified || 0,
          existingIsDir: !!existsData.isDirectory,
          incomingLabel: '📤 New File to Upload',
          newSize: item.file.size || 0,
          newModified: item.file.lastModified || Date.now(),
          newIsDir: false,
          suggestedName: chosenNewName,
          currentIndex: i + 1,
          totalFiles: totalFiles
        });

        if (!decision || decision.action === 'cancel' || isOpCancellationRequested) {
          panel.classList.remove('active');
          showToast('⏹️ Upload cancelled');
          setOperationCancellable(false, null);
          loadDirectory(currentPath);
          return;
        }

        if (decision.applyAll) {
          batchConflictAction = decision.action;
        }
        chosenAction = decision.action;
        if (decision.newName) {
          chosenNewName = decision.newName;
        }
      }

      if (chosenAction === 'skip') {
        skippedCount++;
        totalBytes = Math.max(0, totalBytes - (item.file.size || 0));
        continue;
      } else if (chosenAction === 'rename') {
        const lastSlash = effectiveRelPath.lastIndexOf('/');
        effectiveRelPath = lastSlash >= 0
          ? (effectiveRelPath.substring(0, lastSlash + 1) + chosenNewName)
          : chosenNewName;
      }
      // If chosenAction === 'overwrite', effectiveRelPath remains unchanged and replaces the existing file
    }

    if (isOpCancellationRequested) break;

    panel.classList.add('active');
    statusText.textContent = 'Uploading (' + (i + 1) + ' / ' + totalFiles + '): ' + effectiveRelPath;
    activeUploadCurrentTarget = targetVirtualPath;
    if (bar) {
      bar.classList.remove('indeterminate');
      bar.style.animation = 'none';
      bar.style.transform = 'none';
    }

    await new Promise((resolve) => {
      const xhr = new XMLHttpRequest();
      activeUploadXhr = xhr;
      const url = withAuthUrl('/api/upload?path=' + encodeURIComponent(currentPath) + '&relativePath=' + encodeURIComponent(effectiveRelPath));
      xhr.open('POST', url, true);
      xhr.setRequestHeader('X-UFS-Session', clientSessionId);
      if (authToken) {
        xhr.setRequestHeader('X-UFS-Auth', authToken);
      }
      xhr.upload.onprogress = (e) => {
        if (e.lengthComputable && !isOpCancellationRequested) {
          const currentTotal = uploadedBytesSoFar + e.loaded;
          const pct = totalBytes > 0 ? Math.min(100, Math.round((currentTotal / totalBytes) * 100)) : 100;
          if (bar) {
            bar.classList.remove('indeterminate');
            bar.style.animation = 'none';
            bar.style.transform = 'none';
            bar.style.width = pct + '%';
          }
          if (pctText) pctText.textContent = pct + '%';
          const elapsedSec = Math.max(0.2, (Date.now() - startTime) / 1000);
          const speed = currentTotal / elapsedSec;
          subText.textContent = formatBytes(currentTotal) + ' of ' + formatBytes(totalBytes) + ' • ' + formatBytes(speed) + '/s';
          const remainingBytes = Math.max(0, totalBytes - currentTotal);
          const remainingSec = speed > 0 ? Math.round(remainingBytes / speed) : null;
          opLastPercent = pct;
          opCustomEtaSec = remainingSec;
          updateOperationTimers(elapsedSec, remainingSec);
        }
      };
      xhr.onabort = () => {
        activeUploadXhr = null;
        resolve();
      };
      xhr.onload = () => {
        activeUploadXhr = null;
        if (xhr.status === 401) {
          openAuthModal();
        } else if (xhr.status >= 200 && xhr.status < 300) {
          uploadedCount++;
        }
        uploadedBytesSoFar += (item.file.size || 0);
        const currentPct = totalBytes > 0 ? Math.min(100, Math.round((uploadedBytesSoFar / totalBytes) * 100)) : 100;
        if (bar) {
          bar.classList.remove('indeterminate');
          bar.style.animation = 'none';
          bar.style.transform = 'none';
          bar.style.width = currentPct + '%';
        }
        if (pctText) pctText.textContent = currentPct + '%';
        sendSessionHeartbeat();
        resolve();
      };
      xhr.onerror = () => {
        activeUploadXhr = null;
        sendSessionHeartbeat();
        resolve();
      };
      xhr.send(item.file);
      setTimeout(sendSessionHeartbeat, 120);
    });

    activeUploadXhr = null;
    activeUploadCurrentTarget = null;
    if (isOpCancellationRequested) break;
  }

  if (isOpCancellationRequested) {
    if (activeUploadCurrentTarget) {
      try {
        await apiFetch('/api/delete?path=' + encodeURIComponent(activeUploadCurrentTarget), { method: 'POST' });
      } catch (e) {}
      activeUploadCurrentTarget = null;
    }
    completeOperationProgress(
      '⏹️ Upload Cancelled (' + uploadedCount + ' of ' + totalFiles + ' saved)',
      'Incomplete files cleaned up safely',
      2500
    );
    showToast('⏹️ Upload cancelled. ' + uploadedCount + ' of ' + totalFiles + ' file(s) saved safely.', 'amber');
    setOperationCancellable(false, null);
    loadDirectory(currentPath);
    return;
  }

  setOperationCancellable(false, null);

  if (uploadedCount === 0 && skippedCount > 0) {
    hideOperationProgress();
    showToast('⏭️ Skipped ' + skippedCount + ' conflicting file' + (skippedCount === 1 ? '' : 's'));
    loadDirectory(currentPath);
    return;
  }

  stopOperationTimer();
  const finalElapsedSec = (Date.now() - startTime) / 1000;
  updateOperationTimers(finalElapsedSec, 0);
  statusText.textContent = 'Upload complete (' + uploadedCount + ' uploaded' + (skippedCount > 0 ? ', ' + skippedCount + ' skipped' : '') + ')';
  pctText.textContent = '100%';
  if (bar) {
    bar.classList.remove('indeterminate');
    bar.style.animation = 'none';
    bar.style.transform = 'none';
    bar.style.width = '100%';
  }
  setTimeout(() => { hideOperationProgress(); }, 2500);
  if (uploadedCount > 0) {
    showToast('⬆️ Uploaded ' + uploadedCount + ' file' + (uploadedCount === 1 ? '' : 's'), 'emerald');
  }
  loadDirectory(currentPath);
}

/* Drag & Drop Files + Recursive Folders Support */
async function traverseEntry(entry, prefix, fileEntries, emptyDirs) {
  if (!entry) return;
  if (entry.isFile) {
    await new Promise((resolve) => {
      entry.file((file) => {
        fileEntries.push({ file: file, relativePath: prefix ? (prefix + '/' + file.name) : file.name });
        resolve();
      }, () => resolve());
    });
  } else if (entry.isDirectory) {
    const dirPath = prefix ? (prefix + '/' + entry.name) : entry.name;
    const reader = entry.createReader();
    const children = [];
    const readBatch = async () => {
      const batch = await new Promise((resolve) => reader.readEntries(resolve, () => resolve([])));
      if (batch && batch.length > 0) {
        children.push(...batch);
        await readBatch();
      }
    };
    await readBatch();
    if (children.length === 0) {
      emptyDirs.push(dirPath);
    } else {
      for (const child of children) {
        await traverseEntry(child, dirPath, fileEntries, emptyDirs);
      }
    }
  }
}

let dragCounter = 0;
function isExternalFileDragEvent(e) {
  if (isInternalDragActive) return false;
  const dt = e.dataTransfer;
  if (!dt || !dt.types) return false;
  for (let i = 0; i < dt.types.length; i++) {
    if (dt.types[i] === 'Files') return true;
  }
  return false;
}

window.addEventListener('dragenter', (e) => {
  if (!isExternalFileDragEvent(e)) return;
  e.preventDefault();
  if (isReadOnly) return;
  dragCounter++;
  document.getElementById('dropOverlay').classList.add('active');
});
window.addEventListener('dragover', (e) => {
  if (isInternalDragActive) {
    lastDragOverInsideWindow = Date.now();
    return;
  }
  if (!isExternalFileDragEvent(e)) return;
  e.preventDefault();
});
window.addEventListener('dragleave', (e) => {
  if (!isExternalFileDragEvent(e)) return;
  e.preventDefault();
  dragCounter--;
  if (dragCounter <= 0) {
    dragCounter = 0;
    document.getElementById('dropOverlay').classList.remove('active');
  }
});
window.addEventListener('drop', async (e) => {
  if (isInternalDragActive) {
    cleanupInternalDragState();
    return;
  }
  if (!isExternalFileDragEvent(e)) return;
  e.preventDefault();
  dragCounter = 0;
  document.getElementById('dropOverlay').classList.remove('active');
  if (isReadOnly) return;

  const items = e.dataTransfer.items;
  if (items && items.length > 0 && items[0].webkitGetAsEntry) {
    const fileEntries = [];
    const emptyDirs = [];
    const entries = [];
    for (let i = 0; i < items.length; i++) {
      const entry = items[i].webkitGetAsEntry();
      if (entry) entries.push(entry);
    }
    setOperationCancellable(true, 'scan');
    showOperationProgress('📂 Scanning dropped items...', 0, 'Reading folder structure', true);
    for (const entry of entries) {
      if (isOpCancellationRequested) break;
      await traverseEntry(entry, '', fileEntries, emptyDirs);
    }
    if (isOpCancellationRequested) {
      hideOperationProgress();
      setOperationCancellable(false, null);
      showToast('⏹️ Scanning cancelled', 'amber');
      return;
    }
    if (fileEntries.length > 0 || emptyDirs.length > 0) {
      uploadQueue(fileEntries, emptyDirs);
    } else {
      hideOperationProgress();
      setOperationCancellable(false, null);
    }
  } else if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
    handleFilesSelected(e.dataTransfer.files);
  }
});

/* OS Clipboard File Paste (Ctrl+V & Context Menu Paste) */
let lastPasteEventTimestamp = 0;

function openOsPasteModal() {
  const folderEl = document.getElementById('osPasteTargetFolder');
  if (folderEl) folderEl.textContent = currentPath || '/';
  const modal = document.getElementById('osPasteModal');
  if (modal) modal.classList.add('active');
}

function closeOsPasteModal() {
  const modal = document.getElementById('osPasteModal');
  if (modal) modal.classList.remove('active');
}

function normalizeClipboardFileEntry(file, index) {
  if (!file) return null;
  let name = (file.name || '').trim();
  const isGenericImageName = !name || /^image(\.[a-z0-9]+)?$/i.test(name);
  if (isGenericImageName && file.type && file.type.startsWith('image/')) {
    const subType = (file.type.split('/')[1] || 'png').split(';')[0].replace(/[^a-z0-9]/gi, '') || 'png';
    const ext = subType === 'jpeg' ? 'jpg' : subType;
    const now = new Date();
    const pad = (n) => String(n).padStart(2, '0');
    const stamp = now.getFullYear() + '-' + pad(now.getMonth() + 1) + '-' + pad(now.getDate()) + '_' +
      pad(now.getHours()) + '-' + pad(now.getMinutes()) + '-' + pad(now.getSeconds());
    const suffix = index > 0 ? ('_' + (index + 1)) : '';
    name = 'Clipboard_' + stamp + suffix + '.' + ext;
  } else if (!name) {
    name = 'Pasted_File_' + Date.now() + (index > 0 ? ('_' + (index + 1)) : '');
  }
  return { file: file, relativePath: name };
}

async function handleContextMenuPaste() {
  if (isReadOnly) {
    showToast('🔒 Paste is disabled in Read-Only mode', 'danger');
    return;
  }
  // 1. If internal clipboard has staged items, paste them immediately
  if (clipboardItems.length > 0 && clipboardMode) {
    await executePasteTo(currentPath);
    return;
  }
  // 2. Try reading image/files from Async Clipboard API if browser supports it
  if (navigator.clipboard && navigator.clipboard.read) {
    try {
      const clipItems = await navigator.clipboard.read();
      const extractedFiles = [];
      let idx = 0;
      for (const clipItem of clipItems) {
        const mediaType = clipItem.types.find(t => t.startsWith('image/') || t.startsWith('video/') || t.startsWith('audio/') || t === 'application/pdf');
        if (mediaType) {
          const blob = await clipItem.getType(mediaType);
          if (blob) {
            const ext = (mediaType.split('/')[1] || 'bin').split(';')[0];
            const f = new File([blob], 'image.' + ext, { type: mediaType, lastModified: Date.now() });
            const entry = normalizeClipboardFileEntry(f, idx++);
            if (entry) extractedFiles.push(entry);
          }
        }
      }
      if (extractedFiles.length > 0) {
        showToast('📋 Uploading ' + extractedFiles.length + ' file(s) from OS clipboard...', 'emerald');
        await uploadQueue(extractedFiles, []);
        return;
      }
    } catch (err) {
      // Browser blocked navigator.clipboard.read() or clipboard contains native OS files only accessible via Ctrl+V paste event
    }
  }
  // 3. Open the OS Paste helper modal so the user can press Ctrl+V immediately
  openOsPasteModal();
}

window.addEventListener('paste', async (e) => {
  if (isTypingInTextInput(e)) return;
  if (isBlockingModalOpen()) return;

  lastPasteEventTimestamp = Date.now();
  const cd = e.clipboardData || window.clipboardData;
  const isFirefox = /firefox/i.test(navigator.userAgent || '');

  if (cd) {
    // 1. Synchronously snapshot ALL raw files and directory entries before ANY microtask/await yields
    const rawFiles = [];
    const dirEntries = [];
    const entryFilePromises = [];

    // Snapshot items from cd.items (DataTransferItemList)
    if (cd.items && cd.items.length > 0) {
      for (let i = 0; i < cd.items.length; i++) {
        const it = cd.items[i];
        if (!it || it.kind !== 'file') continue;

        // Extract entry synchronously
        let entry = null;
        if (typeof it.webkitGetAsEntry === 'function') {
          try {
            entry = it.webkitGetAsEntry();
          } catch (err) {}
        }

        if (entry) {
          if (entry.isDirectory) {
            dirEntries.push(entry);
            continue; // Skip getAsFile for directories to avoid empty dummy files
          } else if (entry.isFile) {
            // Initiate entry.file() asynchronous read immediately while the DataTransferItem is synchronously active
            entryFilePromises.push(new Promise((resolve) => {
              try {
                entry.file((file) => resolve(file), () => resolve(null));
              } catch (err) {
                resolve(null);
              }
            }));
            continue; // File will be collected via entryFilePromises
          }
        }

        // Also extract file synchronously via getAsFile()
        if (typeof it.getAsFile === 'function') {
          try {
            const file = it.getAsFile();
            if (file) rawFiles.push(file);
          } catch (err) {}
        }
      }
    }

    // Snapshot directly from cd.files (standard FileList source when copying multiple files in Windows Explorer)
    if (cd.files && cd.files.length > 0) {
      for (let i = 0; i < cd.files.length; i++) {
        const file = cd.files[i];
        if (file) rawFiles.push(file);
      }
    }

    // If there are no OS files or directories in clipboard, let staged items handle it or exit
    if (rawFiles.length === 0 && dirEntries.length === 0 && entryFilePromises.length === 0) {
      if (clipboardItems.length > 0 && clipboardMode && !isReadOnly) {
        e.preventDefault();
        closeOsPasteModal();
        executePasteTo(currentPath);
      } else if (document.getElementById('osPasteModal').classList.contains('active')) {
        showToast('ℹ️ No files found in your OS clipboard. Copy files in Windows Explorer first, then press Ctrl+V.', 'danger');
      }
      return;
    }

    // We have OS clipboard files/folders to process! Prevent browser default handling immediately
    e.preventDefault();
    closeOsPasteModal();

    if (isReadOnly) {
      showToast('🔒 Cannot paste files in Read-Only mode', 'danger');
      return;
    }

    // 2. Await all entry.file() promises in parallel
    if (entryFilePromises.length > 0) {
      try {
        const resolvedFiles = await Promise.all(entryFilePromises);
        for (const rf of resolvedFiles) {
          if (rf) rawFiles.push(rf);
        }
      } catch (err) {}
    }

    // 3. Recursively traverse any directory entries found
    const fileEntries = [];
    const emptyDirs = [];
    const dirNames = new Set();
    for (const de of dirEntries) {
      if (de && de.name) dirNames.add(de.name);
    }

    for (const dirEntry of dirEntries) {
      try {
        await traverseEntry(dirEntry, '', fileEntries, emptyDirs);
      } catch (err) {}
    }

    // 4. Normalize and deduplicate standalone files
    const seenFileKeys = new Set();
    for (const fe of fileEntries) {
      const k = (fe.relativePath || fe.file.name) + '_' + (fe.file.size || 0) + '_' + (fe.file.lastModified || 0);
      seenFileKeys.add(k);
    }

    for (let idx = 0; idx < rawFiles.length; idx++) {
      const file = rawFiles[idx];
      if (!file) continue;

      // Skip 0-byte or placeholder files corresponding to directories that were already traversed
      if (dirNames.has(file.name) && (file.size === 0 || file.size === 4096)) {
        continue;
      }

      const normalized = normalizeClipboardFileEntry(file, fileEntries.length);
      if (normalized) {
        const key = (normalized.relativePath || file.name) + '_' + (file.size || 0) + '_' + (file.lastModified || 0);
        if (!seenFileKeys.has(key)) {
          seenFileKeys.add(key);
          fileEntries.push(normalized);
        }
      }
    }

    if (fileEntries.length > 0 || emptyDirs.length > 0) {
      const totalItems = fileEntries.length + emptyDirs.length;
      if (totalItems === 1) {
        const singleName = (fileEntries[0] && fileEntries[0].file) ? fileEntries[0].file.name : 'item';
        showToast('📋 Uploading "' + singleName + '" from OS clipboard...', 'emerald');
      } else {
        showToast('📋 Uploading ' + totalItems + ' items from OS clipboard...', 'emerald');
      }
      await uploadQueue(fileEntries, emptyDirs);
      return;
    } else {
      showToast('ℹ️ No accessible files found in clipboard.', 'amber');
      return;
    }
  }

  // Fallback when cd is null/undefined
  if (clipboardItems.length > 0 && clipboardMode && !isReadOnly) {
    e.preventDefault();
    closeOsPasteModal();
    executePasteTo(currentPath);
  } else if (document.getElementById('osPasteModal').classList.contains('active')) {
    showToast('ℹ️ No files found in your OS clipboard. Copy files in Windows Explorer first, then press Ctrl+V.', 'danger');
  }
});

loadDirectory('/');
sendSessionHeartbeat();
setInterval(sendSessionHeartbeat, 4000);
</script>
</body>
</html>
        """.trimIndent()
    }
}