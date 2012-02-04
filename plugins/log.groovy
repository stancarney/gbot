def init(manager, session) {
	isEnabled = true

	this.manager = manager
}

def shutdown() {
}

def logFilenameForChannel(channelToLog) {
	def logFile = "global.log"
	manager.config.gbot.servers.each { server, channels ->
		channels.each { channel, channelConfig ->
			if ("#$channel" == channelToLog) {
				logFile = channelConfig.log
			}
		}
	}
	logFile
}

def getDateString() {
	"[${(new Date()).format('HH:mm')}]"
}

def withLogFileForChannel(channelName, closure) {
	def logFile = new File(logFilenameForChannel(channelName))
	try {
		closure.call(logFile)
	} finally {
		logFile.close()
	}
}

def handleChannelMessage(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString} <${event.nick}> ${event.message}\n")
		}
	}
}

def handleCtcpEvent(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString}  * ${event.ctcpString.replaceAll('ACTION', event.nick)}\n")
		}
	}
}

def handleJoin(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString} ${event.nick} has joined channel ${event.channelName}\n")
		}
	}
}

def handlePart(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString} ${event.who} has left channel ${event.channelName}\n")
		}
	}
}

def handleQuitEvent(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString} ${event.nick} has quit: ${event.quitMessage}\n")
		}
	}
}

def handleKickEvent(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString} ${event.who} has been kicked off ${event.channelName}: Kicked by ${event.byWho()}\n")
		}
	}
}

def handleModeEvent(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString} ${event.setBy()} has changed mode: ${event.rawEventData}\n")
		}
	}
}

def handleTopic(event) {
	if (isEnabled) {
		withLogFileForChannel(event.channel?.name) { logFile ->
			logFile.append("${dateString} ${event.setBy} has set the topic for ${event.channel.name}: \"${event.topic}\"\n")
		}
	}
}
