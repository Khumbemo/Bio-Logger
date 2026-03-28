// Bio Logger - Full Application Logic

// --- Constants & State ---
const STORAGE_KEY = 'bio_logger_data';
let state = {
    surveys: [],
    activeId: null,
    settings: {
        darkMode: true,
        coordsFormat: 'DD',
        gpsContinuous: false
    },
    user: null,
    lastCoords: null,
    speciesList: ["Quercus robur", "Fagus sylvatica", "Pinus sylvestris", "Betula pendula"]
};

// --- Initialization ---
document.addEventListener('DOMContentLoaded', () => {
    try {
        loadData();
        initNavigation();
        initSettings();
        initTelemetry();
        initSwipeNavigation();

        // Splash Transition
        setTimeout(() => {
            const splash = document.getElementById('splash-screen');
            if (splash) splash.classList.remove('active');
            if (state.user) {
                showApp();
            } else {
                const loginScr = document.getElementById('login-screen');
                if (loginScr) loginScr.classList.add('active');
            }
        }, 2000);

        // Global Listeners
        setupListener('btn-login', 'click', () => login('user'));
        setupListener('btn-guest', 'click', () => login('guest'));

        // Tool Selection
        document.querySelectorAll('.tool-card').forEach(card => {
            card.addEventListener('click', () => {
                if (card.dataset.tool) openTool(card.dataset.tool);
            });
        });

        // Sub-tool Cards
        document.querySelectorAll('.sub-tool-card').forEach(card => {
            card.addEventListener('click', () => {
                const parent = card.closest('.content-section');
                const menu = parent.querySelector('.sub-menu-grid');
                const content = parent.querySelector('.sub-content');
                const tabId = card.dataset.tab;

                if (menu) menu.classList.add('hidden');
                if (content) content.classList.remove('hidden');

                const tabBtn = content.querySelector(`.tab-btn[data-tab="${tabId}"]`);
                if (tabBtn) switchSubTab(tabId, tabBtn);
            });
        });

        // Tab Buttons
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', () => switchSubTab(btn.dataset.tab, btn));
        });

        // New Survey Buttons
        document.querySelectorAll('.btn-new-survey-tool').forEach(btn => {
            btn.addEventListener('click', createNewSurvey);
        });

        // --- Save Logic ---
        setupListener('btn-nv-save', 'click', saveNote);
        setupListener('btn-nv-voice', 'click', startVoiceRecording);
        setupListener('btn-fq-save', 'click', saveQuadrat);
        setupListener('btn-ft-save', 'click', saveTransect);
        setupListener('btn-fe-save', 'click', saveForestEnv);

        // Agro Lab Saves
        setupListener('btn-ag-save', 'click', saveAgroTrial);
        setupListener('btn-ap-save', 'click', saveAgroPhenology);
        setupListener('btn-at-save', 'click', savePotTrial);
        setupListener('btn-aw-save', 'click', saveAgroIrrigation);
        setupListener('btn-ah-save', 'click', saveAgroHealth);
        setupListener('btn-ae-refresh', 'click', refreshAgroEnv);

        // Garden Scape Saves
        setupListener('btn-gs-save', 'click', saveGardenPlan);
        setupListener('btn-gsl-save', 'click', saveSoilLog);
        setupListener('btn-gt-save', 'click', saveGardenTask);
        setupListener('btn-gw-save', 'click', saveGardenWater);
        setupListener('btn-gi-save', 'click', saveGardenInput);
        setupListener('btn-gy-save', 'click', saveYield);

        // Media Logic
        setupListener('btn-nv-photo', 'click', () => document.getElementById('nv-file-input').click());
        setupListener('nv-file-input', 'change', handlePhotoUpload);

        // --- Export Logic ---
        setupListener('btn-export-excel', 'click', exportToExcel);
        setupListener('btn-export-json', 'click', exportSurveyJSON);
        setupListener('btn-export-csv', 'click', exportSurveyCSV);

        // Data filter listener
        setupListener('data-filter-tool', 'change', renderDataEntries);

    } catch (err) { console.error("Init Error: " + err.message); }
});

function setupListener(id, event, callback) {
    const el = document.getElementById(id);
    if (el) el.addEventListener(event, callback);
}

