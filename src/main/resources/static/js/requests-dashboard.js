function formataCpf(cpf) {
    if (!cpf) return "-";
    return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, "$1.$2.$3-$4");
}

function formataTelefone(telefone) {
    if (!telefone) return "-";
    const cleaned = telefone.replace(/\D/g, '');
    const semCodigoPais = cleaned.startsWith('55') ? cleaned.slice(2) : cleaned;
    if (semCodigoPais.length === 11) {
        return `(${semCodigoPais.slice(0, 2)}) ${semCodigoPais.slice(2, 7)}-${semCodigoPais.slice(7)}`;
    } else if (semCodigoPais.length === 10) {
        return `(${semCodigoPais.slice(0, 2)}) ${semCodigoPais.slice(2, 6)}-${semCodigoPais.slice(6)}`;
    } else {
        return telefone;
    }
}

function formatarDataISOParaBR(dataISO) {
    if (!dataISO) return '';
    const dataObj = new Date(dataISO + 'T00:00:00');
    return new Intl.DateTimeFormat('pt-BR').format(dataObj);
}

async function preencheCardsEstatisticas(payload) {
    document.getElementById("total-wakanders").innerHTML = payload.totalWakanders;
    document.getElementById("total-completos").innerHTML = payload.totalWakandersCadastroCompleto;
    document.getElementById("total-incompletos").innerHTML = payload.totalWakandersCadastroIncompleto;
}

async function preencheCardsWakanders(payload) {
    const divList = document.getElementById('listCards');
    arrayWakandersTela = payload.content;
    let html = `
        <h3>Wakanders (${payload.number + 1} de ${payload.totalPages})</h3>

        <div class="header-list">
            <div>Dados pessoais</div>
            <div>Status Asaas</div>
            <div>Status Formulário</div>
            <div>Status Cadastro</div>
        </div>
    `;
    payload.content.forEach(wakander => {
        const statusClass = status => status === "COMPLETO" ? "completo" : "incompleto";

        html += `
        <div class="card-user-row">
            <div class="card-user" data-id="${wakander.idWakander}">
                <div class="info">
                    <strong>${wakander.nome}</strong>
                    <span>CPF: ${formataCpf(wakander.cpf)}</span>
                    <span>${formataTelefone(wakander.contato?.whatsapp)}</span>
                </div>
                
                <div class="info">
                    <span class="cabecalho-mobile">Status Asaas:</span>
                    <div class="status neutro">
                        ${wakander.statusDadosFiadorViaAsaas}
                    </div>
                </div>            
                <div class="info">
                    <span class="cabecalho-mobile">Status Formulário:</span>
                    <div class="status neutro">
                        ${wakander.statusDadosPessoaisViaFormulario}
                    </div>
                </div>
                <div class="info">
                    <span class="cabecalho-mobile">Status Cadastro:</span>
                    <div class="status ${statusClass(wakander.statusCadastro)}">
                        ${wakander.statusCadastro}
                    </div>
                </div>
            </div>
            <div class="card-user-actions">
                <div class="kebab-menu" onclick="toggleFiadorAction(this, event)">
                    <div class="kebab-dot"></div>
                    <div class="kebab-dot"></div>
                    <div class="kebab-dot"></div>
                </div>
                <button class="fiador-action-button" onclick="gerarLinkAtualizacao('${wakander.idWakander}', event)">alterar dados fiador</button>
            </div>
        </div>
        `;
    });

    divList.innerHTML = html;
}

async function preenchePagination(payload) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    for (let i = 1; i <= payload.totalPages; i++) {
        const button = document.createElement('button');
        button.textContent = i;
        button.className = 'page-button';
        if (payload.number + 1 === i) {
            button.classList.add('active');
        }
        button.addEventListener('click', () => mudaPagina(i-1));
        pagination.appendChild(button);
    }

}

