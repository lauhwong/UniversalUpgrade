UpgradeSupport
============


How to use lib
--------------

```groovy
dependencies {
  compile 'com.github.lauhwong:uupgrade:1.0'
  annotationProcessor 'com.github.lauhwong:uupgrade-compiler:1.0'
}
```
or use snap-shot in maven
```groovy
repositories {
        maven {
           url 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
    }

dependencies {
      compile 'com.github.lauhwong:uupgrade:1.0-SNAPSHOT'
        annotationProcessor 'com.github.lauhwong:uupgrade-compiler:1.0-SNAPSHOT'
    }
```
and then use it in your application just like this:
```java
@UpgradeInstance(id = "db")
public class DbUpgradeHandler1 implements VersionHandler<SQLiteDatabase>{
        @VersionUpgrade(fromVersion = 0, toVersion = 1)
        public void handle01() {
    
        }
    
        @VersionUpgrade(fromVersion = 1, toVersion = 2)
        public void handle12() {
    
        }
        @VersionUpgrade(fromVersion = 1, toVersion = 2,priority=1)
         public void handle12() {
            
         }
}
@UpgradeInstance(id = "db")
public class DbUpgradeHandler2 implements VersionHandler<SQLiteDatabase>{
        @VersionUpgrade(fromVersion = 2, toVersion = 3)
        public void handle01() {
    
        }
    
        @VersionUpgrade(fromVersion = 3, toVersion = 4)
        public void handle34() {
    
        }
        @VersionUpgrade(fromVersion = 4, toVersion = 5,priority=1)
         public void handle45() {
            
         }
}

```
each id will generate class with capture(id)+UpgradeManager,and then you can do with this manager
```java
public class Test{
    public void upgrade(){
        DbUpgradeManager dbUpgradeManager = new DbUpgradeManager();
        dbUpgradeManager.setSeedInstance(db);
        try {
              dbUpgradeManager.applyUpgrade();
          } catch (UpgradeException e) {
              e.printStackTrace();
          }
    }
}


```
more *details usage* can be explore in *example module*

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
