package me.pugabyte.bearnation.server.models.ticket;

import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.api.framework.persistence.service.MySQLService;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class TicketService extends MySQLService {

	public TicketService(Plugin plugin) {
		super(plugin);
	}

	public Ticket get(int id) {
		Ticket ticket = database.where("id = ?", id).first(Ticket.class);
		if (ticket.getId() == 0)
			throw new InvalidInputException("Ticket not found");
		return ticket;
	}

	public List<Ticket> getAllOpen() {
		return database.where("open = 1").results(Ticket.class);
	}

	public List<Ticket> getAll() {
		return database.results(Ticket.class);
	}

}
