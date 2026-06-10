package academy.wakanda.wakanda_ai.comunicacao.application.service.whatsapp;

public interface ComunicacaoWhatsappService {

    void processaPorTipoMensagem(ZApiEventDto message);
}
