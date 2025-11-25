package fr.ensitech.biblio.controller;

import fr.ensitech.biblio.entity.User;
import fr.ensitech.biblio.enums.RoleEnum;
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
        user.setRole(RoleEnum.C.toString());
        try {
            userService.register(user);
            return new ResponseEntity<User>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
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

    private boolean isUserOk(User user) {
        return user != null
                && user.getFirstname() != null && !user.getFirstname().isBlank()
                && user.getLastname() != null && !user.getLastname().isBlank()
                && user.getEmail() != null && !user.getEmail().isBlank()
                && user.getPassword() != null && !user.getPassword().isBlank();
    }
}
