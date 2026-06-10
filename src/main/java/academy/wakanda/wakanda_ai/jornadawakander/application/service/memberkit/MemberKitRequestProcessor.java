package academy.wakanda.wakanda_ai.jornadawakander.application.service.memberkit;

public interface MemberKitRequestProcessor {
    boolean validaSeProcessa(MemberKitTipoRequisicao tipo);
    void processaEvento(MemberKitMessageEnvelope envelope);
}
