package riccardogulin.u5d12.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.BadRequestException;
import riccardogulin.u5d12.payloads.NewUserDTO;
import riccardogulin.u5d12.payloads.UserLoginDTO;
import riccardogulin.u5d12.payloads.UserLoginResponseDTO;
import riccardogulin.u5d12.services.AuthService;
import riccardogulin.u5d12.services.UsersService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private UsersService usersService;

	@PostMapping("/login")
	public UserLoginResponseDTO login(@RequestBody UserLoginDTO body) {
		return new UserLoginResponseDTO(this.authService.checkCredentialsAndGenerateToken(body));
	}

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public User save(@RequestBody @Validated NewUserDTO body, BindingResult validationResult) {
		// @Validated serve per "attivare" le regole di validazione descritte nel DTO
		// BindingResult contiene l'esito della validazione, quindi sarà utile per capire se ci sono stati errori e quali essi siano
		if (validationResult.hasErrors()) {
			String message = validationResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage())
					.collect(Collectors.joining(". "));
			throw new BadRequestException("Ci sono stati errori nel payload! " + message);
		}

		return this.usersService.save(body);
	}
}