// --- Data Persistence ---
function loadData() {
    try {
        const saved = localStorage.getItem(STORAGE_KEY);
        if (saved) {
            state = Object.assign(state, JSON.parse(saved));
            if (!Array.isArray(state.surveys)) state.surveys = [];

            // Migrations for new features
            state.surveys.forEach(s => {
                if (!s.agroLab) s.agroLab = { trials: [], potTrials: [], phenology: [], irrigation: [], health: [] };
                if (!s.gardenScape) s.gardenScape = { plans: [], soilLogs: [], yields: [], tasks: [], watering: [], inputs: [] };
                if (!s.gardenScape.tasks) s.gardenScape.tasks = [];
                if (!s.gardenScape.watering) s.gardenScape.watering = [];
                if (!s.gardenScape.inputs) s.gardenScape.inputs = [];
            });

            if (!state.speciesList) state.speciesList = ["Quercus robur", "Fagus sylvatica", "Pinus sylvestris", "Betula pendula"];
            updateSurveySelects();
            applyTheme();
            renderSpeciesDatalist();
        }
    } catch(e) { console.error("Load error", e); }
}

function saveData() {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (e) { console.error("Save error", e); }
}

function login(type) {
    state.user = { name: type === 'guest' ? 'Guest' : 'Researcher' };
    saveData();
    document.getElementById('login-screen').classList.remove('active');
    showApp();
}

function showApp() {
    document.getElementById('app-container').classList.remove('hidden');
    switchScreen('dashboard');
}

// --- Navigation ---
function initNavigation() {
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('click', () => switchScreen(item.dataset.screen));
    });
    setupListener('btn-back', 'click', handleBackAction);
}

function handleBackAction() {
    const openSide = document.getElementById('settings-panel');
    if (openSide && openSide.classList.contains('open')) {
        openSide.classList.remove('open'); return;
    }

    const subContent = document.querySelector('.sub-content:not(.hidden)');
    if (subContent) {
        subContent.classList.add('hidden');
        const parent = subContent.closest('.content-section');
        const menu = parent.querySelector('.sub-menu-grid');
        if (menu) menu.classList.remove('hidden');
        const titles = { 'forest-capture': 'Forest Capture', 'agro-lab': 'Agro Lab', 'garden-scape': 'Garden Scape', 'note-vault': 'Note Vault' };
        document.getElementById('screen-title').innerText = titles[parent.id] || 'Toolbox';
        return;
    }

    const activeSub = document.querySelector('.sub-tool.active');
    if (activeSub) {
        activeSub.classList.remove('active');
        document.getElementById('tools').classList.add('active');
        document.getElementById('screen-title').innerText = 'Toolbox';
        document.getElementById('btn-back').classList.add('hidden');
        return;
    }

    const activeNav = document.querySelector('.nav-item.active');
    if (activeNav && activeNav.dataset.screen !== 'dashboard') {
        switchScreen('dashboard');
    } else {
        if (window.Android) window.Android.exitApp();
    }
}

function switchScreen(id) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    const target = document.getElementById(id);
    if (target) target.classList.add('active');

    document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
    const nav = document.querySelector(`.nav-item[data-screen="${id}"]`);
    if (nav) nav.classList.add('active');

    const titles = { 'dashboard': 'Bio Logger', 'tools': 'Toolbox', 'data': 'Data Archive' };
    document.getElementById('screen-title').innerText = titles[id] || 'Bio Logger';
    document.getElementById('btn-back').classList.add('hidden');

    if (id === 'data') renderDataEntries();
}

function initSwipeNavigation() {
    let startX = 0;
    const screens = ['dashboard', 'tools', 'data'];
    document.addEventListener('touchstart', e => startX = e.changedTouches[0].screenX);
    document.addEventListener('touchend', e => {
        const endX = e.changedTouches[0].screenX;
        const diff = startX - endX;
        if (Math.abs(diff) < 100) return;
        if (document.querySelector('.sub-tool.active') || document.getElementById('settings-panel').classList.contains('open')) return;

        const activeNav = document.querySelector('.nav-item.active');
        if (!activeNav) return;
        const current = activeNav.dataset.screen;
        const idx = screens.indexOf(current);

        if (diff > 0 && idx < screens.length - 1) switchScreen(screens[idx+1]);
        if (diff < 0 && idx > 0) switchScreen(screens[idx-1]);
    });
}

function openTool(toolId) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    document.getElementById('btn-back').classList.remove('hidden');

    const map = {
        'forest': { id: 'forest-capture', title: 'Forest Capture' },
        'agro': { id: 'agro-lab', title: 'Agro Lab' },
        'garden': { id: 'garden-scape', title: 'Garden Scape' },
        'note': { id: 'note-vault', title: 'Note Vault' }
    };

    const cfg = map[toolId];
    if (cfg) {
        const target = document.getElementById(cfg.id);
        target.classList.add('active');
        document.getElementById('screen-title').innerText = cfg.title;

        const menu = target.querySelector('.sub-menu-grid');
        const content = target.querySelector('.sub-content');
        if (menu) menu.classList.remove('hidden');
        if (content) content.classList.add('hidden');

        updateSurveySelects();
        refreshToolContext();
    }
}

