package JuegoDeDados.Services;

import JuegoDeDados.Model.DTO.PlayerDTO;
import JuegoDeDados.Model.ERole;
import JuegoDeDados.Model.Entity.Player;
import JuegoDeDados.Model.Entity.RoleEntity;
import JuegoDeDados.Repository.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private static Long anonymousCounter = 1L;
    @Autowired
    private final PlayerRepository playerRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public PlayerDTO toDTO(Player player) {
        return new PlayerDTO(player);
    }

    public Player toEntity(PlayerDTO playerDTO){
        return new Player(playerDTO);
    }

    public Player addPlayer(PlayerDTO playerDTO) {

        // Verificar si el nombre es nulo o está vacío.
        if (playerDTO.getName() == null || playerDTO.getName().trim().isEmpty()) {
            playerDTO.setName("Anónimo" + anonymousCounter);
            anonymousCounter++;
        } else {
            // Obtener el Optional
            Optional<Player> existingPlayerOptional = playerRepository.findPlayerByName(playerDTO.getName());

            if (existingPlayerOptional.isPresent()) {
                throw new IllegalStateException("Player with the same name already exists");
            }
        }

        Player player = toEntity(playerDTO);

        player.setRoles(setPlayerRoles(playerDTO));
        player.setPassword(passwordEncoder.encode(playerDTO.getPassword()));
        playerRepository.save(player);

        return player;
    }

    private Set<RoleEntity> setPlayerRoles(PlayerDTO playerDTO) {
        return playerDTO.getRoles().stream()
                .map(role -> RoleEntity.builder()
                        .name(ERole.valueOf(role))
                        .build())
                .collect(Collectors.toSet());
    }

    public void updatePlayerName(Long id, String newName) {
        Optional<Player> existingPlayer = playerRepository.findById(id);
        Optional<Player> existingName = playerRepository.findPlayerByName(newName);
        if (existingPlayer.isPresent()) {
            if (!existingName.isPresent()) {
                Player updatedPlayer = existingPlayer.get();
                updatedPlayer.setName(newName);
                playerRepository.save(updatedPlayer);
            } else {
                throw new IllegalStateException("Name already exists");
            }
        } else {
            throw new IllegalStateException("Player not found");
        }
    }

    public List<PlayerDTO> getPlayersWithAverage() {
        List<Player> players = playerRepository.findAll();

        return players.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public float getAverageRankingPlayers() {
        if (playerRepository == null) {
            throw new EntityNotFoundException("No existe ningún jugador");
        }

        List<Player> players = playerRepository.findAll();
        float count = 0;
        for (Player x : players) {
            count += (float) x.calculateWinningAverage();
        }
        return count / players.size();
    }

    // Retorna lista por si hay mas de un jugador con el mismo promedio.
    public List<PlayerDTO> getPlayerWorseAverage() {
        if (playerRepository == null) {
            throw new EntityNotFoundException("No existe ningún jugador");
        }
        Optional<Player> playerWorseAverage = playerRepository.findAll().stream()
                .min(Comparator.comparingDouble(Player::calculateWinningAverage));
        if (playerWorseAverage.isPresent()){
            double worseAverage = playerWorseAverage.get().calculateWinningAverage();
            List<Player> sameAverage = playerRepository.findAll().stream()
                    .filter(player -> player.calculateWinningAverage() == worseAverage)
                    .toList();
            return sameAverage.stream().map(this::toDTO).toList();
        }
        return null;
    }

    // Retorna lista por si hay mas de un jugador con el mismo promedio.
    public List<PlayerDTO> getPlayerBestAverage() {
        if (playerRepository == null) {
            throw new EntityNotFoundException("No existe ningún jugador");
        }
        Optional<Player> playerWorseAverage = playerRepository.findAll().stream()
                .max(Comparator.comparingDouble(Player::calculateWinningAverage));
        if (playerWorseAverage.isPresent()){
            double bestAverage = playerWorseAverage.get().calculateWinningAverage();
            List<Player> sameAverage = playerRepository.findAll().stream()
                    .filter(player -> player.calculateWinningAverage() == bestAverage)
                    .toList();
            return sameAverage.stream().map(this::toDTO).toList();
        }
        return null;
    }
}