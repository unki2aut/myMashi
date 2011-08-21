# myMashi - content filtering mashup

myMashi was started as a project for university and its idea was to mash up
content from several sources, weigh its importants and show the content on the
mainpage ordererd by date and weight. I started to only collect data from
RSS feeds but it is ment to get data from almost any source in the web.
myMashi should also be user-based, so every user can have his/her own little
mashi.
The weighting is done in two steps:
1. the user sets some keywords for a source of things he would be insterested in
2. the user uses a like-button to train a bayesian-classifier


## TODOs

* introduce users (use lift user model)
* include the bayesian-classifier from the tests and choose a fitting model
  for the keyword storage, M(dislikes_user) = M(all) / M(likes_user)
* use proguard for minification


## Stuff

* create mymashi.war:
  `sbt package` (Project extends DefaultWebProject)


* run myMashi local:

1. `cd mymashi`
2. `sbt update ~jetty-run`
3. surf for `localhost:8080` in your browser
