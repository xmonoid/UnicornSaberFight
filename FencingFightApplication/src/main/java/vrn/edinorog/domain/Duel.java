package vrn.edinorog.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vrn.edinorog.enums.CompetitionStage;

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

    private final CompetitionStage currentStage;

    private final int currentRoundIndex;

    @Setter
    private Integer redScore;
    @Setter
    private Integer blueScore;
    @Setter
    private Integer mutualHitCount;
    @Setter
    private Winner winner;
    @Setter
    private DuelStatus duelStatus;
    @Setter
    private Integer ringNumber;
    @Setter
    private Integer orderNumber;

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
