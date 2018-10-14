# Kuoushi-Bot
This is a hard fork of the original BreadBot by Birdgeek. Features are being removed, re-developed, and also added anew as I think of them. This bot will most likely not be for everyone as the features are mostly going to be for what I want to do on the bot itself, but please feel free to fork and make this bot your own if you need. I have included a JSON config file if you want to use the bot as is, and I will be releasing executable JAR files for various stages of development if it has the features you want and you still want to use it. 

One quick note, I am not a professional developer. I'm making this to support my own stream stuff and that's about it. If other people use it, then that's pretty neat.

## Features
Relay chat messages from Discord to Twitch/Hitbox/Half-Life Goldsrc Servers (Source coming soon)

Announce when streams go live - 1) in the relay channel, 2) in a special announcements only channel

Basic configuration through manually editable JSON file

Add or enable any number of streams and services for the bot to watch and announce/relay

- (Hitbox support hasn't been tested lately)

## Roadmap
Refactoring messaging backend to be more generic to allow for better support of future features

Reimplement command backend

Poll/vote system

Allow the server to change server variables on HLDS/Source servers in response to chat commands

Support for chat realy FROM Goldsrc and Source servers to Discord

More configurable relay directions (such as choosing what service is hosting the base channel (Twitch/Hitbox/Discord/IRC/etc), instead of just assuming Discord is the base channel

Full reimplementation of HSLStatsX:CE proxy in the bot (this is a distant, distant feature)

## Installation

###### Current Version: 0.2.1

There's currently no JAR file available, but you should be able to just load this into Eclipse and compile it there. First alpha will be when it feels kind of stable with more in-server commands working and available for configuration.

## Usage:

Create a config.json file based on the template with your desired settings then run:

`java -jar kuobot.jar`

## Contributing

If you think you can parse through my spaghetti code enough to contribute a feature, then by all means do the following.

 1. Fork it!
 2. Create your feature branch: `git checkout -b my-new-feature`
 3. Commit your changes: `git commit -am 'Add some feature'`
 4. Push to the branch: `git push origin my-new-feature`
 5. Submit a pull request

If you just want to suggest a feature for the bot to support - go to the issues page and add a feature request!

I will warn you though that I won't be implementing any specific features from the request page on any kind of timely schedule. The more I like the feature, the more likely/quickly it'll be implemented. That's it.

## Credits

I'm using a few other libraries to do a lot of the heavy lifting.

### Libraries
- [JDA](https://github.com/DV8FromTheWorld/JDA)
- [PircBotX](https://github.com/TheLQ/pircbotx)
- [Steam Condenser](https://github.com/koraktor/steam-condenser-java)



## License

MIT License

Copyright (c) 2016 Brad Snurka

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.