function switchSubTab(tabId, btn) {
    const parent = btn.closest('.content-section');
    parent.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
    parent.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));

    const target = document.getElementById(tabId);
    if (target) target.classList.add('active');
    btn.classList.add('active');
    document.getElementById('screen-title').innerText = btn.innerText;

    if (tabId === 'f-map') initMap();
}

function refreshToolContext() {
    const nv = document.getElementById('note-vault');
    if (nv && nv.classList.contains('active')) renderNotes();
    const ag = document.getElementById('agro-lab');
    if (ag && ag.classList.contains('active')) renderGerminationChart();
}

// --- Survey Management ---
function updateSurveySelects() {
    const selects = document.querySelectorAll('.active-survey-select');
    selects.forEach(sel => {
        sel.innerHTML = '<option value="">-- Global Session --</option>';
        state.surveys.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.id;
            opt.innerText = `${s.name} (${s.date.split('T')[0]})`;
            if (s.id === state.activeId) opt.selected = true;
            sel.appendChild(opt);
        });

        sel.onchange = (e) => {
            state.activeId = e.target.value;
            saveData();
            refreshToolContext();
        };
    });
}

function createNewSurvey() {
    const name = prompt("Enter Name for This Recording Session:");
    if (name) {
        const id = 's' + Date.now();
        state.surveys.unshift({
            id, name, date: new Date().toISOString(),
            forestCapture: { quadrats: [], transects: [], env: null },
            agroLab: { trials: [], potTrials: [], phenology: [], irrigation: [], health: [] },
            gardenScape: { plans: [], soilLogs: [], yields: [], tasks: [], watering: [], inputs: [] },
            notes: []
        });
        state.activeId = id;
        saveData();
        updateSurveySelects();
        if (window.Android) window.Android.showToast("New Session Started");
    }
}

// --- Logic Saves ---
function getActiveSurvey() {
    if (!state.activeId) {
        if (state.surveys.length === 0) {
            // Create a default session if none exists
            const id = 's' + Date.now();
            state.surveys.push({
                id, name: 'Default Session', date: new Date().toISOString(),
                forestCapture: { quadrats: [], transects: [], env: null },
                agroLab: { trials: [], potTrials: [], phenology: [], irrigation: [], health: [] },
                gardenScape: { plans: [], soilLogs: [], yields: [], tasks: [], watering: [], inputs: [] },
                notes: []
            });
            state.activeId = id;
            updateSurveySelects();
        } else {
            state.activeId = state.surveys[0].id;
        }
    }
    const found = state.surveys.find(s => s.id === state.activeId);
    if (!found && state.surveys.length > 0) {
        state.activeId = state.surveys[0].id;
        return state.surveys[0];
    }
    return found;
}

function saveNote() {
    const textEl = document.getElementById('nv-text');
    if (!textEl.value.trim()) return alert("Type note first");

    const survey = getActiveSurvey();
    const entry = { id: Date.now(), type: 'note', content: textEl.value, timestamp: new Date().toLocaleString() };
    survey.notes.unshift(entry);
    textEl.value = '';
    renderNotes();
    saveData();
    if (window.Android) window.Android.showToast("Note Recorded");
}

function startVoiceRecording() {
    if (window.Android && window.Android.startVoiceRecognition) {
        window.Android.startVoiceRecognition();
    } else {
        alert("Voice Recording available on Android app.");
    }
}

function onVoiceResult(text) {
    const textEl = document.getElementById('nv-text');
    if (textEl) textEl.value += (textEl.value ? " " : "") + text;
}

function handlePhotoUpload(e) {
    const file = e.target.files[0];
    if (!file) return;
    const survey = getActiveSurvey();
    const reader = new FileReader();
    reader.onload = (re) => {
        const entry = { id: Date.now(), type: 'photo', content: 'Image captured', image: re.target.result, timestamp: new Date().toLocaleString() };
        survey.notes.unshift(entry);
        renderNotes();
        saveData();
        if (window.Android) window.Android.showToast("Photo saved to Note Vault");
    };
    reader.readAsDataURL(file);
}

function saveQuadrat() {
    const species = document.getElementById('fq-species').value;
    if (!species) return alert("Enter species");
    updateSpeciesDatalist(species);
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'quadrat', species,
        dbh: document.getElementById('fq-dbh').value,
        height: document.getElementById('fq-height').value,
        health: document.getElementById('fq-health').value,
        timestamp: new Date().toLocaleString()
    };
    survey.forestCapture.quadrats.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Quadrat Saved");
}

