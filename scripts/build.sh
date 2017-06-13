#!/bin/bash

set -e

clone_repo () {
  if [ -z "$1" ]                           # REPO URL is mandatory
  then
    echo "-REPO URL is mandatory.-"
    return 1
  else
    echo "-REPO URL = \"$1\".-"
  fi

  set -x
  if [ $(git ls-remote $1 | grep "refs/heads/$TRAVIS_BRANCH" | wc -l) = 1 ] ; then
    git clone --branch $TRAVIS_BRANCH --depth=1 $1
  else
    git clone --branch master --depth=1 $1
  fi
  set +x

  return 0
}

EXIT_STATUS=0

DOCS_REPO="https://github.com/frees-io/freestyle-docs.git"

VERSION="$(grep -F -m 1 'version in ThisBuild :=' version.sbt)"; VERSION="${VERSION#*\"}"; VERSION="${VERSION%\"*}"

SCALA_JS_SCRIPT="sbt ++$TRAVIS_SCALA_VERSION test:fastOptJS validateJS"
SCALA_JVM_SCRIPT="sbt ++$TRAVIS_SCALA_VERSION orgScriptCI"

PUBLISH_PROJECT="sbt ++$TRAVIS_SCALA_VERSION publishLocal"
CLONE_DOCS_REPO="clone_repo $DOCS_REPO"
DOCS_SCRIPT="cd freestyle-docs && sbt ++$TRAVIS_SCALA_VERSION -Dfrees.version=$VERSION 'tut' && cd .."

if [ "$SCALAENV" = "jvm" ]; then
  eval $SCALA_JVM_SCRIPT || EXIT_STATUS=$?
fi

if [ "$SCALAENV" = "js" ]; then
  eval $SCALA_JS_SCRIPT || EXIT_STATUS=$?
fi

if [ "$FREESBUILD" = "docs" ]; then
  eval $PUBLISH_PROJECT || EXIT_STATUS=$?
  eval $CLONE_DOCS_REPO || EXIT_STATUS=$?
  eval $DOCS_SCRIPT || EXIT_STATUS=$?
fi

exit $EXIT_STATUS
