package net.lyragames.practice.profile;

import lombok.Getter;
import lombok.Setter;
import net.lyragames.practice.kit.EditedKit;
import net.lyragames.practice.kit.Kit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */

@Getter @Setter
public class Profile {

    @Getter private static List<Profile> profiles = new LinkedList<>();

    private final UUID uuid;
    private final String name;

    private List<EditedKit> editedKits = new ArrayList<>();

    public Profile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public List<EditedKit> getEditKitsByKit(Kit kit) {
        return editedKits.stream().filter(editedKit -> editedKit.getOriginalKit().equals(kit.getName()))
                .collect(Collectors.toList());
    }

    public static Profile getByUUID(UUID uuid) {
        return profiles.stream().filter(profile -> profile.getUuid().equals(uuid))
                .findFirst().orElse(null);
    }
}
