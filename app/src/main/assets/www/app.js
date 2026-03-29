// Bio Logger - Full Application Logic

// --- Constants & State ---
const STORAGE_KEY = 'bio_logger_data';
let state = {
    surveys: [],
    activeSurveyId: null,
    entries: [],
    settings: {
        themeMode: 'dark', // dark, light, auto
        brightness: 100
    },
    speciesList: ["Quercus robur", "Fagus sylvatica", "Pinus sylvestris", "Betula pendula"],
    lastTelemetry: { temp: 24, hum: 65, elev: 150, loc: 'Detecting...' }
};

let map = null;
let currentMarker = null;
let tempSpeciesEntries = [];
let tempInterceptEntries = [];

// --- Initialization ---
document.addEventListener('DOMContentLoaded', () => {
    try {
        console.log("Initializing Bio Logger...");
        loadData();
        initNavigation();
        initSettings();
        initTelemetry();
        initDataTabs();
        initForms();
        initSwipeNavigation();
        initForestTools();

        // Splash Transition
        setTimeout(() => {
            const splash = document.getElementById('splash-screen');
            const app = document.getElementById('app-container');
            if (splash) splash.classList.remove('active');
            if (app) app.classList.remove('hidden');
            switchScreen('dashboard');
            console.log("Transition complete.");
        }, 2000);

    } catch (err) {
        console.error("Init Error: " + err.message);
        setTimeout(() => {
            const splash = document.getElementById('splash-screen');
            const app = document.getElementById('app-container');
            if (splash) splash.classList.remove('active');
            if (app) app.classList.remove('hidden');
        }, 1000);
    }
});

function setupListener(id, event, callback) {
    const el = document.getElementById(id);
    if (el) el.addEventListener(event, callback);
}

// --- Data Persistence ---
function loadData() {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
        state = Object.assign(state, JSON.parse(saved));
    }
    applyTheme();
    applyBrightness();
    renderArchive();
    renderSpeciesDatalist();
    updateSurveyUI();
}

function saveData() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    renderArchive();
}

// --- Navigation ---
function initNavigation() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', () => switchScreen(item.dataset.screen));
    });

    const btnBack = document.getElementById('btn-back');
    if (btnBack) {
        btnBack.onclick = () => {
            handleBackAction();
        };
    }
}

// System Back Button Bridge
window.handleBackAction = () => {
    const settingsPanel = document.getElementById('settings-panel');
    if (settingsPanel && settingsPanel.classList.contains('open')) {
        settingsPanel.classList.remove('open');
        return;
    }

    const activeSubContent = document.querySelector('.sub-content:not(.hidden)');
    if (activeSubContent) {
        activeSubContent.classList.add('hidden');
        const activeSubTool = document.querySelector('.content-section.active');
        if (activeSubTool) {
            const menu = activeSubTool.querySelector('.sub-menu-grid');
            if (menu) menu.classList.remove('hidden');
        }
        return;
    }

    const activeSection = document.querySelector('.content-section.active');
    if (activeSection && activeSection.id !== 'dashboard') {
        if (activeSection.id === 'forest-capture' || activeSection.id === 'agro-lab' || activeSection.id === 'garden-scape' || activeSection.id === 'note-vault') {
            switchScreen('tools');
        } else {
            switchScreen('dashboard');
        }
    } else {
        if (typeof Android !== 'undefined' && Android.exitApp) {
            Android.exitApp();
        }
    }
};

function switchScreen(id) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    const target = document.getElementById(id);
    if (target) target.classList.add('active');

    document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
    const nav = document.querySelector(`.nav-item[data-screen="${id}"]`);
    if (nav) nav.classList.add('active');

    const btnBack = document.getElementById('btn-back');
    if (btnBack) {
        btnBack.classList.add('hidden');
    }

    if (id === 'data') {
        renderArchive();
        calculateAnalytics();
    }
}