function mudaPagina(page) {
    const section = document.getElementById('actions');
    section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    const valorSelecionado = select.value;
    if(onBusca) {
        fetchCardsWakandersPorQueryBusca(urlRequests, token, page, inputBusca.value);
    }else if(valorSelecionado == 1) {
        fetchCardsWakandersPorStatus(urlRequests, token, page, "COMPLETO");
    }else if(valorSelecionado == 2) {
        fetchCardsWakandersPorStatus(urlRequests, token, page, "INCOMPLETO");
    }else{
        fetchCardsWakanders(urlRequests, token, page);
    }
}

function extrairBaseUrlRequests(urlCompleta) {
    const url = new URL(urlCompleta);
    return `${url.protocol}`;
}

function preencheModalWakander(id) {
    arrayWakandersTela.forEach((wakander) => {
        if(wakander.idWakander == id) {
            document.getElementById('modalNome').innerHTML = wakander.nome || '-';
            document.getElementById('modalCpf').innerHTML = formataCpf(wakander.cpf) || '-';
            document.getElementById('modalMemberkit').innerHTML = wakander.idMemberKit || '-';
            document.getElementById('modalWhatsapp').innerHTML = formataTelefone(wakander.contato?.whatsapp) || '-';
            document.getElementById('modalEmail').innerHTML = wakander.contato?.email || '-';
            document.getElementById('modalNascimento').innerHTML = formatarDataISOParaBR(wakander.dataNascimento) || '-';
            document.getElementById('modalFinanceiro').innerHTML = wakander.financeiro?.status || '-';
            document.getElementById('modalIdAsaas').innerHTML = wakander.fiador?.idAsaas || '-';
            document.getElementById('modalAssinaturaAsaas').innerHTML = wakander.fiador?.idAssinatura || '-';
            document.getElementById('modalFiadorNome').innerHTML = wakander.fiador?.nome || '-';
            document.getElementById('modalFiadorCpf').innerHTML = wakander.fiador?.cpf || '-';
            document.getElementById('modalFiadorTelefone').innerHTML = wakander.fiador?.telefone || '-';
            document.getElementById('modalJornada').innerHTML = wakander.jornadaAtual || '-';
            document.getElementById('modalStatusCadastro').innerHTML = wakander.statusCadastro || '-';
        }
    })
}

function abrirInfoWakanders(id) {
    const modal = document.getElementById("modalOverlay");
    const wakanderModal = document.getElementById("wakanderModal");
    wakanderModal.style.opacity = "0";
    modal.style.display = "flex";
    setTimeout(() => wakanderModal.style.opacity = 1, 100);
    preencheModalWakander(id);
}

function limpaDadosDeBusca(){
    inputBusca.value = "";
    botaoBusca.innerText  = '🔍';
    onBusca = false;
}

function toggleFiadorAction(kebabEl, event) {
    if (event) {
        event.stopPropagation();
        event.preventDefault();
    }

    const actionButton = kebabEl?.nextElementSibling;
    if (!actionButton || !actionButton.classList.contains('fiador-action-button')) {
        return;
    }

    const willOpen = actionButton.style.display !== 'inline-flex';

    document.querySelectorAll('.fiador-action-button').forEach(btn => {
        btn.style.display = 'none';
    });

    actionButton.style.display = willOpen ? 'inline-flex' : 'none';
}

