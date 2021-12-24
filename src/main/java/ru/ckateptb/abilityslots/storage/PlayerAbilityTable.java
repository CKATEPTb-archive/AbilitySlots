package ru.ckateptb.abilityslots.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainerConverter;
import ru.ckateptb.tablecloth.storage.ormlite.field.DataType;
import ru.ckateptb.tablecloth.storage.ormlite.field.DatabaseField;
import ru.ckateptb.tablecloth.storage.ormlite.table.DatabaseTable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "abilityslots_players")
public class PlayerAbilityTable {
    @DatabaseField(id = true, canBeNull = false, unique = true)
    private String uuid;
    @DatabaseField(canBeNull = false, dataType = DataType.SERIALIZABLE)
    private AbilitySlotContainerConverter container;
}
