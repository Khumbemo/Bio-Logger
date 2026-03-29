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
let tempTransectEntries = [];
let iviChart = null;
let germChart = null;
let compareChart = null;
let tempGermEntries = [];

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
        initAgroTools();
        initGardenTools();
        initNoteVault();

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
        const activeSubTool = document.querySelector('.content-section.active');
        if (activeSubTool) {
            const menu = activeSubTool.querySelector('.sub-menu-grid');
            const content = activeSubTool.querySelector('.sub-content');
            if (menu) menu.classList.remove('hidden');
            if (content) content.classList.add('hidden');

            // Re-update header title for the module
            updateHeaderTheme(activeSubTool.id);
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

function updateHeaderTheme(id) {
    console.log("Updating header theme for: " + id);
    const header = document.querySelector('#app-container > header');
    const title = document.getElementById('header-title');
    if (!header || !title) return;

    header.classList.remove('forest-theme', 'agro-theme', 'garden-theme');
    title.innerText = 'BIO LOGGER';

    if (id === 'forest-capture') {
        header.classList.add('forest-theme');
        title.innerText = 'FOREST CAPTURE';
    } else if (id === 'agro-lab') {
        header.classList.add('agro-theme');
        title.innerText = 'AGROCLIMATIC LAB';
    } else if (id === 'garden-scape') {
        header.classList.add('garden-theme');
        title.innerText = 'GARDEN SCAPE';
    }
}

function switchScreen(id) {
    const activeSection = document.querySelector('.content-section.active');
    if (activeSection) {
        activeSection.dataset.scrollPos = activeSection.scrollTop;
    }

    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    const target = document.getElementById(id);
    if (target) {
        target.classList.add('active');
        updateHeaderTheme(id);
        if (target.dataset.scrollPos) {
            target.scrollTop = target.dataset.scrollPos;
        }
    }

    document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
    const nav = document.querySelector(`.nav-item[data-screen="${id}"]`);
    if (nav) nav.classList.add('active');

    if (id === 'data') {
        renderArchive();
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
        const inv = document.getElementById('surv-investigator').value;
        const loc = document.getElementById('surv-loc').value;
        if (!name || !inv || !loc) return showSyncToast("Complete all metadata fields.");
        const survey = {
            id: Date.now(),
            name,
            investigator: inv,
            location: loc,
            date: new Date().toLocaleDateString(),
            gps: document.getElementById('tel-gps').innerText
        };
        state.surveys.unshift(survey);
        state.activeSurveyId = survey.id;
        saveData();
        updateSurveyUI();
        showSyncToast("Expedition Initialized.");
        document.getElementById('forest-content').classList.add('hidden');
        document.getElementById('forest-menu').classList.remove('hidden');
    });

    // CBI Calculation
    document.querySelectorAll('.cbi-score').forEach(inp => {
        inp.oninput = () => {
            const scores = Array.from(document.querySelectorAll('.cbi-score')).map(i => Math.min(3, Math.max(0, parseFloat(i.value) || 0)));
            const avg = scores.reduce((a,b) => a+b, 0) / scores.length;
            document.getElementById('cbi-res').innerText = avg.toFixed(2);

            let sev = "UNBURNED";
            if (avg > 0 && avg <= 1) sev = "LOW SEVERITY";
            else if (avg > 1 && avg <= 2) sev = "MODERATE SEVERITY";
            else if (avg > 2) sev = "HIGH SEVERITY";
            document.getElementById('cbi-severity').innerText = sev;
        };
    });

    setupListener('btn-sync-q-gps', 'click', () => {
        document.getElementById('q-gps').value = document.getElementById('tel-gps').innerText;
    });

    setupListener('btn-save-wp', 'click', () => {
        const name = document.getElementById('wp-name').value;
        const type = document.getElementById('wp-type').value;
        if (!name) return showSyncToast("Enter Waypoint Name.");
        saveForestData('GPS Waypoint', {
            name,
            type,
            gps: document.getElementById('tel-gps').innerText
        });
        document.getElementById('wp-name').value = '';
    });

    // One-Tap Auto-fill for Env
    setupListener('btn-autofill-env', 'click', () => {
        const t = document.getElementById('e-weather-temp');
        const h = document.getElementById('e-weather-hum');
        if (t) t.value = state.lastTelemetry.temp + "°C";
        if (h) h.value = state.lastTelemetry.hum + "%";
        showSyncToast("Environmental Matrix Synced.");
    });

    setupListener('btn-save-env-data', 'click', () => {
        const slope = document.getElementById('e-slope').value;
        const aspect = document.getElementById('e-aspect').value;
        const canopy = document.getElementById('e-canopy').value;
        const soil = document.getElementById('e-soil-type').value;

        if (!slope || !aspect || !canopy || !soil) return showSyncToast("Complete all environmental fields.");

        saveForestData('Environmental Matrix', {
            temp: document.getElementById('e-weather-temp').value,
            hum: document.getElementById('e-weather-hum').value,
            slope, aspect, canopy, soil
        });
    });

    // Quadrat Logic
    setupListener('btn-add-qs', 'click', () => {
        const name = document.getElementById('qs-name').value;
        const count = parseInt(document.getElementById('qs-abundance').value) || 0;
        const dbh = parseFloat(document.getElementById('qs-dbh').value) || 0;
        if (!name || count <= 0) return showSyncToast("Enter species name and count.");

        tempSpeciesEntries.push({
            name,
            stage: document.getElementById('qs-stage').value,
            abundance: count,
            dbh: dbh
        });

        if (!state.speciesList.includes(name)) {
            state.speciesList.push(name);
            saveData();
            renderSpeciesDatalist();
        }

        renderTempQuad();
        calculateQuadStats();
    });

    setupListener('btn-final-save-quad', 'click', () => {
        if (tempSpeciesEntries.length === 0) return showSyncToast("No data to save.");
        saveForestData('Quadrat Plot', {
            plot: document.getElementById('q-num').value,
            size: document.getElementById('q-size').value,
            entries: tempSpeciesEntries,
            stats: calculateQuadStats()
        });
        tempSpeciesEntries = [];
        renderTempQuad();
        document.getElementById('quad-results-box').classList.add('hidden');
    });

    // Transect Logic
    setupListener('btn-init-transect', 'click', () => {
        document.getElementById('transect-work-area').classList.remove('hidden');
        tempTransectEntries = [];
        renderTempTransect();
    });

    setupListener('btn-add-ts', 'click', () => {
        const species = document.getElementById('ts-species').value;
        const dist = document.getElementById('ts-dist').value;
        if (!species || !dist) return;
        tempTransectEntries.push({ species, dist });
        renderTempTransect();
    });

    setupListener('btn-save-transect-final', 'click', () => {
        saveForestData('Belt Transect', {
            length: document.getElementById('t-len').value,
            bearing: document.getElementById('t-bearing').value,
            points: tempTransectEntries
        });
        document.getElementById('transect-work-area').classList.add('hidden');
    });

    // CBI Save
    setupListener('btn-save-cbi', 'click', () => {
        const res = document.getElementById('cbi-res').innerText;
        const sev = document.getElementById('cbi-severity').innerText;
        saveForestData('CBI Analysis', {
            index: res,
            severity: sev,
            strata: Array.from(document.querySelectorAll('.cbi-score')).map(i => ({ name: i.dataset.strata, val: i.value }))
        });
    });

    // Herbarium
    setupListener('btn-save-herb', 'click', () => {
        const binomial = document.getElementById('h-binomial').value;
        const family = document.getElementById('h-family').value;
        if (!binomial || !family) return showSyncToast("Enter Binomial and Family.");
        saveForestData('Herbarium Voucher', {
            binomial,
            family: family,
            notes: document.getElementById('h-notes').value,
            specimenID: document.getElementById('herb-id-box').innerText
        });
        document.getElementById('h-binomial').value = '';
        document.getElementById('h-family').value = '';
        document.getElementById('h-notes').value = '';
    });

    setupListener('btn-herb-photo', 'click', () => {
        document.getElementById('herb-photo-preview').classList.remove('hidden');
        showSyncToast("Camera initialized (Simulated)");
    });
}

