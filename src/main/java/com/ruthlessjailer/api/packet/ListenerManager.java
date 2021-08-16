package com.ruthlessjailer.api.packet;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;

/**
 * @author RuthlessJailer
 */
public interface ListenerManager extends Listener {

	/**
	 * Convenience method.
	 *
	 * @param clazz the packet to listen for
	 * @param mutator the lambda argument that acts as the listener
	 *
	 * @return the created {@link PacketListener} instance
	 *
	 * @see PacketListener
	 */
	<P extends Packet<?>> PacketListener<P> register(Class<P> clazz, BiFunction<Player, P, P> mutator);

	/**
	 * Start sending packets through the provided listener.
	 *
	 * @param listener the listener to register
	 */
	<P extends Packet<?>> void register(PacketListener<P> listener);

	/**
	 * Stop sending packets through the provided listener.
	 *
	 * @param listener the listener to unregister
	 *
	 * @return {@literal true} if the listener was present, {@literal false} otherwise
	 */
	<P extends Packet<?>> boolean unregister(PacketListener<P> listener);

	/**
	 * Begin listening for packets for all currently online players.
	 * This method only needs to be called once by the plugin owning this {@link ListenerManager} instance.
	 *
	 * @param plugin the plugin, used to register a task
	 */
	void listen(Plugin plugin);
}