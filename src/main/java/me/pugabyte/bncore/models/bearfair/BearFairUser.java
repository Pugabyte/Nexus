package me.pugabyte.bncore.models.bearfair;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemMetaConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.Location;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.plural;
import static me.pugabyte.bncore.utils.Utils.sendActionBar;

@Data
@Entity("bearfair_user")
@NoArgsConstructor
@AllArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class, ItemStackConverter.class, ItemMetaConverter.class})
public class BearFairUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	// Points
	public transient static final int DAILY_SOURCE_POINTS = 5;
	public transient static final int DAILY_SOURCE_MAX = 5;
	private Map<BFPointSource, Map<LocalDate, Integer>> pointsReceivedToday = new HashMap<>();
	private int totalPoints;
	// First Visit
	private boolean firstVisit = true;
	// Easter Eggs
	@Embedded
	private List<Location> easterEggsLocs = new ArrayList<>();
	// Quests
	private boolean Quest_Main_Start = false;
	private boolean Quest_Main_Finish = false;
	private int Quest_Main_Step = 0;
	private boolean Quest_Hive_Access = false;
	private boolean Quest_talkedWith_Collector = false;
	private boolean Quest_talkedWith_Miner = false;
	//
	private boolean Quest_SDU_Start = false;
	private boolean Quest_SDU_Finish = false;
	private int Quest_SDU_Step = 0;
	//
	private boolean Quest_MGN_Start = false;
	private boolean Quest_MGN_Finish = false;
	private int Quest_MGN_Step = 0;
	//	@Embedded
//	private List<ItemStack> arcadePieces = new ArrayList<>();
	private boolean Quest_MGN_hasCPU = false;
	private boolean Quest_MGN_hasProcessor = false;
	private boolean Quest_MGN_hasMemoryCard = false;
	private boolean Quest_MGN_hasMotherBoard = false;
	private boolean Quest_MGN_hasPowerSupply = false;
	private boolean Quest_MGN_hasSpeaker = false;
	private boolean Quest_MGN_hasHardDrive = false;
	private boolean Quest_MGN_hasDiode = false;
	private boolean Quest_MGN_hasJoystick = false;


	//
	private boolean Quest_Halloween_Start = false;
	private boolean Quest_Halloween_Finish = false;
	private int Quest_Halloween_Step = 0;
	private boolean Quest_Halloween_Key = false;
	//
	private boolean Quest_Pugmas_Start = false;
	private boolean Quest_Pugmas_Finish = false;
	private int Quest_Pugmas_Step = 0;
	private boolean Quest_Pugmas_Switched = false;
	@Embedded
	private List<Location> presentLocs = new ArrayList<>();
	//

	public BearFairUser(UUID uuid) {
		this.uuid = uuid;
	}

	public void givePoints(int points, boolean actionBar) {
		if (actionBar)
			sendActionBar(getPlayer(), "&e+" + points + plural(" point", points));
		givePoints(points);
	}

	public void givePoints(int points) {
		totalPoints += points;
	}

	public void takePoints(int points) {
		totalPoints -= points;
	}

	public void giveDailyPoints(BFPointSource source) {
		pointsReceivedToday.putIfAbsent(source, new HashMap<LocalDate, Integer>() {{
			put(LocalDate.now(), 0);
		}});

		int timesCompleted = pointsReceivedToday.get(source).getOrDefault(LocalDate.now(), 0);

		if (timesCompleted == DAILY_SOURCE_MAX)
			return;

		if ((timesCompleted + 1) == DAILY_SOURCE_MAX)
			send(BearFair20.PREFIX + "Max daily points reached for &e" + StringUtils.camelCase(source.name()));

		getPointsReceivedToday().get(source).put(LocalDate.now(), timesCompleted + 1);

		givePoints(DAILY_SOURCE_POINTS);
		sendActionBar(getPlayer(), "&e+" + DAILY_SOURCE_POINTS + plural(" point", DAILY_SOURCE_POINTS));
	}

	public enum BFPointSource {
		ARCHERY,
		BASKETBALL,
		FROGGER,
		PUGDUNK,
		REFLECTION
	}


}
