package me.pugabyte.bncore.features.dailyrewards;

import com.google.common.base.Strings;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.dailyreward.DailyReward;
import me.pugabyte.bncore.models.dailyreward.DailyRewardService;
import me.pugabyte.bncore.models.dailyreward.Reward;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.Voter;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.loreize;

public class DailyRewardsMenu extends MenuUtils implements InventoryProvider {
	private DailyRewardService service = new DailyRewardService();
	private DailyReward dailyReward;

	private ItemStack back = new ItemBuilder(Material.BARRIER).name("&cScroll back 1 day").build();
	private ItemStack back7 = new ItemBuilder(Material.BARRIER).amount(7).name("&cScroll back 7 days").build();
	private ItemStack forward = new ItemBuilder(Material.ARROW).name("&2Scroll forward 1 day").build();
	private ItemStack forward7 = new ItemBuilder(Material.ARROW).amount(7).name("&2Scroll forward 7 days").build();

	private final int MAX_DAY = DailyRewardsFeature.getMaxDays();
	private ItemStack claimed = new ItemStack(Material.WHITE_WOOL);
	private ItemStack unclaimed = new ItemStack(Material.WHITE_WOOL);
	private ItemStack locked = new ItemStack(Material.BLACK_WOOL);

	DailyRewardsMenu(DailyReward dailyReward) {
		this.dailyReward = dailyReward;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		scroll(contents, 0, 1);
	}

	private ItemStack nameItem(ItemStack item, String name, String lore, int day) {
		ItemStack itemStack = super.nameItem(item, name, lore);
		itemStack.setAmount(day);
		return itemStack;
	}

	private void clearScreen(InventoryContents contents){
		for(SlotPos slotPos:contents.slots()){
			contents.set(slotPos, ClickableItem.from(new ItemStack(Material.AIR), null));
		}
	}

	private void scroll(InventoryContents contents, int change, int day) {
		day += change;
		if (day < 1) day = 1;
		if (day > MAX_DAY - 6) day = MAX_DAY - 6;

		final int initialDay = day;
		contents.set(new SlotPos(0, 0), ClickableItem.from(back, e -> scroll(contents, -1, initialDay)));
		contents.set(new SlotPos(2, 0), ClickableItem.from(back7, e -> scroll(contents, -7, initialDay)));
		contents.set(new SlotPos(0, 8), ClickableItem.from(forward, e -> scroll(contents, 1, initialDay)));
		contents.set(new SlotPos(2, 8), ClickableItem.from(forward7, e -> scroll(contents, 7, initialDay)));

		int column = 1;
		for (int i = 0; i < 7; ++i) {

			if (this.dailyReward.getStreak() >= day) {
				if (this.dailyReward.hasClaimed(day)) {
					ItemStack item = nameItem(claimed.clone(), "&eDay " + day, "&3Claimed" + "", day);
					contents.set(new SlotPos(1, column), ClickableItem.empty(addGlowing(item)));
				} else {
					ItemStack item = nameItem(unclaimed.clone(), "&eDay " + day, "&6&lUnclaimed" + "Click to select reward.", day);
					final int currentDay = day;
					contents.set(new SlotPos(1, column), ClickableItem.from(item, e -> {
						selectItem(contents, currentDay, initialDay);
					}));
				}
			} else {
				ItemStack item = nameItem(locked.clone(), "&eDay " + day, "&cLocked" + "", day);
				contents.set(new SlotPos(1, column), ClickableItem.empty(item));
			}

			++day;
			++column;
		}
	}


	private void selectItem(InventoryContents contents, int currentDay, int initialDay) {

		clearScreen(contents);
		contents.set(new SlotPos(0,0), ClickableItem.from(backItem(), e -> scroll(contents, 0, initialDay)));

		Reward[] reward = new Reward[3];
		reward[0] = DailyRewardsFeature.getReward1(currentDay);
		reward[1] = DailyRewardsFeature.getReward2(currentDay);
		reward[2] = DailyRewardsFeature.getReward3(currentDay);

		for(int i = 0; i < i; i++){

			Reward currentReward = reward[i];
			int option = i;
			String rewardDescription = "&e" + currentReward.getDescription();
			ItemStack item = nameItem(currentReward != null ? currentReward.getItems().get(0) : addGlowing(new ItemStack(Material.PAPER)), rewardDescription, "&3Click to claim", currentDay);

			contents.set(new SlotPos(0, (2+i*2)), ClickableItem.from(item, e-> {

				applyReward(currentDay, option);

			}));

		}


	}
	@Override
	public void update(Player player, InventoryContents inventoryContents) {}



	private void applyReward(int day, int option) {
		Player player = (Player) dailyReward.getPlayer();

		Reward reward = DailyRewardsFeature.getReward(day, option);
		List<ItemStack> items = reward.getItems();
		Integer money = reward.getMoney();
		Integer levels = reward.getLevels();
		Integer votePoints = reward.getVotePoints();
		String command = reward.getCommand();

		if (items != null){
			for (ItemStack item:items) {
				if(Reward.RequiredSubmenu.COLOR.contains(item.getType())){
					MenuUtils.colorSelectMenu(player, item.getType(), itemClickData -> {
						Utils.giveItem(player, new ItemStack(itemClickData.getItem().getType()));
						dailyReward.claim(day);
						service.save(dailyReward);
					});
				} else if (Reward.RequiredSubmenu.NAME.contains(item.getType())){
					BNCore.getSignMenuFactory().lines(" ", "^^^^^^^^^", "Please enter a", "Player name").response(lines -> {
						Utils.giveItem(player, new ItemBuilder(Material.PLAYER_HEAD).skullOwner(lines[0]).build());
						dailyReward.claim(day);
						service.save(dailyReward);
					});
				} else {
					Utils.giveItem(player, item);
					dailyReward.claim(day);
					service.save(dailyReward);
				}
			}

		} else {

			if (money != null)
				BNCore.getEcon().depositPlayer(player, money);

			if (levels != null)
				Utils.runConsoleCommand("exp give " + player.getName() + " " + levels);

			if (votePoints != null) {
				Voter voter = new VoteService().get(player);
				voter.addPoints(votePoints);
			}

			if (!Strings.isNullOrEmpty(command))
				Utils.runConsoleCommand(command.replaceAll("%player%", player.getName()));

			dailyReward.claim(day);
			service.save(dailyReward);

		}

	}

}