function calculateQuadStats() {
    if (tempSpeciesEntries.length === 0) return;
    const size = parseFloat(document.getElementById('q-size').value) || 100;

    // Group by species
    const speciesData = {};
    tempSpeciesEntries.forEach(e => {
        if (!speciesData[e.name]) speciesData[e.name] = { count: 0, ba: 0 };
        speciesData[e.name].count += e.abundance;
        // Basal Area = (pi * (d/2)^2) / 10000 -> m2
        if (e.dbh > 0) {
            const area = (Math.PI * Math.pow(e.dbh/2, 2)) / 10000;
            speciesData[e.name].ba += (area * e.abundance);
        }
    });

    const totalN = tempSpeciesEntries.reduce((a,b) => a + b.abundance, 0);
    const names = Object.keys(speciesData);

    // Shannon-Wiener
    let shannon = 0;
    names.forEach(n => {
        const p = speciesData[n].count / totalN;
        if (p > 0) shannon -= p * Math.log(p);
    });

    // Simpson's (D)
    let simpsonSum = 0;
    names.forEach(n => {
        const ni = speciesData[n].count;
        simpsonSum += (ni * (ni - 1));
    });
    const simpson = totalN > 1 ? 1 - (simpsonSum / (totalN * (totalN - 1))) : 0;

    // IVI Logic (Simplified for single plot)
    // In single plot: IVI = Rel Density + Rel Dominance (Rel Frequency needs multiple plots)
    let totalBA = 0;
    names.forEach(n => totalBA += speciesData[n].ba);

    const iviData = names.map(n => {
        const relDensity = (speciesData[n].count / totalN) * 100;
        const relDom = totalBA > 0 ? (speciesData[n].ba / totalBA) * 100 : 0;
        return { name: n, ivi: (relDensity + relDom).toFixed(2) };
    }).sort((a,b) => b.ivi - a.ivi);

    document.getElementById('res-shannon').innerText = shannon.toFixed(3);
    document.getElementById('res-simpson').innerText = simpson.toFixed(3);
    document.getElementById('quad-results-box').classList.remove('hidden');

    updateIVIChart(iviData);

    return { shannon, simpson, iviData };
}

