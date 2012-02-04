def init(manager, session) {
	isEnabled = true
}

def shutdown() {
}

def handleChannelMessage(event) {
	if (isEnabled && PluginManager.isGbotMessage(event) && event.message ==~ /.*opme/) {
		event.channel.say("Oping because he asked me to. ${event.nick}.")
		event.channel.op(event.nick)
	}
}

