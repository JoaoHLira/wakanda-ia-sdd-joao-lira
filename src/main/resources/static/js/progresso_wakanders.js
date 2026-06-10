(function() {
    if (!localStorage.getItem('token')) {
        const url = new URL(window.location.href);
        window.location.href = `${url.protocol}//${url.host}/wakanda-ai/api/painel-dados/login`;
    }
})();

const RANKING_ENDPOINT = "/wakanda-ai/api/gameficacao/progresso/ranking-wakanders?page=0&size=5";

function getAuthHeaders() {
    const token = localStorage.getItem("token");
    if (!token) {
        return {};
    }
    return {
        Authorization: `Bearer ${token}`
    };
}

function escapeHtml(value) {
    if (value === null || value === undefined) {
        return "-";
    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

function updateElementText(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = value;
    }
}

function showError(message) {
    const errorBox = document.getElementById("errorBox");
    const errorMessage = document.getElementById("errorMessage");
    if (!errorBox || !errorMessage) {
        return;
    }

    errorMessage.textContent = message;
    errorBox.style.display = "block";
}

function hideError() {
    const errorBox = document.getElementById("errorBox");
    if (errorBox) {
        errorBox.style.display = "none";
    }
}

function renderStatusRow(message) {
    const body = document.getElementById("leaderboardBody");
    if (!body) {
        return;
    }

    body.innerHTML = `<tr><td colspan="4" class="table-status">${escapeHtml(message)}</td></tr>`;
}

function renderRankingRows(items) {
    const body = document.getElementById("leaderboardBody");
    if (!body) {
        return;
    }

    if (!Array.isArray(items) || items.length === 0) {
        renderStatusRow("Não há dados para exibição.");
        return;
    }

    body.innerHTML = items
        .map((wakander, index) => {
            const nome = escapeHtml(wakander.nome || "Wakander");
            const xpTotal = Number.isFinite(wakander.xpTotal) ? wakander.xpTotal : 0;
            const missoesConcluidas = Number.isFinite(wakander.missoesConcluidas) ? wakander.missoesConcluidas : 0;

            return `
                <tr>
                    <td>#${index + 1}</td>
                    <td>${nome}</td>
                    <td>${xpTotal}</td>
                    <td>${missoesConcluidas}</td>
                </tr>
            `;
        })
        .join("");
}

function updateSummary(payload) {
    const total = Number.isFinite(payload?.totalElements) ? payload.totalElements : 0;

    updateElementText("totalWakanders", `${total} wakander(es) com destaque recente`);
    updateElementText("wakandersComMissoes", total);

    // Estas métricas ainda não possuem endpoint dedicado nesta tela.
    updateElementText("avgWisdoms", "-");
    updateElementText("avgLevel", "-");
    updateElementText("wakandersSemMissoesConcluidas", "N/D");
    updateElementText("novosWakanders", "N/D");
}

async function loadRanking() {
    hideError();
    renderStatusRow("Carregando ranking...");

    try {
        const response = await fetch(RANKING_ENDPOINT, {
            method: "GET",
            headers: {
                ...getAuthHeaders()
            }
        });

        if (!response.ok) {
            if (response.status === 404) {
                renderStatusRow("Não há dados para exibição.");
                updateSummary({ totalElements: 0 });
                return;
            }
            throw new Error("Falha ao carregar ranking de wakanders.");
        }

        const payload = await response.json();
        renderRankingRows(payload?.content || []);
        updateSummary(payload || {});
    } catch (error) {
        renderStatusRow("Não foi possível carregar o ranking no momento.");
        showError("Não foi possível carregar o ranking agora. Tente novamente.");
        console.error("Erro ao buscar ranking de wakanders:", error);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const retryButton = document.getElementById("retryButton");
    if (retryButton) {
        retryButton.addEventListener("click", () => {
            loadRanking();
        });
    }

    document.addEventListener("visibilitychange", () => {
        if (document.visibilityState === "visible") {
            loadRanking();
        }
    });

    loadRanking();
});