function updateIVIChart(data) {
    const ctx = document.getElementById('chart-ivi');
    if (!ctx) return;

    if (iviChart) iviChart.destroy();

    iviChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.slice(0, 5).map(d => d.name),
            datasets: [{
                label: 'Importance Value Index (Rel D + Rel Dom)',
                data: data.slice(0, 5).map(d => d.ivi),
                backgroundColor: 'rgba(0, 230, 118, 0.5)',
                borderColor: '#00e676',
                borderWidth: 1
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            scales: { x: { beginAtZero: true, grid: { display: false } }, y: { grid: { display: false } } },
            plugins: { legend: { display: false } }
        }
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
        list.innerHTML = tempSpeciesEntries.map(e => `
            <div class="glass" style="padding:10px; font-size:0.7rem; margin-bottom:5px; display:flex; justify-content:space-between;">
                <span><strong>${e.name}</strong> (${e.stage}) | x${e.abundance} ${e.dbh ? '| '+e.dbh+'cm' : ''}</span>
                <span style="color:var(--danger)" onclick="removeTempQs('${e.name}')">✕</span>
            </div>
        `).join('');
    }
}

window.removeTempQs = (name) => {
    tempSpeciesEntries = tempSpeciesEntries.filter(e => e.name !== name);
    renderTempQuad();
    calculateQuadStats();
};

