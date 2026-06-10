package academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.datahelper;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.jornadawakanda.datahelper.JornadaWakandaDataHelper;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoExternaDTO;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoWakandaRequest;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.MissaoWakanda;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.OrdemMissao;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.ProcessamentoStatus;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.tipomissao.datahelper.TipoMissaoDataHelper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

public class MissaoWakandaDataHelper {

        public static MissaoWakandaRequest criarMissaoValida() {
                return MissaoWakandaRequest.builder().titulo("Missao teste").descricao("Descricao missao teste")
                                .xpBase(100)
                                .idTipoMissao(TipoMissaoDataHelper.criarTipoMissaoResponseValido().getIdTipoMissao())
                                .idJornada(UUID.randomUUID()).sabedorias(new Sabedorias(10, // teorico
                                                15, // processo
                                                10, // knowHow
                                                15, // comportamental
                                                10 // criativo
                                )).conteudoUrl("https://wakanda.com.br").build();
        }

        public static MissaoWakandaRequest criarMissaoJornadaInativa() {
                return MissaoWakandaRequest.builder().titulo("Missao teste").descricao("Descricao missao teste")
                                .xpBase(100).idTipoMissao(UUID.randomUUID())
                                .idJornada(JornadaWakandaDataHelper.criarJornadaResponseInativa().getIdJornada())
                                .sabedorias(new Sabedorias(10, // teorico
                                                15, // processo
                                                10, // knowHow
                                                15, // comportamental
                                                10 // criativo
                                )).build();
        }

        public static MissaoWakandaRequest criarMissaoXPRequestInvalido() {
                return MissaoWakandaRequest.builder().titulo("Missão XP Inválido").descricao("Descrição").xpBase(0)
                                .idTipoMissao(UUID.randomUUID()).idJornada(UUID.randomUUID())
                                .sabedorias(new Sabedorias(10, // teorico
                                                15, // processo
                                                10, // knowHow
                                                15, // comportamental
                                                10 // criativo
                                )).build();
        }

        public static MissaoWakanda criaMissaoWakanda() {
                return new MissaoWakanda(UUID.randomUUID(), "Missão Java Básico",
                                "Missão para introduzir conceitos básicos de Java", 100, UUID.randomUUID(),
                                UUID.randomUUID(), MissaoStatus.ATIVA, OrdemMissao.criar(1),
                                new Sabedorias(10, 10, 10, 10, 10), "12345", UUID.randomUUID(), UUID.randomUUID(), null, ProcessamentoStatus.EM_PROCESSO);
        }

        public static MissaoWakandaRequest criarMissaoSemSabedorias() {
                return MissaoWakandaRequest.builder().titulo("Missão de Teste").descricao("Tipo Missão Sem Sabedorias")
                                .xpBase(100).idJornada(UUID.randomUUID()).idTipoMissao(UUID.randomUUID()).build();
        }

        public static MissaoWakanda criaMissaoWakandaPersonalizada(String titulo, OrdemMissao ordem, UUID jornadaId,
                        MissaoStatus status) {
                return new MissaoWakanda(UUID.randomUUID(), titulo, "Descricao", 100, UUID.randomUUID(), jornadaId,
                                status, ordem, new Sabedorias(10, 10, 10, 10, 10), "123456", UUID.randomUUID(), UUID.randomUUID(),
                                "http://wakanda.com.br", ProcessamentoStatus.EM_PROCESSO);
        }

        public static MissaoWakanda criaMissaoWakandaAtivaComUUID(UUID idMissao, UUID idJornada) {
                return new MissaoWakanda(idMissao, "Missão Java Básico",
                                "Missão para introduzir conceitos básicos de Java", 100, UUID.randomUUID(), idJornada,
                                MissaoStatus.ATIVA, OrdemMissao.criar(1), new Sabedorias(10, 10, 10, 10, 10), "12345",
                        UUID.randomUUID(), UUID.randomUUID(), "http://wakanda.com.br", ProcessamentoStatus.EM_PROCESSO);
        }

        public static MissaoExternaDTO criarMissao(String idExterno, String titulo) {
                return MissaoExternaDTO.builder().idExterno(idExterno).titulo(titulo).descricao("Descrição")
                                .idTipoMissao(UUID.randomUUID()).idJornada(UUID.randomUUID())
                                .idExternoPai(Optional.empty()).build();
        }

        public static MissaoWakanda criaMissao(String titulo, OrdemMissao ordem, UUID jornadaId, MissaoStatus status) {
                MissaoWakandaRequest request = MissaoWakandaRequest.builder().titulo(titulo).descricao("Descricao")
                                .xpBase(100).idTipoMissao(UUID.randomUUID()).idJornada(jornadaId)
                                .idMissaoExterna("ext-" + titulo).sabedorias(new Sabedorias(10, 10, 10, 10, 10))
                                .build();

                MissaoWakanda missao = new MissaoWakanda(request, ordem.getOrdem(), null, false);

                ReflectionTestUtils.setField(missao, "missaoStatus", status);

                return missao;
        }
}