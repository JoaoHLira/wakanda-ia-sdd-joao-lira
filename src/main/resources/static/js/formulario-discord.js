document.addEventListener("DOMContentLoaded", function () {
  let emailErrorCount = 0;
  const helpButton = document.getElementById("helpButton");
  const discordForm = document.getElementById("discordForm");
  const emailInput = document.getElementById("email");
  const themeCheckbox = document.getElementById("checkbox");
  const numeroSuporte = '557399206450';

  const nome = document.body.dataset.username || "Usuário";
  const idDiscord = document.body.dataset.iddiscord || "";

  function applySavedTheme() {
    const savedTheme = localStorage.getItem('discordTheme') || 'dark';
    if (savedTheme === 'dark') {
      document.body.classList.add('dark-mode');
      document.body.classList.remove('light-mode');
      themeCheckbox.checked = false;
    } else {
      document.body.classList.add('light-mode');
      document.body.classList.remove('dark-mode');
      themeCheckbox.checked = true;
    }
  }

  applySavedTheme();

  themeCheckbox.addEventListener("change", function () {
    if (this.checked) {
      document.body.classList.remove('dark-mode');
      document.body.classList.add('light-mode');
      localStorage.setItem('discordTheme', 'light');
    } else {
      document.body.classList.remove('light-mode');
      document.body.classList.add('dark-mode');
      localStorage.setItem('discordTheme', 'dark');
    }
  });

  discordForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const botao = this.querySelector("button[type='submit']");
    const textoOriginal = "VALIDAR";

    botao.disabled = true;
    botao.innerHTML = `<span class="loading-spinner">⏳</span> Aguarde`;

    document.getElementById("mensagemErro").style.display = "none";
    document.getElementById("mensagemSucesso").style.display = "none";

    const email = emailInput.value;
    const payload = { nome, idDiscord, email };

    try {
      const resposta = await fetch("/wakanda-ai/api/wakander/jornada/associar-discord", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      const respostaTexto = await resposta.text();

      if (resposta.ok) {
        if (respostaTexto.includes("já foi")) {
          const sucessoEl = document.getElementById("mensagemSucesso");
          sucessoEl.innerText = "Seu discord já foi validado!";
          sucessoEl.style.display = "block";
          document.getElementById("mensagemErro").style.display = "none";
        }

        setTimeout(() => {
          window.location.href = "/wakanda-ai/api/formulario/resposta-discord";
        }, 1700);
        emailErrorCount = 0;
        helpButton.style.display = "none";
      } else {
        emailErrorCount++;
        mostrarErroDepoisDeDelay(respostaTexto);
      }
    } catch (erro) {
      emailErrorCount++;
      mostrarErroDepoisDeDelay(erro.message);
    }

    function mostrarErroDepoisDeDelay(mensagem) {
      setTimeout(() => {
        const erroEl = document.getElementById("mensagemErro");
        const texto = extrairMensagemErro(mensagem);
        document.getElementById("erroTexto").innerText = texto;
        erroEl.style.display = "flex";
        document.getElementById("mensagemSucesso").style.display = "none";

        botao.disabled = false;
        botao.innerHTML = textoOriginal;

        if (emailErrorCount >= 3) {
          helpButton.style.display = "block";
        }
      }, 1700);
    }
  });

  helpButton.addEventListener("click", function () {
    const email = emailInput.value || "Não informado";
    const nomeAjuda = nome || "Não_Identificado";

    const now = new Date();
    const hours = now.getHours();
    let greeting = "Olá!";
    if (hours >= 5 && hours < 12) {
      greeting = "Bom dia!";
    } else if (hours >= 12 && hours < 18) {
      greeting = "Boa tarde!";
    } else {
      greeting = "Boa noite!";
    }

    const message = `${greeting} 🍵\n\nNão estou conseguindo validar meu Discord no formulário.\n*👤 Username*: ${nomeAjuda}\n*📧 Email*: ${email}\n\nℹ️ Poderiam me ajudar, por favor?`;
    const whatsappUrl = `https://wa.me/${numeroSuporte}?text=${encodeURIComponent(message)}`;
    window.open(whatsappUrl, '_blank');
  });

  function extrairMensagemErro(mensagem) {
    try {
      const json = JSON.parse(mensagem);
      return json.message || "Erro inesperado ao validar Discord.";
    } catch {
      return mensagem.includes("Usuario já foi associado")
        ? "Seu discord já foi validado!"
        : mensagem.toLowerCase().replace("error:", "").trim();
    }
  }
});
