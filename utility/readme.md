# common utility module for common objects and data maintenance tasks

## data maintenance tasks

to add/update/fix data:

test dry run: 

```
./gradlew run -p utility
```
update player metadata:

```
./gradlew run -p utility -Pupdate-player-metadata
```

update player game stats (optional first arg will be year):

``` 
./gradlew run -p utility -Pupdate-player-game-stats [--args=2020]
```