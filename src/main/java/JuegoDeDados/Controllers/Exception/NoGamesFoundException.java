package JuegoDeDados.Controllers.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoGamesFoundException extends RuntimeException {
    public NoGamesFoundException(String playerName) {
        super("El jugador " + playerName + " aún no ha tirado los dados");
    }
}
