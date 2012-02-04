class PluginManager {
	def config
	def session
	def plugins = [:]
	def pluginDir = new File("plugins")
	def pluginDirLastMod = 0

	def dispatch(event) {
		def lastMod = pluginDir.lastModified()
		if (lastMod > pluginDirLastMod) {
			println "checking plugins"
			pluginDir.eachFileMatch(~/.*\.groovy/) { file ->
				if (!plugins.containsKey(file.name)) {
					plugins[file.name] = new Plugin()
				} else {
					try {
						println "shutting down ${file.name}"
						plugins[file.name].script?.invokeMethod('shutdown', null)
					} catch (Exception e) {
						e.printStackTrace()
					}
				}
				plugins[file.name].reload(file, this, session)
			}
			pluginDirLastMod = lastMod
		}

		def eventName = event.type.toString().split("_").collect{it.toLowerCase().capitalize()}.join()
		plugins.each { name, plugin ->
			try {
				plugin.script.invokeMethod("handle$eventName", event)
			} catch (MissingMethodException e) { 
			}
		}
	}

	static boolean isGbotMessage(event){
        	event.message ==~ /gBot\:.*/
	}
}

class Plugin {
	def groovyShell = new GroovyShell()

	Script script
	long lastScriptLoad

	def reload(file, manager, session) {
		def lastMod = file.lastModified()
		if (!script || lastMod > lastScriptLoad) {
			println "reloading $file"
			script = groovyShell.parse(file)
			lastScriptLoad = lastMod
		}
		try {
			script.invokeMethod('init', [manager, session])
		} catch (Exception e) {
			e.printStackTrace()
		}
	}
}