function initSwipeNavigation() {
    let startX = 0;
    const screens = ['dashboard', 'tools', 'data'];
    document.addEventListener('touchstart', e => startX = e.changedTouches[0].screenX);
    document.addEventListener('touchend', e => {
        const endX = e.changedTouches[0].screenX;
        const diff = startX - endX;
        if (Math.abs(diff) < 100) return;

        const activeSubContent = document.querySelector('.sub-content:not(.hidden)');
        if (activeSubContent) return;
        if (document.getElementById('settings-panel') && document.getElementById('settings-panel').classList.contains('open')) return;

        const activeNav = document.querySelector('.nav-item.active');
        if (!activeNav) return;
        const current = activeNav.dataset.screen;
        const idx = screens.indexOf(current);

        if (diff > 0 && idx < screens.length - 1) switchScreen(screens[idx+1]);
        if (diff < 0 && idx > 0) switchScreen(screens[idx-1]);
    });
}

function initDataTabs() {
    document.querySelectorAll('[data-data-tab]').forEach(btn => {
        btn.addEventListener('click', () => {
            const targetId = btn.dataset.dataTab + '-tab';
            document.querySelectorAll('.data-tab-pane').forEach(p => p.classList.remove('active'));
            const targetPane = document.getElementById(targetId);
            if (targetPane) targetPane.classList.add('active');

            document.querySelectorAll('[data-data-tab]').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            if (btn.dataset.dataTab === 'analytics') calculateAnalytics();
        });
    });
}

// --- Telemetry & Weather ---
function initTelemetry() {
    if ("geolocation" in navigator) {
        navigator.geolocation.watchPosition(p => {
            const { latitude, longitude, altitude } = p.coords;
            const gpsEl = document.getElementById('tel-gps');
            const elevEl = document.getElementById('tel-elev');
            if (gpsEl) gpsEl.innerText = `${latitude.toFixed(3)}, ${longitude.toFixed(3)}`;
            if (elevEl) elevEl.innerText = altitude ? `${altitude.toFixed(0)}m` : "150m";

            const liveReadout = document.getElementById('gps-readout');
            if (liveReadout) liveReadout.innerText = `LAT: ${latitude.toFixed(6)} | LNG: ${longitude.toFixed(6)}`;

            updateMap(latitude, longitude);
            fetchLocationName(latitude, longitude);
        }, null, { enableHighAccuracy: true });
    }

    setInterval(() => {
        const iconEl = document.getElementById('header-weather-icon');
        if (!iconEl) return;
        if (!navigator.onLine) { iconEl.style.display = 'none'; return; }
        iconEl.style.display = 'inline-block';
        const hour = new Date().getHours();
        const isNight = hour < 6 || hour > 19;
        const temp = (22 + Math.random() * 4).toFixed(1);
        const hum = (60 + Math.random() * 10).toFixed(0);
        state.lastTelemetry.temp = temp;
        state.lastTelemetry.hum = hum;
        if (document.getElementById('tel-temp')) document.getElementById('tel-temp').innerText = temp + "°C";
        if (document.getElementById('tel-hum')) document.getElementById('tel-hum').innerText = hum + "%";
        iconEl.innerText = isNight ? "🌙" : (hum > 80 ? "🌧️" : (hum > 70 ? "☁️" : "☀️"));
    }, 8000);
}

function updateMap(lat, lng) {
    const container = document.getElementById('f-map-leaflet');
    if (!container) return;
    if (!map) {
        map = L.map('f-map-leaflet').setView([lat, lng], 15);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);
    }
    if (map) {
        map.setView([lat, lng]);
        if (currentMarker) map.removeLayer(currentMarker);
        currentMarker = L.circleMarker([lat, lng], { radius: 8, color: '#00e676', fillColor: '#00e676', fillOpacity: 0.5 }).addTo(map);
    }
}

