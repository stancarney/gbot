gBot
===============

gBot is a simple IRC bot written in Groovy utilizing Jerklib for IRC connectivity.

* `gbot-config.groovy` Configures servers and channels to connect to as well as other information plugins may require.
* `gbot.grovy` Main executable.
* `gbot.sh` Simple wrapper script to start gbot.
* `blog.groovy` Working example of how to upload logs to a Redmine forum.


Plugin Examples
------------------------------------------------

The following plugins serve as an example on how to create your own plugins suited to your development environment. Every .groovy file in the plugins directory is created as a plugin. `log.groovy` contains a listing of possible events.

* `plugins/autoop.groovy` Plugin that will automatically op anyone joining the channel.
* `plugins/echo.groovy` Echos everything said in the channel. Really just used for testing.
* `plugins/hudson.groovy` Posts channel updates based on Hudson/Jenkins build breaks.
* `plugins/issue.groovy` Searches messages in the channel looking for matching Redmine ticket numbers and posts a link to the ticket.
* `plugins/log.groovy` Logs everything said in the channel. This file is uploaded to Redmine by glog.groovy in our case.
* `plugins/opme.groovy` Ops people when they ask for it. No need if autoop.groovy is enabled.
* `plugins/redmine.groovy` Checks a running Redmin instance for updates..


