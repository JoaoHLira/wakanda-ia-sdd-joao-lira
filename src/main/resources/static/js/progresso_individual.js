// ─────────────────────────────────────────────
(function() {
    if (!localStorage.getItem('token')) {
        const url = new URL(window.location.href);
        window.location.href = `${url.protocol}//${url.host}/wakanda-ai/api/painel-dados/login`;
    }
})();
// Toggle dark mode com persistência no localStorage e ícone dinâmico
// ─────────────────────────────────────────────
const toggleTheme = document.getElementById("toggleTheme");

// Aplica o tema salvo ao carregar a página
function applySavedTheme() {
  const savedTheme = localStorage.getItem("theme");
  if (savedTheme === "dark") {
    document.body.classList.add("dark-mode");
    toggleTheme.textContent = "☀️"; // ícone sol no modo escuro
  } else {
    document.body.classList.remove("dark-mode");
    toggleTheme.textContent = "🌙"; // ícone lua no modo claro
  }
}

// Alterna tema e salva escolha
toggleTheme.addEventListener("click", () => {
  document.body.classList.toggle("dark-mode");
  const isDark = document.body.classList.contains("dark-mode");
  localStorage.setItem("theme", isDark ? "dark" : "light");
  toggleTheme.textContent = isDark ? "☀️" : "🌙"; // troca ícone
});

// Executa ao carregar
applySavedTheme();

// ─────────────────────────────────────────────
// Modal Wakander não encontrado
// ─────────────────────────────────────────────
const notFoundModal = document.getElementById("notFoundModal");
const closeModal = document.getElementById("closeModal");

// Função para abrir modal (pode ser chamada do backend quando não encontrar Wakander)
function showNotFoundModal() {
  notFoundModal.style.display = "flex";
}

// Fechar modal
closeModal.addEventListener("click", () => {
  notFoundModal.style.display = "none";
});

// Fechar modal ao clicar fora
notFoundModal.addEventListener("click", (event) => {
  if (event.target === notFoundModal) {
    notFoundModal.style.display = "none";
  }
});

/* ─────────────────────────────────────────────────────────────
   Radar SVG: renderização sem bibliotecas
   ───────────────────────────────────────────────────────────── */

/* Util: graus para radianos */
const degToRad = (deg) => (deg * Math.PI) / 180;

/* Util: polar -> cartesiano (0° no topo, sentido horário) */
function polarToCartesian(cx, cy, radius, angleDeg) {
  const angle = degToRad(angleDeg - 90); // -90 para começar no topo
  const x = cx + radius * Math.cos(angle);
  const y = cy + radius * Math.sin(angle);
  return { x, y };
}

