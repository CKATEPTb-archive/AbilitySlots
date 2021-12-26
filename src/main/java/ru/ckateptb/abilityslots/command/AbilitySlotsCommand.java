package ru.ckateptb.abilityslots.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.service.AbilityUserService;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

import java.util.ArrayList;
import java.util.List;

@Component
public class AbilitySlotsCommand {
    private final AbilityService abilityService;
    private final AbilityUserService abilityUserService;
    private final AbilitySlotsConfig config;
    private final AbilitySlots plugin = AbilitySlots.getInstance();

    public AbilitySlotsCommand(AbilityService abilityService, AbilityUserService abilityUserService, AbilitySlotsConfig config) {
        this.abilityService = abilityService;
        this.abilityUserService = abilityUserService;
        this.config = config;
        new CommandAPICommand("abilityslots")
                .withAliases("abilityslot", "abilities", "ability", "as")
                .withHelp("1", "2")
                .withPermission("abilityslots.command")
                .executes((sender, args) -> {
                    // TODO HELP COMMAND
                    sender.sendMessage("AbilitySlots help command");
                })
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
                        new CommandAPICommand("reload")
                                .withPermission("abilityslots.command.reload")
                                .executes(this::executeReload)
                )
                .register();
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

    public void executeReload(CommandSender sender, Object[] args) {
        plugin.reload();
        sender.sendMessage(config.getCommandReloadSuccessMessage());
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
