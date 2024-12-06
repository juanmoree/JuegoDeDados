package JuegoDeDados.Model.DTO;

import JuegoDeDados.Model.Entity.Game;
import JuegoDeDados.Model.Entity.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDTO {

    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;
    @JsonIgnore
    private List<Game> games;
    private double average;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> roles;

    public PlayerDTO(Player player) {
        this.name = player.getName();
        this.games = player.getGames();
        this.average = player.calculateWinningAverage();
    }
}
