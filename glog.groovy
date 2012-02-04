@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.0')
def grabWorkaround() {} //Grab need to annotate something. Since this is a script there is nothing to annotate.

println args
def config = new ConfigSlurper().parse(new File(args[0]).toURL())
SSLSubverter.subvert()

config.gbot.servers.each {server, channels ->

	channels.each {name, options ->

		println "-------------------"

		println "Processing: ${name}."
		println "Server: ${options.redmine.server}"
		println "Username: ${options.redmine.username}"
		println "Password: ${options.redmine.password}"
		println "Form URL: ${options.redmine.forumUrl}"

		//Login
		println "Logging in."
		def http = new groovyx.net.http.HTTPBuilder(options.redmine.server)
		def token = getAuthToken(http, '/login')
		post(http, '/login', [authenticity_token: token, username: options.redmine.username, password: options.redmine.password, login: '"Login &#187;"'])

		//Post log
		try {
			println "Opening log."
			File file = new File(options.log)
			Date date = new Date()

			println "Posting log file."
			token = getAuthToken(http, options.redmine.forumUrl)
			post(http, options.redmine.forumUrl, [authenticity_token: token, 'message[subject]': "IRC Log for ${date}", 'message[content]': "<pre>${file.text}</pre>", commit: 'Create'])

			file.renameTo(new File("${options.log}-${date.format("yyyy-MM-dd'T'HH:mm:ssz")}"))
		} catch (e) {
			println "Could not post log because of the following error.\n${e}"
		}
	}
}

def getAuthToken(http, path) {
	def token
	http.get(path: path, contentType: groovyx.net.http.ContentType.TEXT) { resp, reader ->
		def page = new XmlSlurper(new org.cyberneko.html.parsers.SAXParser()).parseText(reader.text)
		token = page.depthFirst().find { it['@name'] == 'authenticity_token'}.@value
	}
	return token
}

def post(http, path, body) {
	http.post(path: path, body: body, requestContentType: groovyx.net.http.ContentType.URLENC) { resp, reader ->
		println "Output: ${reader}"
		println "${Location: resp.getFirstHeader('Location')}"
	}
}
