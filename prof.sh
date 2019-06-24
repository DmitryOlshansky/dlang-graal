java -Doutdir=tests/graal -jar target/lexer-jar-with-dependencies.jar `cat std.txt` &
sleep 1
$HOME/bin/profiler.sh -e cpu -f flame.svg -d 30 $!
