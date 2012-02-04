#!/usr/bin/env groovy -cp lib/jerklib.jar

import jerklib.ConnectionManager
import jerklib.Profile
import jerklib.Session
import jerklib.events.*
import jerklib.events.IRCEvent.Type
import jerklib.listeners.IRCEventListener

println args
def config = new ConfigSlurper().parse(new File(args[0]).toURL())
ConnectionManager manager = new ConnectionManager(new Profile(config.gbot.nick))

SSLSubverter.subvert()

config.gbot.servers.each {server, channels ->

  println server
  println channels

  Session session = manager.requestConnection(server)

  def pluginManager = new PluginManager(config: config, session: session)

  channels.each {name, options ->
    session.addIRCEventListener(new Gbot(nick: config.gbot.nick, name: "#${name}", pluginManager: pluginManager));
  }
}

public class Gbot implements IRCEventListener {

  Long counter = 0

  def channel
  def nick
  def name
  PluginManager pluginManager

  public void receiveEvent(IRCEvent e) {

    Session s = e.session
    channel = s.getChannel(name)

    def eventChannel
    try {
      eventChannel = e.channel
    } catch (MissingPropertyException exception) {
      eventChannel = null
    }

    // skip message if channel specific and not for my channel 
    if (eventChannel && channel != eventChannel) {
      return
    }

    switch (e.type) {
      case Type.CONNECT_COMPLETE:
      case Type.CONNECTION_LOST:
      case Type.KICK_EVENT:
        s.join(name)
        break

      case Type.CHANNEL_MESSAGE:
        MessageEvent event = (MessageEvent) e
        counter++
        if (event.message ==~ /$nick.*/) {
          if (event.message ==~ /.*reload/) {
            channel.say('reloading config')
            pluginManager.config = new ConfigSlurper().parse(pluginManager.config.configFile)
          }
          else if (event.message ==~ /.*writeconfig*/) {
            channel.say('writing config')
            pluginManager.config.writeTo(new File('gbot-config-saved.groovy').newWriter())
          }
        }
        println "${counter} ${new Date()}: ${e.message}"
        break

      case Type.JOIN_COMPLETE:
        channel.say("Hola ${channel}")
        break

      default:
        //Unfortunately these are here because Jerklib decided not to implement them as events.
        if (e.getRawEventData() ==~ /.*terminated!/) s.join(name)

        println "${e.getType()} ${e.getRawEventData()}"
    }

    pluginManager.dispatch(e)
  }
}
