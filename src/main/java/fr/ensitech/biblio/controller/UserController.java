package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.enums.Role;
import fr.ensitech.biblio.service.IUserService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController implements IUserController{

    @Autowired
    private IUserService userService;

    // Création de compte utilisateur
    // => http://localhost:8080/api/users/register
    @PostMapping("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public ResponseEntity<User> register(@RequestBody User user) {
        if (!isUserOk(user)) {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
        user.setRole(Role.USER);
        try {
            User savedUser = userService.register(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    // Activer le compte utilisateur
    @PostMapping("/activate")
    @Override
    public ResponseEntity<String> activate(@RequestParam String email) {
        try {
            String message = userService.activate(email);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de l'activation : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Authentification de l'utilisateur
    @PostMapping("/login")
    @Override
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String password) {
        try {
            // Vérification d'Email
            if (email == null || email.isBlank()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // Vérification via service
            String message = userService.login(email, password);
            if (message.startsWith("Erreur")) {
                // identifiants invalides ou compte non activé
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Récupération de l'utilisateur
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return  new ResponseEntity<>(user, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Désactiver le compte
    @PutMapping("/unsubscribe")
    @Override
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        try {
            String message = userService.unsubscribe(email);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la désincription : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Mise à jour du profil
    @PutMapping("/{id}/profile")
    @Override
    public ResponseEntity<String> updateProfile(@PathVariable long id, @RequestBody User updatedUser) {
        try {
            // Vérifier que l'id est valide
            if (id <=0) {
                return new ResponseEntity<>("Email invalide.", HttpStatus.BAD_REQUEST);
            }

            // Vérifier que l'objet user n'est pas null
            if (updatedUser == null) {
                return new ResponseEntity<>("Données utilisateur manquantes.", HttpStatus.BAD_REQUEST);
            }

            // Vérifier que les champs sont bien renseignés
            if (updatedUser.getFirstname() == null || updatedUser.getFirstname().isBlank()
                    || updatedUser.getLastname() == null || updatedUser.getLastname().isBlank()) {
                return new ResponseEntity<>("Le prénom et le nom sont obligatoires.", HttpStatus.BAD_REQUEST);
            }

            // Ne pas autoriser la modification de l'email, du mot de passe et de l'id
            if (updatedUser.getEmail() != null || updatedUser.getPasswordHash() != null || updatedUser.getId() != 0) {
                return new ResponseEntity<>("La modification de l'email et du mot de passe sont interdits.", HttpStatus.BAD_REQUEST);
            }

            // Appel au service
            String message = userService.updateProfile(id, updatedUser);
            if (message.startsWith("Erreur")) {
                return  new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok(message);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la mise à jour du profil: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // Mise à jour du mot de passe
    @PutMapping("/{id}/{oldPwd}/{newPwd}")
    @Override
    public ResponseEntity<String> updatePassword(
            @PathVariable long id,
            @PathVariable String oldPwd,
            @PathVariable String newPwd) {

        try {
            // Vérifier les champs
            if (id <= 0
                    || oldPwd == null || oldPwd.isBlank()
                    || newPwd == null || newPwd.isBlank()) {
                return new ResponseEntity<>("Email, ancien mot de passe et nouveau mot de passe sont obligatoires.", HttpStatus.BAD_REQUEST);
            }

            // Vérifier que le nouveau mot de passe est différent de l'ancien
            if (oldPwd.equals(newPwd)) {
                return new ResponseEntity<>("Le nouveau mot de passe doit être différent de l'ancien.", HttpStatus.BAD_REQUEST);
            }

            //Appel au service
            String message = userService.updatePassword(id, oldPwd, newPwd);
            if (message.startsWith("Erreur")) {
                return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
            }
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la mise à jour du mot de passe : ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/email/{email}")
    @Override
    public ResponseEntity<User> getUserByEmail(@RequestParam(value = "email") String email) {
        System.out.println("id = " + email);
        if (email == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User user = userService.getUserByEmail(email);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{id}")
    @Override
    public ResponseEntity<User> getUserById(@RequestParam(value = "id") Long id) {

        System.out.println("id = " + id);
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isUserOk(User user) {
        return user != null
                && user.getFirstname() != null && !user.getFirstname().isBlank()
                && user.getLastname() != null && !user.getLastname().isBlank()
                && user.getEmail() != null && !user.getEmail().isBlank()
                && user.getPasswordHash() != null && !user.getPasswordHash().isBlank()
                && user.getSecurityQuestion() != null && user.getSecurityQuestion().getId() > 0
                && user.getSecurityAnswer() != null && !user.getSecurityAnswer().isBlank();
    }
}
