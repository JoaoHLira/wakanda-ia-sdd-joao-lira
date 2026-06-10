package academy.wakanda.wakanda_ai.jornadawakander.infra;

public interface JornadaWakanderClient {
    <T> T requisicaoPostParaOMemberKit(Object request, Class<T> responseType);
}
