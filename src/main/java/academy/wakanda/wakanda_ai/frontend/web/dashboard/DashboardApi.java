package academy.wakanda.wakanda_ai.frontend.web.dashboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/painel-dados")
@Log4j2
@RequiredArgsConstructor
public class DashboardApi {

    @GetMapping("/dashboard")
    public ModelAndView mostrarDashboard() {
        log.info("[start] DashboardApi - mostrarDashboard");
        ModelAndView modelAndView = new ModelAndView("dashboard");
        log.debug("[end] DashboardApi - mostrarDashboard");
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView mostrarLogin() {
        log.info("[start] DashboardApi - mostrarLogin");
        ModelAndView modelAndView = new ModelAndView("login");
        log.debug("[end] DashboardApi - mostrarLogin");
        return modelAndView;
    }
}
