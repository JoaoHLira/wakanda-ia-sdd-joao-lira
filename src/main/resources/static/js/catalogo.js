(function() {
    if (!localStorage.getItem('token')) {
        const url = new URL(window.location.href);
        window.location.href = `${url.protocol}//${url.host}/wakanda-ai/api/painel-dados/login`;
    }
})();

document.addEventListener("DOMContentLoaded", async () => {
  const baseUrl = extrairBaseUrl(window.location.href);
  const apiBase = baseUrl + "/wakanda-ai/api";

  const trilhaApi  = apiBase + "/trilhas";
  const jornadaApi = apiBase + "/jornadas";
  const missaoApi  = apiBase + "/missoes";

  const token = localStorage.getItem("token");

  const headers = {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`
  };

  const missoesCache = new Map();

  try {
    const [trilhas, jornadas] = await Promise.all([
      fetch(trilhaApi + "", { headers }).then(r => r.json()),
      fetch(jornadaApi + "", { headers }).then(r => r.json())
    ]);

    const jornadasPorTrilha = groupBy(jornadas, j => j.idTrilhaWakanda);

    renderTrilhas(trilhas, jornadasPorTrilha);
    carregarContagemMissoesPorJornada(jornadas);

  } catch (e) {
    console.error("Erro carregando catálogo:", e);
    renderErroGeral();
  }

  function renderTrilhas(trilhas, jornadasPorTrilha) {
    const trilhasListEl = document.querySelector(".trilhas-list");
    const emptyStateEl = document.querySelector(".empty-state");

    if (!trilhasListEl) return;

    if (!trilhas || trilhas.length === 0) {
      if (emptyStateEl) emptyStateEl.style.display = "block";
      trilhasListEl.innerHTML = "";
      return;
    }

    if (emptyStateEl) emptyStateEl.style.display = "none";

    trilhasListEl.innerHTML = trilhas.map(trilha => {
      const jornadas = (jornadasPorTrilha[trilha.idTrilha] || [])
        .sort((a, b) => (a.ordemJornada ?? 0) - (b.ordemJornada ?? 0));

      return `
        <article class="trilha-card" data-trilha-id="${trilha.idTrilha}">
          <details class="trilha-accordion" open>
            <summary class="trilha-header">
              <div class="trilha-info">
                <h3 class="trilha-nome">${escapeHtml(trilha.nome)}</h3>
                <p class="trilha-descricao">${escapeHtml(trilha.descricao || "")}</p>
              </div>
              <div class="trilha-meta">
                <span class="badge badge-trilha">
                  <span>${jornadas.length}</span>&nbsp;Jornadas
                </span>
              </div>
            </summary>

            <div class="trilha-body">
              ${
                jornadas.length > 0
                  ? `
                    <div class="jornadas-list">
                      ${jornadas.map(jornada => renderJornada(jornada)).join("")}
                    </div>
                  `
                  : `
                    <div class="empty-state empty-jornadas">
                      <p>Nenhuma jornada cadastrada para esta trilha.</p>
                    </div>
                  `
              }
            </div>
          </details>
        </article>
      `;
    }).join("");

    bindLazyLoadMissoes();
  }

  function renderJornada(jornada) {
    const statusRaw = (jornada.statusJornada ?? "N/D").toString().toUpperCase();
    const isAtiva = statusRaw === "ATIVA" || statusRaw === "ATIVO";

    return `
      <article class="jornada-card" data-jornada-id="${jornada.idJornada}">
        <details class="jornada-accordion">
          <summary class="jornada-header">
            <div class="jornada-info">
              <h4 class="jornada-nome">${escapeHtml(jornada.titulo)}</h4>
              <p class="jornada-descricao">${escapeHtml(jornada.descricao || "")}</p>
            </div>

            <div class="jornada-meta">
              <span class="badge badge-jornada">Ordem: ${jornada.ordemJornada ?? "-"}</span>
              <span class="badge badge-xp">${jornada.xpTotal ?? 0} XP</span>
              <span class="badge badge-xp-bonus">Bônus: ${jornada.xpBonus ?? 0} XP</span>

              <span class="badge badge-missoes">
                <span data-missoes-count="${jornada.idJornada}">0</span>&nbsp;Missões
              </span>

              <span class="badge badge-status ${isAtiva ? "status-ativa" : "status-inativa"}">
                ${escapeHtml(statusRaw)}
              </span>
            </div>
          </summary>

          <div class="jornada-body">
            <ul class="missoes-list" data-missoes-container data-offset="0"></ul>

            <button class="btn-load-more" data-load-more style="display:none;">
              Carregar mais
            </button>

            <div class="empty-state empty-missoes" data-empty-missoes style="display:none;">
              <p>Nenhuma missão cadastrada para esta jornada.</p>
            </div>
          </div>
        </details>
      </article>
    `;
  }

  function bindLazyLoadMissoes() {
    const jornadaDetails = document.querySelectorAll(".jornada-accordion");

    jornadaDetails.forEach(detailsEl => {
      detailsEl.addEventListener("toggle", async () => {
        if (!detailsEl.open) return;

        const jornadaCard = detailsEl.closest(".jornada-card");
        const idJornada = jornadaCard?.dataset?.jornadaId;
        if (!idJornada) return;

        const container = jornadaCard.querySelector("[data-missoes-container]");
        const emptyEl = jornadaCard.querySelector("[data-empty-missoes]");
        const loadMoreBtn = jornadaCard.querySelector("[data-load-more]");

        if (container.dataset.loaded === "true") return;

        const missoesTodas = await carregarMissoesTodas(idJornada);
        missoesCache.set(idJornada, missoesTodas);

        renderPaginaMissoes(idJornada, container, emptyEl, loadMoreBtn);

        loadMoreBtn.addEventListener("click", () => {
          renderPaginaMissoes(idJornada, container, emptyEl, loadMoreBtn, true);
        });
      });
    });
  }

  async function carregarMissoesTodas(idJornada) {
    try {
      const [pageAtivas, pageInativas] = await Promise.all([
        fetch(`${missaoApi}/${idJornada}/missoes?page=0&size=1000&missaoStatus=ATIVA`, { headers }).then(r => r.json()),
        fetch(`${missaoApi}/${idJornada}/missoes?page=0&size=1000&missaoStatus=INATIVA`, { headers }).then(r => r.json())
      ]);

      const ativ = pageAtivas?.content ?? [];
      const inat = pageInativas?.content ?? [];

      return [...ativ, ...inat].sort((a, b) => (a.ordemMissao ?? 0) - (b.ordemMissao ?? 0));
    } catch (e) {
      console.error("Erro ao carregar missões (ativas/inativas):", e);
      return [];
    }
  }

  function renderPaginaMissoes(idJornada, container, emptyEl, loadMoreBtn, append = false) {
    const lista = missoesCache.get(idJornada) || [];
    const offsetAtual = Number(container.dataset.offset || "0");
    const novoOffset = append ? offsetAtual + 10 : 0;

    const slice = lista.slice(novoOffset, novoOffset + 10);

    if (!append) container.innerHTML = "";

    if (slice.length === 0 && !append) {
      emptyEl.style.display = "block";
      loadMoreBtn.style.display = "none";
      container.dataset.loaded = "true";
      return;
    }

    emptyEl.style.display = "none";

    const html = slice.map(m => {
      const statusMissao = (m.missaoStatus ?? "N/D").toString().toUpperCase();
      const ativa = statusMissao === "ATIVA" || statusMissao === "ATIVO";
      const ordem = m.ordemMissao ?? "-";

      return `
        <li class="missao-item">
          <a class="missao-link" href="${apiBase}/gameficacao/missoes/${m.idMissao}"> <!-- Usa baseUrl -->
            <span class="missao-ordem">${ordem}</span>
            <span class="missao-nome">${escapeHtml(m.titulo)}</span>
            <span class="missao-status ${ativa ? "status-ativa" : "status-inativa"}">${escapeHtml(statusMissao)}</span>
            <span class="missao-xp">${m.xpBase ?? 0} XP</span>
          </a>
        </li>
      `;

    }).join("");

    container.insertAdjacentHTML("beforeend", html);
    container.dataset.offset = novoOffset;

    if (novoOffset + 10 < lista.length) {
      loadMoreBtn.style.display = "inline-flex";
    } else {
      loadMoreBtn.style.display = "none";
      container.dataset.loaded = "true";
    }
  }


  async function carregarContagemMissoesPorJornada(jornadas) {
    await Promise.all(
      (jornadas || []).map(async (jornada) => {
        try {
          const [ativas, inativas] = await Promise.all([
            fetch(`${missaoApi}/${jornada.idJornada}/missoes?page=0&size=1&missaoStatus=ATIVA`, { headers }).then(r => r.json()),
            fetch(`${missaoApi}/${jornada.idJornada}/missoes?page=0&size=1&missaoStatus=INATIVA`, { headers }).then(r => r.json())
          ]);

          const total = (ativas?.totalElements ?? 0) + (inativas?.totalElements ?? 0);
          const span = document.querySelector(`[data-missoes-count="${jornada.idJornada}"]`);
          if (span) span.textContent = total;

        } catch (e) {
          console.error("Erro buscando total de missões da jornada", jornada.idJornada, e);
        }
      })
    );
  }

  function renderErroGeral() {
    const trilhasListEl = document.querySelector(".trilhas-list");
    const emptyStateEl = document.querySelector(".empty-state");
    if (emptyStateEl) emptyStateEl.style.display = "block";
    if (trilhasListEl) trilhasListEl.innerHTML = "";
    if (emptyStateEl) emptyStateEl.innerHTML = "<p>Erro ao carregar catálogo.</p>";
  }

  function groupBy(arr, keyFn) {
    return (arr || []).reduce((acc, item) => {
      const key = keyFn(item);
      (acc[key] ||= []).push(item);
      return acc;
    }, {});
  }

  function extrairBaseUrl(urlCompleta) {
    const url = new URL(urlCompleta);
    return `${url.protocol}//${url.host}`;
  }

  function escapeHtml(str) {
    return String(str)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  }
});