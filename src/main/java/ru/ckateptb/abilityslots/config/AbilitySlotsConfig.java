/*
 * Copyright (c) 2022 CKATEPTb <https://github.com/CKATEPTb>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.ckateptb.abilityslots.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.plugin.Plugin;
import org.springframework.stereotype.Component;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.tablecloth.config.ConfigField;
import ru.ckateptb.tablecloth.config.YamlConfig;

@Getter
@Setter
@Component
public class AbilitySlotsConfig extends YamlConfig {
    @ConfigField(name = "debug.sequence", comment = "Debug ability action for sequences")
    private boolean sequenceDebug = false;

    @ConfigField(name = "database.mysql.enabled")
    private boolean mysqlEnable = false;
    @ConfigField(name = "database.mysql.host")
    private String mysqlHost = "localhost";
    @ConfigField(name = "database.mysql.port")
    private String mysqlPort = "3306";
    @ConfigField(name = "database.mysql.database")
    private String mysqlDatabase = "database";
    @ConfigField(name = "database.mysql.username")
    private String mysqlUsername = "username";
    @ConfigField(name = "database.mysql.password")
    private String mysqlPassword = "password";

    @ConfigField(name = "global.castPreventType", comment = "Available types: COOLDOWN (the user can use the ability while it is not on cooldown), ENERGY (the user can use the abilities as long as he has a energy to use), MIXED (use both options)")
    private String castPreventType = AbilityCastPreventType.MIXED.name();
    @ConfigField(name = "global.energy.name")
    private String energyName = "§5Ability Energy";
    @ConfigField(name = "global.energy.color", comment = "Available types: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    private String energyColor = BarColor.YELLOW.name();
    @ConfigField(name = "global.energy.style", comment = "Available types: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20")
    private String energyStyle = BarStyle.SOLID.name();
    @ConfigField(name = "global.energy.max", comment = "Base resource limit")
    private double energyMax = 100;
    @ConfigField(name = "global.energy.regen", comment = "The amount of resource that will be restored every second")
    private double energyRegen = 5;
    @ConfigField(name = "global.maxPresetsPerPlayer", comment = "The maximum number of presets a player can create")
    private int maxPresetsPerPlayer = 10;

    @ConfigField(name = "global.protection.cacheDuration", comment = "For optimization, we use cached data about whether the user can use abilities in the specified location. Specify how long (in millies) the cached data stays up to date. The higher the value, the lower the load and the accuracy.")
    private long protectionCacheDuration = 30000;
    @ConfigField(name = "global.protection.worldguard")
    private boolean protectionWorldGuard = true;
    @ConfigField(name = "global.protection.lwc")
    private boolean protectionLWC = true;
    @ConfigField(name = "global.protection.towny")
    private boolean protectionTowny = true;
    @ConfigField(name = "global.protection.griefprevention")
    private boolean protectionGriefPrevention = true;

    @ConfigField(name = "global.board.enabled", comment = "Displaying current abilities in the scoreboard")
    private boolean boardEnabled = true;
    @ConfigField(name = "global.board.header")
    private String boardHeader = "§f§lAbilities:";
    @ConfigField(name = "global.board.emptySlot")
    private String boardEmptySlot = "§8-- Empty --";
    @ConfigField(name = "global.board.abilitySlot")
    private String boardAbilitySlot = "%slot%";
    @ConfigField(name = "global.board.comboDelimiter")
    private String boardComboDelimiter = "§f§lCombos:";

    @ConfigField(name = "language.baseComponent.respectCategoryPrefix")
    private boolean respectCategoryPrefix = true;
    @ConfigField(name = "language.baseComponent.author")
    private String authorText = "§f§lAuthor: §8";
    @ConfigField(name = "language.baseComponent.category")
    private String categoryText = "§f§lCategory: §8";
    @ConfigField(name = "language.baseComponent.description")
    private String descriptionText = "§f§lDescription: §8";
    @ConfigField(name = "language.baseComponent.instruction")
    private String instructionText = "§f§lInstruction: §8";
    @ConfigField(name = "language.baseComponent.bindToSlot")
    private String bindToSlotText = "§8Click for binding: §8";

    @ConfigField(name = "language.command.message.abilityNotFound")
    private String commandCouldNotFindAbilityMessage = "§cCould not find the specified ability.";
    @ConfigField(name = "language.command.message.invalidSlot")
    private String commandInvalidSlotMessage = "§cSlot must be an integer from 1 to 9 (inclusive).";
    @ConfigField(name = "language.command.message.playerNotFound")
    private String commandPlayerNotFoundMessage = "§cThe specified player was not found.";
    @ConfigField(name = "language.command.message.playerIsNotAbilityUser")
    private String commandPlayerIsNotAbilityUserMessage = "§cThe specified player is not an ability user.";
    @ConfigField(name = "language.command.message.abilityIsNotAvailableForUser")
    private String commandAbilityIsNotAvailableForUserMessage = "§cThis ability is not available for user.";
    @ConfigField(name = "language.command.message.reloadSuccess")
    private String commandReloadSuccessMessage = "§aPlugin reloaded successfully, see console for details.";
    @ConfigField(name = "language.command.message.display.categoryPrefix")
    private String commandDisplayCategoryPrefixMessage = "§f§l";
    @ConfigField(name = "language.command.message.display.abilities")
    private String commandDisplayAbilitiesMessage = "§f§lAbilities:";
    @ConfigField(name = "language.command.message.display.passives")
    private String commandDisplayPassivesMessage = "§f§lPassives:";
    @ConfigField(name = "language.command.message.display.sequences")
    private String commandDisplaySequencesMessage = "§f§lSequences:";
    @ConfigField(name = "language.command.message.preset.invalidName")
    private String commandPresetInvalidNameMessage = "§cYou made a mistake in the preset name";
    @ConfigField(name = "language.command.message.preset.limit")
    private String commandPresetLimitMessage = "§cYou have reached the maximum count of presets, delete one to create a new one";
    @ConfigField(name = "language.command.message.preset.exists")
    private String commandPresetExistsMessage = "§cA preset with the same name already exists";
    @ConfigField(name = "language.command.message.preset.created")
    private String commandPresetCreatedMessage = "§aYou have successfully created a new preset. You can use it using the command /as p b presetName, where presetName is the name of your preset";
    @ConfigField(name = "language.command.message.preset.notFound")
    private String commandPresetNotFoundMessage = "§cThe preset with the specified name was not found.";
    @ConfigField(name = "language.command.message.preset.activated")
    private String commandPresetActivatedMessage = "§aYou have successfully activated the specified preset";
    @ConfigField(name = "language.command.message.preset.deleted")
    private String commandPresetDeletedMessage = "§aYou have successfully deleted the specified preset";

    @ConfigField(name = "language.command.description.display")
    private String commandDisplayDescription = "Show available abilities \\(in specified category\\)";
    @ConfigField(name = "language.command.description.bind")
    private String commandBindDescription = "Bind the specified ability to the specified slot \\(if no slot is specified, it will be selected automatically\\)";
    @ConfigField(name = "language.command.description.clear")
    private String commandClearDescription = "Unbind the ability from the specified slot \\(if no slot is specified, it will unbind all\\)";
    @ConfigField(name = "language.command.description.who")
    private String commandWhoDescription = "Display player abilities";
    @ConfigField(name = "language.command.description.preset.list")
    private String commandPresetListDescription = "View Specified Player Abilities";
    @ConfigField(name = "language.command.description.preset.create")
    private String commandPresetCreateDescription = "Save current abilities to preset with specified name";
    @ConfigField(name = "language.command.description.preset.delete")
    private String commandPresetDeleteDescription = "Delete the specified preset";
    @ConfigField(name = "language.command.description.preset.bind")
    private String commandPresetBindDescription = "Activate the specified preset";
    @ConfigField(name = "language.command.description.reload")
    private String commandReloadDescription = "Reload plugin config and abilities";

    @Override
    public void init() {
    }

    public AbilityCastPreventType getCastPreventType() {
        return AbilityCastPreventType.valueOf(castPreventType);
    }

    public BarColor getEnergyColor() {
        return BarColor.valueOf(energyColor);
    }

    public BarStyle getEnergyStyle() {
        return BarStyle.valueOf(energyStyle);
    }

    @Override
    public Plugin getPlugin() {
        return AbilitySlots.getInstance();
    }
}
