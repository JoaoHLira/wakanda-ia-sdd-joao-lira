package academy.wakanda.wakanda_ai.frontend.web.formulario;

import academy.wakanda.wakanda_ai.autenticacao.application.service.AutenticacaoService;
import academy.wakanda.wakanda_ai.docs.swagger.FormularioAPIDocs;
import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.wakander.application.api.WakanderDetalhadoResponse;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderService;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import academy.wakanda.wakanda_ai.wakander.domain.WakanderContato;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/formulario")
@RequiredArgsConstructor
public class FormularioApi {

    private final WakanderService wakanderService;
    private final AutenticacaoService autenticacaoService;

    @GetMapping("/cadastro/{token}")
    public ModelAndView mostraFormularioDeCadastro(@PathVariable String token) {
        log.info("[start] FormularioApi - mostraFormularioDeCadastro");
        try {
            Wakander wakander = autenticacaoService.buscaWakanderPeloToken(token);
            log.debug("[finish] FormularioApi - mostraFormularioDeCadastro");
            return wakanderService.retornaFormularioCadastroCompleto(wakander).orElseGet(() -> criaModel(wakander));
        } catch (APIException ex) {
            return new ModelAndView("token-invalido");
        }
    }

    private static ModelAndView criaModel(Wakander wakander) {
        ModelAndView model = new ModelAndView("formulario-cadastro");
        model.addObject("nomeFiador", wakander.getFiador().getNome());
        model.addObject("cpfFiador", wakander.getFiador().getCpf());
        model.addObject("whatsappFiador", wakander.getFiador().getTelefone());
        model.addObject("idWakander", wakander.getIdWakander());
        return model;
    }

    @GetMapping("/cancelamento-assinatura/{idWakander}")
    public ModelAndView mostraFormularioCancelamento(@PathVariable UUID idWakander) {
        log.info("[start] FormularioApi - mostraFormularioCancelamento");
        WakanderDetalhadoResponse wakanderDetalhado = wakanderService.buscaWakanderPorId(idWakander);
        ModelAndView modelAndView = new ModelAndView("cancelamento-assinatura");
        modelAndView.addObject("wakander", wakanderDetalhado);
        modelAndView.addObject("nomeResolvido", resolveNome(wakanderDetalhado));
        modelAndView.addObject("contatoResolvido", resolveContato(wakanderDetalhado));
        log.debug("[finish] FormularioApi - mostraFormularioCancelamento");
        return modelAndView;
    }


    private String resolveContato(WakanderDetalhadoResponse wakanderDetalhado) {
        return Optional.ofNullable(wakanderDetalhado.getContato())
                .map(WakanderContato::getWhatsapp)
                .orElseGet(() -> wakanderDetalhado.getFiador().getTelefone());
    }

    private String resolveNome(WakanderDetalhadoResponse wakanderDetalhado) {
        return (wakanderDetalhado.getNome() != null && !wakanderDetalhado.getNome().isBlank())
                ? wakanderDetalhado.getNome()
                : wakanderDetalhado.getFiador().getNome();
    }

    @GetMapping("/dados-complementares/{token}")
    public ModelAndView mostrarFormularioDeDadosComplementar(@PathVariable String token) {
        log.info("[start] FormularioApi - mostrarFormularioDeDadosComplementar");
        WakanderDetalhadoResponse wakanderDetalhado = new WakanderDetalhadoResponse(
                autenticacaoService.buscaWakanderPeloToken(token));
        ModelAndView modelAndView = new ModelAndView("formulario-dados-complementar");
        modelAndView.addObject("wakander", wakanderDetalhado);
        log.debug("[end] FormularioApi - mostrarFormularioDeDadosComplementar");
        return modelAndView;
    }

    @GetMapping("/{username}/{idDiscord}/associar-discord")
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView retornaAssociarContaDiscord(@PathVariable String username, @PathVariable String idDiscord) {
        log.info("[start] FormularioApi - associarContaDiscord");
        ModelAndView mv = new ModelAndView("formulario-discord");
        mv.addObject("username", username);
        mv.addObject("idDiscord", idDiscord);
        return mv;
    }

    @GetMapping("/resposta-discord")
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView respostaTokenDiscord() {
        return new ModelAndView("resposta-discord");
    }

    @FormularioAPIDocs.AtualizaFiador
    @GetMapping("/atualiza-fiador/{token}")
    public ModelAndView atualizaFiador(@PathVariable String token) {
        try {
            Wakander wakander = autenticacaoService.buscaWakanderPeloToken(token);
            ModelAndView modelAndView = new ModelAndView("atualiza-fiador");
            modelAndView.addObject("idWakander", wakander.getIdWakander());
            return modelAndView;
        } catch (APIException ex) {
            return new ModelAndView("token-invalido");
        }
    }

    @GetMapping("/resposta-atualiza-fiador")
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView paginaDeSucessoDoAtualizaFiador() {
        return new ModelAndView("resposta-atualiza-fiador");
    }

}