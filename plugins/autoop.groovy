def init(manager, session) {
	isEnabled = true
}

def shutdown() {
}

def handleJoin(event) {
	if (isEnabled) {
		event.channel.op(event.nick)
	}
}