/* Constrói o radar */
function renderRadar(container) {
  const labels = JSON.parse(container.dataset.labels || "[]");
  const values = JSON.parse(container.dataset.values || "[]");
  const levels = Number(container.dataset.levels || 4); // nº de polígonos internos

  const N = labels.length;
  if (N === 0 || values.length !== N) return;

  const max = Math.max(...values);

  // Dimensões
  const size = container.clientWidth; // torna responsivo
  const padding = 30; // margem para labels
  const cx = size / 2;
  const cy = size / 2;
  const outerRadius = size / 2 - padding;

  // Cria SVG
  const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
  svg.setAttribute("viewBox", `0 0 ${size} ${size}`);
  svg.setAttribute("class", "radar-svg");

  // GROUPS
  const gGrid = document.createElementNS(svg.namespaceURI, "g");
  const gAxes = document.createElementNS(svg.namespaceURI, "g");
  const gLabels = document.createElementNS(svg.namespaceURI, "g");
  const gData = document.createElementNS(svg.namespaceURI, "g");
  gGrid.setAttribute("class", "radar-grid");
  gAxes.setAttribute("class", "radar-axis");
  gLabels.setAttribute("class", "radar-labels");
  gData.setAttribute("class", "radar-data");

  // 1) Grids internos (polígonos concêntricos)
  for (let lvl = 1; lvl <= levels; lvl++) {
    const r = (outerRadius * lvl) / levels;
    const points = [];
    for (let i = 0; i < N; i++) {
      const angle = (360 / N) * i;
      const { x, y } = polarToCartesian(cx, cy, r, angle);
      points.push(`${x},${y}`);
    }
    const poly = document.createElementNS(svg.namespaceURI, "polygon");
    poly.setAttribute("points", points.join(" "));
    poly.setAttribute("fill", "none");
    poly.setAttribute("vector-effect", "non-scaling-stroke");
    gGrid.appendChild(poly);
  }

  // 2) Eixos (linha do centro para cada vértice externo)
  for (let i = 0; i < N; i++) {
    const angle = (360 / N) * i;
    const { x, y } = polarToCartesian(cx, cy, outerRadius, angle);
    const axis = document.createElementNS(svg.namespaceURI, "line");
    axis.setAttribute("x1", cx);
    axis.setAttribute("y1", cy);
    axis.setAttribute("x2", x);
    axis.setAttribute("y2", y);
    axis.setAttribute("vector-effect", "non-scaling-stroke");
    gAxes.appendChild(axis);
  }

  // 3) Polígono de dados
  const dataPoints = [];
  for (let i = 0; i < N; i++) {
    const angle = (360 / N) * i;
    const r = (values[i] / max) * outerRadius;
    const { x, y } = polarToCartesian(cx, cy, r, angle);
    dataPoints.push(`${x},${y}`);
  }
  const dataPoly = document.createElementNS(svg.namespaceURI, "polygon");
  dataPoly.setAttribute("points", dataPoints.join(" "));
  dataPoly.setAttribute("class", "radar-polygon");
  dataPoly.setAttribute("vector-effect", "non-scaling-stroke");
  gData.appendChild(dataPoly);

  // 4) Pontos de dados
  for (let i = 0; i < N; i++) {
    const angle = (360 / N) * i;
    const r = (values[i] / max) * outerRadius;
    const { x, y } = polarToCartesian(cx, cy, r, angle);
    const dot = document.createElementNS(svg.namespaceURI, "circle");
    dot.setAttribute("cx", x);
    dot.setAttribute("cy", y);
    dot.setAttribute("r", 4);
    dot.setAttribute("class", "radar-point");
    gData.appendChild(dot);
  }

  // 5) Labels (um pouco além do outerRadius)
  for (let i = 0; i < N; i++) {
    const angle = (360 / N) * i;
    const { x, y } = polarToCartesian(cx, cy, outerRadius + 14, angle);
    const label = document.createElementNS(svg.namespaceURI, "text");
    label.setAttribute("x", x);
    label.setAttribute("y", y);
    label.setAttribute("class", "radar-label");
    label.textContent = labels[i];
    gLabels.appendChild(label);
  }

  // Monta SVG
  svg.appendChild(gGrid);
  svg.appendChild(gAxes);
  svg.appendChild(gData);
  svg.appendChild(gLabels);

  // Injeta no container
  // Limpa anterior
  container.innerHTML = "";
  container.appendChild(svg);
}

/* Render inicial e re-render em resize para manter responsivo */
function initRadar() {
  const container = document.getElementById("radarContainer");
  if (!container) return;

  const render = () => renderRadar(container);
  render();

  // Redesenha no resize (debounced)
  let t;
  window.addEventListener("resize", () => {
    clearTimeout(t);
    t = setTimeout(render, 100);
  });
}

document.addEventListener("DOMContentLoaded", () => {
  const container = document.getElementById("radarContainer");
  const badgeWrapper = document.getElementById("sabedoriasBadges");
  if (!container || !badgeWrapper) return;

  const labels = JSON.parse(container.dataset.labels || "[]");
  const values = JSON.parse(container.dataset.values || "[]");

  badgeWrapper.innerHTML = labels
    .map((label, i) => {
      const valor = values[i] ?? 0;
      return `<span class="badge">${label} - ${valor}</span>`;
    })
    .join("");
});

function formatarData(isoDate) {
  if (!isoDate) return "";
  const data = new Date(isoDate);
  return data.toLocaleDateString("pt-BR"); // saída: 10/12/2025
}

document.addEventListener("DOMContentLoaded", () => {
  const spanUltima = document.getElementById("ultimaAtualizacao");
  if (spanUltima?.textContent) {
    spanUltima.textContent = formatarData(spanUltima.textContent);
  }
});

document.addEventListener("DOMContentLoaded", initRadar);
