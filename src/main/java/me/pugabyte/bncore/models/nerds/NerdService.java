package me.pugabyte.bncore.models.nerds;

import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.utils.Utils;

import java.util.List;

public class NerdService extends BaseService {
	@Override
	public Nerd get(String uuid) {
		Nerd nerd = database.where("uuid = ?", uuid).first(Nerd.class);
		nerd.fromPlayer(Utils.getPlayer(uuid));
		return nerd;
	}

	public Nerd find(String partialName) {
		return database
				.select("nerd.*")
				.table("nerd")
				.leftJoin("hours")
					.on("hours.uuid = nerd.uuid")
				.where("name like ?")
				.orderBy("position(? in name), hours.total desc")
				.args("%" + partialName + "%", partialName)
				.first(Nerd.class);
	}

	public List<Nerd> getOnlineNerds() {
		List<Nerd> nerds = database.where("uuid in ?", Utils.getOnlineUuids()).results(Nerd.class);
		for (Nerd nerd : nerds)
			nerd.fromPlayer(Utils.getPlayer(nerd.getUuid()));
		return nerds;
	}

}
