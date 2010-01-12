rsync -avP build/dist/anarres-cpp-*.tar.gz shevek@pink.anarres.org:public_html/projects/jcpp/
rsync -avP --exclude=.svn --exclude=autohandler build/javadoc shevek@pink.anarres.org:public_html/projects/jcpp/
cp build/tar/lib/anarres-cpp.jar /home/shevek/java/iengine/trunk/lib/runtime/jcpp/
cp build/tar/lib/anarres-cpp.jar /home/shevek/java/karma/trunk/lib/dp/
cp build/tar/lib/anarres-cpp.jar /home/shevek/java/dp/trunk/lib/runtime/cpp/