function saveTransect() {
    const dist = document.getElementById('ft-dist').value;
    const species = document.getElementById('ft-species').value;
    if (!dist) return alert("Enter distance");
    updateSpeciesDatalist(species);
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'transect', dist, species,
        count: document.getElementById('ft-count').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.forestCapture.transects) survey.forestCapture.transects = [];
    survey.forestCapture.transects.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Transect Log Saved");
}

function saveForestEnv() {
    const canopy = document.getElementById('fe-canopy').value;
    if (!canopy) return alert("Enter canopy %");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'env', canopy,
        soil: document.getElementById('fe-soil').value,
        timestamp: new Date().toLocaleString()
    };
    survey.forestCapture.env = entry;
    saveData();
    if (window.Android) window.Android.showToast("Env Data Saved");
}

// --- Agro Lab Saves ---
function saveAgroTrial() {
    const species = document.getElementById('ag-species').value;
    if (!species) return alert("Enter species");
    updateSpeciesDatalist(species);
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'trial', species,
        seeds: document.getElementById('ag-seeds').value,
        timestamp: new Date().toLocaleString()
    };
    survey.agroLab.trials.push(entry);
    saveData();
    renderGerminationChart();
    if (window.Android) window.Android.showToast("Trial Started");
}

function saveAgroPhenology() {
    const name = document.getElementById('ap-name').value;
    if (!name) return alert("Enter plant ID");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'phenology', name,
        height: document.getElementById('ap-height').value,
        leaves: document.getElementById('ap-leaves').value,
        stage: document.getElementById('ap-stage').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.agroLab.phenology) survey.agroLab.phenology = [];
    survey.agroLab.phenology.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Growth Logged");
}

function savePotTrial() {
    const name = document.getElementById('at-name').value;
    if (!name) return alert("Enter name");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'pottrial', name,
        treat: document.getElementById('at-treat').value,
        status: document.getElementById('at-status').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.agroLab.potTrials) survey.agroLab.potTrials = [];
    survey.agroLab.potTrials.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Pot Trial Saved");
}

function saveAgroIrrigation() {
    const plot = document.getElementById('aw-id').value;
    if (!plot) return alert("Enter plot ID");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'irrigation', plot,
        vol: document.getElementById('aw-vol').value,
        method: document.getElementById('aw-method').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.agroLab.irrigation) survey.agroLab.irrigation = [];
    survey.agroLab.irrigation.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Irrigation Logged");
}

function saveAgroHealth() {
    const plant = document.getElementById('ah-id').value;
    if (!plant) return alert("Enter plant ID");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'health', plant,
        issue: document.getElementById('ah-issue').value,
        sev: document.getElementById('ah-sev').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.agroLab.health) survey.agroLab.health = [];
    survey.agroLab.health.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Health Issue Recorded");
}

function refreshAgroEnv() {
    document.getElementById('ae-temp').innerText = document.getElementById('tel-temp').innerText;
    document.getElementById('ae-hum').innerText = document.getElementById('tel-hum').innerText;
}

// --- Garden Scape Saves ---
function saveGardenPlan() {
    const crop = document.getElementById('gs-crop').value;
    if (!crop) return alert("Enter crop");
    updateSpeciesDatalist(crop);
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'plan', crop,
        date: document.getElementById('gs-date').value,
        timestamp: new Date().toLocaleString()
    };
    survey.gardenScape.plans.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Plan Added");
}

function saveSoilLog() {
    const ph = document.getElementById('gsl-ph').value;
    if (!ph) return alert("Enter pH");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'soillog', ph,
        moist: document.getElementById('gsl-moist').value,
        npk: document.getElementById('gsl-npk').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.gardenScape.soilLogs) survey.gardenScape.soilLogs = [];
    survey.gardenScape.soilLogs.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Soil Log Saved");
}

function saveGardenTask() {
    const desc = document.getElementById('gt-desc').value;
    if (!desc) return alert("Enter description");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'task',
        cat: document.getElementById('gt-type').value,
        desc, timestamp: new Date().toLocaleString()
    };
    if (!survey.gardenScape.tasks) survey.gardenScape.tasks = [];
    survey.gardenScape.tasks.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Task Logged");
}

function saveGardenWater() {
    const zone = document.getElementById('gw-zone').value;
    if (!zone) return alert("Enter zone");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'water', zone,
        dur: document.getElementById('gw-duration').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.gardenScape.watering) survey.gardenScape.watering = [];
    survey.gardenScape.watering.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Watering Recorded");
}

