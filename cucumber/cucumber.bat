javac -cp "jars/*;." implementation/Thief.java step_definitions/SackTest.java
java -cp "jars/*;." cucumber.api.cli.Main -p pretty --snippets camelcase -g step_definitions features