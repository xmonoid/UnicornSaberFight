package vrn.edinorog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vrn.edinorog.domain.Duel;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter @Setter
public class DuelsManagementDto {

    private MethodType methodType;

    private Map<Integer, List<Duel>> playGroundNumberToDuelsMap;

    private Map<Integer, List<Duel>> playGroundNumberToFinishedDuelsMap;

    private int playGroundNumber;

    private int newPlayGroundNumber;

    private Long duelId;

    public enum MethodType {
        ADD_DUELS,
        CHANGE_PLAYGROUND_NUMBER,
        FINISH_DUEL,
        CLEAR_FINIS_DUELS_LIST
    }

}