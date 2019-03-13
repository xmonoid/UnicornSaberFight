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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RED_FIGHTER_ID")
    private final Fighter redFighter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BLUE_FIGHTER_ID")
    private final Fighter blueFighter;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter
    private Integer duelGroupId;

    private final Integer duelRound;

    @ManyToOne
    @JoinColumn(name = "NOMINATION_ID")
    private final Nomination nomination;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private final CompetitionStage currentStage;

    private final int currentRoundIndex;

    @Setter
    private Integer redScore = 0;
    @Setter
    private Integer blueScore = 0;
    @Setter
    private Integer mutualHitCount;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Setter
    private Winner result;
    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    @Setter
    private DuelStatus duelStatus = DuelStatus.UNFINISHED;

    public Fighter getWinner() {
        if (result == null || duelStatus == null || !duelStatus.equals(DuelStatus.FINISHED)) {
            return null;
        }

        switch (result) {
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
        if (result == null || duelStatus == null || !duelStatus.equals(DuelStatus.FINISHED)) {
            return null;
        }

        switch (result) {
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

    public int getWinnerPoints () {
        if (result == null) {
            return 0;
        }

        if (result.equals(Winner.RED_TECHNICAL_WIN) || result.equals(Winner.BLUE_TECHNICAL_WIN)) {
            return 2;
        }

        if (result.equals(Winner.RED) || result.equals(Winner.BLUE)) {

            if (redScore == 0 || blueScore == 0) {
                return 4;
            }

            if (Math.abs(redScore - blueScore) > 6) {
                return 4;
            }

            return 3;

        }

        return 0;
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
