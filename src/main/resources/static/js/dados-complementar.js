document.addEventListener("DOMContentLoaded", function () {
   localStorage.setItem("baseUrl", extrairBaseUrl(window.location.href));

    buscarDadosWakander();

    async function buscarDadosWakander() {
        const telaEscura = document.getElementById("blackScreen");
        const loader = document.querySelector('.loader');
        const modalFalha = document.getElementById('modal-erro');
        const modalSucesso = document.getElementById('modal-sucess');
        const messageErro = document.getElementById('mensagemStatusErro');
        const messageSucesso = document.getElementById('mensagemStatusSucesso');

        const baseUrl = localStorage.getItem("baseUrl");
        const id = window.location.pathname.split('/').pop();

        telaEscura.style.display = "flex";
        loader.style.display = "flex";

        try {
            const resposta = await fetch(`${baseUrl}/wakanda-ai/api/wakander/dado-oculto/${id}`);

            if (!resposta.ok) {
                const errorData = await resposta.json();
                throw new Error(errorData.message || `Erro na requisição: ${resposta.status}`);
            }

            const dados = await resposta.json();
            preencheForm(dados);
            localStorage.setItem("dadosWakander", JSON.stringify(dados));

            loader.style.display = "none";
            telaEscura.style.display = "none";
        } catch (erro) {
            console.error(erro.message);
            loader.style.display = "none";
            modalSucesso.style.display = "flex";
            messageSucesso.innerHTML = erro.message;
        }
    }


    function extrairBaseUrl(urlCompleta) {
        const url = new URL(urlCompleta);
        return `${url.protocol}`;
    }

    function formatarCpf(cpf) {
        if (!cpf) return '';

        cpf = cpf.replace(/[^\d*]/g, '');

        if (cpf.length !== 11) return cpf;

        return (
            cpf.substring(0, 3) + '.' +
            cpf.substring(3, 6) + '.' +
            cpf.substring(6, 9) + '-' +
            cpf.substring(9, 11)
        );
    }


    function formatarTelefone(numero) {
        if (!numero) return '';


        numero = numero.replace(/[^\d*]/g, '');


        if (numero.length < 13) return numero;


        return (
            '+' + numero.substring(0, 2) + ' (' +
            numero.substring(2, 4) + ') ' +
            numero.substring(4, 5) + ' ' +
            numero.substring(5, 9) + '-' +
            numero.substring(9, 13)
        );
    }


    function preencheForm(dados) {

        if (dados == null) {
            throw new Error('Nenhum dado!');
        }

        document.getElementById("nome").value = dados.nome;

        function preencherCampo(id, valor, formatador = null) {
            if (valor != null && valor.trim() !== "") {
                ''
                const campo = document.getElementById(id);
                campo.readOnly = true;
                campo.disabled = true;
                campo.value = formatador ? formatador(valor) : valor;
            }
        }

        preencherCampo("cpf", dados.cpf, formatarCpf);
        preencherCampo("data-nascimento", dados.dataNascimento);
        preencherCampo("whatsapp", dados.contato?.whatsapp, formatarTelefone);
        preencherCampo("email", dados.contato?.email);
    }


    function aplicarMascaraCpf(input) {
        let valor = input.value;

        valor = valor.replace(/\D/g, '');

        valor = valor.slice(0, 11);

        if (valor.length >= 10) {
            valor = valor.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
        } else if (valor.length >= 7) {
            valor = valor.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
        } else if (valor.length >= 4) {
            valor = valor.replace(/(\d{3})(\d{1,3})/, "$1.$2");
        }

        input.value = valor;
    }

    const whatsappInput = document.getElementById('whatsapp');

    whatsappInput.value = '+55 ';

    function formatPhoneNumber(value) {
        if (!value.startsWith('+55')) {
            value = '+55 ' + value;
        }

        let numeros = value.replace(/\D/g, '').substring(2);

        if (numeros.length > 11) {
            numeros = numeros.substring(0, 11);
        }

        let formatado = '+55 ';
        if (numeros.length > 0) formatado += `(${numeros.substring(0, 2)}`;
        if (numeros.length >= 2) formatado += `) `;
        if (numeros.length >= 3) formatado += `${numeros.substring(2, 3)} `;
        if (numeros.length >= 4) formatado += `${numeros.substring(3, 7)}`;
        if (numeros.length >= 7) formatado += `-${numeros.substring(7)}`;

        return formatado;
    }

    whatsappInput.addEventListener('keydown', function (e) {
        const allowedKeys = [
            'Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'Tab',
            'Home', 'End'
        ];

        const prefixLength = '+55 '.length;
        if (e.target.selectionStart < prefixLength && !allowedKeys.includes(e.key)) {
            e.preventDefault();
            return;
        }

        if (!allowedKeys.includes(e.key) && !/^\d$/.test(e.key)) {
            e.preventDefault();
        }
    });

    whatsappInput.addEventListener('blur', function (e) {
        e.target.value = formatPhoneNumber(e.target.value);
    });

    whatsappInput.addEventListener('focus', function (e) {
        if (!e.target.value.startsWith('+55')) {
            e.target.value = '+55 ';
        }
    });
});