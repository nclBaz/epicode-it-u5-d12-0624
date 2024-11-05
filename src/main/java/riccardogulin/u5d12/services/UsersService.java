package riccardogulin.u5d12.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.BadRequestException;
import riccardogulin.u5d12.exceptions.NotFoundException;
import riccardogulin.u5d12.payloads.NewUserDTO;
import riccardogulin.u5d12.repositories.UsersRepository;

import java.util.UUID;

@Service
public class UsersService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private PasswordEncoder bcrypt;

	public User save(NewUserDTO body) {
		// 1. Verifico che l'email non sia già in uso
		this.usersRepository.findByEmail(body.email()).ifPresent(
				// 1.1 Se trovo uno user con quell'indirizzo triggera un errore
				user -> {
					throw new BadRequestException("Email " + body.email() + " già in uso!");
				}
		);

		// 2. Se è ok allora aggiungo i campi "server-generated" come ad esempio avatarURL
		User newUser = new User(body.name(), body.surname(), body.email(), bcrypt.encode(body.password()),
				"https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());

		// 3. Salvo il nuovo utente
		return this.usersRepository.save(newUser);
	}

	public Page<User> findAll(int page, int size, String sortBy) {
		if (size > 100) size = 100; // Limitiamo la size max a 100 così da client non possono inserire numeri troppo grandi
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		// Pageable ci consente di configurare come devono essere paginati i risultati passando numero di pagina, numero elementi pagina e criterio di ordinamento
		return this.usersRepository.findAll(pageable);
	}

	public User findById(UUID userId) {
		return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
	}

	public User findByIdAndUpdate(UUID userId, NewUserDTO body) {
		// 1. Cerco l'utente nel db
		User found = this.findById(userId);

		// 2. Controllo se l'email nuova è già in uso
		if (!found.getEmail().equals(body.email())) {
			this.usersRepository.findByEmail(body.email()).ifPresent(
					// 1.1 Se trovo uno user con quell'indirizzo triggera un errore
					user -> {
						throw new BadRequestException("Email " + body.email() + " già in uso!");
					}
			);
		}

		// 3. Modifico l'utente trovato nel db
		found.setName(body.name());
		found.setSurname(body.surname());
		found.setEmail(body.email());
		found.setPassword(body.password());
		found.setAvatarURL("https://ui-avatars.com/api/?name=" + body.name() + "+" + body.surname());

		// 4. Risalvo l'utente
		return this.usersRepository.save(found);
	}

	public void findByIdAndDelete(UUID userId) {
		User found = this.findById(userId);
		this.usersRepository.delete(found);
	}

	public User findByEmail(String email) {
		return this.usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("L'utente con email " + email + " non è stato trovato"));
	}


}
