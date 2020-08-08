package org.mcteam.ancientgates.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;
import org.mcteam.ancientgates.Plugin;


public class SpigotUpdater extends Updater {

	public SpigotUpdater(final JavaPlugin plugin, final int id, final UpdateType type) {
		super(plugin, id, type);
		this.getThread().start();
	}

	@Override
	protected boolean read() {
		try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.getId()).openStream();
		        Scanner scanner = new Scanner(inputStream)) {
			if (scanner.hasNext()) {
				this.versionName = scanner.next();
			}
		} catch (final IOException e) {
			Plugin.log(Level.WARNING, "Spigot might be down or have it's protection up! This error can be safely ignored");
			this.setResult(UpdateResult.FAIL_DBO);
			return false;
		}
		return true;
	}

	@Override
	public final boolean downloadFile() {
		return false;
	}

}