async function gerarLinkAtualizacao(wakanderId, event) {
    if (event) {
        event.stopPropagation();
        event.preventDefault();
    }

    document.querySelectorAll('.fiador-action-button').forEach(btn => {
        btn.style.display = 'none';
    });

    try {
        const response = await fetch(`/wakanda-ai/api/wakander/${wakanderId}/fiador/atualizacao-link`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error('Erro ao gerar o link de atualização');
        }

        const data = await response.json();
        if (typeof showLinkPopup === 'function') {
            showLinkPopup(data.link || 'Link não disponível');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Ocorreu um erro ao gerar o link de atualização');
    }
}

const verificarToken = async (urlBase) => {
    const token = localStorage.getItem('token');
    const loading = document.querySelector('.blackscreen');
    loading.style.display = 'flex';
    if (!token) {
        console.warn('Token não encontrado. Redirecionando para login...');
        localStorage.removeItem('token');
        window.location.href = `${urlBase}/wakanda-ai/api/painel-dados/login`;
        return;
    }

    try {
        const response = await fetch(`${urlBase}/wakanda-ai/api/autenticacao/token-teste`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            localStorage.removeItem('token');
            if (response.status === 403) {
                alert('Seu cadastro ainda não foi liberado pela liderança. Entre em contato com o suporte.');
            } else if (response.status === 401) {
                console.warn('Token inválido ou expirado. Redirecionando para login...');
            } else {
                console.warn('Não foi possível validar o token. Redirecionando para login...');
            }
            window.location.href = `${urlBase}/wakanda-ai/api/painel-dados/login`;
            return;
        }
        loading.style.display = 'none';
    } catch (error) {
        console.error('Erro na verificação do token:', error);
        localStorage.removeItem('token');
        window.location.href = `${urlBase}/wakanda-ai/api/painel-dados/login`;
    }
}

const fetchCardsWakanders = async (url, token, page) => {
    const loading = document.querySelector('.blackscreen');
    loading.style.display = 'flex';
    const incluirCancelados = document.getElementById('includeCancelled').checked;
    try {
        const response = await fetch(`${url}/wakanda-ai/api/wakander/busca-wakanders?sort=nome,asc&size=${size}&page=${page}&incluiCancelados=${incluirCancelados}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch wakanders`);
        }

        const payload = await response.json();
        await preencheCardsWakanders(payload);
        await preenchePagination(payload);
        loading.style.display = 'none';
    } catch (e) {
        console.error('Erro na busca de wakanders:', e);
        alert(e.message);
    }
};

const fetchCardsWakandersPorStatus = async (url, token, page, status) => {
    const incluirCancelados = document.getElementById('includeCancelled').checked;
    try {
        const response = await fetch(`${url}/wakanda-ai/api/wakander/busca-wakanders/${status}?sort=nome,asc&size=${size}&page=${page}&incluiCancelados=${incluirCancelados}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch wakanders`);
        }

        const payload = await response.json();
        await preencheCardsWakanders(payload);
        await preenchePagination(payload);
    } catch (e) {
        console.error('Erro na busca de wakanders:', e);
        alert(e.message);
    }
};

const fetchCardsWakandersPorQueryBusca = async (url, token, page, busca) => {
    const loading = document.querySelector('.blackscreen');
    loading.style.display = 'flex';
    const incluirCancelados = document.getElementById('includeCancelled').checked;
    try {
        const response = await fetch(`${url}/wakanda-ai/api/wakander/busca-wakanders?sort=nome,asc&size=${size}&page=${page}&busca=${busca}&incluiCancelados=${incluirCancelados}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch wakanders`);
        }
        const payload = await response.json();
        await preencheCardsWakanders(payload);
        await preenchePagination(payload);
        loading.style.display = 'none';
    } catch (e) {
        console.error('Erro na busca de wakanders:', e);
        alert(e.message);
        loading.style.display = 'none';

    }
};

const syncAsaasDataRequest = async () => {
    const loading = document.querySelector('.blackscreen');
    loading.style.display = 'flex';
    try {
        const response = await fetch(`${url}/wakanda-ai/api/wakander/atualiza-dados-asaas`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            loading.style.display = 'none';
            openPopup("❌", "Erro, verifique permissões do usuario");
            throw new Error(`Failed to request`);
        }
        loading.style.display = 'none';
        openPopup("✅", "Sincronização em andamento!");
    } catch (e) {
        console.error('Erro na verificação do token:', error);
        loading.style.display = 'none';
        openPopup("❌", "Erro ao fazer requisição, procure o suporte!");
    }
}

