![image](https://github.com/user-attachments/assets/936e03c4-d2f5-4acc-b2d7-b59dc6f22fd4)

> **JAVA 17 REQUIRED**

---

### › Features

- Russian and English language support
- Checking with or without timer
- Titles, bossbar and chat messages

### › Abilities:

- Teleporting to staff during check
- Teleportation of the player to the previous location
- Prohibit movement
- Prohibit throwing things away
- Prohibit dealing and receiving damage
- Prohibit breaking blocks
- Prohibit placing blocks
- Prohibit writing to the chat (except for a personal chat during the check with the inspector)
- Prohibition of all commands except those allowed specified in the config
- The very recognition of the use of cheats
- Execution of commands at self-recognition, exit and kick
- Chat messages from the person being checked come only to inspector

### › Screenshots

![7d23b3c23ecc6bcfa777fd16dcd2ee46077f8640](https://github.com/kainlighty/LightCheck/assets/111251772/cce24929-3756-4af9-81e4-bfe02065bc60)
![7d775ed462693e815bc4655e8a43e555a2df591f](https://github.com/kainlighty/LightCheck/assets/111251772/09fb152a-2c6c-4039-9825-7b6052e40863)

### › Commands and Permissions

| Command              | Description                            | Permission                |
|----------------------|----------------------------------------|---------------------------|
| check                | Help by commands                       | lightcheck.check          |
| check list           | The list of currently checking         | lightcheck.list           |
| check \<player>      | Summon a player to check               | lightcheck.check          |
| check confirm        | Confirm the use of cheats (for player) | lightcheck.confirm        |
| check approve        | To find the player guilty              | lightcheck.approve        |
| check disprove       | To find the player innocent            | lightcheck.disprove       |
| check timer continue | Continue the timer to the player       | lightcheck.timer.continue |
| check timer stop     | Stop the timer to the player           | lightcheck.timer.stop     |
| check stop-all       | Cancel all current checks              | lightcheck.stop-all       |
| check reload         | Reload configurations                  | lightcheck.reload         |

| Permissions without commands |
|------------------------------|
| lightcheck.timer.*           |
| lightcheck.bypass            |
| lightcheck.admin             |

### › [API](https://github.com/kainlighty/LightCheck/tree/master/API/src/main/java/ru/kainlight/lightcheck/API)

#### Maven
```
<dependency>
  <groupId>ru.kainlight.lightcheck</groupId>
  <artifactId>api</artifactId>
  <version>2.2.5</version>
  <scope>provided</scope>
</dependency>

> $ mvn install
```

#### Gradle — Groovy DSL:
```groovy
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = "https://maven.pkg.github.com/kainlighty/LightCheck"
    }
}

dependencies {
    compileOnly 'ru.kainlight.lightcheck:api:2.2.5'
}
```
#### Gradle — Kotlin DSL:
```kotlin
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/kainlighty/LightCheck")
    }
}

dependencies {
    compileOnly("ru.kainlight.lightcheck:api:2.2.5")
}
```

### Events

| Name                | Description                                                                           |
|---------------------|---------------------------------------------------------------------------------------|
| PlayerCheckEvent    | You can cancel a player challenge for checking <br> or do something at that moment.   |
| PlayerApproveEvent  | It is called when the player is confirmed for checking                                |
| PlayerDisproveEvent | It is called when a player is disproved on checking                                   |

#### Methods

> Get provider: `LightCheckAPI.getProvider()`

| API                                    | Description                                             |
|----------------------------------------|---------------------------------------------------------|
| getCheckedPlayers()                    | Get players who are being checked                       |
| getCheckedPlayer(player)               | Get a player who is being checked                       |
| getCheckedPlayerByInspector(inspector) | Get the player by the inspector who is being checked    |
| isChecking(player)                     | Check if the player is being checking                   |
| isCheckingByInspector(inspector)       | Check if the player is being checked by the inspector   |
| getInspectorByPlayer(player)           | Get `InspectorPlayer` for the player being checked      |
| getInspectors()                        | Get a list of all inspectors                            |
| getCachedCheckLocations()              | Get all locations for checks                            |
| getOccupiedLocations()                 | Get all occupied locations for checks                   |
| call(player, inspector)                | Start a player check                                    |
| stopAll()                              | Stop all checks                                         |
| addLog(username, text)                 | Add your own event to the log for the specified player  |

| CheckedPlayer                | Description                                                         |
|------------------------------|---------------------------------------------------------------------|
| getPlayer()                  | Get a bukkit player                                                 |
| getInspector()               | Get an `InspectorPlayer` from the player                            |
| getPreviousLocation()        | Get the previous location _(from where he was teleported)_          |
| approve()                    | Approve the player punishment                                       |
| disprove()                   | Disprove the player (do not punish)                                 |
| teleportToInspector()        | Teleport the player to the inspector                                |
| teleportToCheckLocation()    | Teleport to an unoccupied location for checks                       |
| teleportToPreviousLocation() | Teleport to the previous location                                   |
| startTimer()                 | Start the countdown to punishment                                   |
| setTimer(value)              | Set or change value for the timer                                   |
| getTimer()                   | Get the current timer value from the player                         |
| hasTimer()                   | Check if the player has a timer _(if stopped, it is also **true**)_ |
| stopTimer()                  | Stop the timer                                                      |

| InspectorPlayer              | Description                                                |
|------------------------------|------------------------------------------------------------|
| getPlayer()                  | Get a bukkit player                                        |
| getCheckedPlayer()           | Get an `CheckedPlayer` from the inspector                  |
| getPreviousLocation()        | Get the previous location _(from where he was teleported)_ |
| teleportToPreviousLocation() | Teleport to the previous location                          |