package simulation;

public enum PointStates {
    NO_FIRE("No Fire"),
    LITTER("Litter"),
    FLOOR("Floor"),
    UNDERSTORY("Understory"),
    CONIFEROUS("Coniferous"),
    DECIDUOUS("Deciduous"),
    FIRE("Fire");

    private final String description;

    PointStates(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public static PointStates fromDescription(String description) {
        for (PointStates state : PointStates.values()) {
            if (state.description.equalsIgnoreCase(description)) {
                return state;
            }
        }
        throw new IllegalArgumentException("No enum constant with description " + description);
    }
}