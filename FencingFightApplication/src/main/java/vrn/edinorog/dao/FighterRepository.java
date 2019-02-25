package vrn.edinorog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import vrn.edinorog.domain.Fighter;

public interface FighterRepository extends JpaRepository<Fighter, Long> {

}