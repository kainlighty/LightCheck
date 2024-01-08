# LightCheck

![HEADER](https://github.com/kainlighty/LightCheck/assets/111251772/0c09dea4-88c0-45e7-99ae-db4b472b6f33)

### › [Download for 1.16](https://github.com/kainlighty/LightCheck/releases/download/2.0.7/LightCheck-2.0.7.jar)

## › Features

- #### Russian and English language support
- #### Checking with or without timer
- #### Titles, bossbar and chat messages

## › Abilities:

- #### Teleporting to staff during check
- #### Teleportation of the player to the previous location
- #### Prohibit movement
- #### Prohibit throwing things away
- #### Prohibit dealing and receiving damage
- #### Prohibit breaking blocks
- #### Prohibit placing blocks
- #### Prohibit writing to the chat (except for a personal chat during the check with the inspector)
- #### Prohibition of all commands except those allowed specified in the config
- #### The very recognition of the use of cheats
- #### Execution of commands at self-recognition, exit and kick
- #### Chat messages from the person being checked come only to inspector

## › Screenshots

![7d23b3c23ecc6bcfa777fd16dcd2ee46077f8640](https://github.com/kainlighty/LightCheck/assets/111251772/cce24929-3756-4af9-81e4-bfe02065bc60)
![7d775ed462693e815bc4655e8a43e555a2df591f](https://github.com/kainlighty/LightCheck/assets/111251772/09fb152a-2c6c-4039-9825-7b6052e40863)

| Command          | Description                            | Permission            |
|------------------|----------------------------------------|-----------------------|
| check            | Help by commands                       | lightcheck.check      |
| check list       | The list of currently checking         | lightcheck.list       |
| check \<player>  | Summon a player to check               | lightcheck.check      |
| check confirm    | Confirm the use of cheats (for player) | -                     |
| check approve    | To find the player guilty              | lightcheck.approve    |
| check disprove   | To find the player innocent            | lightcheck.disprove   |
| check timer stop | Stop the timer to the player           | lightcheck.timer.stop |
| check stop-all   | Cancel all current checks              | lightcheck.admin      |
| check reload     | Reload configurations                  | *ONLY CONSOLE*        |
| check reconfig   | Update configurations                  | *ONLY CONSOLE*        |

| Permissions without commands | Description                                    |
|------------------------------|------------------------------------------------|
| lightcheck.bypass            | Prohibit checking players with this permission |
| lightcheck.admin             | Full access to the plugin                      |

## › [API](https://github.com/kainlighty/LightCheck/tree/master/src/main/java/ru/kainlight/lightcheck/API)
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.kainlighty</groupId>
    <artifactId>LightCheck</artifactId>
    <version>2.1.0</version>
    <scope>provided</scope>
</dependency>
```

## - Events:

- #### PlayerCheckEvent
- #### PlayerApproveEvent
- #### PlayerDisproveEvent

## - Methods

- #### LightCheckAPI.getCheckedPlayers();
- #### LightCheckAPI.getCheckedPlayer();
- #### LightCheckAPI.call();
- #### LightCheckAPI.approve();
- #### LightCheckAPI.disprove();
- #### LightCheckAPI.teleportToInspector();
- #### LightCheckAPI.teleportBack(); // *TO PREVIOUS LOCATION*
- #### LightCheckAPI.getTimer();
- #### LightCheckAPI.hasTimer();
- #### LightCheckAPI.setTimer();
- #### LightCheckAPI.removeTimer();
- #### LightCheckAPI.getInspector();
