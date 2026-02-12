const config = {
    bgColor: "#0F0C29",
    accentColor1: "#00D2FF",
    accentColor2: "#00b875",
    showSteps: true,
    showBPM: true,
    is24h: true
};

function updateUI() {
    const watch = document.getElementById('watch');
    const glow = document.getElementById('glow');
    const outerProgress = document.querySelector('.outer-progress');
    const innerProgress = document.querySelector('.inner-progress');
    const stepsStat = document.querySelector('.steps');
    const bpmStat = document.querySelector('.bpm');
    const jsonOut = document.getElementById('json-out');

    watch.style.backgroundColor = config.bgColor;
    glow.style.background = `radial-gradient(circle at center, ${config.accentColor1}44 0%, transparent 70%)`;

    outerProgress.style.borderTopColor = config.accentColor1;
    innerProgress.style.borderTopColor = config.accentColor1 + '99';

    stepsStat.style.display = config.showSteps ? 'flex' : 'none';
    stepsStat.querySelector('.stat-value').style.color = config.accentColor1;

    bpmStat.style.display = config.showBPM ? 'flex' : 'none';

    document.documentElement.style.setProperty('--accent-blue', config.accentColor1);
    document.documentElement.style.setProperty('--accent-purple', config.accentColor2);

    jsonOut.textContent = JSON.stringify(config, null, 2);
}

function updateTime() {
    const now = new Date();
    let hours = now.getHours();
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    if (!config.is24h) {
        hours = hours % 12 || 12;
    }
    const hoursStr = String(hours).padStart(2, '0');

    document.getElementById('time').textContent = `${hoursStr}:${minutes}`;
    document.getElementById('seconds').textContent = seconds;

    const options = { weekday: 'short', month: 'short', day: 'numeric' };
    document.getElementById('date').textContent = now.toLocaleDateString('es-ES', options).toUpperCase();
}

// Event Listeners
document.getElementById('cfg-bg').addEventListener('input', (e) => { config.bgColor = e.target.value; updateUI(); });
document.getElementById('cfg-accent1').addEventListener('input', (e) => { config.accentColor1 = e.target.value; updateUI(); });
document.getElementById('cfg-accent2').addEventListener('input', (e) => { config.accentColor2 = e.target.value; updateUI(); });
document.getElementById('cfg-steps').addEventListener('change', (e) => { config.showSteps = e.target.checked; updateUI(); });
document.getElementById('cfg-bpm').addEventListener('change', (e) => { config.showBPM = e.target.checked; updateUI(); });
document.getElementById('cfg-format').addEventListener('change', (e) => { config.is24h = e.target.value === '24h'; updateUI(); });

document.getElementById('copy-btn').addEventListener('click', () => {
    const jsonText = document.getElementById('json-out').textContent;
    navigator.clipboard.writeText(jsonText).then(() => {
        const btn = document.getElementById('copy-btn');
        const originalText = btn.textContent;
        btn.textContent = '¡Copiado!';
        btn.style.background = '#00b875';
        setTimeout(() => {
            btn.textContent = originalText;
            btn.style.background = '';
        }, 2000);
    });
});

setInterval(updateTime, 1000);
updateTime();
updateUI();
