package me.pugabyte.bearnation.survival.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.features.holidays.bearfair20.models.RecipeObject;
import me.pugabyte.bearnation.features.holidays.bearfair20.quests.Recipes;

public class AllRecipesCommand extends CustomCommand {

	public AllRecipesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommandAsConsole("minecraft:recipe give " + player().getName() + " *");

		for (RecipeObject recipe : Recipes.recipes)
			runCommandAsConsole("minecraft:recipe take " + player().getName() + " bncore:custom_bearfair_" + recipe.getKey());
	}

}
