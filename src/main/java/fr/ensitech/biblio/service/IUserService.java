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
    String register(User user) throws Exception;
    String activate(String email) throws Exception;
    String login(String email, String password) throws Exception;
    String unsubscribe(String email) throws Exception;
}
