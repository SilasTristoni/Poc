package com.example.security_poc;

import java.util.ArrayList;
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

    // --- LÓGICA DO XSS: Armazenamento em memória para comentários ---
    private List<String> comments = new ArrayList<>();


    public LoginController(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    // --- ROTA VULNERÁVEL (SQLI) ---
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

    // --- ROTA SEGURA (SQLI) ---
    @GetMapping("/seguro")
    public String loginSeguroForm() {
        return "login-seguro";
    }

    @PostMapping("/login-seguro")
    public String handleLoginSeguro(@RequestParam String username, @RequestParam String password, Model model) {
        // SOLUÇÃO AQUI: Usando Spring Data JPA que usa Prepared Statements por baixo dos panos.
        User user = userRepository.findByUsernameAndPassword(username, password);

        if (user != null) {
            model.addAttribute("message", "Login SEGURO bem-sucedido! (Usuário: " + user.getUsername() + ")");
        } else {
            model.addAttribute("message", "Falha no login SEGURO!");
        }
        return "resultado";
    }

    // --- NOVAS ROTAS PARA DEMO XSS (Slide 5 e 6) ---
    
    @GetMapping("/xss-demo")
    public String xssDemoPage(Model model) {
        // Passa a lista de comentários para o template
        model.addAttribute("comments", comments);
        return "xss-demo";
    }

    @PostMapping("/add-comment")
    public String addComment(@RequestParam String comment, Model model) {
        // A vulnerabilidade não está aqui ao salvar, mas sim ao RENDERIZAR.
        // O dado é salvo "cru" (com o script).
        if (comment != null && !comment.isEmpty()) {
            comments.add(comment);
        }
        // Redireciona de volta para a página de demo
        return "redirect:/xss-demo";
    }


    // --- NOVAS ROTAS PARA DEMO CSRF (Slide 8 e 9) ---
    
    @GetMapping("/csrf-demo")
    public String csrfDemoPage() {
        // Apenas exibe a página com o formulário protegido
        return "csrf-demo";
    }

    @PostMapping("/change-password-protegido")
    public String changePasswordProtegido(@RequestParam String newPass, Model model) {
        // Este endpoint SÓ FUNCIONA se o formulário enviar o Token CSRF correto.
        // O Thymeleaf fez isso por nós automaticamente.
        model.addAttribute("message", "Ação Protegida por CSRF bem-sucedida! (Senha alterada para: " + newPass + ")");
        return "resultado";
    }
}