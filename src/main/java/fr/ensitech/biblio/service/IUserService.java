package fr.ensitech.biblio.service;

import fr.ensitech.biblio.entity.User;

import java.util.Date;
import java.util.List;

public interface IUserService {

    void createUser(User user) throws Exception;
    User getUserById(long id) throws Exception;
    User getUserByEmail(String email) throws Exception;
    List<User> getUsersByBirthdate(Date dateInf, Date dateSup) throws Exception;

    // Méthodes d'authentification de l'utilisateur
    User register(User user) throws Exception;
    void sendActivationEmail(User user) throws Exception;
    String activate(String email) throws Exception;
    void sendActivationNotification(User user) throws Exception;
    String login(String email, String password) throws Exception;
    String unsubscribe(String email) throws Exception;
    void sendUnsubscribeConfirmation(User user) throws Exception;

    // Méthodes pour la mise à jour
    String updateProfile(long id, User updatedUser) throws Exception;
    String updatePassword(long id, String oldPwd, String newPwd) throws Exception;

    // Méthodes pour sécuriser le mot de passe
    boolean isPasswordExpired(User user) throws Exception;
    boolean renewPassword(String email, String oldPassword, String newPassword) throws Exception;
}
