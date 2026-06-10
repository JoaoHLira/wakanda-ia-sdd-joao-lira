const baseURL = window.location.origin;
const pathParts = window.location.pathname.split('/');
const token = pathParts[pathParts.length - 1];
const form = document.getElementById('cadastroForm');

function applyCpfCnpjMask(inputElement) {
    inputElement.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');

        if (value.length <= 11) {
            // Formato CPF: 000.000.000-00
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d)/, '$1.$2');
            value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        } else {
            // Formato CNPJ: 00.000.000/0000-00
            value = value.substring(0, 14);
            value = value.replace(/^(\d{2})(\d)/, '$1.$2');
            value = value.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
            value = value.replace(/\.(\d{3})(\d)/, '.$1/$2');
            value = value.replace(/(\d{4})(\d)/, '$1-$2');
        }

        e.target.value = value;
    });
}

function applyPhoneMask(inputElement) {
    inputElement.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 11) value = value.substring(0, 11);

        if (value.length > 10) {
            // Formato para celular: (00) 00000-0000
            value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1) $2-$3');
        } else if (value.length > 5) {
            // Formato para fixo: (00) 0000-0000
            value = value.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3');
        } else if (value.length > 2) {
            value = value.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
        } else if (value.length > 0) {
            value = value.replace(/^(\d*)/, '($1');
        }

        e.target.value = value;
    });
}

function applyCepMask(inputElement) {
    inputElement.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 8) value = value.substring(0, 8);
        value = value.replace(/^(\d{5})(\d{1,3})?/, '$1-$2');
        e.target.value = value;
    });
}

// Validação de formulário
function validateForm() {
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;

    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('border-red-500');
            isValid = false;
        } else {
            field.classList.remove('border-red-500');
        }
    });

    return isValid;
}

// Envio do formulário
form.addEventListener('submit', async function(event) {
    event.preventDefault();

    if (!validateForm()) {
        showMessage('Por favor, preencha todos os campos obrigatórios.', 'error');
        return;
    }

    const formData = {
        name: document.getElementById('nome').value,
        cpfCnpj: document.getElementById('cpfCnpj').value.replace(/\D/g, ''),
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value.replace(/\D/g, ''),
        mobilePhone: document.getElementById('mobilePhone').value.replace(/\D/g, ''),
        address: document.getElementById('address').value,
        addressNumber: document.getElementById('addressNumber').value,
        complement: document.getElementById('complement').value,
        province: document.getElementById('province').value,
        postalCode: document.getElementById('postalCode').value.replace(/\D/g, ''),
        externalReference: document.getElementById('idWakander') ? document.getElementById('idWakander').value : null,
        notificationDisabled: document.getElementById('notificationDisabled').checked
    };

    try {
        const response = await fetch(`${baseURL}/wakanda-ai/api/financeiro/assinaturas/fiador/${token}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao atualizar os dados');
        }

        showMessage('Dados atualizados com sucesso!', 'success');

        setTimeout(() => {
            window.location.replace('/wakanda-ai/api/formulario/resposta-atualiza-fiador');
        }, 1000);

    } catch (error) {
        console.error('Erro:', error);
        showMessage(error.message || 'Ocorreu um erro ao processar sua solicitação', 'error');
    }
});

// Função para exibir mensagens
function showMessage(message, type) {
    const messageArea = document.getElementById('messageArea');
    if (!messageArea) return;

    messageArea.textContent = message;
    messageArea.className = `mt-4 p-3 rounded-lg text-center font-medium ${
        type === 'success' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
    }`;
    messageArea.classList.remove('hidden');

    // Esconde a mensagem após 5 segundos
    setTimeout(() => {
        messageArea.classList.add('hidden');
    }, 5000);
}

// Inicialização quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', function() {
    // Aplica máscaras
    const cpfCnpjInput = document.getElementById('cpfCnpj');
    const phoneInputs = document.querySelectorAll('.phone-mask');
    const cepInput = document.getElementById('postalCode');

    if (cpfCnpjInput) applyCpfCnpjMask(cpfCnpjInput);
    if (phoneInputs.length) {
        phoneInputs.forEach(input => applyPhoneMask(input));
    }
    if (cepInput) applyCepMask(cepInput);

    // Remove a classe de erro quando o usuário começa a digitar
    form.querySelectorAll('input').forEach(input => {
        input.addEventListener('input', function() {
            if (this.value.trim() !== '') {
                this.classList.remove('border-red-500');
            }
        });
    });
});
