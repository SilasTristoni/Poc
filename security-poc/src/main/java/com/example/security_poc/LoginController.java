package com.example.security_poc;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public LoginController(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    // --- ROTA VULNERÁVEL ---
    @GetMapping("/vulneravel")
    public String loginVulneravelForm() {
        return "login-vulneravel";
    }

    @PostMapping("/login-vulneravel")
    public String handleLoginVulneravel(@RequestParam String username, @RequestParam String password, Model model) {
        // VULNERABILIDADE AQUI: Concatenação de strings para montar a query SQL
        String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(sql);
            if (!users.isEmpty()) {
                model.addAttribute("message", "Login VULNERÁVEL bem-sucedido! (Usuário: " + users.get(0).get("username") + ")");
            } else {
                model.addAttribute("message", "Falha no login VULNERÁVEL!");
            }
        } catch (Exception e) {
            model.addAttribute("message", "Erro na query: " + e.getMessage());
        }
        return "resultado";
    }

    // --- ROTA SEGURA ---
    @GetMapping("/seguro")
    public String loginSeguroForm() {
        return "login-seguro";
    }

    @PostMapping("/login-seguro")
    public String handleLoginSeguro(@RequestParam String username, @RequestParam String password, Model model) {
        // SOLUÇÃO AQUI: Usando Spring Data JPA que usa Prepared Statements por baixo dos panos.
        // A entrada do usuário é tratada como DADO, nunca como CÓDIGO.
        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            model.addAttribute("message", "Login SEGURO bem-sucedido! (Usuário: " + user.getUsername() + ")");
        } else {
            model.addAttribute("message", "Falha no login SEGURO!");
        }
        return "resultado";
    }
}