function saveGardenInput() {
    const name = document.getElementById('gi-name').value;
    if (!name) return alert("Enter input name");
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'input', name,
        qty: document.getElementById('gi-qty').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.gardenScape.inputs) survey.gardenScape.inputs = [];
    survey.gardenScape.inputs.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Input Logged");
}

function saveYield() {
    const crop = document.getElementById('gy-crop').value;
    if (!crop) return alert("Enter crop");
    updateSpeciesDatalist(crop);
    const survey = getActiveSurvey();
    const entry = {
        id: Date.now(), type: 'yield', crop,
        weight: document.getElementById('gy-weight').value,
        timestamp: new Date().toLocaleString()
    };
    if (!survey.gardenScape.yields) survey.gardenScape.yields = [];
    survey.gardenScape.yields.push(entry);
    saveData();
    if (window.Android) window.Android.showToast("Yield Logged");
}

function updateSpeciesDatalist(species) {
    if (!species) return;
    if (!state.speciesList.includes(species)) {
        state.speciesList.push(species);
        saveData();
        renderSpeciesDatalist();
    }
}

function renderSpeciesDatalist() {
    const dl = document.getElementById('species-list');
    if (!dl) return;
    dl.innerHTML = '';
    (state.speciesList || []).forEach(s => {
        const opt = document.createElement('option');
        opt.value = s;
        dl.appendChild(opt);
    });
}

function renderNotes() {
    const list = document.getElementById('nv-list');
    if (!list) return;
    list.innerHTML = '';
    const survey = getActiveSurvey();
    if (!survey || !survey.notes.length) {
        list.innerHTML = '<p class="empty-msg">No records in this session</p>';
        return;
    }
    survey.notes.forEach(n => {
        const div = document.createElement('div');
        div.className = 'data-item glass';
        div.style.marginBottom = '10px';
        let content = `<small>${n.timestamp}</small><p>${n.content}</p>`;
        if (n.image) content += `<img src="${n.image}" style="width:100%; border-radius:8px; margin-top:8px;">`;
        div.innerHTML = content;
        list.appendChild(div);
    });
}

