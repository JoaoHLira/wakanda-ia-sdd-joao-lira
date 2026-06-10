async function enviarDados() {

    const nome = document.getElementById("nome").value;
    const cpf = document.getElementById("cpf").value;
    const dataNascimento = document.getElementById("data-nascimento").value;
    const whatsapp = document.getElementById("whatsapp").value;
    const email = document.getElementById("email").value;
    const dadosWakander = JSON.parse(localStorage.getItem("dadosWakander"))

    const telaEscura = document.getElementById("blackScreen");
    const loader = document.querySelector('.loader');
    const modalFalha = document.getElementById('modal-erro');
    const modalSucesso = document.getElementById('modal-sucess');
    const messageErro = document.getElementById('mensagemStatusErro');
    const messageSucesso = document.getElementById('mensagemStatusSucesso');

    const endPoint = "/wakanda-ai/api/wakander/dados-wakander"
    const url = localStorage.getItem("baseUrl") + endPoint;
    const dados = {
        idWakander: dadosWakander.idWakander,
        nome,
        cpf: extrairNumeros(cpf),
        dataNascimento,
        whatsapp: extrairNumeros(whatsapp),
        email
    };

    telaEscura.style.display = "flex";
    loader.style.display = "flex";

    try {
        const resposta = await fetch(url, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dados)
        });

        if (!resposta.ok) {
            const errorData = await resposta.json();
            throw new Error(errorData.message || `Erro na requisição: ${resposta.status}`);
        }

        loader.style.display = "none";
        modalSucesso.style.display = "flex";
        messageSucesso.innerHTML = "Dados atualizado com sucesso!";

    } catch (erro) {
        console.log(erro);
        loader.style.display = "none";
        modalFalha.style.display = "flex";
        messageErro.innerHTML = erro != null ? erro.message : "Erro ao atualizar dados, revise as informações e tente novamente";
    }
}

function extrairNumeros(str) {
    return str.replace(/\D/g, '');
}

document.getElementById("enviarFormulario").addEventListener("click", enviarDados);