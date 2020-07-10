package me.pugabyte.bearnation.api.models.task;

import com.dieselpoint.norm.Transaction;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.api.framework.persistence.service.MySQLService;
import me.pugabyte.bearnation.api.models.task.Task.Status;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TaskService extends MySQLService {

	public TaskService(Plugin plugin) {
		super(plugin);
	}

	public List<Task> process(String type) {
		List<Task> tasks = new ArrayList<>();
		Transaction trans = database.startTransaction();
		try {
			 tasks = database.transaction(trans)
					.where("type = ? and status = ? and timestamp <= now() for update", type, Status.PENDING.name())
					.results(Task.class);

			tasks.forEach(task -> {
				task.setStatus(Status.RUNNING);
				database.transaction(trans).upsert(task);
			});
			trans.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			trans.rollback();
		}

		return tasks;
	}

	public void complete(Task task) {
		Transaction trans = database.startTransaction();
		try {
			task.setStatus(Status.COMPLETED);
			Task first = database.transaction(trans).where("taskId = ?", task.getTaskId()).first(Task.class);
			if (first.getStatus() != Status.RUNNING)
				throw new InvalidInputException("Tried to complete a task that was not running, but was " + first.getStatus());

			database.transaction(trans).upsert(task);
			trans.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			trans.rollback();
		}
	}

}
