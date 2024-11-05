package riccardogulin.u5d12.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import riccardogulin.u5d12.payloads.ErrorsResponseDTO;

import java.time.LocalDateTime;

@RestControllerAdvice // <-- OBBLIGATORIA
// @RestControllerAdvice ci rende questa classe un 'Controller' specifico per la GESTIONE DELLE ECCEZIONI a livello centralizzato
// Controller è messo tra virgolette, perché qua non andremo a creare degli endpoint, bensì andremo a scrivere dei metodi che gestiscono le varie
// eccezioni. Gestire le eccezioni vuol dire che, ogniqualvolta si scatena un certo tipo di eccezione, devo inviare una risposta con tanto di status
// code opportuno e messaggio di errore adeguato al client in maniera che sappia quale sia stato il problema
// Questa classe è quindi utile per una gestione CENTRALIZZATA delle eccezioni, quindi invece di gestire le eccezioni a livello di singolo endpoint
// (con dei try-catch), tutte le eccezioni arriveranno ai seguenti metodi, ognuno dei quali sarà responsabile della gestione di una specifica eccezione
// Ci sarà un'annotazione speciale che si chiama @ExceptionHandler da utilizzare sui metodi, in modo da poter specificare quale eccezione quel
// metodo debba gestire
public class ExceptionsHandler {

	@ExceptionHandler(BadRequestException.class) // Tra le parentesi indico quale eccezione dovrà essere gestita da questo metodo
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
	public ErrorsResponseDTO handleBadrequest(BadRequestException ex) {
		return new ErrorsResponseDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
	public ErrorsResponseDTO handleUnauthorized(UnauthorizedException ex) {
		return new ErrorsResponseDTO(ex.getMessage(), LocalDateTime.now());
	}


	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND) // 404
	public ErrorsResponseDTO handleNotFound(NotFoundException ex) {
		return new ErrorsResponseDTO(ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
	public ErrorsResponseDTO handleGeneric(Exception ex) {
		ex.printStackTrace(); // Non dimentichiamoci che è estremamente utile sapere dove è stata generata un'eccezione per poterla facilmente fixare
		return new ErrorsResponseDTO("Problema lato server! Giuro che risolveremo presto!", LocalDateTime.now());
	}
}
