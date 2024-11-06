package riccardogulin.u5d12.tools;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import riccardogulin.u5d12.entities.User;

@Component
public class MailgunSender {
	private String apiKey;
	private String domain;

	public MailgunSender(@Value("${mailgun.apikey}") String apiKey,
	                     @Value("${mailgun.domain}") String domain) {
		this.apiKey = apiKey;
		this.domain = domain;
	}

	public void sendRegistrationEmail(User recipient) {
		HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domain + "/messages")
				.basicAuth("api", this.apiKey)
				.queryString("from", "riccardo.gulin@gmail.com")
				.queryString("to", recipient.getEmail())
				.queryString("subject", "Registrazione completata!")
				.queryString("text", "Benvenuto " + recipient.getName() + " sulla nostra piattaforma!")
				.asJson();
		System.out.println(response.getBody()); // <-- Consiglio di stampare, soprattutto le prime volte,
		// la risposta che ci mandano, per rilevare eventuali problemi
	}
}
