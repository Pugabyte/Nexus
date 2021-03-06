package gg.projecteden.nexus.features.trust;

import com.griefcraft.model.Permission;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCAccessEvent;
import gg.projecteden.nexus.models.trust.TrustService;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
public class LWCTrustModule extends JavaModule {

	@Override
	public void onAccessRequest(LWCAccessEvent event) {
		UUID owner;
		try {
			owner = UUID.fromString(event.getProtection().getOwner());
		} catch (IllegalArgumentException e) {
			return;
		}

		UUID requester = event.getPlayer().getUniqueId();
		List<UUID> trusted = new TrustService().get(owner).getLocks();

		if (trusted.contains(requester))
			event.setAccess(Permission.Access.PLAYER);
	}

}
