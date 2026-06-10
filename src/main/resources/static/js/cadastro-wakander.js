const baseURL = window.location.origin;
const pathParts = window.location.pathname.split('/');
const token = pathParts[pathParts.length - 1];

const form = document.getElementById('wakander-form');

function applyCpfMask(inputElement) {
    inputElement.addEventListener('input', function (e) {
        let value = e.target.value.replace(/\D/g, '');
        if (value.length > 11) value = value.slice(0, 11);

        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d)/, '$1.$2');
        value = value.replace(/(\d{3})(\d{1,2})$/, '$1-$2');

        e.target.value = value;
    });
}

function initPhoneInput(inputElement) {
    return window.intlTelInput(inputElement, {
        initialCountry: "br",
        separateDialCode: true,
        preferredCountries: ["br", "us", "fr", "it", "pt", "es"],
        utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
        customPlaceholder: function(selectedCountryPlaceholder, selectedCountryData) {
            return "Ex: " + selectedCountryPlaceholder;
        }
    });
}

const studentPhoneInput = document.getElementById('student-phone');
const itiStudent = initPhoneInput(studentPhoneInput);

const errorMessage = document.querySelector('#student-phone + .input-error-message');

studentPhoneInput.addEventListener('blur', function() {
    if (!itiStudent.isValidNumber()) {
        studentPhoneInput.classList.add('iti--invalid');
        if (errorMessage) errorMessage.style.display = 'block';
    } else {
        studentPhoneInput.classList.remove('iti--invalid');
        if (errorMessage) errorMessage.style.display = 'none';
    }
});

studentPhoneInput.addEventListener('input', function() {
    if (itiStudent.isValidNumber()) {
        studentPhoneInput.classList.remove('iti--invalid');
        if (errorMessage) errorMessage.style.display = 'none';
    }
});

