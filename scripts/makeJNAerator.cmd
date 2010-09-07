MAVEN_OPTS=-Xmx500m
#-Dmaven.compiler.debug=false
MVN_NOTEST="mvn -Djarsigner.skip=false -Dstorepass=$KEYSTORE_PASS -Dmaven.test.skip=true" 
$MVN_NOTEST clean install
$MVN_NOTEST deploy

#mvn -f jnaerator-runtime/pom.xml clean deploy install && mvn "-Dstorepass=$KEYSTORE_PASS" -f jnaerator/pom.xml clean deploy site-deploy
