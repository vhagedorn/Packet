# Packet

A simple netty packet listener framework.

## Cerebral dysfunction

For those of you who are mentally impaired: **this requires NMS**.
It's way faster than using some garbage API like ProtocolLib, which uses reflection.

If your pea brain has not comprehended the above message, or simply refuses to for unknown reasons:
have fun watching your TPS tank as you painstakingly waste days of your life supporting years-dated server versions all in one plugin!1!!<sup>1</sup>

<sup>1</sup>*If you disagree with the fact that 1.8 is dead then please leave immediately and use
[TinyProtocol](https://github.com/dmulloy2/ProtocolLib/tree/master/TinyProtocol) or even (God have mercy)
[ProtocolLib](https://github.com/dmulloy2/ProtocolLib).*

## Dependency

With all that out of the way let's get down to business.

You can either
- Copy the literally only 3 classes directly into your project (recommended).
- Follow the below procedure to add this as a dependency to your project (cooler).

#### To install as a dependency

1. Clone this repo into a new folder
2. Init gradle
3. `publishToMavenLocal`


```bash
git clone https://github.com/RuthlessJailer/Packet/
cd Packet
gradle init
./gradlew publishToMavenLocal
```

With that out of the way you can now add it to your build script.


### Gradle 😎

```gradle
repositories {
	mavenCentral()
	mavenLocal()
	
	// More repositories below...
}

dependencies {
	implementation 'com.ruthlessjailer.api.packet:Packet:1.0.0'
	
	// More dependencies below...
}
```

### Maven 🤮

```xml
<dependencies>
	<dependency>
		<groupId>com.ruthlessjailer.api.packet</groupId>
		<artifactId>Packet</artifactId>
		<version>1.0.0</version>
		<scope>compile</scope>
	</dependency>
	    
	<!-- More dependencies below... -->
	
</dependencies>
```

## Example usage

Main:

```java
/**
 * @author RuthlessJailer
 */
public class ListenerPlugin extends JavaPlugin {
	private static ListenerPlugin badDesignPatterns;

	public final ListenerManager listenerManager = new PacketInjector();

	@Override
	public void onEnable() {
		badDesignPatterns = this;
		listenerManager.listen(this); // begin listening

		// ~~standard~~ boring register method
		listenerManager.register(new SpecialPacketListener());
	}

	@Override
	public void onDisable() {
		listenerManager.drop(this); // stop listening
	}

	public static ListenerPlugin singletonCringe() { return badDesignPatterns; }
}
```

Listener:

```java
/**
 * @author RuthlessJailer
 */
public final class SpecialPacketListener implements PacketListener<SpecialPacket> {

	// Long, boring way of doing it -_-

	@Override
	public @NotNull
	Class<SpecialPacket> getType() {
		return SpecialPacket.class;
	}

	@Override
	public @Nullable
	SpecialPacket mutate(final Player player, final SpecialPacket packet) {
		if (player.getName() == "RuthlessJailer") { // very cool
			return packet;
		} else { // pleb
			return null;
		}
	}

	// Lazy way to do it 😎
	// (this can be literally anywhere so long as the plugin has loaded)

	static { // ✨ static blocks ✨
		ListenerManager listenerManager = ListenerPlugin.singletonCringe().listenerManager;

		// ~~convenience~~ chad register method
		listenerManager.register(SpecialPacket.class, (player, packet) -> {

			// returning `packet` will passively monitor all incoming or outgoing SpecialPackets
			// returning `null` will "cancel" the "event", preventing it from going to the server or client as this listener takes priority over the server's packet handler

			return packet;
		});
	}

}
```