function renderTempTransect() {
    const list = document.getElementById('ts-temp-list');
    if (list) {
        list.innerHTML = tempTransectEntries.map(e => `<div class="glass" style="padding:10px; font-size:0.7rem; margin-bottom:5px">${e.dist}m: <strong>${e.species}</strong></div>`).join('');
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
    showSyncToast("Field Data Synchronized.");
    document.getElementById('forest-content').classList.add('hidden');
    document.getElementById('forest-menu').classList.remove('hidden');
}

// --- Tools Logic ---
function initGardenTools() {
    setupListener('btn-gm-calc', 'click', () => {
        const l = parseFloat(document.getElementById('gm-len').value) || 0;
        const w = parseFloat(document.getElementById('gm-wid').value) || 0;
        if (l <= 0 || w <= 0) return showSyncToast("Enter valid dimensions.");
        const area = l * w;
        const areaEl = document.getElementById('gm-area');
        if (areaEl) areaEl.innerText = area.toFixed(2) + " m²";
        const resEl = document.getElementById('gm-res');
        if (resEl) resEl.classList.remove('hidden');
        saveGardenData('Field Mapping', { length: l, width: w, area: area });
    });

    setupListener('btn-gy-calc', 'click', () => {
        const crop = document.getElementById('gy-crop').value;
        const area = parseFloat(document.getElementById('gy-area').value) || 0;
        const constant = parseFloat(document.getElementById('gy-const').value) || 0;
        if (!crop || area <= 0 || constant <= 0) return showSyncToast("Complete all yield fields.");
        const yieldVal = area * constant;
        const valEl = document.getElementById('gy-val');
        if (valEl) valEl.innerText = yieldVal.toFixed(2) + " kg";
        const resBox = document.getElementById('gy-res-box');
        if (resBox) resBox.classList.remove('hidden');
        saveGardenData('Yield Estimation', { crop: crop, area, constant, yield: yieldVal });
    });

    const rotHintEl = document.getElementById('gr-hint');
    const rotSelect = document.getElementById('gr-year');
    if (rotSelect) {
        rotSelect.onchange = () => {
            const hints = {
                "1": "Legumes (Beans, Peas, Clover) - Fixes Nitrogen.",
                "2": "Brassicas (Cabbage, Broccoli, Kale) - Uses Nitrogen.",
                "3": "Alliums (Onion, Garlic, Leek) & Roots (Carrots).",
                "4": "Solanaceous (Potato, Tomato, Pepper) - Higher risk of pests."
            };
            rotHintEl.innerText = hints[rotSelect.value];
        };
        rotSelect.onchange();
    }

    setupListener('btn-gr-save', 'click', () => {
        saveGardenData('Crop Rotation', { year: rotSelect.value, hint: rotHintEl.innerText });
    });

    setupListener('btn-gl-calc', 'click', () => {
        const r = parseFloat(document.getElementById('gl-row').value) || 1;
        const p = parseFloat(document.getElementById('gl-plant').value) || 1;
        const area = parseFloat(document.getElementById('gl-area').value) || 0;
        const capacity = Math.floor((area * 10000) / (r * p));
        const valEl = document.getElementById('gl-val');
        if (valEl) valEl.innerText = capacity;
        const resBox = document.getElementById('gl-res-box');
        if (resBox) resBox.classList.remove('hidden');
    });

    setupListener('btn-gs-save', 'click', () => {
        const ph = document.getElementById('gs-ph').value;
        const texture = document.getElementById('gs-texture').value;
        if (!ph || !texture) return showSyncToast("Complete all soil fields.");
        saveGardenData('Soil Condition', { ph: ph, texture: texture });
    });

    setupListener('btn-gs-match', 'click', () => {
        const zone = document.getElementById('gs-zone').value;
        const season = document.getElementById('gs-season').value;
        const recommendations = {
            '1-3': { 'spring': 'Radish, Spinach, Peas', 'summer': 'Potato, Cabbage', 'autumn': 'Kale, Garlic', 'winter': 'Indoor Herbs' },
            '4-6': { 'spring': 'Lettuce, Carrots', 'summer': 'Tomato, Cucumber', 'autumn': 'Beets, Broccoli', 'winter': 'Cover Crops' },
            '7-9': { 'spring': 'Okra, Eggplant', 'summer': 'Sweet Potato, Peppers', 'autumn': 'Onions, Beans', 'winter': 'Cabbage' },
            '10+': { 'spring': 'Ginger, Turmeric', 'summer': 'Tropical Fruits', 'autumn': 'Sweet Potato', 'winter': 'All Year Crops' }
        };
        const resList = document.getElementById('gs-match-list');
        resList.innerText = recommendations[zone][season] || "Consult local guide.";
        document.getElementById('gs-match-res').classList.remove('hidden');
    });

    setupListener('btn-gp-check', 'click', () => {
        const crop = document.getElementById('gp-crop').value.toLowerCase();
        const companionData = {
            'tomato': { good: 'Basil, Marigold, Carrots', bad: 'Potato, Fennel, Cabbage' },
            'carrot': { good: 'Tomato, Onion, Leek', bad: 'Dill, Parsnip' },
            'potato': { good: 'Beans, Corn, Cabbage', bad: 'Tomato, Cucumber, Sunflower' }
        };
        const res = companionData[crop] || { good: 'General companions apply.', bad: 'No specific avoidances known.' };
        document.getElementById('gp-good').innerText = "✓ COMPANIONS: " + res.good;
        document.getElementById('gp-bad').innerText = "✕ AVOID: " + res.bad;
        document.getElementById('gp-res').classList.remove('hidden');
    });

    setupListener('btn-gt-save', 'click', () => {
        const crop = document.getElementById('gt-crop').value;
        const stage = document.getElementById('gt-stage').value;
        if (!crop) return showSyncToast("Enter Crop ID.");
        saveGardenData('Life Cycle Stage', { crop, stage });
    });

    setupListener('gw-search', 'input', (e) => {
        const q = e.target.value.toLowerCase();
        const wiki = {
            'tomato': 'Solanum lycopersicum. Optimal pH: 6.0-6.8. Requires 6-8 hours of sunlight.',
            'carrot': 'Daucus carota. Deep, well-drained sandy loam required. pH 6.0-6.5.',
            'kale': 'Brassica oleracea. Cold-hardy biennial. Rich in Vitamins A, C, K.'
        };
        document.getElementById('gw-res').innerText = wiki[q] || "No research data found for this taxon.";
    });
}

function initNoteVault() {
    setupListener('btn-nv-voice', 'click', () => {
        if (typeof Android !== 'undefined' && Android.startVoiceRecognition) {
            Android.startVoiceRecognition();
        } else {
            showSyncToast("Voice recognition only supported in Android app mode.");
        }
    });

    window.onVoiceResult = (text) => {
        const area = document.getElementById('nv-text');
        if (area) {
            const current = area.value;
            area.value = current ? current + " " + text : text;
        }
    };

    setupListener('btn-nv-save', 'click', () => {
        const text = document.getElementById('nv-text').value;
        const tag = document.getElementById('nv-tag').value;
        if (!text) return;
        const entry = {
            id: Date.now(),
            module: 'note',
            title: tag ? `Observation [${tag}]` : 'Neural Observation',
            data: { text, tag },
            timestamp: new Date().toLocaleString()
        };
        state.entries.unshift(entry);
        saveData();
        document.getElementById('nv-text').value = '';
        document.getElementById('nv-tag').value = '';
        showSyncToast("Observation Synchronized.");
    });
}

function saveGardenData(type, data) {
    const entry = {
        id: Date.now(),
        module: 'garden',
        title: type,
        data,
        timestamp: new Date().toLocaleString()
    };
    state.entries.unshift(entry);
    saveData();
    showSyncToast("Garden Matrix Synchronized.");
}

function showSyncToast(msg) {
    if (typeof Android !== 'undefined' && Android.showToast) {
        Android.showToast(msg);
    } else {
        alert(msg);
    }
}

function initAgroTools() {
    setupListener('btn-add-germ-day', 'click', () => {
        const day = parseInt(document.getElementById('ag-day-n').value);
        const count = parseInt(document.getElementById('ag-count-n').value);
        if (isNaN(day) || isNaN(count)) return showSyncToast("Invalid data.");
        tempGermEntries.push({ day, count });
        tempGermEntries.sort((a,b) => a.day - b.day);
        renderTempGerm();
        calculateGermStats();
    });

    setupListener('btn-save-germ-final', 'click', () => {
        const seed = document.getElementById('ag-seed-type').value;
        const total = document.getElementById('ag-seed-count').value;
        if (!seed || !total || tempGermEntries.length === 0) return showSyncToast("No data to save.");
        saveAgroData('Germination Study', {
            seed: seed,
            total: total,
            entries: tempGermEntries,
            stats: calculateGermStats()
        });
        tempGermEntries = [];
        renderTempGerm();
        document.getElementById('germ-results-box').classList.add('hidden');
    });

    setupListener('btn-save-pot-init', 'click', () => {
        const id = document.getElementById('ap-id').value;
        const seed = document.getElementById('ap-seed').value;
        const soil = document.getElementById('ap-soil').value;
        const date = document.getElementById('ap-date').value;
        if (!id || !seed || !soil || !date) return showSyncToast("Complete all pot fields.");
        saveAgroData('Pot Initiation', { id, seed, soil, date });
        showSyncToast("Pot Study Initialized.");
    });

    setupListener('btn-save-growth', 'click', () => {
        const sample = document.getElementById('agro-sample-id').value;
        const h = document.getElementById('agro-height').value;
        const l = document.getElementById('agro-leaves').value;
        if (!sample || !h || !l) return showSyncToast("Complete all growth fields.");
        saveAgroData('Growth Metrics', { sample, height: h, leaves: l });
    });

    setupListener('btn-save-agro-env', 'click', () => {
        const co2 = document.getElementById('ae-co2').value;
        const lux = document.getElementById('ae-lux').value;
        const moist = document.getElementById('ae-soil-moist').value;
        if (!co2 || !lux || !moist) return showSyncToast("Complete all environmental fields.");
        saveAgroData('Env Monitor', {
            co2: co2,
            lux: lux,
            soilMoist: moist
        });
    });

    setupListener('btn-save-agro-control', 'click', () => {
        const target = document.getElementById('ac-target').value;
        const type = document.getElementById('ac-type').value;
        const amount = document.getElementById('ac-amount').value;
        if (!target || !amount) return showSyncToast("Complete all input fields.");
        saveAgroData('Input Control', { target, type, amount });
    });

    setupListener('btn-agro-compare', 'click', () => {
        const t1 = document.getElementById('ac-t1').value;
        const t2 = document.getElementById('ac-t2').value;
        const metric = document.getElementById('ac-comp-metric').value;
        if (!t1 || !t2) return showSyncToast("Enter Treatment IDs.");

        const data1 = state.entries.filter(e => e.module === 'agro' && (e.data.sample === t1 || (e.data.seed === t1))).map(e => parseFloat(e.data[metric] || (e.data.stats ? e.data.stats.rate : 0)));
        const data2 = state.entries.filter(e => e.module === 'agro' && (e.data.sample === t2 || (e.data.seed === t2))).map(e => parseFloat(e.data[metric] || (e.data.stats ? e.data.stats.rate : 0)));

        const v1 = data1.length ? (data1.reduce((a,b) => a+b, 0) / data1.length) : 0;
        const v2 = data2.length ? (data2.reduce((a,b) => a+b, 0) / data2.length) : 0;

        document.getElementById('agro-compare-results').classList.remove('hidden');
        updateAgroCompareChart(t1, t2, v1, v2, metric);
    });

    setupListener('btn-refresh-agro-viz', 'click', () => {
        const ctx = document.getElementById('chart-agro-summary');
        if (!ctx) return;

        const agroEntries = state.entries.filter(e => e.module === 'agro');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Germination', 'Growth', 'Pot Info', 'Env', 'Control'],
                datasets: [{
                    data: [
                        agroEntries.filter(e => e.title.includes('Germ')).length,
                        agroEntries.filter(e => e.title.includes('Growth')).length,
                        agroEntries.filter(e => e.title.includes('Pot')).length,
                        agroEntries.filter(e => e.title.includes('Env')).length,
                        agroEntries.filter(e => e.title.includes('Control')).length
                    ],
                    backgroundColor: ['#00e676', '#00b0ff', '#d500f9', '#ffea00', '#ff5252']
                }]
            },
            options: { responsive: true, plugins: { legend: { position: 'bottom', labels: { color: '#fff' } } } }
        });
    });
}

