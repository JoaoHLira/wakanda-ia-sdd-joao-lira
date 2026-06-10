function applySavedTheme() {
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
}

function toggleTheme() {
    const body = document.body;
    const button = document.querySelector('.theme-toggle');
    const isDark = body.classList.toggle('dark-mode');
    localStorage.setItem('tema', isDark ? 'dark' : 'light');
    button.textContent = isDark ? '☀️' : '🌙';
}

function closeModal() {
    const modal = document.getElementById("modalOverlay");
    const wakanderModal = document.getElementById("wakanderModal");
    wakanderModal.style.opacity = 0;
    setTimeout(() => modal.style.display = "none", 250);
}

function openConfirmModal() {
    document.getElementById("confirm-modal").style.display = "flex";
    setTimeout(() => document.getElementById("confirm-modal-1").style.opacity = 1, 250);
}

function closeConfirmModal() {
    document.getElementById("confirm-modal-1").style.opacity = 0
    setTimeout(() => document.getElementById("confirm-modal").style.display = "none", 250);
}

function handleConfirm() {
    closeConfirmModal();
    syncFormularioRequest();
}

document.getElementById("sync-wakander").addEventListener("click", openConfirmModal);

function openAsaasModal() {
    document.getElementById("asaas-confirm-modal").style.display = "flex";
    setTimeout(() => document.getElementById("modal-asaas").style.opacity = 1, 250);

}

function closeAsaasModal() {
    document.getElementById("modal-asaas").style.opacity = 0;
    setTimeout(() =>  document.getElementById("asaas-confirm-modal").style.display = "none", 250);

}

function handleAsaasConfirm() {
    closeAsaasModal();
    syncAsaasDataRequest();
}

document.getElementById("sync-asaas").addEventListener("click", openAsaasModal);


function openStatusModal() {
    document.getElementById("status-confirm-modal").style.display = "flex";
    setTimeout(() => document.getElementById("modal-status").style.opacity = 1, 250);

}

function closeStatusModal() {
    document.getElementById("modal-status").style.opacity = 0;
    setTimeout(() =>  document.getElementById("status-confirm-modal").style.display = "none", 250);
}

function openPopup(icon, message){
    document.getElementById("popup-icon").innerHTML = icon;
    document.getElementById("popup-message").innerHTML = message;
    document.getElementById("popup-modal").style.display = "flex"
    setTimeout(() => document.getElementById("popup").style.opacity = 1, 250);
}
function closePopup() {
    document.getElementById("popup").style.opacity = 0;
    setTimeout(() =>  document.getElementById("popup-modal").style.display = "none", 250);
    window.location.reload();
}


function handleStatusConfirm() {
    closeStatusModal();
    syncStatusWakanders();
}

document.getElementById("sync-status").addEventListener("click", openStatusModal);

document.addEventListener('DOMContentLoaded', () => {
    applySavedTheme();
    verificarToken(url);
});

function extrairBaseUrl(urlCompleta) {
    const url = new URL(urlCompleta);
    return `${url.protocol}//${url.host}`;
}

document.getElementById("sair").addEventListener("click", ()=>{
    localStorage.removeItem('token');
    verificarToken(url);

})


const url = extrairBaseUrl(window.location.href);


