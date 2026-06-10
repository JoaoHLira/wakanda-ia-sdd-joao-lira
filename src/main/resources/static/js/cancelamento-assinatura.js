const MENSAGENS = {
  SUCESSO_CANCELAMENTO: 'Sua assinatura foi cancelada com sucesso.',
  SUCESSO_DESISTENCIA: 'Ótimo! Seu cancelamento foi revertido com sucesso. Bem-vindo de volta à sua Jornada!',
  AVISO_MOTIVO: 'Por favor, selecione o motivo do cancelamento para prosseguir.',
  ERRO_TECNICO: 'Estamos com dificuldades técnicas. Por favor, tente novamente mais tarde ou entre em contato com o Suporte.'
};

function mostrarMensagem(tipo, texto) {
  const div = document.getElementById('mensagemFeedback');
  div.className = tipo;
  div.style.display = 'block';
  div.textContent = texto;
}

async function enviarRequisicao(endpoint, payload, acao) {
  try {
    const res = await fetch(endpoint, {
      method: 'PATCH',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(payload)
    });

    if (res.status === 204) {
      if (acao === 'cancelar') {
        mostrarMensagem('sucesso', MENSAGENS.SUCESSO_CANCELAMENTO);
      } else if (acao === 'desistir') {
        mostrarMensagem('sucesso', MENSAGENS.SUCESSO_DESISTENCIA);
      }
      return;
    }

    let data;
    try {
      data = await res.json();
    } catch {}

    if (data && data.message) {
      mostrarMensagem('aviso', data.message);
    } else {
      mostrarMensagem('erro', MENSAGENS.ERRO_TECNICO);
    }
  } catch {
    mostrarMensagem('erro', MENSAGENS.ERRO_TECNICO);
  } finally {
    document.getElementById('loadingSpinner').style.display = 'none';
    const botoes = document.querySelectorAll('#cancelamentoForm button[type="submit"]');
    botoes.forEach(b => b.disabled = false);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  const dataInput = document.getElementById('dataCancelamento');
  const hoje = new Date().toISOString().slice(0, 10);
  dataInput.value = hoje;
});

document.getElementById('cancelamentoForm').addEventListener('submit', async (e) => {
  const form = e.target;
  e.preventDefault();

  const btn = e.submitter;
  if (!btn) return;

  const botoes = form.querySelectorAll('button[type="submit"]');
  botoes.forEach(b => b.disabled = true);
  document.getElementById('loadingSpinner').style.display = 'flex';

  if (btn.value === 'cancelar') {
    const motivo = document.getElementById('motivoCancelamento').value;
    if (!motivo) {
      mostrarMensagem('aviso', MENSAGENS.AVISO_MOTIVO);
      botoes.forEach(b => b.disabled = false);
      document.getElementById('loadingSpinner').style.display = 'none';
      return;
    }
  }

  const idWakander = document.getElementById('idWakander').value;
  const payload = {
    idWakander,
    motivoCancelamento: document.getElementById('motivoCancelamento').value,
    dataCancelamento: document.getElementById('dataCancelamento').value
  };

  if (btn.value === 'cancelar') {
    await enviarRequisicao(`/wakanda-ai/api/wakander/${idWakander}/cancela-assinatura`, payload, 'cancelar');
  } else if (btn.value === 'desistir') {
    await enviarRequisicao(`/wakanda-ai/api/wakander/${idWakander}/reverte-cancelamento`, payload, 'desistir');
  }
});