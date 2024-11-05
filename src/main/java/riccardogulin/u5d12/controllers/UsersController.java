package riccardogulin.u5d12.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
	public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
	                          @RequestParam(defaultValue = "id") String sortBy) {
		// Mettiamo dei valori di default per far si che non ci siano errori se il client non ci invia uno dei query parameters
		return this.usersService.findAll(page, size, sortBy);
	}

	@GetMapping("/{userId}")
	public User findById(@PathVariable UUID userId) {
		return this.usersService.findById(userId);
	}

	@PutMapping("/{userId}")
	public User findByIdAndUpdate(@PathVariable UUID userId, @RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
		if (validationResult.hasErrors()) {
			validationResult.getAllErrors().forEach(System.out::println);
			throw new BadRequestException("Ci sono stati errori nel payload!");
		}
		// Ovunque ci sia un body bisognerebbe validarlo!
		return this.usersService.findByIdAndUpdate(userId, body);
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void findByIdAndDelete(@PathVariable UUID userId) {
		this.usersService.findByIdAndDelete(userId);
	}

}
