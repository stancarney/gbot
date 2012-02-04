def init(manager, session) {
	isEnabled = true

	eventTimer = new Timer()
	this.manager = manager
	this.session = session
	
	if (isEnabled) {
		scheduled = {
                        try {
				checkRedmine()
                        } catch (Exception e) {
				e.printStackTrace()
			}
			eventTimer.runAfter(manager.config.gbot.plugins.redmine.interval, scheduled)
		}

		timerTask = eventTimer.runAfter(manager.config.gbot.plugins.redmine.interval, scheduled)
	}
}

def shutdown() {
	eventTimer.cancel()
}

def checkRedmine() {
	manager.config.gbot.servers.each { server, channels ->
		channels.each { channel, channelConfig ->
			if (channelConfig.containsKey("redmine")) {
				println "${new Date()}: polling redmine for channel $channel"

				// force repo update so revisions appear in activity feed
				def p = "wget --no-check-certificate -qO- --load-cookies /data/gbot/cookies.txt ${channelConfig.redmine.repoUrl}".execute()
				p.waitFor()
				p.destroy()
				//def repo = new URL(channelConfig.redmine.repoUrl).text 

				def atom = (new XmlSlurper()).parseText(new URL(channelConfig.redmine.url).text)
				atom.entry.collect{it}.reverse().each { entry ->
					if (entry.updated[0].text() > channelConfig.redmine.lastUpdate) {
						session.getChannel("#$channel")?.say("${entry.title} (${entry.author.name}): ${entry.link.'@href'}")
						channelConfig.redmine.lastUpdate = entry.updated[0].text()
					}
				}
			}
		}
	}
}
