(function() {
    if (!localStorage.getItem('token')) {
        const url = new URL(window.location.href);
        window.location.href = `${url.protocol}//${url.host}/wakanda-ai/api/painel-dados/login`;
    }
})();

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("sair").addEventListener("click", () => {
        localStorage.removeItem('token');

        window.location.href = '/wakanda-ai/api/painel-dados/login';
    });
});
