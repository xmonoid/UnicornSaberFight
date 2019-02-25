package vrn.edinorog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vrn.edinorog.domain.Nomination;

public interface NominationRepository extends JpaRepository<Nomination, Long> {

}