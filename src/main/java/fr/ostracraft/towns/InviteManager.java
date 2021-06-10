package fr.ostracraft.towns;

import fr.ostracraft.towns.types.Resident;
import fr.ostracraft.towns.types.Town;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class InviteManager {

    private static final HashMap<String, List<String>> hashMap = new HashMap<>();

    public static boolean addInvite(Resident resident, Town town) {
        List<String> list = hashMap.containsKey(resident.getUuid())
                ? hashMap.get(resident.getUuid())
                : new ArrayList<>();
        if (!list.contains(String.valueOf(town.getId()))) {
            list.add(String.valueOf(town.getId()));
            hashMap.remove(resident.getUuid());
            hashMap.put(resident.getUuid(), list);
            return true;
        }
        return false;
    }

    public static void removeInvite(Resident resident, Town town) {
        List<String> list = hashMap.containsKey(resident.getUuid())
                ? hashMap.get(resident.getUuid())
                : new ArrayList<>();
        if (list.contains(String.valueOf(town.getId()))) {
            list.remove(String.valueOf(town.getId()));
            hashMap.remove(resident.getUuid());
            hashMap.put(resident.getUuid(), list);
        }
    }

    public static boolean isInvited(Resident resident, Town town) {
        List<String> list = hashMap.containsKey(resident.getUuid())
                ? hashMap.get(resident.getUuid())
                : new ArrayList<>();
        return list.contains(String.valueOf(town.getId()));
    }
}
