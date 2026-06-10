var numeroSuporte = '557399206450';
var btn = document.getElementById('btnSuporte');
var nome = btn.getAttribute('data-nome') || 'Wakander';
var mensagem = encodeURIComponent('Olá, eu sou o ' + nome + ' e preciso de ajuda!');
btn.onclick = function () {
    window.open('https://wa.me/' + numeroSuporte + '?text=' + mensagem, '_blank');
}
