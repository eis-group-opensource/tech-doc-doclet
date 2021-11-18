Instructions for anual release of the artifact

Pseudo instructions:
* mvn release:prepare -DdryRun
* mv pom.xml.tag pom.xml
* hg ci -m"release 1.2.3 version"
* hg tag  -m"release 1.2.3 version"
* mvn clean source:jar package deploy:deploy
* mv pom.xml.next pom.xml
* hg ci -m"preparing for next development version version"
* hg push -b .

