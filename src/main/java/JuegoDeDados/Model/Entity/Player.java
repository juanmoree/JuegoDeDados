package JuegoDeDados.Model.Entity;

import JuegoDeDados.Model.DTO.PlayerDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @OneToMany(mappedBy = "player",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnore // Evita que se serialice la lista de juegos cuando se serializa el jugador.
    private List<Game> games;

    @ManyToMany(fetch = FetchType.EAGER, // Traer todos los roles de una vez
            targetEntity = RoleEntity.class,
            cascade = CascadeType.PERSIST) // No borra los roles al borrar un usuario
    @JoinTable(name = "player_roles",
            joinColumns = @JoinColumn(name = "fk_player_id"),
            inverseJoinColumns = @JoinColumn(name = "fk_roles_id"))
    @JsonIgnore
    private Set<RoleEntity> roles;

    @Column(unique = true)
    private String name;
    private String password;
    private LocalDate date = LocalDate.now();

    public Player(PlayerDTO playerDTO) {
        this.name = playerDTO.getName();
        this.password = playerDTO.getPassword();
        this.games = new ArrayList<>();
    }

    public double calculateWinningAverage() {
        long totalWonGames = getGames().stream().filter(Game::isWin).count();
        long totalGames = getGames().size();

        if (totalGames == 0) {
            return 0.0;
        } else
            return (double) totalWonGames / totalGames * 100;
    }
}
