package vrn.edinorog.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vrn.edinorog.enums.CompetitionStage;

import javax.persistence.*;

@Entity
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Duel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DUEL_ID")
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "RED_FIGHTER_ID")
    private final Fighter redFighter;

    @ManyToOne
    @JoinColumn(name = "BLUE_FIGHTER_ID")
    private final Fighter blueFighter;

    private final Integer duelGroupId;

    private final Integer duelRound;

    @ManyToOne
    @JoinColumn(name = "NOMINATION_ID")
    private final Nomination nomination;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private final CompetitionStage currentStage;

    private final int currentRoundIndex;

    @Setter
    private Integer redScore;
    @Setter
    private Integer blueScore;
    @Setter
    private Integer mutualHitCount;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Setter
    private Winner winner;
    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    @Setter
    private DuelStatus duelStatus;

    public Fighter getWinner() {
        if (winner == null || duelStatus == null || !duelStatus.equals(DuelStatus.FINISHED)) {
            return null;
        }

        switch (winner) {
            case RED:
            case RED_TECHNICAL_WIN:
                return redFighter;
            case BLUE:
            case BLUE_TECHNICAL_WIN:
                return blueFighter;
            case DRAW:
            case MUTUAL_DEFEAT:
                return null;
        }

        return null;
    }

    public Fighter getLoser() {
        if (winner == null || duelStatus == null || !duelStatus.equals(DuelStatus.FINISHED)) {
            return null;
        }

        switch (winner) {
            case RED:
            case RED_TECHNICAL_WIN:
                return blueFighter;
            case BLUE:
            case BLUE_TECHNICAL_WIN:
                return redFighter;
            case DRAW:
            case MUTUAL_DEFEAT:
                return null;
        }

        return null;
    }

    public enum Winner {
        RED,
        BLUE,
        RED_TECHNICAL_WIN,
        BLUE_TECHNICAL_WIN,
        DRAW,
        MUTUAL_DEFEAT,
    }

    public enum DuelStatus {
        UNFINISHED,
        FINISHED,
        CANCELED
    }
}
