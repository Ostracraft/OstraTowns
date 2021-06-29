package fr.ostracraft.towns.types;

public class TownSettings {
    private boolean pvp;
    private boolean fire;
    private boolean publicSpawn;

    public TownSettings() {
        this.pvp = true;
        this.fire = false;
        this.publicSpawn = true;
    }

    public TownSettings(boolean pvp, boolean fire, boolean publicSpawn) {
        this.pvp = pvp;
        this.fire = fire;
        this.publicSpawn = publicSpawn;
    }

    public boolean isPvp() {
        return pvp;
    }

    public boolean isFire() {
        return fire;
    }

    public boolean isPublicSpawn() {
        return publicSpawn;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }

    public void setPublicSpawn(boolean publicSpawn) {
        this.publicSpawn = publicSpawn;
    }

    public String toString() {
        return isPvp() +
                "#" +
                isFire() +
                "#" +
                isPublicSpawn();
    }

    public static TownSettings fromString(String input) {
        String[] list = input.split("#");
        if(list.length < 3)
            return new TownSettings();
        boolean pvp = list[0].equalsIgnoreCase("true");
        boolean fire = list[1].equalsIgnoreCase("true");
        boolean publicSpawn = list[2].equalsIgnoreCase("true");
        return new TownSettings(pvp, fire, publicSpawn);
    }
}
