![0a950f2c10703f6504c9f98f8ac5f90edd7a97be](https://github.com/kainlighty/LightCheck/assets/111251772/b82a267f-16d3-43e0-a5ac-f3d26cde453d)

# › Features
- Russian and English language support
- Checking with or without timer
- Titles and chat messages
- Delay on the check command (from permission)
### Abilities:
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
- Execution of commands at self-recognition, exit and shutdown of the timer
- Chat messages from the person being checked come only to the person checking

# › Screenshots
![7d23b3c23ecc6bcfa777fd16dcd2ee46077f8640](https://github.com/kainlighty/LightCheck/assets/111251772/c364df7a-8cf3-4d84-9b8b-bc1cd7426b8d) 
![7d775ed462693e815bc4655e8a43e555a2df591f](https://github.com/kainlighty/LightCheck/assets/111251772/62349eb2-0206-43ff-8067-5d43539aa608)

| Permissions without commands | Description
| --- | --- |
| lightcheck.confirm | Confirm without '.other' for the players so that they can confirm their guilt
| lightcheck.bypass | Prohibit checking players with this permission
| lightcheck.cooldown.value | Set the delay value for /check <player>
| lightcheck.notify | For update alerts when logging in
| lightcheck.admin | Full access to the plugin

| Command | Description | Permission |
| --- | --- | --- |
| check | Help by commands | lightcheck.check
| check list | The list of currently checking | lightcheck.list
| check notify | Enable or disable update notification | lightcheck.notify
| check <player> | Summon a player to check | lightcheck.check
| check confirm | Confirm the use of cheats | lightcheck.confirm.other
| check disprove | To find the player innocent | lightcheck.disprove
| check rt | Stop the timer to the player | lightcheck.rt
| check reset | Cancel all current checks | lightcheck.admin
| check reload | Reload configs | lightcheck.reload
