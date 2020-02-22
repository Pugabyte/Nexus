package me.pugabyte.bncore.features.minigames.models.mechanics;

import me.pugabyte.bncore.features.minigames.mechanics.*;

public enum MechanicType {
	ARCHERY(new Archery()),
	ANVIL_DROP(new AnvilDrop()),
	CAPTURE_THE_FLAG(new CaptureTheFlag()),
	DEATH_SWAP(new DeathSwap()),
	FOUR_TEAM_DEATHMATCH(new FourTeamDeathmatch()),
	FREE_FOR_ALL(new FreeForAll()),
	INFECTION(new Infection()),
	INVERTO_INFERNO(new InvertoInferno()),
	GOLD_RUSH(new GoldRush()),
	GRAB_A_JUMBUCK(new GrabAJumbuck()),
	KANGAROO_JUMPING(new KangarooJumping()),
	MONSTER_MAZE(new MonsterMaze()),
	ONE_FLAG_CAPTURE_THE_FLAG(new OneFlagCaptureTheFlag()),
	ONE_IN_THE_QUIVER(new OneInTheQuiver()),
	PAINTBALL(new Paintball()),
	PARKOUR(new Parkour()),
	PIXEL_PAINTERS(new PixelPainters()),
	SPLEEF(new Spleef()),
	SPLEGG(new Splegg()),
	TEAM_DEATHMATCH(new TeamDeathmatch()),
	THIMBLE(new Thimble()),
	TNT_RUN(new TNTRun()),
	XRUN(new XRun());

	private Mechanic mechanic;

	MechanicType(Mechanic mechanic) {
		this.mechanic = mechanic;
	}

	public Mechanic get() {
		return mechanic;
	}

}
