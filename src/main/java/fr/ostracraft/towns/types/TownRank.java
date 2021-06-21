package fr.ostracraft.towns.types;

public enum TownRank {
    CAMPEMENT(1),
    BOURG(3),
    VILLAGE(4),
    CITY(5),
    KINGDOM(7);

    private final int maxOutposts;

    TownRank(int maxOutposts) {
        this.maxOutposts = maxOutposts;
    }

    public int getMaxOutposts() {
        return maxOutposts;
    }
}
