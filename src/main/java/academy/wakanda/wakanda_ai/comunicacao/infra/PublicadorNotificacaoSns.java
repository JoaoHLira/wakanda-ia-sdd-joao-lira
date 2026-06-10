package academy.wakanda.wakanda_ai.comunicacao.infra;

public interface PublicadorNotificacaoSns {
    <T> void enviaNotificacaoSns(String groupId, T payload, String topic);
}
