package com.ruthlessjailer.api.packet;

import net.minecraft.network.protocol.Packet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface representing a class that can mutate incoming or outgoing packets of type {@link P}.<p>
 * <p>
 * It is recommended to use the {@link com.ruthlessjailer.api.packet.ListenerManager#register(Class, java.util.function.BiFunction) convenience register method}, but implementing
 * this class and then using the {@link com.ruthlessjailer.api.packet.ListenerManager#register(PacketListener) standard register method} is also a viable option.<p>
 * <p>
 * Example usage:
 *
 * <pre><code>
 * // create a ListenerManager
 * final {@link com.ruthlessjailer.api.packet.ListenerManager ListenerManager} listenerManager = new {@link  com.ruthlessjailer.api.packet.impl.PacketInjector PacketInjector}();
 *
 * // convenience register method
 * {@link com.ruthlessjailer.api.packet.ListenerManager#register(Class, java.util.function.BiFunction) listenerManager.register}(SpecialPacket.class, (player, packet) -> {
 *
 * 		// returning `packet` will passively monitor
 * 		// 	all incoming or outgoing SpecialPackets
 * 		// returning `null` will "cancel" the "event",
 * 		//	preventing it from going to the server
 * 		//	or client as this listener takes priority
 * 		//	over the server's packet handler
 *
 * 		return packet;
 * }
 *
 * // standard register method
 * {@link com.ruthlessjailer.api.packet.ListenerManager#register(PacketListener) listenerManager.register}(new SpecialPacketListener());
 * </code></pre>
 *
 * @author RuthlessJailer
 */
public interface PacketListener<P extends Packet<?>> {

	@NotNull
	Class<P> getType();

	@Nullable
	P mutate(Player player, P packet);

}