async function fetchLocationName(lat, lng) {
    try {
        const res = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`);
        const data = await res.json();
        const city = data.address.city || data.address.town || data.address.village || "Unknown Sector";
        const locEl = document.getElementById('tel-location');
        if (locEl) locEl.innerText = city;
        state.lastTelemetry.loc = city;
    } catch(e) {
        if (document.getElementById('tel-location')) document.getElementById('tel-location').innerText = "Zone Alpha";
    }
}

// --- Forest Capture Tools ---
function initForestTools() {
    document.querySelectorAll('#forest-capture .sub-tool-card').forEach(card => {
        card.addEventListener('click', () => {
            const tabId = card.dataset.tab;
            const title = card.querySelector('h4').innerText;
            switchForestPane(tabId, title);
        });
    });

    setupListener('btn-show-surveys', 'click', () => {
        switchForestPane('f-surveys', 'SURVEY MANAGEMENT');
        renderSurveys();
    });

    setupListener('btn-save-new-surv', 'click', () => {
        const name = document.getElementById('surv-name').value;
        if (!name) return alert("Enter Survey Title.");
        const survey = {
            id: Date.now(),
            name,
            investigator: document.getElementById('surv-investigator').value,
            location: document.getElementById('surv-loc').value,
            date: new Date().toLocaleDateString(),
            gps: document.getElementById('tel-gps').innerText
        };
        state.surveys.unshift(survey);
        state.activeSurveyId = survey.id;
        saveData();
        updateSurveyUI();
        alert("Expedition Initialized.");
        document.getElementById('forest-content').classList.add('hidden');
        document.getElementById('forest-menu').classList.remove('hidden');
    });

    // CBI Calculation
    document.querySelectorAll('.cbi-score').forEach(inp => {
        inp.oninput = () => {
            const scores = Array.from(document.querySelectorAll('.cbi-score')).map(i => parseFloat(i.value) || 0);
            const avg = scores.reduce((a,b) => a+b, 0) / scores.length;
            document.getElementById('cbi-res').innerText = avg.toFixed(2);
            document.getElementById('cbi-prog').style.width = (avg / 3 * 100) + "%";
        };
    });

    // One-Tap Auto-fill for Env
    setupListener('btn-autofill-env', 'click', () => {
        document.getElementById('e-weather-temp').value = state.lastTelemetry.temp + "°C";
        document.getElementById('e-weather-hum').value = state.lastTelemetry.hum + "%";
        alert("Environmental Matrix Synced.");
    });

    // Quadrat Logic
    setupListener('btn-add-quad-entry', 'click', () => {
        const name = document.getElementById('qs-name').value;
        if (!name) return;
        tempSpeciesEntries.push({ name, stage: document.getElementById('qs-stage').value, abundance: document.getElementById('qs-abundance').value });
        renderTempQuad();
    });

    setupListener('btn-save-quad-final', 'click', () => {
        saveForestData('Quadrat Plot', { plot: document.getElementById('q-num').value, entries: tempSpeciesEntries });
        tempSpeciesEntries = [];
        renderTempQuad();
    });
}

function switchForestPane(id, title) {
    document.getElementById('forest-menu').classList.add('hidden');
    document.getElementById('forest-content').classList.remove('hidden');
    document.getElementById('forest-tool-title').innerText = title.toUpperCase();
    document.querySelectorAll('#forest-content .tab-pane').forEach(p => p.classList.remove('active'));
    const target = document.getElementById(id);
    if (target) target.classList.add('active');
    if (id === 'f-maps' && map) setTimeout(() => map.invalidateSize(), 200);
}

function renderTempQuad() {
    const list = document.getElementById('qs-temp-list');
    if (list) {
        list.innerHTML = tempSpeciesEntries.map(e => `<div class="glass" style="padding:10px; font-size:0.7rem; margin-bottom:5px"><strong>${e.name}</strong> (${e.stage}) | x${e.abundance}</div>`).join('');
    }
}

function updateSurveyUI() {
    const active = state.surveys.find(s => s.id === state.activeSurveyId);
    const banner = document.getElementById('active-survey-banner');
    if (active) {
        if (banner) banner.classList.remove('hidden');
        const nameDisp = document.getElementById('active-surv-name');
        if (nameDisp) nameDisp.innerText = active.name;
    } else {
        if (banner) banner.classList.add('hidden');
    }
}

function renderSurveys() {
    const list = document.getElementById('surveys-list-container');
    if (list) {
        list.innerHTML = state.surveys.map(s => `
            <div class="glass" style="padding:15px; margin-bottom:10px; border-left: 4px solid ${s.id === state.activeSurveyId ? 'var(--primary)' : 'transparent'}">
                <div style="display:flex; justify-content:space-between; align-items:center">
                    <div>
                        <strong>${s.name}</strong><br>
                        <small style="opacity:0.6">${s.date} | ${s.investigator}</small>
                    </div>
                    <button class="secondary-btn" style="width:auto; padding:5px 15px" onclick="setActiveSurvey(${s.id})">SET ACTIVE</button>
                </div>
            </div>
        `).join('');
    }
}

window.setActiveSurvey = (id) => {
    state.activeSurveyId = id;
    saveData();
    updateSurveyUI();
    renderSurveys();
};

function saveForestData(type, data) {
    const entry = {
        id: Date.now(),
        surveyId: state.activeSurveyId,
        module: 'forest',
        title: type,
        data,
        timestamp: new Date().toLocaleString(),
        loc: state.lastTelemetry.loc
    };
    state.entries.unshift(entry);
    saveData();
    alert("Field Data Synchronized.");
    document.getElementById('forest-content').classList.add('hidden');
    document.getElementById('forest-menu').classList.remove('hidden');
}

// --- Tools Logic ---
function initForms() {
    document.querySelectorAll('.tool-card').forEach(card => {
        if (!card.classList.contains('sub-tool-card')) {
            card.onclick = () => openTool(card.dataset.tool);
        }
    });

    document.querySelectorAll('.sub-tool-card').forEach(card => {
        card.addEventListener('click', () => {
            const parent = card.closest('.sub-tool');
            const menu = parent.querySelector('.sub-menu-grid');
            const content = parent.querySelector('.sub-content');
            const tabId = card.dataset.tab;

            if (menu) menu.classList.add('hidden');
            if (content) {
                content.classList.remove('hidden');
                content.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
                const target = document.getElementById(tabId);
                if (target) target.classList.add('active');
            }
        });
    });
}

function openTool(toolId) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    const map = { 'forest': 'forest-capture', 'agro': 'agro-lab', 'garden': 'garden-scape', 'note': 'note-vault' };
    const target = document.getElementById(map[toolId]);
    if (target) target.classList.add('active');
}

// --- Archive & Settings ---
function renderArchive() {
    const list = document.getElementById('unified-data-list');
    if (!list) return;
    list.innerHTML = '';
    const filterEl = document.getElementById('data-filter-tool');
    const filter = filterEl ? filterEl.value : 'all';
    const filtered = state.entries.filter(e => filter === 'all' || e.module === filter);
    if (filtered.length === 0) { list.innerHTML = '<div class="glass" style="padding:40px; text-align:center; opacity:0.5;">No records found.</div>'; return; }
    filtered.forEach(e => {
        const item = document.createElement('div');
        item.className = 'data-item glass';
        item.style.padding = '15px'; item.style.marginBottom = '10px';
        item.innerHTML = `<div style="display:flex; justify-content:space-between; align-items:start;"><div><strong style="color:var(--primary);">${e.title}</strong><br><small style="opacity:0.6; font-size:0.7rem;">${e.module.toUpperCase()} | ${e.timestamp}</small></div><button onclick="deleteEntry(${e.id})" style="background:none; border:none; color:var(--danger); font-size:0.7rem;">DELETE</button></div>`;
        list.appendChild(item);
    });
}

function deleteEntry(id) {
    if (confirm("Permanently erase record?")) {
        state.entries = state.entries.filter(e => e.id !== id);
        saveData();
        renderArchive();
        calculateAnalytics();
    }
}

function calculateAnalytics() {
    const forest = state.entries.filter(e => e.module === 'forest');
    const countEl = document.getElementById('stat-forest-count');
    if (countEl) countEl.innerText = forest.length;
}

function initSettings() {
    const panel = document.getElementById('settings-panel');
    setupListener('btn-settings', 'click', () => { if (panel) panel.classList.add('open'); });
    setupListener('btn-settings-close', 'click', () => { if (panel) panel.classList.remove('open'); });
    const mode = document.getElementById('pref-theme-mode');
    if (mode) {
        mode.value = state.settings.themeMode;
        mode.onchange = (e) => { state.settings.themeMode = e.target.value; applyTheme(); saveData(); };
    }
}

function applyTheme() {
    const m = state.settings.themeMode;
    if (m === 'light') { document.body.classList.add('light-theme'); document.body.classList.remove('dark-theme'); }
    else { document.body.classList.add('dark-theme'); document.body.classList.remove('light-theme'); }
}

function applyBrightness() { document.body.style.filter = `brightness(${state.settings.brightness}%)`; }
function renderSpeciesDatalist() {
    const dl = document.getElementById('species-list');
    if (!dl) return;
    dl.innerHTML = '';
    state.speciesList.forEach(s => { const o = document.createElement('option'); o.value = s; dl.appendChild(o); });
}
