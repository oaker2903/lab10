package it.unibo.oop.lab.streams;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class implements the MusicGroup interface, representing a music group with albums and songs.
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new LinkedHashMap<>();
    private final Set<Song> songs = new LinkedHashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(song -> song.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        //devo attraversare entrambi i campi di map perchè devo filtrare in base all'anno quindi keySet non va bene
        return this.albums.entrySet().stream().filter(e -> e.getValue() == year).map(e -> e.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream().filter(s -> s.getAlbumName().equals(Optional.of(albumName))).count();   //non posso applicare intValue perché sto usando un Long primitivo
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream().filter(s -> s.getAlbumName().isEmpty()).count();   
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream().filter(s -> s.getAlbumName().equals(Optional.of(albumName))).mapToDouble(s -> s.getDuration()).average();
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream().max((a,b) -> Double.compare(a.getDuration(), b.getDuration())).map(s -> s.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        //scorro nelle songs e raggruppo per durata tempo in base al nome dell'album, ritorno il nome dell'album con quelle canzoni -> map<String nome, Double durata_tot> 
        return Optional.empty();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        String getSongName() {
            return songName;
        }

        Optional<String> getAlbumName() {
            return albumName;
        }

        double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = Objects.hash(songName, albumName, duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof Song other
                && albumName.equals(other.albumName)
                && songName.equals(other.songName)
                && duration == other.duration;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
