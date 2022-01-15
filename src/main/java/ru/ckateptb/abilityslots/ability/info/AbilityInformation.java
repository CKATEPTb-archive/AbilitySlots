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

package ru.ckateptb.abilityslots.ability.info;

import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.tablecloth.config.YamlConfigLoadEvent;
import ru.ckateptb.tablecloth.config.YamlConfigSaveEvent;
import ru.ckateptb.tablecloth.minedown.MineDown;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface AbilityInformation extends Listener {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getFormattedName();

    String getFormattedNameForUser(AbilityUser user);

    String getDescription();

    void setDescription(String description);

    String getInstruction();

    void setInstruction(String instruction);

    long getCooldown();

    void setCooldown(long cooldown);

    double getCost();

    void setCost(double cost);

    boolean isActivatedBy(ActivationMethod method);

    ActivationMethod[] getActivationMethods();

    boolean isCanBindToSlot();

    String getAuthor();

    AbilityCategory getCategory();

    Ability createAbility();

    Class<? extends Ability> getAbilityClass();

    boolean isCollisionParticipant();

    default boolean canDestroyAbility(Ability ability) {
        return canDestroyAbility(ability.getInformation());
    }

    boolean canDestroyAbility(AbilityInformation ability);

    Set<AbilityInformation> getDestroyAbilities();

    boolean addDestroyAbility(AbilityInformation ability);

    boolean removeDestroyAbility(AbilityInformation ability);

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigLoadEvent event) {
        AnnotationConfigApplicationContext context = SpringContext.getInstance();
        if (event.getYamlConfig() != context.getBean(AbilitySlotsConfig.class)) return;
        AbilityService abilityService = context.getBean(AbilityService.class);
        YamlConfiguration config = event.getBukkitConfig();
        setEnabled(config.getBoolean(getConfigPath("enabled"), isEnabled()));
        setDisplayName(config.getString(getConfigPath("name"), getDisplayName()));
        setDescription(config.getString(getConfigPath("description"), getDescription()));
        if (!(isActivatedBy(ActivationMethod.PASSIVE) && getActivationMethods().length == 1)) {
            setInstruction(config.getString(getConfigPath("instruction"), getInstruction()));
            setCooldown(config.getLong(getConfigPath("cooldown"), getCooldown()));
            setCost(config.getDouble(getConfigPath("cost"), getCost()));
        }
        if (isCollisionParticipant()) {
            CollisionParticipant destroyerInfo = getAbilityClass().getAnnotation(CollisionParticipant.class);
            List<String> def = Arrays.stream(destroyerInfo.destroyAbilities())
                    .filter(destroyClass -> AnnotatedElementUtils.isAnnotated(destroyClass, AbilityInfo.class))
                    .map(destroyClass -> destroyClass.getAnnotation(AbilityInfo.class))
                    .map(AbilityInfo::name)
                    .toList();
            config.getList(getConfigPath("collision.destroy"), def).stream()
                    .map(name -> (String) name)
                    .map(abilityService::getAbility)
                    .filter(Objects::nonNull)
                    .forEach(this::addDestroyAbility);
        }
        event.scan(getAbilityClass(), null, this::getConfigPath);
    }

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigSaveEvent event) {
        if (event.getYamlConfig() != SpringContext.getInstance().getBean(AbilitySlotsConfig.class)) return;
        event.set(getConfigPath("enabled"), isEnabled());
        event.set(getConfigPath("name"), getDisplayName());
        event.set(getConfigPath("description"), getDescription());
        if (!(isActivatedBy(ActivationMethod.PASSIVE) && getActivationMethods().length == 1)) {
            event.set(getConfigPath("instruction"), getInstruction());
            event.set(getConfigPath("cooldown"), getCooldown());
            event.set(getConfigPath("cost"), getCost());
        }
        if (isCollisionParticipant()) {
            event.set(getConfigPath("collision.destroy"), getDestroyAbilities().stream().map(AbilityInformation::getName).collect(Collectors.toList()));
        }
        event.scan(getAbilityClass(), null, this::getConfigPath);
    }

    default String getConfigPath(String field) {
        return "abilities." + getCategory().getName() + "." + getName() + "." + field;
    }

    default BaseComponent[] toBaseComponent() {
        AbilitySlotsConfig config = SpringContext.getInstance().getBean(AbilitySlotsConfig.class);
        AbilityCategory category = getCategory();
        String prefix = config.isRespectCategoryPrefix() ? category.getPrefix() : "";
        StringBuilder builder = new StringBuilder();
        if (!isActivatedBy(ActivationMethod.PASSIVE) && !isActivatedBy(ActivationMethod.SEQUENCE)) {
            builder
                    .append(config.getBindToSlotText())
                    .append(prefix)
                    .append("[①](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 1)")
                    .append("[②](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 2)")
                    .append("[③](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 3)")
                    .append("[④](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 4)")
                    .append("[⑤](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 5)")
                    .append("[⑥](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 6)")
                    .append("[⑦](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 7)")
                    .append("[⑧](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 8)")
                    .append("[⑨](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 9) - ");
        }
        builder
                .append("[")
                .append(getFormattedName())
                .append("]")
                .append("(")
                .append("hover=")
                .append(ChatColor.RESET)
                .append(config.getAuthorText())
                .append(getAuthor())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getCategoryText())
                .append(prefix)
                .append(category.getDisplayName())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getDescriptionText())
                .append(prefix)
                .append(getDescription())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getInstructionText())
                .append(prefix)
                .append(getInstruction())
                .append(")");
        return MineDown.parse(builder.toString());
    }
}
