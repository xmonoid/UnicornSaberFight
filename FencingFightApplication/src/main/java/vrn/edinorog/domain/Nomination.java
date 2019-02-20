package vrn.edinorog.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

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
@EqualsAndHashCode
public class Nomination {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NOMINATION_ID")
    private Long id;

    private final String name;
}
