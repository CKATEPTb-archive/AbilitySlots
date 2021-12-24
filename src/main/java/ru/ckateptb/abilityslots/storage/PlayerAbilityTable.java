package ru.ckateptb.abilityslots.storage;

import ru.ckateptb.tablecloth.storage.ormlite.field.DatabaseField;
import ru.ckateptb.tablecloth.storage.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "abilityslots_players")
public class PlayerAbilityTable {
    @DatabaseField(id = true, canBeNull = false, unique = true)
    private String uuid;
    @DatabaseField
    private String firstSlot;
    @DatabaseField
    private String secondSlot;
    @DatabaseField
    private String thirdSlot;
    @DatabaseField
    private String fourthSlot;
    @DatabaseField
    private String fifthSlot;
    @DatabaseField
    private String sixthSlot;
    @DatabaseField
    private String seventhSlot;
    @DatabaseField
    private String eighthSlot;
    @DatabaseField
    private String ninthSlot;
}
