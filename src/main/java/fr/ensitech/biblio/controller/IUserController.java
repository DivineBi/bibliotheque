package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface IUserController {
    ResponseEntity<User> register(User user);
    ResponseEntity<String> activate(String email);
    ResponseEntity<User> login(String email, String password);
    ResponseEntity<String> unsubscribe(String email);

    ResponseEntity<String> updateProfile(String email, User updatedUser);
    ResponseEntity<String> updatePassword(String email, String oldPwd, String newPwd);
    ResponseEntity<User> getUserByEmail(String email);
    ResponseEntity<User> getUserById(Long id);
}
