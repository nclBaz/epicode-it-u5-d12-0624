package riccardogulin.u5d12.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.BadRequestException;
import riccardogulin.u5d12.payloads.NewUserDTO;
import riccardogulin.u5d12.services.UsersService;

import java.util.UUID;

/*

1. GET http://localhost:3001/users
2. POST http://localhost:3001/users (+ req.body) --> 201
3. GET http://localhost:3001/users/{userId}
4. PUT http://localhost:3001/users/{userId} (+ req.body)
5. DELETE http://localhost:3001/users/{userId} --> 204

*/


@RestController
@RequestMapping("/users")
public class UsersController {
	@Autowired
	private UsersService usersService;

	@GetMapping
	@PreAuthorize("hasAuthority('ADMIN')") // <-- Solo gli ADMIN possono visualizzare la lista degli utenti in questa app
	public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
	                          @RequestParam(defaultValue = "id") String sortBy) {
		// Mettiamo dei valori di default per far si che non ci siano errori se il client non ci invia uno dei query parameters
		return this.usersService.findAll(page, size, sortBy);
	}

	// ************************************************* /ME ENDPOINTS ***********************************************
	// Se ho effettuato SecurityContextHolder.getContext().setAuthentication(authentication) nel Filter, allora negli endpoint autenticati
	// posso accedere a chi è l'utente che sta effettuando la richiesta, tramite @AuthenticationPrincipal. Grazie a questo Principal quindi
	// possiamo andare ad implementare tutta una serie di endpoint "personali", cioè endpoint per leggere il proprio profilo, cambiare i propri
	// dati oppure anche cancellare se stessi. Inoltre grazie al Principal potremo in futuro anche andare ad effettuare dei controlli, es:
	// endpoint per cancellare un record di cui sono proprietario, devo fare una verifica che il proprietario corrisponda al Principal

	@GetMapping("/me")
	public User getProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
		return currentAuthenticatedUser;
	}

	@PutMapping("/me")
	public User updateProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody @Validated NewUserDTO body) {
		return this.usersService.findByIdAndUpdate(currentAuthenticatedUser.getId(), body);
	}

	@DeleteMapping("/me")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
		this.usersService.findByIdAndDelete(currentAuthenticatedUser.getId());
	}

	@GetMapping("/{userId}")
	public User findById(@PathVariable UUID userId) {
		return this.usersService.findById(userId);
	}

	@PutMapping("/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	public User findByIdAndUpdate(@PathVariable UUID userId, @RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			validationResult.getAllErrors().forEach(System.out::println);
			throw new BadRequestException("Ci sono stati errori nel payload!");
		}
		// Ovunque ci sia un body bisognerebbe validarlo!
		return this.usersService.findByIdAndUpdate(userId, body);
	}

	@DeleteMapping("/{userId}")
	@PreAuthorize("hasAuthority('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void findByIdAndDelete(@PathVariable UUID userId) {
		this.usersService.findByIdAndDelete(userId);
	}

}
