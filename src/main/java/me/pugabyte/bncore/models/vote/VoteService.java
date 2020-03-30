package me.pugabyte.bncore.models.vote;

import com.dieselpoint.norm.Query;
import lombok.Data;
import me.pugabyte.bncore.models.MySQLService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Data
public class VoteService extends MySQLService {

	@Override
	public Voter get(String uuid) {
		return new Voter(uuid, getTotalVotes(uuid), getActiveVotes(uuid), getPoints(uuid));
	}

	public int getTotalVotes() {
		return getTotalVotes(null);
	}

	public int getTotalVotes(String uuid) {
		Query query = database.table("vote").select("count(*)");
		if (uuid != null)
			query.where("uuid = ?", uuid);
		return query.first(Long.class).intValue();
	}

	public List<Vote> getActiveVotes() {
		return getActiveVotes(null);
	}

	public List<Vote> getActiveVotes(String uuid) {
		Query query = database.where("expired = 0");
		if (uuid != null)
			query.and("uuid = ?", uuid);
		return query.results(Vote.class);
	}

	public int getPoints(String uuid) {
		return database.select("balance").table("vote_point").where("uuid = ?", uuid).first(Integer.class);
	}

	public void setPoints(String uuid, int balance) {
		database.sql("update vote_point set balance = ? where uuid = ?", balance, uuid).execute();
	}

	public List<TopVoter> getTopVoters(Month month) {
		LocalDateTime first = LocalDateTime.now().withMonth(month.getValue()).withDayOfMonth(1);
		if (first.isAfter(LocalDateTime.now()))
			first = first.minusYears(1);

		return database.sql(
				"select uuid, count(*) as count " +
				"from vote where MONTH(timestamp) = ? AND YEAR(timestamp) = ? " +
				"group by 1 " +
				"order by count desc")
				.args(month.getValue(), first.getYear())
				.results(TopVoter.class);
	}

}
