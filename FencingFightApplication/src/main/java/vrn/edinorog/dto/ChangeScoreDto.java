package vrn.edinorog.dto;

public class ChangeScoreDto {

    private String fighter;
    private String kind;
    private int newValue;

    public ChangeScoreDto() {}

    public ChangeScoreDto(String fighter, String kind, int newValue) {
        this.fighter = fighter;
        this.kind = kind;
        this.newValue = newValue;
    }

    public String getFighter() {
        return fighter;
    }

    public void setFighter(String fighter) {
        this.fighter = fighter;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getNewValue() {
        return newValue;
    }

    public void setNewValue(int newValue) {
        this.newValue = newValue;
    }

}