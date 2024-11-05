package riccardogulin.u5d12.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import riccardogulin.u5d12.entities.User;
import riccardogulin.u5d12.exceptions.UnauthorizedException;
import riccardogulin.u5d12.services.UsersService;
import riccardogulin.u5d12.tools.JWT;

import java.io.IOException;
import java.util.UUID;

@Component // Non dimenticare @Component altrimenti questa classe non verrà utilizzata nella catena dei filtri
public class JWTCheckerFilter extends OncePerRequestFilter {

	@Autowired
	private JWT jwt;
	@Autowired
	private UsersService usersService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer "))
			throw new UnauthorizedException("Inserire token nell'Authorization Header nel formato corretto!");
		String accessToken = authHeader.substring(7);

		jwt.verifyToken(accessToken);

		// ******************************************************* AUTORIZZAZIONE ****************************************************************

		// Se voglio abilitare le regole di AUTORIZZAZIONE, devo "informare" Spring Security su chi sia l'utente che sta effettuando questa richiesta
		// così facendo Spring Security riuscirà a controllarne il ruolo per poi nei vari endpoint poter utilizzare l'annotazione @PreAuthorize
		// specifica per il controllo ruoli

		// 1. Cerco l'utente tramite id (l'id l'abbiamo messo nel token!)
		String userId = jwt.getIdFromToken(accessToken);
		User currentUser = this.usersService.findById(UUID.fromString(userId));

		// 2. Trovato l'utente posso associarlo al cosiddetto Security Context, questa è la maniera per Spring Security di associare l'utente alla
		// richiesta corrente
		Authentication authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
		// Il terzo parametro serve per poter utilizzare i vari @PreAuthorize perchè così il SecurityContext saprà quali sono i ruoli dell'utente
		// che sta effettuando la richiesta
		SecurityContextHolder.getContext().setAuthentication(authentication); // Aggiorniamo il SecurityContext associandogli l'utente autenticato

		// 3. Andiamo avanti
		filterChain.doFilter(request, response);

	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return new AntPathMatcher().match("/auth/**", request.getServletPath());
	}
}
