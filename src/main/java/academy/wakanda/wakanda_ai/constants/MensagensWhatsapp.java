package academy.wakanda.wakanda_ai.constants;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.memberkit.application.api.dto.LoginMemberkitDto;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderRelatorioDTO;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderInativoResponse;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum MensagensWhatsapp {
    MENSAGEM_PARABENIZANDO("""
            Fala, Wakander!
            Se fosse fácil, todo mundo faria, e é isso que te torna especial! Parabéns pela dedicação e coragem
            para superar desafios e assistir à aula. Continue brilhando, Wakander, porque o mundo é de quem não desiste!"
            """),
    MENSAGEM_BOAS_VINDAS("🚀 Fala, Wakander!\n\nSeja muito bem-vindo à Wakanda e parabéns por dar esse" +
            " passo tão importante na sua jornada rumo ao mercado de tecnologia e engenharia de software! " +
            "💻✨\n\n📝 Antes de começarmos, precisamos que o aluno que será mentorado preencha o formulário" +
            " de onboarding — ele é essencial para organizarmos sua jornada da melhor forma possível.\n\n" +
            "Se esse número for de um responsável ou fiador, pedimos que encaminhe essa mensagem ao estudante para" +
            " que ele mesmo preencha.\n\n📎 Aqui está o formulário: %s\n\nEstamos muito felizes por ter você com a gente."
    ),
    FORMULARIO_CANCELAMENTO_WAKANDER("*✅ Olá Liderança!* \nSegue a solicitação de cancelamento para o Wakander: %s \n\n"),
    NOTIFICA_ERRO_WAKANDER_STATUS_CANCELAMENTO(
            """
                    *❌ Solicitação de cancelamento falhou* 
                    Wakander: %s
                    Status atual: %s
                    """),
    NOTIFICA_ERRO_CANCELAMENTO_PADRAO(
            """
                    *❌ Solicitação de cancelamento falhou*
                    Erro ao cancelar assinatura com ID: %s
                    """),
    RELATORIO_INATIVIDADE_LIDERANCA("*Relatório Wakanda-AI* \nNão existem wakanders inativos na Jornada do Conhecimento."),
    RELATORIO_WAKANDERS_INATIVOS("*Relatório Wakanda-AI* \n*Wakanders Inativos:* \n\n%s"),
    RELATORIO_ATIVIDADE_WAKANDERS(
            "*Relatório Wakanda:*\n\n" +
                    "📌 *Wakanders Ativo “Conhecimento”:* %d\n" +
                    "📌 *Wakanders Estudaram Últimos 7 Dias:* %d\n" +
                    "📌 *Wakanders Estudaram ”Conhecimento” Últimos 7 Dias:* %d\n" +
                    "📌 *Aulas assistidas pelos Wakanders Últimos 7 Dias:* %d\n" +
                    "📌 *Wakanders Estudaram nos Últimos 30 Dias “Conhecimento”:* %d"
    ),
    PROGRESSO_CHECKLIST("""
            🔷Confirmar dados de cadastro
            🔷Entrar no discord
            🔷Entrar no grupo de WhatsApp
            🔷Acessar Plataforma de estudo
            🔷Concluir Comece aqui
            🔷Concluir Primeira Aula
            """),
    CHECKLIST_ACESSO_MEMBERKIT("""
            *🎯 Passos para acessar o Memberkit*:
            
            1- Acesse o link abaixo
            2- Faça login com seu e-mail e senha
            3- Clique no módulo "Comece Aqui"
            4- Assista às aulas
            5- Clique em "Concluir" após assistir cada aula
            
            Segue a URL de acesso:
            %s
            
            obs: qualquer dúvida estamos a disposição!
            """),
    TITULO_CHECKLIST_ONBOARDING("""
            *📌 Checklist de Onboarding!*
            
            _Tudo que já foi finalizado está marcado com ✅._
            
            """),
    MENSAGEM_LOGIN_ONBOARDING("""
            *👇 Segue seus dados de acesso*!
            *Login*: %s
            *Senha*: %s
            """),
    MENSAGEM_FALHA_MEMBERKIT("""
            *❌ [Error] Não foi possível realizar uma requisição para o Memberkit, mesmo após várias tentativas!*
            ⚠️ Por favor, entre em contato com a equipe de desenvolvimento.
            \n*- Dados adicionais -*
            *ID do Wakander*: %s
            *Nome do Wakander*: %s
            *Email do Wakander*: %s
            *Número de WhatsApp do Wakander*: %s
            *Horário de consumo desta mensagem*: %s
            """),
    MENSAGEM_BOAS_VINDAS_GRUPO_WHATSAPP("""
            🌍 Bem-vindo(a) ao grupo!
            Se você já foi adicionado, ótimo! Se recebeu o link, não esqueça de clicar e entrar.
            Estamos te esperando! 💪😎
            
            """),
    MENSAGEM_CONVIDA_DISCORD(""" 
            🚀 Bem-vindo(a) ao Wakanda Academy, Wakander! 🧑‍💻👩‍💻
            Entre no nosso Discord para tirar dúvidas e interagir com outros alunos!
            
            Regras rápidas:
            📍 Dúvidas devem ser enviadas de acordo com o seu nível de estudos.
            📍 Mantenha o respeito e o foco. Evite flood e discussões fora do tema.
            📍 Não compartilhe conteúdos exclusivos fora do grupo.
            
            🔗 Acesse o Discord aqui: linkDoConvite
            Vamos juntos nessa jornada! 😎"""),
    MENSAGEM_CADASTRO_SUCESSO("""
            *Cadastro Concluído com Sucesso!*
            💡 Grandes conquistas começam com pequenas ações.
            Você acaba de dar o primeiro passo na sua jornada!
            """),
    MENSAGEM_NOVO_WAKANDER_GRUPO_LIDERES(
            """
                    🎉 *Novo Wakander a bordo!*
                    🚀 Nome: %s
                    📥 Cadastro concluído com sucesso em *%s às %s*.
                    """),
    MENSAGEM_CONTATO_ENVIADO_CLINT("""
            🎉 *Novo Contato Registrado ou Atualizado na Clint* 
            🚀 *Nome:* %s
            O contato foi *incluído ou atualizado* com sucesso na Clint.
            """),
    MENSAGEM_FALHA_ENVIO_CONTATO_CLINT("""
            ❌ [Erro] Não foi possível completar o envio de contato para a clint, mesmo após diversas tentativas.
            ⚠️ Recomendamos que entre em contato com a equipe de desenvolvimento para investigação adicional do ocorrido.
            - **Dados adicionais** -
            *ID do Cliente*: %s 
            *Nome do Cliente*: %s 
            *Email do Cliente*: %s
            *Número de WhatsApp do Cliente*: %s
            *Horário do ocorrido*: %s
            """),
    MENSAGEM_FALHA_AO_REMOVER_WAKANDER_WHATSAPP("""
            ❌ *Falha ao remover Wakander do grupo Profissão Programador*
            Não foi possível remove-lo, mesmo após 5 tentativas.
            ⚠️ Remoção manual pode ser necessária.
            
            - *Nome:* %s
            - *Telefone:* %s
            
            Por gentileza, verifiquem diretamente no grupo ou entrem em contato com a equipe de desenvolvimento.
            """),
    MENSAGEM_SUCESSO_CANCELAMENTO_CLINT("""
            *✅ [Success] Wakander cancelado com sucesso na Clint!*
            *- Dados adicionais -*
            Nome do Wakander: %s
            Email do Wakander: %s
            WhatsApp do Wakander: %s
            """),
    MENSAGEM_FALHA_CANCELAMENTO_CLINT("""
            *❌ [Error] Não foi possível cancelar o wakander na Clint!*
            *- Dados adicionais -*
            Nome do Wakander: %s
            Email do Wakander: %s
            WhatsApp do Wakander: %s
            """),
    MENSAGEM_ENVIO_FORMULARIO_DADOS_PENDENTE("""
            *🚀 Oi %s, tudo certo?*
            Percebemos que ainda faltam algumas informações no seu cadastro.   Por favor, acesse o formulário abaixo e preencha os dados que estão pendentes:
            
            👉 %s
            """),
    MENSAGEM_LIDERES_WAKANDER_SEM_CONTATO("""
            *⚠️ Atenção, líderes!*
            
            Os Wakanderes abaixo estão com dados de contato incompletos e não puderam receber o formulário para preenchimento:
            
            %s
            
            Por favor, verifique com os Wakanderes ou atualize o contato na base de dados para que possamos dar continuidade ao processo.
            """);

    private final String mensagem;

    MensagensWhatsapp(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem(String parametro) {
        return String.format(mensagem, parametro);
    }

    public String getMensagem() {
        return mensagem;
    }

    public static String formataRelatorioWakandersInativos(List<WakanderInativoResponse> wakanders) {
        StringBuilder relatorio = new StringBuilder();
        int posicao = 1;
        for (WakanderInativoResponse wakander : wakanders) {
            relatorio.append(posicao)
                    .append(" - ")
                    .append(wakander.getNome())
                    .append(" | ")
                    .append(wakander.getDataUltimaAulaAssistida())
                    .append("\n");
            posicao++;
        }
        return RELATORIO_WAKANDERS_INATIVOS.getMensagem(relatorio.toString());
    }

    public String formataRelatorioWakanderAtivos(WakanderRelatorioDTO dto) {
        return String.format(mensagem,
                dto.getWakandersConhecimento(),
                dto.getWakanderEstudaram(),
                dto.getWakanderEstudaramConhecimento(),
                dto.getAulasAssistidasWakander(),
                dto.getEstudaramUltimos30DiasConhecimento());
    }

    public static String mensagemChecklistPadrao() {
        return TITULO_CHECKLIST_ONBOARDING.getMensagem() + PROGRESSO_CHECKLIST.getMensagem();
    }

    public String formataLoginAcessoMemberkit(LoginMemberkitDto dto) {
        return String.format(mensagem,
                dto.getData().getEmail(),
                dto.getData().getPassword());
    }

    public String formataMensagemFalhaMemberkit(String idWakander, String nome, String email, String whatsapp, String horario) {
        return String.format(mensagem, idWakander, nome, email, whatsapp, horario);
    }

    public static String mensagemBoasVindasComChecklist(String checklist) {
        return MENSAGEM_BOAS_VINDAS_GRUPO_WHATSAPP.getMensagem() + checklist;
    }

    public String formataConviteDiscord(String linkConvite) {
        return String.format(mensagem).replace("linkDoConvite", linkConvite);
    }

    public String mensagemErroAguardandoCancelamento(String nome, String url) {
        return String.format(mensagem, nome, url);
    }

    public static String retornaChecklistProgresso(List<Integer> posicoesConcluidas) {
        String mensagemChecklist = TITULO_CHECKLIST_ONBOARDING.getMensagem() + atualizaProgressoChecklist(posicoesConcluidas);
        return mensagemChecklist;
    }

    private static String atualizaProgressoChecklist(List<Integer> posicoesConcluidas) {
        String[] linhasMensagem = PROGRESSO_CHECKLIST.getMensagem().split("\n");
        return IntStream.range(0, linhasMensagem.length)
                .mapToObj(i -> atualizaLinhaChecklist(linhasMensagem[i], i, posicoesConcluidas))
                .collect(Collectors.joining("\n"));
    }

    private static String atualizaLinhaChecklist(String linha, int indice, List<Integer> posicoesConcluidas) {
        return posicoesConcluidas.contains(indice) ? linha.replace("🔷", "✅") : linha;
    }

    public String mensagemNovoWakanderGrupoLideres(String nome, LocalDateTime dataHora) {
        String data = dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String hora = dataHora.format(DateTimeFormatter.ofPattern("HH:mm"));
        return String.format(mensagem, nome, data, hora);
    }

    public String mensagemContatoEnviadoClint(String nome) {
        return String.format(mensagem, nome);
    }

    public String mensagemFalhaEnvioContatoClint(String idWakander, String nome, String email, String whatsapp, String horario) {
        return String.format(mensagem, idWakander, nome, email, whatsapp, horario);
    }

    public String formataMensagem(String... parametros) {
        return String.format(mensagem, (Object[]) parametros);
    }

    public String mensagemCancelamentoClint(Wakander wakander) {
        return String.format(mensagem,
                wakander.getNome(),
                wakander.getContato().getEmail(),
                wakander.getContato().getWhatsapp());
    }

    public String mensagemEnvioFormularioDadosPendente(String nome, String url) {
        return String.format(mensagem, nome, url);
    }

    public String mensagemLideresWakanderSemContato(List<Wakander> wakandersSemContato) {
        StringBuilder sb = new StringBuilder();

        for (Wakander wakander : wakandersSemContato) {
            String nome = wakander.getNome() != null ? wakander.getNome() : "Não informado";
            String telefone = (wakander.getContato() != null && wakander.getContato().getWhatsapp() != null && !wakander.getContato().getWhatsapp().isBlank())
                    ? wakander.getContato().getWhatsapp()
                    : "Não cadastrado!";

            sb.append("👤 *Nome:* ").append(nome).append("\n");
            sb.append("📞 *WhatsApp:* ").append(telefone).append("\n\n");
        }

        return String.format(mensagem, sb.toString().trim());
    }

}