function updateAgroCompareChart(t1, t2, v1, v2, label) {
    const ctx = document.getElementById('chart-agro-compare');
    if (!ctx) return;
    if (compareChart) compareChart.destroy();

    compareChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: [t1, t2],
            datasets: [{
                label: label.toUpperCase(),
                data: [v1, v2],
                backgroundColor: ['rgba(0, 176, 255, 0.5)', 'rgba(213, 0, 249, 0.5)'],
                borderColor: ['#00b0ff', '#d500f9'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: { y: { beginAtZero: true } },
            plugins: { legend: { display: false } }
        }
    });
}

function calculateGermStats() {
    if (tempGermEntries.length === 0) return;
    const totalSeeds = parseInt(document.getElementById('ag-seed-count').value) || 0;
    if (totalSeeds === 0) return;

    const totalGerminated = tempGermEntries.reduce((a,b) => a + b.count, 0);
    const rate = (totalGerminated / totalSeeds) * 100;

    // MGT = sum(ni * di) / sum(ni)
    const sumNiDi = tempGermEntries.reduce((a,b) => a + (b.count * b.day), 0);
    const mgt = totalGerminated > 0 ? sumNiDi / totalGerminated : 0;

    document.getElementById('res-germ-rate').innerText = rate.toFixed(1) + "%";
    document.getElementById('res-germ-mgt').innerText = mgt.toFixed(2);
    document.getElementById('germ-results-box').classList.remove('hidden');

    updateGermChart();
    return { rate, mgt };
}

