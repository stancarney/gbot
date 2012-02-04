def init(manager, session) {
	isEnabled = false
}

def shutdown() {
}

def handleChannelMessage(event) {
	if (isEnabled) {
		event.channel.say(event.message)
	}
}
