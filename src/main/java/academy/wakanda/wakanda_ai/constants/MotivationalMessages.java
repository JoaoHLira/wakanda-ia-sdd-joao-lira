package academy.wakanda.wakanda_ai.constants;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum MotivationalMessages {

    MENSAGEM_UM("Opa, *Wakander*!"
        + "\nVi que você não conseguiu assistir a nenhuma aula recentemente, mas relaxa. Isso acontece e você não tá sozinho nessa."
        + "\n\nO importante é seguir no seu ritmo, e a gente tá aqui pra ajudar no que precisar!😎"
        + "\n\nAh, e cola lá no nosso Discord, pode mandar suas dúvidas que o pessoal tá sempre pronto pra dar aquela força!🚀🚀"),

    MENSAGEM_DOIS("Fala, *Wakander*!"
        + "\nA jornada do aprendizado tem seus altos e baixos, mas cada passo é válido e cheio de propósito."
        + "\n\nVocê não está sozinho! Estamos aqui para te ajudar a conquistar seus objetivos, no seu tempo e no se ritmo."
        + "\n\nAh, e lembra: no nosso Discord tem uma galera top pronta para te dar aquela força!🚀"),

    MENSAGEM_TRES("Ei, *Wakander*!"
        + "\nÀs vezes, o caminho parece calmo, mas não se engane: é no silêncio que a mente cresce.🌱"
        + "\n\nContinue avançando, porque cada movimento, por menor que pareça, te leva mais perto do seu destino."
        + "\n\nEstamos juntos nessa jornada, e no Discord tem uma comunidade incrível te esperando para somar!🚀"),

    MENSAGEM_QUATRO("Salve, *Wakander*!"
        + "\nGrandes conquistas começam com pequenos passos. Se hoje foi mais tranquilo, amanhã pode ser revolucionário.😎"
        + "\n\nConfie no processo, celebre cada avanço e lembre-se: estamos aqui para tudo o que precisar!"
        + "\n\nAh, dá uma passada no nosso Discord e conecta com quem pode te inspirar ainda mais.🚀"),

    MENSAGEM_CINCO("Fala, *Wakander*!"
        + "\nMesmo os dias mais serenos carregam a força de uma tempestade por vir. Você está no caminho certo, mesmo quando parece estar parado.😉"
        + "\n\nO importante é continuar, e saiba que nunca falta apoio por aqui!"
        + "\n\nDá um pulo no Discord, troca ideia e descobre novas inspirações para seguir em frente.🚀"),

    MENSAGEM_SEIS("Eaí, *Wakander*!"
        + "\nA trilha do conhecimento é como um rio: às vezes corre rápido, às vezes é calmo, mas sempre segue em frente. 🌊"
        + "\n\nConfie no processo, avance no seu ritmo, e conte com a gente sempre que precisar!"
        + "\n\nLembra também de dar uma passada no Discord. A galera lá tá sempre pronta para dar aquele empurrãozinho extra.🚀"),

    MENSAGEM_SETE("Fala, *Wakander*!"
        + "\nNotei que você está em um momento de pausa nos estudos, mas fica tranquilo. Isso é super normal e faz parte do processo!😉"
        + "\n\nO importante é seguir no seu ritmo. Estamos aqui para te apoiar no que precisar!😎"
        + "\n\nAh, aproveita e dá uma passada no nosso Discord! Lá você pode tirar dúvidas e trocar ideia com a galera que está sempre pronta para ajudar.🚀"),

    MENSAGEM_OITO("Salve, *Wakander*!"
        + "\nO caminho do aprendizado é feito de curvas, pausas e avanços. Cada momento importa, até mesmo os de descanso."
        + "\n\nSe precisar de um empurrãozinho, conta com a gente! Estamos aqui pra caminhar ao seu lado."
        + "\n\nE não esquece de dar um pulo no nosso Discord. Lá, a troca de ideias é combustível para seguir adiante.🚀"),

    MENSAGEM_NOVE("Opa, *Wakander*!"
        + "\nÀs vezes, a estrada parece longa, mas lembra: cada passo que você dá é uma vitória."
        + "\n\nVai no seu ritmo, respeite seu tempo e saiba que não está sozinho nessa jornada."
        + "\n\nE sempre que precisar, a galera do Discord tá lá pronta pra somar e te inspirar ainda mais.🚀"),

    MENSAGEM_DEZ("Eaí, *Wakander*!"
        + "\nO aprendizado é como plantar uma semente: o crescimento pode parecer lento, mas o resultado é transformador.🌱"
        + "\n\nAcredite no processo, e lembre-se de que estamos aqui para ajudar no que for preciso."
        + "\n\nPassa no Discord e conecta com a galera que também tá nessa missão com você.🚀"),

    MENSAGEM_ONZE("Opa, *Wakander*!"
        + "\nTodo grande sonho começa com pequenos passos. Se hoje parece mais difícil, lembre-se: cada dia é uma nova chance de recomeçar."
        + "\n\nEstamos juntos nessa caminhada e prontos pra te apoiar sempre que precisar."
        + "\n\nE lembra: nosso Discord é o lugar certo pra trocar ideias e crescer junto com a comunidade.🚀"),

    MENSAGEM_DOZE("Ei, *Wakander*!"
        + "\nA jornada do conhecimento é única pra cada um. Tem dia que é acelerado, tem dia que é mais devagar, e tá tudo bem!"
        + "\n\nBora pro Discord trocar experiências e encontrar a motivação que você precisa?🚀");

    private final String message;

    private static final List<MotivationalMessages> mensagens = new ArrayList<>();

    static {
        resetMessages();
    }

    MotivationalMessages(String message) {
        this.message = message;
    }

    public static synchronized void changeCurrentMessage() {
        mensagens.remove(0);
    }

    public static synchronized String getCurrentMessage() {
        if (mensagens.isEmpty()) {
            resetMessages();
        }
        return mensagens.get(0).getMessage();
    }

    private static void resetMessages() {
        mensagens.clear();
        Collections.addAll(mensagens, values());
        Collections.shuffle(mensagens);
    }
}
