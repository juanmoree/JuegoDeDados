package JuegoDeDados.Services;

import JuegoDeDados.Controllers.Exception.NoGamesFoundException;
import JuegoDeDados.Model.DTO.GameDTO;
import JuegoDeDados.Model.Entity.Game;
import JuegoDeDados.Model.Entity.Player;
import JuegoDeDados.Repository.GameRepository;
import JuegoDeDados.Repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GameService {

    @Autowired
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameDTO toDTO(Player player, Game game) {
        return new GameDTO(player, game);
    }

    public GameDTO newGameByIdPlayer(PlayerRepository playerRepository, Long id) {
        Optional<Player> existingPlayerOptional = playerRepository.findById(id);
        Game game = null;
        if (existingPlayerOptional.isPresent()) {
            Player existingPlayer = existingPlayerOptional.get();
            byte dice1 = rolldice();
            byte dice2 = rolldice();

            game = new Game(existingPlayer, dice1, dice2);

            existingPlayer.getGames().add(game);
            gameRepository.save(game);
        } else {
            throw new EntityNotFoundException("Jugador no encontrado");
        }
        return toDTO(existingPlayerOptional.get(), game);
    }

    private byte rolldice() {
        return (byte) (Math.random() * 6 + 1);
    }

    public GameDTO playerRollsDice(Long id) {
        return newGameByIdPlayer(playerRepository, id);
    }
    public void playerDeleteGames(Long id) {
        deleteAllGamesByIdPlayer(playerRepository, id);
    }

    public Map<String, Object> getPlayerGamesById(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new EntityNotFoundException("No existe un jugador con el id " + id);
        }

        Player player = playerRepository.getReferenceById(id);
        if (player.getGames().isEmpty()){
            throw new NoGamesFoundException(player.getName());
        }

        return getGamesByPlayer(player);
    }

    public void deleteAllGamesByIdPlayer(PlayerRepository playerRepository, Long id) {
        Optional<Player> existingPlayer = playerRepository.findById(id);
        if (existingPlayer.isPresent()) {
            if (existingPlayer.get().getGames().isEmpty()) {
                throw new IllegalStateException("No hay partidas que eliminar");
            }
            gameRepository.deleteAllInBatch(existingPlayer.get().getGames());
        } else {
            throw new IllegalStateException("Jugador no encontrado");
        }
    }

    public Map<String, Object> getGamesByPlayer(Player player) {

        if (player.getGames().isEmpty()){
            throw new NoGamesFoundException(player.getName());
        }

        Map<String, Object> response = new HashMap<>();

        List<GameDTO> games = player.getGames().stream()
                .map(game -> toDTO(player, game))
                .toList();

        response.put("name", player.getName());
        response.put("average", player.calculateWinningAverage());
        response.put("games", games);

        return response;
    }
}
