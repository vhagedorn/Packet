package com.ruthlessjailer.api.packet;

import com.ruthlessjailer.api.packet.impl.PacketInjector;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author RuthlessJailer
 */
public final class Packet extends JavaPlugin {

	private static Packet badDesignPatterns;

	public final ListenerManager listenerManager = new PacketInjector();

	@Override
	public void onEnable() {
		badDesignPatterns = this;
		listenerManager.listen(this);
	}

	public static Packet singletonCringe(){ return badDesignPatterns; }
}
