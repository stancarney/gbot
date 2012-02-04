def init(manager, session) {
	isEnabled = true
}

def shutdown() {
}

def handleChannelMessage(event) {
	if (isEnabled) {
		def result = event.message =~ /#\d{3,}+/
		if(result) {
			result.each { it ->
				//The API Key: 7e93ee1d8bf3f0723c0e3891ddb0bb184b2df933 belongs to stan.
				def xml = (new XmlSlurper()).parseText(new URL("https://redmine/issues/${it.replace('#', '')}.xml?key=3c04b89b18ee1d82df93371ddb0be93e3bf3f072").text)
				event.channel.say("${xml?.project?.'@name'} - ${xml?.tracker?.'@name'} #${xml?.id}: ${xml?.subject}: https://redmine/issues/${xml?.id}")
			}
		}
	}
}

