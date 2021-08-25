package com.ruthlessjailer.api.packet.impl;

import com.ruthlessjailer.api.packet.ListenerManager;
import com.ruthlessjailer.api.packet.PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author RuthlessJailer
 */
public final class PacketInjector implements ListenerManager {

	private static final String                  PIPELINE  = "PacketInjector";
	private final        Set<ListenerWrapper<?>> listeners = new HashSet<>();

	@Override
	public <P extends Packet<?>> PacketListener<P> register(final Class<P> clazz, final BiFunction<Player, P, P> mutator) {
		final PacketListener<P> listener = new PacketListener<>() {
			@Override
			public @NotNull Class<P> getType() {
				return clazz;
			}

			@Nullable
			@Override
			public P mutate(final Player player, final P packet) {
				return mutator.apply(player, packet);
			}
		};

		register(listener);

		return listener;
	}

	@Override
	public <P extends Packet<?>> void register(final PacketListener<P> listener) { listeners.add(new ListenerWrapper<>(listener)); }

	@Override
	public <P extends Packet<?>> boolean unregister(final PacketListener<P> listener) { return listeners.remove(new ListenerWrapper<>(listener)); }

	@Override
	public void listen(Plugin plugin) {
		Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(this::inject));
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public void drop(final Plugin plugin) {
		Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getOnlinePlayers().forEach(this::extract));
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	private void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		inject(player);
	}

	private void inject(final Player player) {
		final Channel channel = ((CraftPlayer) player).getHandle().networkManager.channel;

		PacketInterceptor interceptor = (PacketInterceptor) channel.pipeline().get(PIPELINE);

		if (interceptor == null) {
			interceptor = new PacketInterceptor(player);
			channel.pipeline().addBefore("packet_handler", PIPELINE, interceptor);
		}
	}

	private void extract(final Player player) {
		final Channel channel = ((CraftPlayer) player).getHandle().networkManager.channel;

		channel.eventLoop().execute(() -> channel.pipeline().remove(PIPELINE));
	}

	private final class PacketInterceptor extends ChannelDuplexHandler {

		final Player player;

		PacketInterceptor(final Player player) {
			this.player = player;
		}

		@Override
		public void write(final ChannelHandlerContext ctx, Object msg, final ChannelPromise promise) throws Exception {
			for (final ListenerWrapper<?> listener : listeners) { msg = listener.mutate(player, msg); }

			if (msg != null) { super.write(ctx, msg, promise); }
		}

		@Override
		public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
			for (final ListenerWrapper<?> listener : listeners) { msg = listener.mutate(player, msg); }

			if (msg != null) { super.channelRead(ctx, msg); }
		}
	}

	private static final class ListenerWrapper<P extends Packet<?>> {

		final PacketListener<P> listener;

		ListenerWrapper(final PacketListener<P> listener) {
			this.listener = listener;
		}

		Object mutate(final Player player, final Object msg) {
			if (msg != null && listener.getType().isAssignableFrom(msg.getClass())) {
				return listener.mutate(player, (P) msg);
			} else {
				return msg;
			}
		}

		@Override
		public int hashCode() {
			return listener.hashCode();
		}

	}

//	@EventHandler
//	private void removePlayer(final PlayerQuitEvent event){
//		final Player player = event.getPlayer();
//		final Channel channel = ((CraftPlayer) player).getHandle().networkManager.channel;
//		final ChannelPipeline pipeline = channel.pipeline();
//		final String name = PIPELINE + player.getUniqueId();
//
//		if(pipeline.get(name) != null){
//			pipeline.remove(name);
//		}
//	}
}
