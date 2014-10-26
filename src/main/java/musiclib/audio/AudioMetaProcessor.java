package musiclib.audio;

import com.google.common.collect.HashBasedTable;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileFilter;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @version 0.1
 * @author priyanshu
 */
public class AudioMetaProcessor {

    public static String MEDIA_MUSIC = "/Users/priyanshu/Music/iTunes/iTunes Media/Music/";
    public static String[] MUSIC_FORMATS = {"mp3", "mp4", "wma","ogg"};

    public static void main(String[] args) {

        readMetaDate();

    }

    public static void readMetaDate() {
        AudioFile audioFile = null;

            File musicFolder = new File(MEDIA_MUSIC);
            Iterator<File> fileFilter = FileUtils.iterateFiles(musicFolder, MUSIC_FORMATS, true);

            HashBasedTable<String, String, String> musicTable = HashBasedTable.create();

            Set<String> ft = new HashSet<String>();
            Set<String> nonmp3 = new HashSet<String>();
            int c = 0,s=0,d=0;

            AudioFileFilter audioFileFilter = new AudioFileFilter(true);
            File musicFile;
            MP3File mp3File=null;

            while (fileFilter.hasNext()) {
                musicFile = fileFilter.next();
                if (audioFileFilter.accept(musicFile)) {
                    ++c;
                    try {
                        audioFile = AudioFileIO.read(musicFile);
                    } catch (CannotReadException e) {
                        System.out.println("error while reading " + musicFile.getName());
                        e.printStackTrace();
                    } catch (ReadOnlyFileException e) {
                        e.printStackTrace();
                    } catch (TagException e) {
                        e.printStackTrace();
                    } catch (InvalidAudioFrameException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ft.add(audioFile.getAudioHeader().getFormat());

                    if (audioFile instanceof MP3File) {
                        mp3File = (MP3File) audioFile;
                        if (!mp3File.hasID3v2Tag()) {
                            ++d;
                        }
                    } else {
                        nonmp3.add(musicFile.getName());
                    }


                    //System.out.println(musicFile.getName() + "::" + mp3File.hasID3v2Tag());

                   if (mp3File.hasID3v2Tag()) {

                        AbstractID3v2Tag v2 = mp3File.getID3v2TagAsv24();

                       musicTable.put(musicFile.getName(),"artist", v2.getFirst(FieldKey.ARTIST));
                       musicTable.put(musicFile.getName(),"album", v2.getFirst(FieldKey.ALBUM));
                       musicTable.put(musicFile.getName(),"title", v2.getFirst(FieldKey.TITLE));
                       musicTable.put(musicFile.getName(),"composer", v2.getFirst(FieldKey.COMPOSER));
                       musicTable.put(musicFile.getName(),"album artist", v2.getFirst(FieldKey.ALBUM_ARTIST));

                    } else {

                        Tag tag = audioFile.getTag();

                       musicTable.put(musicFile.getName(),FieldKey.ARTIST.toString(), tag.getFirst(FieldKey.ARTIST));
                       musicTable.put(musicFile.getName(),FieldKey.ALBUM.toString(), tag.getFirst(FieldKey.ALBUM));
                       musicTable.put(musicFile.getName(),FieldKey.TITLE.toString(), tag.getFirst(FieldKey.TITLE));
                       musicTable.put(musicFile.getName(),FieldKey.COMPOSER.toString(), tag.getFirst(FieldKey.COMPOSER));
                       musicTable.put(musicFile.getName(),FieldKey.ALBUM_ARTIST.toString(), tag.getFirst(FieldKey.ALBUM_ARTIST));
                       
                    }

                }
            }

        System.out.println(musicTable.cellSet().size()/4);

            System.out.println(" file count " + c + " non-ID3 tags " + d);
            System.out.println("music MUSIC_FORMATS  " + ft.size() + " " + ft.toString());
            System.out.println(" non mp3  " + nonmp3);

    }


}
