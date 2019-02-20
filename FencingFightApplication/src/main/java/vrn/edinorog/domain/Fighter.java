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
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Fighter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "FIGHTER_ID")
    private Long id;

    private final String firstName;
    private final String lastName;
    private final String parentName;
    private final Boolean sex; // true is male, false is female
    private final Long birthDate; // Unix date time
    private final String club;

    @ManyToMany
    @JoinColumn(name = "NOMINATION_ID")
    private final Set<Nomination> nominations;

    @Setter
    private Integer points;
    @Setter
    private Boolean isPrizeWinner;
    @Setter
    private Boolean hasCup;
}
