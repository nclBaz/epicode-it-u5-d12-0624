package riccardogulin.u5d12.payloads;

import java.time.LocalDateTime;

public record ErrorsResponseDTO(String message, LocalDateTime timestamp) {
}