function renderGerminationChart() {
    const canvas = document.getElementById('germ-chart');
    if (!canvas || typeof Chart === 'undefined') return;
    const survey = getActiveSurvey();
    const trials = survey ? (survey.agroLab?.trials || []) : [];

    if (window.myChart) window.myChart.destroy();
    if (trials.length === 0) return;

    window.myChart = new Chart(canvas, {
        type: 'bar',
        data: {
            labels: trials.map(t => t.species),
            datasets: [{
                label: 'Seeds Planted',
                data: trials.map(t => t.seeds),
                backgroundColor: 'rgba(0, 230, 118, 0.5)'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: { y: { beginAtZero: true } }
        }
    });
}

function renderDataEntries() {
    const container = document.getElementById('unified-data-list');
    if (!container) return;
    container.innerHTML = '';
    const filter = document.getElementById('data-filter-tool').value;

    const tools = [
        { id: 'forest', name: 'Forest Matrix', icon: '🌲' },
        { id: 'agro', name: 'Agro Lab', icon: '🧪' },
        { id: 'garden', name: 'Garden Scape', icon: '🏡' },
        { id: 'note', name: 'Note Vault', icon: '📝' }
    ];

    tools.forEach(tool => {
        if (filter !== 'all' && filter !== tool.id) return;

        const folder = document.createElement('div');
        folder.className = 'glass';
        folder.style.marginBottom = '15px';
        folder.style.overflow = 'hidden';
        folder.innerHTML = `
            <div class="folder-header" style="padding: 15px; background: rgba(255,255,255,0.05); display: flex; justify-content: space-between; align-items: center; cursor: pointer;">
                <h4 style="margin:0; color:var(--primary); font-size: 0.9rem;">${tool.icon} ${tool.name}</h4>
                <div style="display:flex; gap:10px; align-items:center;">
                    <button class="extract-folder-btn" style="background:var(--primary); border:none; color:black; font-size:0.6rem; padding:4px 8px; border-radius:4px; font-weight:bold;">EXTRACT</button>
                    <span class="folder-toggle">▼</span>
                </div>
            </div>
            <div class="folder-content" style="padding: 10px; display: block;"></div>
        `;

        const content = folder.querySelector('.folder-content');
        let hasData = false;
        let toolDataRows = [];

        state.surveys.forEach(s => {
            let entries = [];
            if (tool.id === 'note') s.notes.forEach(e => {
                entries.push({...e, cat: 'Note'});
                toolDataRows.push({ Session: s.name, Category: 'Note', Type: e.type, Date: e.timestamp, Details: e.content });
            });
            if (tool.id === 'forest') {
                (s.forestCapture.quadrats || []).forEach(e => {
                    entries.push({...e, cat: 'Quadrat', display: e.species});
                    toolDataRows.push({ Session: s.name, Category: 'Quadrat', Species: e.species, DBH: e.dbh, Height: e.height, Health: e.health, Date: e.timestamp });
                });
                (s.forestCapture.transects || []).forEach(e => {
                    entries.push({...e, cat: 'Transect', display: `${e.dist}m: ${e.species}`});
                    toolDataRows.push({ Session: s.name, Category: 'Transect', Dist: e.dist, Species: e.species, Count: e.count, Date: e.timestamp });
                });
                if (s.forestCapture.env) {
                    const e = s.forestCapture.env;
                    entries.push({...e, cat: 'Env', display: `Canopy: ${e.canopy}%`});
                    toolDataRows.push({ Session: s.name, Category: 'Env', Canopy: e.canopy, Soil: e.soil, Date: e.timestamp });
                }
            }
            if (tool.id === 'agro') {
                (s.agroLab.trials || []).forEach(e => {
                    entries.push({...e, cat: 'Germ', display: e.species});
                    toolDataRows.push({ Session: s.name, Category: 'Germination', Species: e.species, Seeds: e.seeds, Date: e.timestamp });
                });
                (s.agroLab.potTrials || []).forEach(e => {
                    entries.push({...e, cat: 'Pot Trial', display: e.name});
                    toolDataRows.push({ Session: s.name, Category: 'Pot Trial', Name: e.name, Treatment: e.treat, Status: e.status, Date: e.timestamp });
                });
                (s.agroLab.phenology || []).forEach(e => {
                    entries.push({...e, cat: 'Phenology', display: `${e.name}: ${e.height}cm`});
                    toolDataRows.push({ Session: s.name, Category: 'Phenology', Name: e.name, Height: e.height, Leaves: e.leaves, Stage: e.stage, Date: e.timestamp });
                });
                (s.agroLab.irrigation || []).forEach(e => {
                    entries.push({...e, cat: 'Irrigation', display: `${e.plot}: ${e.vol}L`});
                    toolDataRows.push({ Session: s.name, Category: 'Irrigation', Plot: e.plot, Volume: e.vol, Method: e.method, Date: e.timestamp });
                });
                (s.agroLab.health || []).forEach(e => {
                    entries.push({...e, cat: 'Health', display: `${e.plant}: ${e.issue}`});
                    toolDataRows.push({ Session: s.name, Category: 'Health', Plant: e.plant, Issue: e.issue, Severity: e.sev, Date: e.timestamp });
                });
            }
            if (tool.id === 'garden') {
                (s.gardenScape.plans || []).forEach(e => {
                    entries.push({...e, cat: 'Plan', display: e.crop});
                    toolDataRows.push({ Session: s.name, Category: 'Plan', Crop: e.crop, TargetDate: e.date, Date: e.timestamp });
                });
                (s.gardenScape.soilLogs || []).forEach(e => {
                    entries.push({...e, cat: 'Soil', display: `pH: ${e.ph}`});
                    toolDataRows.push({ Session: s.name, Category: 'Soil', pH: e.ph, Moisture: e.moist, NPK: e.npk, Date: e.timestamp });
                });
                (s.gardenScape.yields || []).forEach(e => {
                    entries.push({...e, cat: 'Yield', display: `${e.crop}: ${e.weight}kg`});
                    toolDataRows.push({ Session: s.name, Category: 'Yield', Crop: e.crop, Weight: e.weight, Date: e.timestamp });
                });
                (s.gardenScape.tasks || []).forEach(e => {
                    entries.push({...e, cat: 'Task', display: `${e.cat}: ${e.desc}`});
                    toolDataRows.push({ Session: s.name, Category: 'Task', Type: e.cat, Description: e.desc, Date: e.timestamp });
                });
                (s.gardenScape.watering || []).forEach(e => {
                    entries.push({...e, cat: 'Water', display: `${e.zone}: ${e.dur}m`});
                    toolDataRows.push({ Session: s.name, Category: 'Watering', Zone: e.zone, Duration: e.dur, Date: e.timestamp });
                });
                (s.gardenScape.inputs || []).forEach(e => {
                    entries.push({...e, cat: 'Input', display: `${e.name}: ${e.qty}`});
                    toolDataRows.push({ Session: s.name, Category: 'Input', Name: e.name, Qty: e.qty, Date: e.timestamp });
                });
            }

            if (entries.length > 0) {
                hasData = true;
                const group = document.createElement('div');
                group.innerHTML = `<h5 style="color:var(--text-muted); margin:15px 0 8px; font-size: 0.75rem; text-transform: uppercase; border-bottom: 1px solid var(--glass-border); padding-bottom: 4px;">${s.name}</h5>`;
                entries.sort((a,b) => b.id - a.id).forEach(e => {
                    const item = document.createElement('div');
                    item.className = 'data-item glass';
                    item.style.marginBottom = '8px';
                    item.style.padding = '12px';
                    item.innerHTML = `<span style="font-size: 0.85rem; font-weight:600;">[${e.cat}]</span> <span style="font-size: 0.85rem;">${e.display || e.species || (e.content ? e.content.substring(0,30) : 'Log Entry')}</span><br><small style="color:var(--text-muted); font-size: 0.7rem; margin-top:4px; display:block;">${e.timestamp}</small>`;
                    group.appendChild(item);
                });
                content.appendChild(group);
            }
        });

        if (hasData) {
            container.appendChild(folder);

            // Extract Logic
            folder.querySelector('.extract-folder-btn').onclick = (ev) => {
                ev.stopPropagation();
                const ws = XLSX.utils.json_to_sheet(toolDataRows);
                const wb = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(wb, ws, tool.name);
                XLSX.writeFile(wb, `BioLogger_${tool.name}_Export_${Date.now()}.xlsx`);
            };

            // Toggle Logic
            folder.querySelector('.folder-header').onclick = () => {
                const c = folder.querySelector('.folder-content');
                const t = folder.querySelector('.folder-toggle');
                const isHidden = c.style.display === 'none';
                c.style.display = isHidden ? 'block' : 'none';
                t.innerText = isHidden ? '▼' : '▶';
            };
        }
    });

    if (container.innerHTML === '') {
        container.innerHTML = '<div class="glass" style="padding:40px; text-align:center; border-radius:30px;"><p class="empty-msg" style="margin:0;">Neural archive is empty. Initialize modules to record data stream.</p></div>';
    }
}

// --- Excel Export (Global) ---
function exportToExcel() {
    const survey = getActiveSurvey();
    if (!survey) return alert("No active session detected.");

    const data = [];
    survey.notes.forEach(n => data.push({ Module: 'Vault', Category: 'Note', Type: n.type, Date: n.timestamp, Details: n.content }));
    survey.forestCapture.quadrats.forEach(q => data.push({ Module: 'Forest', Category: 'Quadrat', Species: q.species, DBH: q.dbh, Height: q.height, Health: q.health, Date: q.timestamp }));
    (survey.forestCapture.transects || []).forEach(t => data.push({ Module: 'Forest', Category: 'Transect', Dist: t.dist, Species: t.species, Count: t.count, Date: t.timestamp }));
    if (survey.forestCapture.env) data.push({ Module: 'Forest', Category: 'Env', Canopy: survey.forestCapture.env.canopy, Soil: survey.forestCapture.env.soil, Date: survey.forestCapture.env.timestamp });

    // Agro Lab Data
    survey.agroLab.trials.forEach(t => data.push({ Module: 'Agro', Category: 'Germination', Species: t.species, Seeds: t.seeds, Date: t.timestamp }));
    (survey.agroLab.potTrials || []).forEach(p => data.push({ Module: 'Agro', Category: 'PotTrial', Name: p.name, Treatment: p.treat, Status: p.status, Date: p.timestamp }));
    (survey.agroLab.phenology || []).forEach(p => data.push({ Module: 'Agro', Category: 'Phenology', Name: p.name, Height: p.height, Leaves: p.leaves, Stage: p.stage, Date: p.timestamp }));
    (survey.agroLab.irrigation || []).forEach(i => data.push({ Module: 'Agro', Category: 'Irrigation', Plot: i.plot, Vol: i.vol, Method: i.method, Date: i.timestamp }));
    (survey.agroLab.health || []).forEach(h => data.push({ Module: 'Agro', Category: 'Health', Plant: h.plant, Issue: h.issue, Severity: h.sev, Date: h.timestamp }));

    // Garden Scape Data
    survey.gardenScape.plans.forEach(p => data.push({ Module: 'Garden', Category: 'Plan', Crop: p.crop, TargetDate: p.date, Date: p.timestamp }));
    (survey.gardenScape.soilLogs || []).forEach(s => data.push({ Module: 'Garden', Category: 'Soil', pH: s.ph, Moisture: s.moist, NPK: s.npk, Date: s.timestamp }));
    (survey.gardenScape.tasks || []).forEach(t => data.push({ Module: 'Garden', Category: 'Task', Type: t.cat, Description: t.desc, Date: t.timestamp }));
    (survey.gardenScape.watering || []).forEach(w => data.push({ Module: 'Garden', Category: 'Watering', Zone: w.zone, Duration: w.dur, Date: w.timestamp }));
    (survey.gardenScape.inputs || []).forEach(i => data.push({ Module: 'Garden', Category: 'Input', Name: i.name, Qty: i.qty, Date: i.timestamp }));
    (survey.gardenScape.yields || []).forEach(y => data.push({ Module: 'Garden', Category: 'Yield', Crop: y.crop, Weight: y.weight, Date: y.timestamp }));

    const ws = XLSX.utils.json_to_sheet(data);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, "Neural_Stream");
    XLSX.writeFile(wb, `BioLogger_${survey.name}_Neural_Export.xlsx`);
}

function exportSurveyJSON() {
    const s = getActiveSurvey();
    if (!s) return;
    downloadFile(JSON.stringify(s, null, 2), `neural_stream_${s.name}.json`, 'application/json');
}

function exportSurveyCSV() {
    const s = getActiveSurvey();
    if (!s) return;
    let csv = "Module,Category,Date,Details\n";
    s.notes.forEach(n => csv += `Vault,Note,"${n.timestamp}","${n.content}"\n`);
    // simplified CSV export
    downloadFile(csv, `neural_archive_${s.name}.csv`, 'text/csv');
}

// --- Boilerplate ---
function initSettings() {
    setupListener('btn-settings', 'click', () => document.getElementById('settings-panel').classList.add('open'));
    setupListener('btn-settings-close', 'click', () => document.getElementById('settings-panel').classList.remove('open'));
    document.getElementById('pref-theme').onchange = (e) => {
        state.settings.darkMode = e.target.checked;
        applyTheme(); saveData();
    };
    setupListener('btn-data-export', 'click', () => downloadFile(JSON.stringify(state), 'neural_backup.json', 'application/json'));
    setupListener('btn-data-import', 'click', () => document.getElementById('file-import').click());
    setupListener('file-import', 'change', importData);
    setupListener('btn-data-clear', 'click', () => { if(confirm("Initiate total memory wipe?")) { localStorage.clear(); location.reload(); } });
}

function importData(e) {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (re) => {
        try {
            const data = JSON.parse(re.target.result);
            state = Object.assign(state, data);
            saveData();
            location.reload();
        } catch(err) { alert("Data stream corruption detected. Load failed."); }
    };
    reader.readAsText(file);
}

function applyTheme() { document.body.className = state.settings.darkMode ? 'dark-theme' : 'light-theme'; }

function downloadFile(c, n, t) {
    const a = document.createElement("a");
    const f = new Blob([c], { type: t });
    a.href = URL.createObjectURL(f); a.download = n; a.click();
}

function initTelemetry() {
    if ("geolocation" in navigator) {
        navigator.geolocation.watchPosition(p => {
            const { latitude, longitude, altitude } = p.coords;
            const gpsEl = document.getElementById('tel-gps');
            const elevEl = document.getElementById('tel-elev');
            if (gpsEl) gpsEl.innerText = `${latitude.toFixed(4)}, ${longitude.toFixed(4)}`;
            if (elevEl) elevEl.innerText = altitude ? `${altitude.toFixed(1)}m` : "STABLE";
            state.lastCoords = { lat: latitude, lng: longitude };
        }, err => console.warn("GPS error", err), { enableHighAccuracy: true });
    }

    setInterval(() => {
        const temp = (15 + Math.random() * 10).toFixed(1);
        const hum = (50 + Math.random() * 30).toFixed(0);
        const tEl = document.getElementById('tel-temp');
        const hEl = document.getElementById('tel-hum');
        if (tEl) tEl.innerText = temp + "°C";
        if (hEl) hEl.innerText = hum + "%";
    }, 10000);
}

let mapInst;
function initMap() {
    if (typeof L === 'undefined' || mapInst) return;
    setTimeout(() => {
        const container = document.getElementById('map-container');
        if (!container) return;
        const coords = state.lastCoords ? [state.lastCoords.lat, state.lastCoords.lng] : [20.5937, 78.9629];
        mapInst = L.map('map-container').setView(coords, 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(mapInst);
    }, 300);
}
