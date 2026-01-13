package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.PasswordHistory;
import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.repository.IPasswordHistoryRepository;
import fr.ensitech.biblio.repository.IUserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IPasswordHistoryRepository passwordHistoryRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .email("test@email.com")
                .firstname("Elle")
                .lastname("Kimia")
                .password("mtDePs")
                .securityAnswer("makiso")
                .active(true)
                .build();
    }

    // Test createUser()
    @SneakyThrows
    @Test
    void shouldCreateUser() {
        userService.createUser(user);

        verify(userRepository).save(user);
    }

    // Test getUserById()
    @SneakyThrows
    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(user, result);
    }

    // Test getUserByEmail()
    @SneakyThrows
    @Test
    void shouldReturnUserByEmail() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@email.com");

        assertEquals(user, result);
    }

    // Test register() : succès
    @SneakyThrows
    @Test
    void shouldRegisterUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("mtDePs")).thenReturn("hashedPwd");
        when(passwordEncoder.encode("makiso")).thenReturn("hashedAnswer");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.register(user);

        assertFalse(result.isActive());  // compte inactif
        verify(emailService).sendEmail(
                eq(user.getEmail()),
                anyString(),
                contains("Merci pour votre inscription")
        );
    }

    // Test register() : email déjà utilisé
    @Test
    void shouldThrowWhenEmailAlreadyUsed() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(Exception.class, () -> userService.register(user));
    }

    // Test activate() : succès
    @SneakyThrows
    @Test
    void shouldActivateUser() {
        user.setActive(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = userService.activate(user.getEmail());

        assertEquals("Activation réussie", result);
        assertTrue(user.isActive());
        verify(emailService).sendEmail(eq(user.getEmail()), anyString(), contains("désormais actif"));
    }

    // Test activate : email introuvable
    @SneakyThrows
    @Test
    void shouldReturnErrorWhenEmailNotFound() {
        when(userRepository.findByEmail("whatever@email.com")).thenReturn(Optional.empty());

        String result = userService.activate("whatever@email.com");

        assertEquals("Erreur : email introuvable", result);
    }

    // Test login() : succès
    @SneakyThrows
    @Test
    void shouldLoginSuccessfully() {
        user.setPassword("hashedPwd");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mtDePs", "hashedPwd")).thenReturn(true);

        String result = userService.login(user.getEmail(), "mtDePs");

        assertEquals("Connexion réussie", result);
    }

    // Test login() : mot de passe erroné
    @SneakyThrows
    @Test
    void shouldFailLoginWhenPasswordInvalid() {
        user.setPassword("hashedPwd");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedPwd")).thenReturn(false);

        String result = userService.login(user.getEmail(), "wrong");

        assertEquals("Erreur : identifiants invalides.", result);
    }

    // Test login() : compte non activé
    @SneakyThrows
    @Test
    void shouldFailLoginWhenAccountInactive() {
        user.setActive(false);
        user.setPassword("hashedPwd");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mtDePs", "hashedPwd")).thenReturn(true);

        String result = userService.login(user.getEmail(), "mtDePs");

        assertEquals("Erreur : compte non activé.", result);
    }

    // Test unsubscribe()
    @SneakyThrows
    @Test
    void shouldUnsubscribeUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String result = userService.unsubscribe(user.getEmail());

        assertFalse(user.isActive());
        verify(emailService).sendEmail(eq(user.getEmail()), anyString(), contains("désactivé"));
        assertEquals("Compte désinscrit : l'utilisateur est désormais INACTIF", result);
    }

    // Test updateProfile()
    @SneakyThrows
    @Test
    void shouldUpdateProfile() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User updated = new User();
        updated.setFirstname("New");
        updated.setLastname("Name");
        updated.setBirthdate(new Date());

        String result = userService.updateProfile(1L, updated);

        assertEquals("New", user.getFirstname());
        assertEquals("Name", user.getLastname());
        verify(userRepository).save(user);
        assertEquals("Profil mis à jour avec succès", result);
    }

    // Test updatePassword() : Ancien mot de passe incorrect
    @SneakyThrows
    @Test
    void shouldFailWhenOldPasswordIncorrect() {
        user.setPassword("hashedPwd");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashedPwd")).thenReturn(false);

        String result = userService.updatePassword(1L, "wrong", "newPwd");

        assertEquals("Erreur : ancien mot de passe incorrect.", result);
    }

    // Test updatePassword() : mot de passe réutilisé

    @SneakyThrows
    @Test
    void shouldFailWhenPasswordReused() {
        user.setPassword("hashedPwd");

        PasswordHistory ph = new PasswordHistory();
        ph.setOldPasswordHash("hashedPwd");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPwd", "hashedPwd")).thenReturn(true);
        when(passwordHistoryRepository.findTop5ByUserOrderByChangedAtDesc(user))
                .thenReturn(List.of(ph));
        when(passwordEncoder.matches("newPwd", "hashedPwd")).thenReturn(true);

        String result = userService.updatePassword(1L, "oldPwd", "newPwd");

        assertEquals("Erreur : ce mot de passe a déjà été utilisé récemment.", result);
    }

}
