package me.pugabyte.nexus.features.menus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public final class SignMenuFactory {
	private static final int ACTION_INDEX = 9;
	private static final int SIGN_LINES = 4;

	private static final String NBT_FORMAT = "{\"text\":\"%s\"}";
	private static final String NBT_BLOCK_ID = "minecraft:sign";

	private static final List<String> BLANK = Arrays.asList("", "", "", "");

	private final Plugin plugin;

	private final Map<Player, Menu> inputReceivers;
	private final Map<Player, BlockPosition> signLocations;

	public SignMenuFactory(Plugin plugin) {
		this.plugin = plugin;
		this.inputReceivers = new HashMap<>();
		this.signLocations = new HashMap<>();
		this.listen();
	}

	public Menu blank() {
		return new Menu(BLANK);
	}

	public Menu lines(String... linesArray) {
		if (linesArray == null)
			return blank();
		return new Menu(Arrays.asList(linesArray));
	}

	public Menu lines(List<String> lines) {
		if (lines == null)
			return blank();
		return new Menu(lines);
	}

	private void listen() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();
				String[] input = packet.getStringArrays().read(0);

				Menu menu = inputReceivers.remove(player);
				BlockPosition blockPosition = signLocations.remove(player);

				if (menu == null || blockPosition == null)
					return;

				event.setCancelled(true);

				Tasks.sync(() -> {
					try {
						menu.response.accept(input);
					} catch (Exception ex) {
						menu.onError.accept(input, ex);
						MenuUtils.handleException(player, menu.prefix, ex);
					}
				});

				Location location = blockPosition.toLocation(player.getWorld());
				player.sendBlockChange(location, location.getBlock().getType().createBlockData());
			}
		});
	}

	public static final class Menu {
		private final List<String> lines;
		private Consumer<String[]> response;
		private BiConsumer<String[], Exception> onError;
		private String prefix; // for error handler

		Menu(List<String> lines) {
			this.lines = lines;
		}

		public Menu prefix(String prefix) {
			this.prefix = prefix;
			return this;
		}

		public Menu response(Consumer<String[]> response) {
			this.response = response;
			return this;
		}

		public Menu onError(BiConsumer<String[], Exception> onError) {
			this.onError = onError;
			return this;
		}

		public void open(Player player) {
			Location location = player.getLocation();
			BlockPosition blockPosition = new BlockPosition(location.getBlockX(), Math.max(location.getBlockY() - 5, 0), location.getBlockZ());

			player.sendBlockChange(blockPosition.toLocation(location.getWorld()), Material.OAK_WALL_SIGN.createBlockData());

			PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
			PacketContainer signData = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);

			openSign.getBlockPositionModifier().write(0, blockPosition);

			NbtCompound signNBT = (NbtCompound) signData.getNbtModifier().read(0);

			// DO NOT REMOVE "WW"
			IntStream.range(0, SIGN_LINES).forEach(line -> signNBT.put("Text" + (line + 1), lines.size() > line ? String.format(NBT_FORMAT, colorize(lines.get(line))) : "WW"));

			signNBT.put("x", blockPosition.getX());
			signNBT.put("y", blockPosition.getY());
			signNBT.put("z", blockPosition.getZ());
			signNBT.put("id", NBT_BLOCK_ID);

			signData.getBlockPositionModifier().write(0, blockPosition);
			signData.getIntegers().write(0, ACTION_INDEX);
			signData.getNbtModifier().write(0, signNBT);

			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, signData);
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
			} catch (InvocationTargetException exception) {
				exception.printStackTrace();
			}

			Nexus.getSignMenuFactory().signLocations.put(player, blockPosition);
			Nexus.getSignMenuFactory().inputReceivers.putIfAbsent(player, this);
		}
	}
}
