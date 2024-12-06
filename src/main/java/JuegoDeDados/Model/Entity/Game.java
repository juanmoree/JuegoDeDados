package JuegoDeDados.Model.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean win;
    private byte dice1;
    private byte dice2;
    @ManyToOne
    @JoinColumn(name = "player_id") // Verificar nombre
    private Player player;

    public Game (Player player, byte dice1, byte dice2){
        this.player = player;
        this.dice1 = dice1;
        this.dice2 = dice2;
        this.win = (dice1 + dice2) == 7;
        player.getGames().add(this);
    }
}
