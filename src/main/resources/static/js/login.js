function toggleTheme() {
    const body = document.body;
    const button = document.querySelector('.theme-toggle');
    const isDarkMode = body.classList.toggle('dark-mode');


    button.textContent = isDarkMode ? '☀️' : '🌙';


    localStorage.setItem('tema', isDarkMode ? 'dark' : 'light');
}

window.addEventListener('DOMContentLoaded', () => {
    const savedTheme = localStorage.getItem('tema');
    const body = document.body;
    const button = document.querySelector('.theme-toggle');

    if (savedTheme === 'dark') {
        body.classList.add('dark-mode');
        button.textContent = '☀️';
    } else {
        body.classList.remove('dark-mode');
        button.textContent = '🌙';
    }

    if(localStorage.getItem("token")){
        const url = extrairBaseUrl(window.location.href);
        window.location.href = url + "/wakanda-ai/api/gameficacao/home";
    }
});

async function login() {
    const username = document.getElementById('email').value;
    const senha = document.getElementById('senha').value;
    const url = extrairBaseUrl(window.location.href);
    const endPoint = "/wakanda-ai/api/autenticacao/login";
    const loading = document.querySelector('.blackscreen');
    const messageError = document.getElementById('message-erro');

    messageError.style.display = 'none';
    loading.style.display = 'flex';

    try {
        const response = await fetch(url + endPoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, senha })
        });

        if (!response.ok) {
            const errorBody = await response.json();
            const errorMessage = errorBody.message || 'Erro desconhecido.';

            throw new Error(errorMessage);
        }

        const json = await response.json();
        localStorage.setItem('token', json.token);
        window.location.href = url + "/wakanda-ai/api/gameficacao/home";

    } catch (error) {
        loading.style.display = 'none';
        messageError.innerText = error.message;
        messageError.style.display = 'block';
        console.error('Erro na requisição POST:', error.message);
    }
}


function extrairBaseUrl(urlCompleta) {
    const url = new URL(urlCompleta);
    return `${url.protocol}//${url.host}`;
}

document.addEventListener('keydown', function(event) {
    if (event.key === 'Enter') {
        login();
    }
});
