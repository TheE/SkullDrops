SkullDrops
==========

SkullDrops is an extremely simple plugin for bukkit that allows players to receive skulls as drops from dying entities.

Usage
---------
In the *config.yml*, you can define the drop chance by monster in percent: 100 means the skull will always, 0 it will never drop. If you set `playerKilledOnly` to `true` the skull is only dropped if the entity was killed by a player.

Additionally you can use `/skull <name>` to receive the skull of the given player. This only works for players with the corresponding `skullDrop.skull` permission.

Compiling
---------

This project is written for Java 6 and the build process makes use of Maven, to handle dependencies automatically.
For compiling you will need to:

1. Install [the Java 6 JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. Install [Apache Maven](http://maven.apache.org).
3. Download the source code for SkullDrops from this repository, using either Git or the download button.
4. Navigate to the directory where the source code is and type `mvn clean install` in command prompt or terminal. 

Contributing
---------
We accept contributions, especially through pull requests on GitHub. Submissions must be licensed under the GNU General Public License v3.