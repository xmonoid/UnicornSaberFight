package vrn.edinorog.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Fighter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FIGHTER_ID")
    @EqualsAndHashCode.Include
    private Long id;

    private final String firstName;
    private final String lastName;
    private final String parentName;
    private final Boolean sex; // true is male, false is female
    private final Long birthDate; // Unix date time
    @Setter
    private String club;
    @Setter
    private String city;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "NOMINATION_ID")
    private final Set<Nomination> nominations;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Setter
    private Achievement medalAchievement = Achievement.NONE;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Setter
    private Achievement cupAchievement = Achievement.NONE;
    @Setter
    private Boolean isActive = false; // it is false when a fighter has at least one fight, but he cannot continue to take part

    @Setter
    @Transient
    private Integer points = 0;

    @Setter
    @Transient
    private Set<Long> fighterIdFromTheSameClub;

    @Setter
    @Transient
    private Set<Long> rivalIds;

    public void incPoints(int value) {
        points += value;
    }

    public enum Achievement {

        GOLD,
        SILVER,
        BRONZE,
        PLAY_OFF,
        NONE

    }
}
