package academy.wakanda.wakanda_ai.jornadawakander.application.service;

import academy.wakanda.wakanda_ai.handler.APIException;
import academy.wakanda.wakanda_ai.jornadawakander.application.api.DiscordRequest;
import academy.wakanda.wakanda_ai.jornadawakander.application.service.discord.DiscordService;
import academy.wakanda.wakanda_ai.jornadawakander.domain.OnboardingWakander;
import academy.wakanda.wakanda_ai.utils.templates.Templates;
import academy.wakanda.wakanda_ai.wakander.application.service.WakanderRepository;
import academy.wakanda.wakanda_ai.wakander.domain.Wakander;
import br.com.six2six.fixturefactory.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static academy.wakanda.wakanda_ai.utils.templates.WakanderTemplate.WAKANDER;
import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingWakanderApplicationServiceTest {
    @InjectMocks
    OnboardingWakanderApplicationService onboardingWakanderApplicationService;
    @Mock
    OnboardingWakanderRepository onboardingRepository;
    @Mock
    WakanderRepository wakanderRepository;
    @Mock
    DiscordService discordService;
    
	@BeforeEach
	void setUp() {
		loadTemplates(Templates.BASE_PACKAGE);
	}
	
    @Test
    @DisplayName("Deve associar usuário do Discord com sucesso")
    void deveAssociarUsuarioDiscordComSucesso() {
		Wakander wakander = Fixture.from(Wakander.class).gimme(WAKANDER);
		DiscordRequest request = new DiscordRequest("teste", "123456789", wakander.getContato().getEmail());

        OnboardingWakander onboardingWakander = new OnboardingWakander(wakander);

        when(wakanderRepository.buscaWakanderPorEmail(request.getEmail())).thenReturn(wakander);
        when(onboardingRepository.buscaPorIdWakander(wakander.getIdWakander())).thenReturn(onboardingWakander);
        when(wakanderRepository.save(wakander)).thenReturn(wakander);

        onboardingWakanderApplicationService.associarUsuarioDiscord(request);

        verify(wakanderRepository).save(wakander);
        verify(onboardingRepository).save(onboardingWakander);
        verify(discordService).atualizaCargoParaWakander(request.getIdDiscord());
    }

    @Test
    @DisplayName("Deve lançar exceção quando Wakander não for encontrado por email")
    void deveLancarExcecaoSeWakanderNaoEncontrado() {
		DiscordRequest request = new DiscordRequest("teste", "123456789", "teste@gmail");

        when(wakanderRepository.buscaWakanderPorEmail(request.getEmail()))
                .thenThrow(APIException.build(HttpStatus.NOT_FOUND, "Email invalido!"));

        APIException exception = assertThrows(APIException.class, () -> 
            onboardingWakanderApplicationService.associarUsuarioDiscord(request)
        );

        assertEquals("Email invalido!", exception.getMessage());
        verify(onboardingRepository, never()).save(any());
        verify(wakanderRepository, never()).save(any());
    }
}