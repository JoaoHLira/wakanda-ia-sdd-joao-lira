const numeroSuporte = '557399206450';
const btn = document.getElementById('btnSuporte');
const mensagem = encodeURIComponent('Olá! Estou com problemas para completar o cadastro. Poderia me ajudar?');

btn.onclick = function() {
    window.open('https://wa.me/' + numeroSuporte + '?text=' + mensagem, '_blank');
} 