function updateGermChart() {
    const ctx = document.getElementById('chart-germ');
    if (!ctx) return;
    if (germChart) germChart.destroy();

    // Cumulative sum
    let cum = 0;
    const labels = tempGermEntries.map(e => "D" + e.day);
    const data = tempGermEntries.map(e => {
        cum += e.count;
        return cum;
    });

    germChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Cumulative Germination',
                data: data,
                borderColor: '#00b0ff',
                backgroundColor: 'rgba(0, 176, 255, 0.1)',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            scales: { x: { grid: { display: false } }, y: { beginAtZero: true } },
            plugins: { legend: { display: false } }
        }
    });
}

function renderTempGerm() {
    const list = document.getElementById('germ-temp-list');
    if (list) {
        list.innerHTML = tempGermEntries.map(e => `<div class="glass" style="padding:5px 10px; font-size:0.7rem; margin-bottom:5px; display:inline-block; margin-right:5px;">D${e.day}: ${e.count}</div>`).join('');
    }
}

function saveAgroData(type, data) {
    const entry = {
        id: Date.now(),
        module: 'agro',
        title: type,
        data,
        timestamp: new Date().toLocaleString()
    };
    state.entries.unshift(entry);
    saveData();
    showSyncToast("Lab Data Synchronized.");
}

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
            const toolTitle = card.querySelector('h4').innerText;

            if (menu) menu.classList.add('hidden');
            if (content) {
                content.classList.remove('hidden');
                content.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
                const target = document.getElementById(tabId);
                if (target) target.classList.add('active');

                // Update Header Title to Sub-Tool Name
                const headerTitle = document.getElementById('header-title');
                if (headerTitle) headerTitle.innerText = toolTitle.toUpperCase();
            }
        });
    });
}

