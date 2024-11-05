package riccardogulin.u5d12.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties({"password", "role", "accountNonLocked", "credentialsNonExpired", "accountNonExpired", "authorities", "enabled"})
public class User implements UserDetails {
	@Id
	@GeneratedValue
	@Setter(AccessLevel.NONE)
	private UUID id;
	private String name;
	private String surname;
	private String email;
	private String password;
	private String avatarURL;
	@Enumerated(EnumType.STRING)
	private Role role;

	public User(String name, String surname, String email, String password, String avatarURL) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.avatarURL = avatarURL;
		this.role = Role.USER; // Tutti all'inizio vengono creati come utenti "semplici" poi in caso un admin potrà decidere di "promuoverli"
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Questo metodo deve tornare una lista di ruoli dell'utente. Più in dettaglio vuole che venga restituita una lista di oggetti che implementano
		// GrantedAuthority. SimpleGrantedAuthority è una classe che rappresenta i ruoli degli utenti nel mondo Spring Security
		// ed implementa GrantedAuthority, quindi dobbiamo prendere il nostro ruolo (enum) e passare il name()
		// di quel ruolo al costruttore dell'oggetto
		return List.of(new SimpleGrantedAuthority(this.role.name()));
	}

	@Override
	public String getUsername() {
		return this.getEmail();
	}

}
