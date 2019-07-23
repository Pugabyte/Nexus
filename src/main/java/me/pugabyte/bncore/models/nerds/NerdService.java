package me.pugabyte.bncore.models.nerds;

import com.dieselpoint.norm.Database;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.BaseService;
import me.pugabyte.bncore.models.persistence.BearNationDatabase;
import me.pugabyte.bncore.models.persistence.Persistence;

public class NerdService extends BaseService {
	private Database database = Persistence.getConnection(BearNationDatabase.BEARNATION);

	@Override
	public Nerd get(String uuid) {
		return database.where("uuid = ?", uuid).first(Nerd.class);
	}

	public void save(Nerd nerd) {
		BNCore.async(() -> database.upsert(nerd).execute());
	}

}
