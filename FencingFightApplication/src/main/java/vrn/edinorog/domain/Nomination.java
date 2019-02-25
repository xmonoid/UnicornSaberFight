package vrn.edinorog.domain;

import lombok.*;
import vrn.edinorog.enums.CompetitionStage;

import javax.persistence.*;

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

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Setter
    private CompetitionStage currentStage;

    @Setter
    private Integer currentRoundIndex;
}
