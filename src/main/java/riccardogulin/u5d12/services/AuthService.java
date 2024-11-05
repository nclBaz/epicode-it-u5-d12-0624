package riccardogulin.u5d12.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.UnauthorizedException;
import riccardogulin.u5d12.payloads.UserLoginDTO;
import riccardogulin.u5d12.tools.JWT;

@Service
public class AuthService {
	@Autowired
	private UsersService usersService;

	@Autowired
	private JWT jwt;

	public String checkCredentialsAndGenerateToken(UserLoginDTO body) {
		// 1. Controllo le credenziali
		// 1.1 Cerco nel DB se esiste un utente con l'email fornita
		User found = this.usersService.findByEmail(body.email());
		// 1.2 Verifico che la password di quell'utente corrisponda a quella fornita
		if (found.getPassword().equals(body.password())) {
			// 2. Se sono OK --> Genero il token
			String accessToken = jwt.createToken(found);
			// 3. Ritorno il token
			return accessToken;
		} else {
			// 4. Se le credenziali sono errate --> 401 (Unauthorized)
			throw new UnauthorizedException("Credenziali errate!");
		}
	}

}
