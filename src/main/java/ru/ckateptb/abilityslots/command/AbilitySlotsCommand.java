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

package ru.ckateptb.abilityslots.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.service.AbilityCategoryService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.service.AbilityUserService;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;
import ru.ckateptb.tablecloth.minedown.MineDown;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class AbilitySlotsCommand {
    private final AbilityService abilityService;
    private final AbilityCategoryService categoryService;
    private final AbilityUserService abilityUserService;
    private final AbilitySlotsConfig config;
    private final AbilitySlots plugin = AbilitySlots.getInstance();

    public AbilitySlotsCommand(AbilityService abilityService, AbilityCategoryService categoryService, AbilityUserService abilityUserService, AbilitySlotsConfig config) {
        this.abilityService = abilityService;
        this.categoryService = categoryService;
        this.abilityUserService = abilityUserService;
        this.config = config;
        new CommandAPICommand("abilityslots")
                .withAliases("abilityslot", "abilities", "ability", "as")
                .withPermission("abilityslots.command")
                .executes(this::executeHelp)
                .withSubcommand(
                        new CommandAPICommand("display")
                                .withPermission("abilityslots.command.display")
                                .withAliases("d")
                                .executes(this::executeDisplay)
                )
                .withSubcommand(
                        new CommandAPICommand("display")
                                .withPermission("abilityslots.command.display")
                                .withAliases("d")
                                .withArguments(new StringArgument("category").replaceSuggestions(this::categorySuggestion))
                                .executes(this::executeDisplay)
                )
                .withSubcommand(
                        new CommandAPICommand("bind")
                                .withPermission("abilityslots.command.bind")
                                .withAliases("b")
                                .withArguments(new StringArgument("ability").replaceSuggestions(this::abilitySuggestion))
                                .executes(this::executeBind)
                )
                .withSubcommand(
                        new CommandAPICommand("bind")
                                .withPermission("abilityslots.command.bind")
                                .withAliases("b")
                                .withArguments(new StringArgument("ability").replaceSuggestions(this::abilitySuggestion))
                                .withArguments(new IntegerArgument("slot"))
                                .executes(this::executeBind)
                )
                .withSubcommand(
                        new CommandAPICommand("bind")
                                .withPermission("abilityslots.command.bind")
                                .withAliases("b")
                                .withArguments(new StringArgument("ability").replaceSuggestions(this::abilitySuggestion))
                                .withArguments(new IntegerArgument("slot"))
                                .withArguments(new PlayerArgument("target").withPermission("abilityslots.command.bind.other"))
                                .executes(this::executeBind)
                )
                .withSubcommand(
                        new CommandAPICommand("clear")
                                .withPermission("abilityslots.command.clear")
                                .withAliases("cl", "c")
                                .executes(this::executeClear)
                )
                .withSubcommand(
                        new CommandAPICommand("clear")
                                .withPermission("abilityslots.command.clear")
                                .withAliases("cl", "c")
                                .withArguments(new PlayerArgument("target").withPermission("abilityslots.command.clear.other"))
                                .executes(this::executeClear)
                )
                .withSubcommand(
                        new CommandAPICommand("who")
                                .withPermission("abilityslots.command.who")
                                .withAliases("w")
                                .executes(this::executeWho)
                )
                .withSubcommand(
                        new CommandAPICommand("who")
                                .withPermission("abilityslots.command.who")
                                .withAliases("w")
                                .withArguments(new PlayerArgument("target").withPermission("abilityslots.command.who.other"))
                                .executes(this::executeWho)
                )
                .withSubcommand(
                        new CommandAPICommand("presets")
                                .withPermission("abilityslots.command.preset")
                                .withAliases("preset", "p")
                                .withSubcommand(
                                        new CommandAPICommand("create")
                                                .withPermission("abilityslots.command.preset.create")
                                                .withArguments(new StringArgument("name"))
                                                .withAliases("c", "s", "save")
                                                .executesPlayer(this::executePresetCreate)
                                )
                                .withSubcommand(
                                        new CommandAPICommand("delete")
                                                .withPermission("abilityslots.command.preset.delete")
                                                .withArguments(new StringArgument("name").replaceSuggestions(this::presetSuggestion))
                                                .withAliases("d", "r", "remove")
                                                .executesPlayer(this::executePresetDelete)
                                )
                                .withSubcommand(
                                        new CommandAPICommand("bind")
                                                .withPermission("abilityslots.command.preset.bind")
                                                .withArguments(new StringArgument("name").replaceSuggestions(this::presetSuggestion))
                                                .withAliases("b", "u", "use")
                                                .executesPlayer(this::executePresetBind)
                                )
                                .withSubcommand(
                                        new CommandAPICommand("list")
                                                .withPermission("abilityslots.command.preset.list")
                                                .withAliases("l")
                                                .executesPlayer(this::executePresetList)
                                )
                )
                .withSubcommand(
                        new CommandAPICommand("reload")
                                .withPermission("abilityslots.command.reload")
                                .executes(this::executeReload)
                )
                .register();
    }

    public String[] categorySuggestion(SuggestionInfo info) {
        return categoryService.getCategories().stream()
                .filter(AbilityCategory::isEnabled)
                .filter(category -> {
                    if (info.sender() instanceof Player player) {
                        PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
                        if (user == null) return false;
                        return user.canUse(category);
                    } else return true;
                })
                .map(AbilityCategory::getName)
                .toArray(String[]::new);
    }

    public String[] abilitySuggestion(SuggestionInfo info) {
        return abilityService.getAbilities().stream()
                .filter(AbilityInformation::isEnabled)
                .filter(AbilityInformation::isCanBindToSlot)
                .filter(ability -> {
                    if (info.sender() instanceof Player player) {
                        PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
                        if (user == null) return false;
                        return user.canBind(ability);
                    } else return true;
                })
                .map(AbilityInformation::getName)
                .toArray(String[]::new);
    }

    public String[] presetSuggestion(SuggestionInfo info) {
        if (info.sender() instanceof Player player) {
            PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
            if (user != null) {
                user.getPresets().keySet().toArray(String[]::new);
            }
        }
        return new String[]{};
    }

    public void executePresetCreate(CommandSender sender, Object[] args) {
        String name = parseArgument(String.class, args);
        if (name == null || name.trim().isEmpty()) {
            sender.sendMessage(config.getCommandPresetInvalidNameMessage());
            return;
        }
        if (sender instanceof Player player) {
            PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
            if (user == null) {
                sender.sendMessage(config.getCommandPlayerIsNotAbilityUserMessage());
                return;
            }
            if (user.getPresets().size() >= config.getMaxPresetsPerPlayer()) {
                sender.sendMessage(config.getCommandPresetLimitMessage());
                return;
            }
            if (user.savePreset(name)) {
                sender.sendMessage(config.getCommandPresetCreatedMessage());
            } else {
                sender.sendMessage(config.getCommandPresetExistsMessage());
            }
        } else {
            sender.sendMessage(config.getCommandPlayerNotFoundMessage());
        }
    }

    public void executePresetBind(CommandSender sender, Object[] args) {
        String name = parseArgument(String.class, args);
        if (name == null || name.trim().isEmpty()) {
            sender.sendMessage(config.getCommandPresetInvalidNameMessage());
            return;
        }
        if (sender instanceof Player player) {
            PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
            if (user == null) {
                sender.sendMessage(config.getCommandPlayerIsNotAbilityUserMessage());
                return;
            }
            if (user.bindPreset(name)) {
                sender.sendMessage(config.getCommandPresetActivatedMessage());
            } else {
                sender.sendMessage(config.getCommandPresetNotFoundMessage());
            }
        } else {
            sender.sendMessage(config.getCommandPlayerNotFoundMessage());
        }
    }

    public void executePresetDelete(CommandSender sender, Object[] args) {
        String name = parseArgument(String.class, args);
        if (name == null || name.trim().isEmpty()) {
            sender.sendMessage(config.getCommandPresetInvalidNameMessage());
            return;
        }
        if (sender instanceof Player player) {
            PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
            if (user == null) {
                sender.sendMessage(config.getCommandPlayerIsNotAbilityUserMessage());
                return;
            }
            if (user.removePreset(name)) {
                sender.sendMessage(config.getCommandPresetDeletedMessage());
            } else {
                sender.sendMessage(config.getCommandPresetNotFoundMessage());
            }
        } else {
            sender.sendMessage(config.getCommandPlayerNotFoundMessage());
        }
    }

    public void executePresetList(CommandSender sender, Object[] args) {
        if (sender instanceof Player player) {
            PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
            if (user == null) {
                sender.sendMessage(config.getCommandPlayerIsNotAbilityUserMessage());
                return;
            }
            sender.sendMessage(String.join("\n", user.getPresets().keySet()));
        } else {
            sender.sendMessage(config.getCommandPlayerNotFoundMessage());
        }
    }

    public void executeReload(CommandSender sender, Object[] args) {
        plugin.reload();
        sender.sendMessage(config.getCommandReloadSuccessMessage());
    }

    public void executeHelp(CommandSender sender, Object[] args) {
        PluginDescriptionFile description = plugin.getDescription();
        String help = ChatColor.GOLD +
                description.getName() +
                ChatColor.GREEN +
                " v." +
                description.getVersion() +
                ChatColor.RED +
                " by [CKATEPTb](https://github.com/CKATEPTb)\n" +
                ChatColor.GRAY +
                "[/abilityslots display \\[category\\]](suggest_command=/abilityslots display show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandDisplayDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots bind \\(ability\\) \\[slot\\]](suggest_command=/abilityslots bind show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandBindDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots clear \\[slot\\]](suggest_command=/abilityslots clear show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandClearDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots who \\[player\\]](suggest_command=/abilityslots who show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandWhoDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots preset list](suggest_command=/abilityslots preset list show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandPresetListDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots preset create \\(name\\)](suggest_command=/abilityslots preset create show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandPresetCreateDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots preset delete \\(name\\)](suggest_command=/abilityslots preset delete show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandPresetDeleteDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots preset bind \\(name\\)](suggest_command=/abilityslots preset bind show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandPresetBindDescription() + ")\n" +
                ChatColor.GRAY +
                "[/abilityslots reload](suggest_command=/abilityslots reload show_text=" +
                ChatColor.RESET +
                ChatColor.GRAY +
                config.getCommandReloadDescription() + ")\n";
        sender.spigot().sendMessage(MineDown.parse(help));
    }

    public void executeClear(CommandSender sender, Object[] args) {
        Player target = parseArgument(Player.class, args);
        if (sender instanceof Player player) {
            if (target == null) target = player;
        }
        if (target == null) {
            sender.sendMessage(config.getCommandPlayerNotFoundMessage());
            return;
        }
        PlayerAbilityUser user = abilityUserService.getAbilityPlayer(target);
        if (user == null) {
            sender.sendMessage(config.getCommandPlayerIsNotAbilityUserMessage());
            return;
        }
        user.clearAbilities();
    }

    public void executeWho(CommandSender sender, Object[] args) {
        Player target = parseArgument(Player.class, args);
        if (sender instanceof Player player) {
            if (target == null) target = player;
        }
        if (target == null) {
            sender.sendMessage(config.getCommandPlayerNotFoundMessage());
            return;
        }
        PlayerAbilityUser user = abilityUserService.getAbilityPlayer(target);
        if (user == null) {
            sender.sendMessage(config.getCommandPlayerIsNotAbilityUserMessage());
            return;
        }
        AbilitySlotsConfig config = SpringContext.getInstance().getBean(AbilitySlotsConfig.class);
        StringBuilder builder = new StringBuilder();
        for (int slotIndex = 1; slotIndex <= 9; ++slotIndex) {
            AbilityInformation ability = user.getSlotContainer().getAbility(slotIndex);
            builder.append("\n");

            if (ability == null) {
                builder.append(config.getBoardEmptySlot());
            } else {
                AbilityCategory category = ability.getCategory();
                String prefix = config.isRespectCategoryPrefix() ? category.getPrefix() : "";
                builder
                        .append("[")
                        .append(ability.getFormattedName())
                        .append("]")
                        .append("(")
                        .append("hover=")
                        .append(ChatColor.RESET)
                        .append(config.getAuthorText())
                        .append(ability.getAuthor())
                        .append("\n")
                        .append(ChatColor.RESET)
                        .append(config.getCategoryText())
                        .append(prefix)
                        .append(category.getDisplayName())
                        .append("\n")
                        .append(ChatColor.RESET)
                        .append(config.getDescriptionText())
                        .append(prefix)
                        .append(ability.getDescription())
                        .append("\n")
                        .append(ChatColor.RESET)
                        .append(config.getInstructionText())
                        .append(prefix)
                        .append(ability.getInstruction())
                        .append(")");
            }
        }
        sender.spigot().sendMessage(MineDown.parse(builder.toString()));
    }

    public void executeDisplay(CommandSender sender, Object[] args) {
        AbilityCategory category = categoryService.getCategory(parseArgument(String.class, args));
        Stream<AbilityInformation> abilityStream = this.abilityService.getAbilities().stream();
        // sort by display name
        abilityStream = abilityStream.sorted((o1, o2) -> ChatColor.stripColor(o1.getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(o2.getDisplayName())));
        if (category != null) {
            abilityStream = abilityStream.filter(ability -> ability.getCategory() == category);
        } else {
            // sort by category display name
            abilityStream = abilityStream.sorted((o1, o2) -> ChatColor.stripColor(o1.getCategory().getDisplayName()).compareToIgnoreCase(ChatColor.stripColor(o2.getCategory().getDisplayName())));
        }
        if (sender instanceof Player player) {
            PlayerAbilityUser user = abilityUserService.getAbilityPlayer(player);
            if (user != null) {
                abilityStream = abilityStream.filter(user::canUse);
            }
        }
        Map<AbilityCategory, List<AbilityInformation>> abilities = new HashMap<>();
        abilityStream.forEach(ability -> abilities.computeIfAbsent(ability.getCategory(), key -> new ArrayList<>()).add(ability));
        abilities.forEach((key, value) -> {
            if (!value.isEmpty()) {
                String categoryPrefix = config.getCommandDisplayCategoryPrefixMessage();
                String categoryMessage = key.getDisplayName() + ":";
                if (!categoryPrefix.isBlank()) {
                    categoryMessage = categoryPrefix + ChatColor.stripColor(categoryMessage);
                }
                sender.sendMessage(categoryMessage);
                List<AbilityInformation> passives = new ArrayList<>();
                List<AbilityInformation> sequences = new ArrayList<>();
                sender.sendMessage(config.getCommandDisplayAbilitiesMessage());
                value.forEach(ability -> {
                    if (ability.isActivatedBy(ActivationMethod.PASSIVE)) {
                        passives.add(ability);
                    } else if (ability.isActivatedBy(ActivationMethod.SEQUENCE)) {
                        sequences.add(ability);
                    } else {
                        sender.spigot().sendMessage(ability.toBaseComponent());
                    }
                });
                if (!passives.isEmpty()) {
                    sender.sendMessage(config.getCommandDisplayPassivesMessage());
                    passives.forEach(ability -> sender.spigot().sendMessage(ability.toBaseComponent()));
                }
                if (!sequences.isEmpty()) {
                    sender.sendMessage(config.getCommandDisplaySequencesMessage());
                    sequences.forEach(ability -> sender.spigot().sendMessage(ability.toBaseComponent()));
                }
            }
        });
    }

    public void executeBind(CommandSender sender, Object[] args) {
        AbilityInformation ability = abilityService.getAbility(parseArgument(String.class, args));
        if (ability == null) {
            sender.sendMessage(config.getCommandCouldNotFindAbilityMessage());
            return;
        }
        Integer slot = parseArgument(Integer.class, args);
        Player target = parseArgument(Player.class, args);
        if (sender instanceof Player player) {
            if (slot == null) slot = player.getInventory().getHeldItemSlot() + 1;
            if (target == null) target = player;
        }
        if (slot == null || slot < 1 || slot > 9) {
            sender.sendMessage(config.getCommandInvalidSlotMessage());
            return;
        }
        if (target == null) {
            sender.sendMessage(config.getCommandPlayerNotFoundMessage());
            return;
        }
        PlayerAbilityUser user = abilityUserService.getAbilityPlayer(target);
        if (user == null) {
            sender.sendMessage(config.getCommandPlayerIsNotAbilityUserMessage());
            return;
        }
        if (!user.canBind(ability)) {
            sender.sendMessage(config.getCommandAbilityIsNotAvailableForUserMessage());
            return;
        }
        user.setAbility(slot, ability);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> parseArguments(Class<T> type, Object[] args) {
        List<T> list = new ArrayList<>();
        for (Object arg : args) {
            if (ClassUtils.isAssignable(type, arg.getClass())) {
                list.add((T) arg);
            }
        }
        return list;
    }

    public <T> T parseArgument(Class<T> type, Object[] args) {
        List<T> list = parseArguments(type, args);
        if (list.isEmpty()) return null;
        return list.get(0);
    }
}