const syncFormularioRequest = async () => {
    const loading = document.querySelector('.blackscreen');
    loading.style.display = 'flex';
    try {
        const response = await fetch(`${url}/wakanda-ai/api/wakander/envia-formularios-wakanders`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            loading.style.display = 'none';
            openPopup("❌", "Erro, verifique permissões do usuario");
            throw new Error(`Failed to request`);
        }
        loading.style.display = 'none';
        openPopup("✅", "Sincronização em andamento!");
    } catch (e) {
        console.error('Erro na verificação do token:', error);
    }
}

const syncStatusWakanders = async () => {
    const loading = document.querySelector('.blackscreen');
    loading.style.display = 'flex';
    try {
        const response = await fetch(`${url}/wakanda-ai/api/wakander/atualiza-status-cadastro`, {
            method: 'PATCH',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            loading.style.display = 'none';
            openPopup("❌", "Erro, verifique permissões do usuario");
            throw new Error(`Failed to request`);
        }
        loading.style.display = 'none';
        openPopup("✅", "Sincronização em andamento!");
    } catch (e) {
        console.error('Erro na verificação do token:', error);
    }
}

const fetchEstatisticasWakanders = async (url, token) => {
    const incluirCancelados = document.getElementById('includeCancelled').checked;
    try {
        const response = await fetch(`${url}/wakanda-ai/api/wakander/estatistica-wakanders?incluiCancelados=${incluirCancelados}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch estatisticas`);
        }
        const payload = await response.json();
        await preencheCardsEstatisticas(payload);
    } catch (e) {
        console.error('Erro na verificação do token:', error);
    }
}

const urlRequests = extrairBaseUrlRequests(window.location.href);
const token = localStorage.getItem('token');const select = document.getElementById('statusSelect');
const botaoBusca = document.getElementById('search-button');
const inputBusca = document.getElementById("search-input");
const size = 8;
let onBusca = false;
let arrayWakandersTela = [];


fetchEstatisticasWakanders(urlRequests, token);
fetchCardsWakanders(urlRequests, token, 0);

select.addEventListener('change', () => {
    const valorSelecionado = select.value;
    limpaDadosDeBusca()
    if(valorSelecionado == 1) {
        fetchCardsWakandersPorStatus(urlRequests, token, 0, "COMPLETO");
    }else if(valorSelecionado == 2) {
        fetchCardsWakandersPorStatus(urlRequests, token, 0, "INCOMPLETO");
    }else{
        fetchCardsWakanders(urlRequests, token, 0);
    }
});
botaoBusca.addEventListener('click', () => {
    const value = inputBusca.value;
    if(!onBusca) {
        if(value != null && value.trim() !== "") {
            fetchCardsWakandersPorQueryBusca(urlRequests, token, 0, value);
            botaoBusca.innerText = '❌';
            onBusca = true;
        }
    }else{
        limpaDadosDeBusca();
        fetchCardsWakanders(urlRequests, token, 0);
    }

});
inputBusca.addEventListener('keypress', (event) => {
    if (event.key === "Enter") {
        const value = inputBusca.value;
        if(value != null && value.trim() !== "") {
            fetchCardsWakandersPorQueryBusca(urlRequests, token, 0, value);
            botaoBusca.innerText  = '❌';
            onBusca = true;
        }
    }
});
document.getElementById('listCards').addEventListener('click', function(event) {
    if (event.target.closest('.kebab-menu') || event.target.closest('.card-user-actions')) {
        return;
    }
    const card = event.target.closest('.card-user');
    if (card) {
        const id = card.dataset.id;
        abrirInfoWakanders(id);
    }
});

document.addEventListener('click', function(event) {
    if (event.target.closest('.card-user-actions')) {
        return;
    }
    document.querySelectorAll('.fiador-action-button').forEach(btn => {
        btn.style.display = 'none';
    });
});
document.getElementById('includeCancelled').addEventListener('click', function() {
    fetchEstatisticasWakanders(urlRequests, token);
    fetchCardsWakanders(urlRequests, token, 0)
    select.value = '0';
    limpaDadosDeBusca()

});


