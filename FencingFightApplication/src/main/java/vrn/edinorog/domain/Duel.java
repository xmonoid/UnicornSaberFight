package vrn.edinorog.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Duel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DUEL_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "RED_FIGHTER_ID")
    private final Fighter redFighter;

    @ManyToOne
    @JoinColumn(name = "BLUE_FIGHTER_ID")
    private final Fighter blueFighter;

    private final Integer roundId;

    @Setter
    private Integer redScore;
    @Setter
    private Integer blueScore;
    @Setter
    private Winner winner;

    public enum Winner {
        RED,
        BLUE,
        DRAW,
        MUTUAL_DEFEAT
    }
}
