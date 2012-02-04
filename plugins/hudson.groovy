def init(manager, session) {
	isEnabled = true

	eventTimer = new Timer()
	this.manager = manager
	this.session = session
	
	if (isEnabled) {
		scheduled = {
			try {
				checkHudson()
			} catch (Exception e) {
				e.printStackTrace()
			}
			eventTimer.runAfter(manager.config.gbot.plugins.hudson.interval, scheduled)
		}

		timerTask = eventTimer.runAfter(manager.config.gbot.plugins.hudson.interval, scheduled)
	}
}

def shutdown() {
	eventTimer.cancel()
}

def checkHudson() {
	manager.config.gbot.servers.each { server, channels ->
		channels.each { channel, channelConfig ->
			if (channelConfig.containsKey("hudson")) {
				println "${new Date()}: polling hudson for channel $channel"

				def rss = (new XmlSlurper()).parseText(new URL(channelConfig.hudson.url).text)
				rss.entry.collect{it}.reverse().each { entry ->
					if (entry.updated[0].text() > channelConfig.hudson.lastUpdate) {
						session.getChannel("#$channel")?.say("Hudson build: ${entry.title} ${entry.link.'@href'}")
						channelConfig.hudson.lastUpdate = entry.updated[0].text()
						if (buildFailed(entry.title.text())) {
							session.getChannel("#$channel")?.say("${getCulprits(entry.title, channelConfig.hudson.url).join(' or ')} broke the build")
						}
					}
				}
			}
		}
	}
}

def buildFailed(title) {
	title.contains("FAILURE")
}

def getCulprits(title, rssUrl) {
	def buildNo = (title =~ /\S+ #(\d+) \(FAILURE\)/)[0][1]
	def apiUrl = rssUrl.replaceAll("rssAll", "$buildNo/api/xml")
	def buildInfo = (new XmlSlurper()).parseText(new URL(apiUrl).text)
	buildInfo.culprit.collect{ it.fullName.text() }
}
