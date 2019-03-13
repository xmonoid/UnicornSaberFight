package vrn.edinorog.enums;

public enum CompetitionStage {

    INIT_STAGE("Этап инициализации"), // only for splitting
    QUALIFYING_STAGE("Отборочный этап"),
    ADDITIONAL_DUELS("Дополнительные бои"),
    PLAY_OFF_STAGE("Плей-офф"),
    SEMI_FINALl("Полуфинал"),
    THIRD_FINAL("Бой за третье место"),
    FINAL("Финал");

    private String title;

    CompetitionStage(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public static CompetitionStage valueOfTitle(String title) {
        if (title == null) {
            return null;
        }

        for (CompetitionStage competitionStage : values()) {
            if (competitionStage.title.equals(title)) {
                return competitionStage;
            }
        }

        throw new IllegalArgumentException(String.format("Competition stage with title '%s' wasn't found", title));

    }
}