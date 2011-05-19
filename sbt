java -Xmx512M -Xss2M -XX:+CMSClassUnloadingEnabled \
  -XX:+CMSPermGenSweepingEnabled \
  -XX:PermSize=64M -XX:MaxPermSize=256M \
  -jar `dirname $0`/sbt-launcher.jar "$@"