form.addEventListener('submit', async function(event) {
    event.preventDefault();
    if (!itiStudent.isValidNumber()) {
        alert("Número de WhatsApp inválido! Corrija o campo.");
        studentPhoneInput.focus();
        return;
    }

    const cpfError = document.getElementById('cpf-error-message');
    if (cpfError) cpfError.style.display = 'none';

    const whatsappNumeros = itiStudent.getNumber().replace(/^\+/, '');
    const data = {
        nomeFiador: document.getElementById('guarantor-name').value,
        idWakander: token,
        nome: document.getElementById('student-name').value,
        cpf: document.getElementById('student-cpf').value.replace(/\D/g, ''),
        dataNascimento: document.getElementById('data-nascimento').value,
        whatsapp: whatsappNumeros,
        email: document.getElementById('student-email').value,
    };
    try {
        const url = `${baseURL}/wakanda-ai/api/wakander/cadastro/${token}`;
        const headers = { 'Content-Type': 'application/json' };
        const response = await fetch(url, {
            method: 'PATCH',
            headers: headers,
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            const error = await response.json();

            if (response.status === 400 && error.message && error.message.includes('CPF')) {
                if (cpfError) {
                    cpfError.textContent = error.message;
                    cpfError.style.display = 'block';
                } else {
                    alert(error.message);
                }
                return;
            }

            if (response.status === 409 && error.message && error.message.includes('CPF')) {
                document.getElementById('wakander-form').style.display = 'none';
                document.getElementById('cpf-ja-cadastrado').style.display = 'block';
                var welcome = document.querySelector('.welcome-container');
                if (welcome) welcome.style.display = 'none';
                return;
            }
            throw new Error(error.message || 'Erro desconhecido');
        }
        alert('Cadastro completado com sucesso!');
    } catch (error) {
        console.error('Error:', error);
        alert('Erro ao enviar dados. Tente novamente.');
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const autofillToggle = document.getElementById('autofill-toggle');
    const undoBanner = document.getElementById('undo-banner');
    const undoAutofill = document.getElementById('undo-autofill');
    const cpfInput = document.getElementById('student-cpf');
    const guarantorName = document.getElementById('guarantor-name');
    const guarantorCpf = document.getElementById('guarantor-cpf');
    const guarantorPhone = document.getElementById('guarantor-phone');
    const studentName = document.getElementById('student-name');
    const studentCpf = document.getElementById('student-cpf');
    const studentEmail = document.getElementById('student-email');
    const studentBirth = document.getElementById('data-nascimento');
    
    applyCpfMask(guarantorCpf);
    applyCpfMask(studentCpf);
    
    function validateCpf(cpf) {
        cpf = cpf.replace(/\D/g, '');
        
        if (cpf.length !== 11) return false;
        
        if (/^(\d)\1{10}$/.test(cpf)) return false;
        
        let sum = 0;
        for (let i = 0; i < 9; i++) {
            sum += parseInt(cpf.charAt(i)) * (10 - i);
        }
        let remainder = (sum * 10) % 11;
        if (remainder === 10 || remainder === 11) remainder = 0;
        if (remainder !== parseInt(cpf.charAt(9))) return false;
        
        sum = 0;
        for (let i = 0; i < 10; i++) {
            sum += parseInt(cpf.charAt(i)) * (11 - i);
        }
        remainder = (sum * 10) % 11;
        if (remainder === 10 || remainder === 11) remainder = 0;
        if (remainder !== parseInt(cpf.charAt(10))) return false;
        
        return true;
    }
    
    function validateCpfInput() {
        const cpfValue = cpfInput.value;
        const cpfNumbers = cpfValue.replace(/\D/g, '');
        const cpfErrorElement = document.getElementById('cpf-error-message');
        
        if (cpfNumbers.length === 0) {
            cpfInput.setCustomValidity('');
            cpfInput.classList.remove('invalid-cpf');
            if (cpfErrorElement) cpfErrorElement.style.display = 'none';
            return;
        }
        
        if (cpfNumbers.length !== 11) {
            cpfInput.setCustomValidity('CPF deve ter 11 números');
            cpfInput.classList.add('invalid-cpf');
            if (cpfErrorElement) {
                cpfErrorElement.textContent = 'CPF deve ter 11 números';
                cpfErrorElement.style.display = 'block';
            }
            return;
        }
        
        if (!validateCpf(cpfValue)) {
            cpfInput.setCustomValidity('CPF inválido');
            cpfInput.classList.add('invalid-cpf');
            if (cpfErrorElement) {
                cpfErrorElement.textContent = 'CPF inválido';
                cpfErrorElement.style.display = 'block';
            }
            return;
        }
        
        cpfInput.setCustomValidity('');
        cpfInput.classList.remove('invalid-cpf');
        if (cpfErrorElement) cpfErrorElement.style.display = 'none';
    }
    
    cpfInput.addEventListener('input', validateCpfInput);
    cpfInput.addEventListener('blur', validateCpfInput);

    if (autofillToggle && undoBanner) {
        autofillToggle.addEventListener('change', function() {
            if (this.checked) {
                if (studentName && guarantorName) studentName.value = guarantorName.value;
                if (studentCpf && guarantorCpf) studentCpf.value = guarantorCpf.value;
                if (guarantorPhone && studentPhoneInput) itiStudent.setNumber(guarantorPhone.value);
                undoBanner.style.display = 'block';
            } else {
                if (studentName) studentName.value = '';
                if (studentCpf) studentCpf.value = '';
                if (studentPhoneInput) itiStudent.setNumber('');
                if (studentEmail) studentEmail.value = '';
                undoBanner.style.display = 'none';
            }
        });
    }
    if (undoAutofill && autofillToggle) {
        undoAutofill.addEventListener('click', function() {
            autofillToggle.checked = false;
            autofillToggle.dispatchEvent(new Event('change'));
        });
    }
});

document.querySelectorAll('#student-name, #student-cpf, #student-email, #student-phone')
    .forEach(field => {
        field.addEventListener('input', function() {
            if (this.value.trim() !== '' && this.classList.contains('autofilled-field')) {
                this.classList.remove('autofilled-field');
            }
        });
});
