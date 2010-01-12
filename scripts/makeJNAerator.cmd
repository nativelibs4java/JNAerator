MAVEN_OPTS=-Xmx500m

mvn -Djarsigner.skip=false -Dstorepass=$KEYSTORE_PASS deploy

#mvn -f jnaerator-runtime/pom.xml clean deploy install && mvn "-Dstorepass=$KEYSTORE_PASS" -f jnaerator/pom.xml clean deploy site-deploy
