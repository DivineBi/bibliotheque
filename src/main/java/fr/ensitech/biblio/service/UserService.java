package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

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
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsersByBirthdate(Date dateInf, Date dateSup) throws Exception {

        return userRepository.findByBirthdateBetween(dateInf, dateSup);
    }
    //  METHODS POUR L'AUTHENTIFICATION UTILISATEUR
    //---------------------------------------------------

    @Override
    public String register(User user) throws Exception {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "Erreur : un utilisateur avec cet email existe déjà.";
        }

        user.setActive(false); // Compte INACTIF lors de la création
        userRepository.save(user);
        // Envoi email d’activation
        emailService.sendEmail(
                user.getEmail(),
                "Activation de votre compte",
                "Bonjour " + user.getFirstname() + ",\n\n" +
                        "Merci pour votre inscription. Cliquez sur ce lien pour activer votre compte : " +
                        "http://localhost:8080/api/users/activate?email=" + user.getEmail()
        );

        return "Inscription réussie. Un email d’activation a été envoyé.";

    }

    @Override
    public String activate(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "Erreur : email introuvable";
        }

        user.setActive(true);
        userRepository.save(user);

        // Notification d’activation
        emailService.sendEmail(
                user.getEmail(),
                "Votre compte est activé",
                "Bonjour " + user.getFirstname() + ",\n\nVotre compte est désormais actif."
        );

        return "Activation réussie";
    }

    @Override
    public String login(String email, String password) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null || !user.getPassword().equals(password)) {
            return "Erreur : identifiants invalides.";
        }

        if (!user.isActive()) {
            return "Erreur : compte non activé.";
        }

        return "Connexion réussie";
    }

    @Override
    public String unsubscribe(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "Erreur : utilisateur introuvable.";
        }

        user.setActive(false);  // rendre INACTIF
        userRepository.save(user);

        // Confirmation de désinscription
        emailService.sendEmail(
                email,
                "Confirmation de désinscription",
                "Bonjour " + user.getFirstname() + ",\n\nVotre compte a été désactivé. Merci d’avoir utilisé notre service."
        );

        return "Compte désinscrit : l'utilisateur est désormais INACTIF.";

    }
}
