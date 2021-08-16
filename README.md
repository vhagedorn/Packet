# Packet

A simple netty packet listener framework.

For those of you who are mentally impaired: this requires NMS. It's way faster than using some garbage API like ProtocolLib, which uses reflection.

### Example usage:

```java
public class ListenerPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		// obtain a ListenerManager
		final ListenerManager listenerManager = Packet.singletonCringe().listenerManager;

		// convenience register method
		listenerManager.register(SpecialPacket.class, (player, packet) -> {

			// returning `packet` will passively monitor all incoming or outgoing SpecialPackets
			// returning `null` will "cancel" the "event", preventing it from going to the server or client as this listener takes priority over the server's packet handler

			return packet;
		});

		// standard register method
		listenerManager.register(new SpecialPacketListener());
	}
}
```