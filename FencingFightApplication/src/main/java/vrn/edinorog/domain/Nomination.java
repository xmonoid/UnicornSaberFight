package vrn.edinorog.domain;

import lombok.*;
import vrn.edinorog.enums.CompetitionStage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Nomination {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NOMINATION_ID")
    @EqualsAndHashCode.Include
    private Long id;

    private final String name;

    @Setter
    private CompetitionStage currentStage;

    @Setter
    private int currentRoundIndex;
}
