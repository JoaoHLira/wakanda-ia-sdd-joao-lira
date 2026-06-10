package academy.wakanda.wakanda_ai.frontend.web.gameficacao;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.api.MissaoWakandaDetalhadoResponse;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.application.service.MissaoWakandaService;
import academy.wakanda.wakanda_ai.gameficacao.catalogo.missaowakanda.domain.Sabedorias;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.api.ProgressoIndividualResponse;
import academy.wakanda.wakanda_ai.gameficacao.progresso.progressowakander.application.service.ProgressoWakanderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/gameficacao")
@Log4j2
@RequiredArgsConstructor
public class GameficacaoViewController {

    private final MissaoWakandaService missaoService;
    private final ProgressoWakanderService progressoWakander;

    @GetMapping("/home")
    public ModelAndView exibirHomeMentor() {
        log.info("[start] GameficacaoViewController - exibirHomeMentor");
        ModelAndView modelAndView = new ModelAndView("mentor/home_mentor");
        log.debug("[end] GameficacaoViewController - exibirHomeMentor");
        return modelAndView;
    }

    @GetMapping("/catalogo/missoes")
    public ModelAndView exibirCatalogoMissoes() {
        log.info("[start] GameficacaoViewController - exibirCatalogoMissoes");
        ModelAndView modelAndView = new ModelAndView("mentor/catalogo");
        log.debug("[end] GameficacaoViewController - exibirCatalogoMissoes");
        return modelAndView;
    }

    @GetMapping("/missoes/{idMissao}")
    public ModelAndView exibirDetalheMissao(@PathVariable UUID idMissao) {
        log.info("[start] GameficacaoViewController - exibirDetalheMissao");
        MissaoWakandaDetalhadoResponse missao = missaoService.buscaMissaoPorId(idMissao);
        ModelAndView modelAndView = new ModelAndView("mentor/missao_detalhe");
        modelAndView.addObject("missao", missao);
        log.debug("[end] GameficacaoViewController - exibirDetalheMissao");
        return modelAndView;
    }

    @GetMapping("/ProgressoWakanders")
    public ModelAndView exibirProgressoWakanders() {
        log.info("[start] GameficacaoViewController - exibirProgressoWakanders");
        ModelAndView modelAndView = new ModelAndView("mentor/progresso_wakanders");
        log.debug("[end] GameficacaoViewController - exibirProgressoWakanders");
        return modelAndView;
    }

     @GetMapping("/progresso")
     public ModelAndView exibirFormularioProgressoIndividual() {
         ModelAndView mv = new ModelAndView("mentor/progresso_individual");
         mv.addObject("buscaRealizada", false); // primeira vez
         return mv;
     }

}
