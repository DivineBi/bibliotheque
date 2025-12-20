package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.PasswordHistory;
import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.repository.IPasswordHistoryRepository;
import fr.ensitech.biblio.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPasswordHistoryRepository passwordHistoryRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // injection du bean

    @Override
    public void createUser(User user) throws Exception {
        userRepository.save(user);
    }

    @Override
    public User getUserById(long id) throws Exception {

        Optional<User> optional = userRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public User getUserByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<User> getUsersByBirthdate(Date dateInf, Date dateSup) throws Exception {

        return userRepository.findByBirthdateBetween(dateInf, dateSup);
    }
    //  METHODS POUR L'AUTHENTIFICATION UTILISATEUR
    //---------------------------------------------------

    @Override
    public User register(User user) throws Exception {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email déjà utilisé");
        }

        // Hachage du mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword())); //hachage
        user.setActive(false); // Compte INACTIF lors de la création

        // Hachage de la réponse à la question secrète
        user.setSecurityAnswer(passwordEncoder.encode(user.getSecurityAnswer()));
        User savedUser = userRepository.save(user);

        // Envoi automatique d'email d'activation
        sendActivationEmail(savedUser);
        return savedUser;
    }

    @Override
    public void sendActivationEmail(User user) throws Exception {
        // Envoi email d’activation
        emailService.sendEmail(
                user.getEmail(),
                "Activation de votre compte",
                "Bonjour " + user.getFirstname() + ",\n\n" +
                        "Merci pour votre inscription. Cliquez sur ce lien pour activer votre compte : " +
                        "http://localhost:8080/api/users/activate?email=" + user.getEmail()
        );
    }

    @Override
    public String activate(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return "Erreur : email introuvable";
        }

        User user = optionalUser.get();

        user.setActive(true);
        userRepository.save(user);
        sendActivationNotification(user);
        return "Activation réussie";
    }

    @Override
    public void sendActivationNotification(User user) throws Exception {
        // Notification d’activation
        emailService.sendEmail(
                user.getEmail(),
                "Votre compte est activé",
                "Bonjour " + user.getFirstname() + ",\n\nVotre compte est désormais actif."
        );
    }

    @Override
    public String login(String email, String password) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "Erreur : identifiants invalides.";
        }
        User user = optionalUser.get();

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return "Erreur : identifiants invalides.";
        }

        if (!user.isActive()) {
            return "Erreur : compte non activé.";
        }

        return "Connexion réussie";
    }

    @Override
    public String unsubscribe(String email) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return "Erreur : utilisateur introuvable.";
        }
        User user = optionalUser.get();

        if (user == null) {
            return "Erreur : utilisateur introuvable.";
        }

        user.setActive(false);  // rendre INACTIF
        userRepository.save(user);
        sendUnsubscribeConfirmation(user);
        return "Compte désinscrit : l'utilisateur est désormais INACTIF.";

    }

    @Override
    public void sendUnsubscribeConfirmation(User user) throws Exception {
        // Confirmation de désinscription
        emailService.sendEmail(
                user.getEmail(),
                "Confirmation de désinscription",
                "Bonjour " + user.getFirstname() + ",\n\nVotre compte a été désactivé. Merci d’avoir utilisé notre service."
        );

    }

    @Override
    public String updateProfile(long id, User updatedUser) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return "Erreur : utilisateur introuvable.";
        }
        User user = optionalUser.get();

        // Mettre à jour les champs autorisés
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setBirthdate(updatedUser.getBirthdate());

        userRepository.save(user);
        return "Profil mis à jour avec succès.";
    }

    @Override
    public String updatePassword(long id, String oldPwd, String newPwd) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return "Erreur : utilisateur introuvable";
        }
        User user = optionalUser.get();

        // Vérification de l'ancien mot de passe avec BCrypt
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            return "Erreur : ancien mot passe incorrect.";
        }

        // Vérification historique (5 derniers mots de passe)
        if (isPasswordReused(user, newPwd)) {
            return "Erreur : ce mot de passe a déjà été utilisé récemment.";
        }

        // Sauvegarde dans l'historique
        PasswordHistory history = new PasswordHistory();
        history.setUser(user);
        history.setOldPasswordHash(user.getPassword());
        history.setChangedAt(LocalDateTime.now());
        passwordHistoryRepository.save(history);

        // Hachage et mise à jour du nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPwd));
        user.setPasswordLastUpdated(LocalDateTime.now());
        userRepository.save(user);

        return "Mot de passe mis à jour avec succès";
    }

    // Vérifie de la validité du mot de passe (12 semaines)
    @Override
    public boolean isPasswordExpired(User user) throws Exception {
        if (user.getPasswordLastUpdated() == null) return true;
        return user.getPasswordLastUpdated().isBefore(LocalDateTime.now().minusWeeks(12));
    }

    // Renouvelle le mot de passe d'un utilisateur
    @Override
    public boolean renewPassword(String email, String oldPassword, String newPassword) throws Exception {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false;
        }
        User user = optionalUser.get();

        // Vérifie l'ancien mot de passe
        if (isPasswordReused(user, newPassword)) {
            return false;
        }

        // Sauvegarde dans l'historique
        PasswordHistory history = new PasswordHistory();
        history.setUser(user);
        history.setOldPasswordHash(user.getPassword());
        history.setChangedAt(LocalDateTime.now());
        passwordHistoryRepository.save(history);

        // Mets à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordLastUpdated(LocalDateTime.now());
        userRepository.save(user);

        return true;
    }

    // Vérifie si le nouveau mot de passe est déjà utilisé dans les 5 derniers.
    private boolean isPasswordReused(User user, String newPassword) {
        List<PasswordHistory> lastPasswords = passwordHistoryRepository.findTop5ByUserOrderByChangedAtDesc(user);

        return lastPasswords.stream().anyMatch(ph -> passwordEncoder.matches(newPassword, ph.getOldPasswordHash()));
    }



}