function openTool(toolId) {
    const map = { 'forest': 'forest-capture', 'agro': 'agro-lab', 'garden': 'garden-scape', 'note': 'note-vault' };
    switchScreen(map[toolId]);
}

// --- Archive & Settings ---
function renderArchive() {
    const list = document.getElementById('unified-data-list');
    if (!list) return;
    list.innerHTML = '';
    const filterEl = document.getElementById('data-filter-tool');
    const filter = filterEl ? filterEl.value : 'all';
    const filtered = state.entries.filter(e => filter === 'all' || e.module === filter);

    const countEl = document.getElementById('stat-total-count');
    if (countEl) countEl.innerText = state.entries.length;

    if (filtered.length === 0) {
        list.innerHTML = '<div class="glass" style="padding:40px; text-align:center; opacity:0.5;">No records found.</div>';
        return;
    }

    filtered.forEach(e => {
        const item = document.createElement('div');
        item.className = 'data-item glass';
        item.style.padding = '15px'; item.style.marginBottom = '10px';
        item.innerHTML = `
            <div style="display:flex; justify-content:space-between; align-items:start;">
                <div>
                    <strong style="color:var(--primary);">${e.title}</strong><br>
                    <small style="opacity:0.6; font-size:0.7rem;">${e.module.toUpperCase()} | ${e.timestamp}</small>
                </div>
                <button onclick="deleteEntry(${e.id})" style="background:none; border:none; color:var(--danger); font-size:0.7rem;">DELETE</button>
            </div>`;
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
    setupListener('data-filter-tool', 'change', () => renderArchive());
    setupListener('btn-export-xlsx', 'click', () => exportToXLSX());
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
function exportToXLSX() {
    if (state.entries.length === 0) return showSyncToast("No data to export.");
    const flatData = state.entries.map(e => {
        const row = {
            Timestamp: e.timestamp,
            Module: e.module,
            Title: e.title,
            Location: e.loc || ""
        };
        // Flatten data object
        Object.keys(e.data).forEach(k => {
            if (typeof e.data[k] === 'object') {
                row[k] = JSON.stringify(e.data[k]);
            } else {
                row[k] = e.data[k];
            }
        });
        return row;
    });

    const ws = XLSX.utils.json_to_sheet(flatData);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "BioLogger_Data");
    XLSX.writeFile(wb, `BioLogger_Export_${Date.now()}.xlsx`);
}

function renderSpeciesDatalist() {
    const dl = document.getElementById('species-list');
    if (!dl) return;
    dl.innerHTML = '';
    state.speciesList.forEach(s => { const o = document.createElement('option'); o.value = s; dl.appendChild(o); });
}
