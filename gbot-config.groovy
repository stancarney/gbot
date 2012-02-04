gbot {
  nick = "gBot"

  servers {
    "192.168.1.1" {

      VW {
        log = "project1.log"
        redmine {
          server = 'https://redmine'
          username = 'ircuser'
          password = 'password'

          forumUrl = "${server}/boards/5/topics/new"

          repoUrl = "${server}/projects/project1/repository"
          url = "${server}/projects/project1/activity.atom?key=JshgagKHyrj8275hGksfgjadfhh825GKhsghJ82h"
          lastUpdate = '2010-11-17T15:00:00-00:00'
        }
        hudson {
          url = 'http://redmine:8081/hudson/job/project1/rssAll'
          lastUpdate = '2010-11-17T15:00:00-00:00'
        }
      }

      JKPAY {
        log = "project2.log"
        redmine {
          server = 'https://redmine'
          username = 'ircuser'
          password = 'password'

          forumUrl = "${server}/boards/6/topics/new"

          repoUrl = "${server}/projects/project2/repository"
          url = "${server}/projects/project2/activity.atom?key=JshgagKHyrj8275hGksfgjadfhh825GKhsghJ82h"
          repoUrl = "${server}/projects/project2/repository"
          lastUpdate = '2010-11-17T15:00:00-00:00'
        }
        hudson {
          url = 'http://redmine:8081/hudson/job/project2/rssAll'
          lastUpdate = '2010-11-17T15:00:00-00:00'
        }
      }
    }
  }

  plugins {
    hudson {
      interval = 30000
    }
    redmine {
      interval = 30000
    }
  }
}
