# LightCheck

![0a950f2c10703f6504c9f98f8ac5f90edd7a97be]([https://github.com/kainlighty/LightCheck/assets/111251772/b82a267f-16d3-43e0-a5ac-f3d26cde453d](https://proxy.spigotmc.org/0a950f2c10703f6504c9f98f8ac5f90edd7a97be?url=https%3A%2F%2Fi.imgur.com%2Fls7JJg3.png))

## › Features
- #### Russian and English language support
- #### Checking with or without timer
- #### Titles and chat messages
- #### Delay on the check command (from permission)

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
- #### Execution of commands at self-recognition, exit and shutdown of the timer
- #### Chat messages from the person being checked come only to the person checking

## › Screenshots
![7d23b3c23ecc6bcfa777fd16dcd2ee46077f8640]([https://github.com/kainlighty/LightCheck/assets/111251772/c364df7a-8cf3-4d84-9b8b-bc1cd7426b8d](https://proxy.spigotmc.org/7d23b3c23ecc6bcfa777fd16dcd2ee46077f8640?url=https%3A%2F%2Fi.imgur.com%2Ffq0qD3n.png))
![7d775ed462693e815bc4655e8a43e555a2df591f]([https://github.com/kainlighty/LightCheck/assets/111251772/62349eb2-0206-43ff-8067-5d43539aa608](https://proxy.spigotmc.org/7d775ed462693e815bc4655e8a43e555a2df591f?url=https%3A%2F%2Fi.imgur.com%2F24RzQeJ.png))

| Command         | Description                            | Permission          |
|-----------------|----------------------------------------|---------------------|
| check           | Help by commands                       | lightcheck.check    |
| check list      | The list of currently checking         | lightcheck.list     |
| check \<player> | Summon a player to check               | lightcheck.check    |
| check confirm   | Confirm the use of cheats (for player) | -                   |
| check approve   | To find the player guilty              | lightcheck.approve  |
| check disprove  | To find the player innocent            | lightcheck.disprove |
| check rt        | Stop the timer to the player           | lightcheck.rt       |
| check stop-all  | Cancel all current checks              | lightcheck.admin    |
| check reload    | Reload configurations                  | *ONLY CONSOLE*      |
| check reconfig  | Update configurations                  | *ONLY CONSOLE*      |


| Permissions without commands | Description                                    |
|------------------------------|------------------------------------------------|
| lightcheck.bypass            | Prohibit checking players with this permission |
| lightcheck.admin             | Full access to the plugin                      |

## › [API](https://github.com/kainlighty/LightVanish/tree/main/src/main/java/ru/kainlight/lightcheck/API)

- #### _PlayerCheckEvent_
- #### _PlayerApproveEvent_
- #### _PlayerDisproveEvent_

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
