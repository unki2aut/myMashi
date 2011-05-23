#!/bin/bash
java -Xmx512M -Xss2M -XX:+CMSClassUnloadingEnabled \
  -XX:PermSize=128M -XX:MaxPermSize=256M \
  -jar `dirname $0`/sbt-launcher.jar "$@"
