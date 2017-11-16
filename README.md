# SheetPlayer-ocr
Reading pictures of sheet music and outputting them in audio files (midi).

This was a personal project for me that I had motivation to do, and to learn lots of different things.

In essence, the idea is that you can take a picture of a piece of sheet music, and it would play it back to you audibly.
It turned out to be a much larger project than I expected, and than I had the knowledge to do. However, thanks to great open source libraries I was able to get it to work, generally.

## Special thanks to:

+ [Audiveris (does all of the transcribing)](https://github.com/Audiveris/audiveris)
+ [Music21](http://web.mit.edu/music21/)
+ Stack Overflow (for teaching me how to do pretty much everything)

### Interested in using?
It's actually kind of complicated but here is the general flow:

Picture taken from phone >> Uploaded to Nodejs server >> nodejs server calls python script that calls Audiveris >> Audiveris transcribes >>
once transcribing is done, python then converts a .mxl to a .midi for listening >> phone can download new .midi file for listening

In order to use you would have to set up the nodejs server (found in /sheetplayer-server/) and edit the android application to connect to your server.
