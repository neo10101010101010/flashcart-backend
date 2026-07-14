package com.flashcart.flashcart_backend;

import com.flashcart.flashcart_backend.models.Usuario;
import com.flashcart.flashcart_backend.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void prepararUsuario() {

        if (!usuarioRepository.existsByUsername("neo")) {

            Usuario usuario = new Usuario();
            usuario.setUsername("neo");
            usuario.setEmail("neo@test.com");
            usuario.setPassword(passwordEncoder.encode("123456"));

            usuarioRepository.save(usuario);
        }
    }

    @Test
    void loginCorrecto() throws Exception {

        String body = """
        {
            "username":"neo",
            "password":"123456"
        }
        """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("neo"));
    }

    @Test
    void loginPasswordIncorrecto() throws Exception {

        String body = """
    {
        "username":"neo",
        "password":"incorrecta"
    }
    """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

    @Test
    void registroCorrecto() throws Exception {

        String body = """
    {
        "username":"nuevoUsuario",
        "email":"nuevo@test.com",
        "password":"123456"
    }
    """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("nuevoUsuario"));
    }

    @Test
    void registroUsuarioExistente() throws Exception {

        String body = """
    {
        "username":"neo",
        "email":"neo@test.com",
        "password":"123456"
    }
    """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("El usuario ya existe"));
    }
}
