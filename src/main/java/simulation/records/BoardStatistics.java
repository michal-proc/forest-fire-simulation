package simulation.records;

public record BoardStatistics(
        int allFields,
        int burntFields,
        int fireFields,
        int litterFields,
        int floorFields,
        int understoryFields,
        int coniferousFields,
        int deciduousFields
) {
}
