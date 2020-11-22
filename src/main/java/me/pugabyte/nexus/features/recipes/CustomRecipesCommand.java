package me.pugabyte.nexus.features.recipes;

import me.pugabyte.nexus.framework.annotations.Disabled;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

@Disabled
public class CustomRecipesCommand extends CustomCommand {

	public CustomRecipesCommand(CommandEvent event) {
		super(event);
	}


	@Path()
	void open() {
		CraftingRecipeMenu.open(CraftingMenuType.MAIN, player());
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		send(PREFIX + "Reloading all recipes...");
		int amount = CustomRecipes.getRecipes().size();
		CustomRecipes.recipes.keySet().forEach(Bukkit::removeRecipe);
		CustomRecipes.recipes.clear();
		new CustomRecipes();
		send(PREFIX + "Successfully reloaded &e" + amount + "&3 custom recipes");
	}


}