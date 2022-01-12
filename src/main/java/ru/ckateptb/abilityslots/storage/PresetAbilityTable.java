package ru.ckateptb.abilityslots.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ckateptb.tablecloth.storage.ormlite.field.DatabaseField;
import ru.ckateptb.tablecloth.storage.ormlite.table.DatabaseTable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "abilityslots_presets")
public class PresetAbilityTable {
    @DatabaseField(id = true, canBeNull = false, unique = true)
    private String id;
    @DatabaseField(canBeNull = false)
    private String uuid;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(canBeNull = false)
    private String abilities;
}
