package br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Objects;

@Entity
public class Word {

    public static Comparator<Word> ordenacaoCrescente = (w1, w2) ->
            w1.getFrontWord().compareToIgnoreCase(w2.getFrontWord());

    public static Comparator<Word> ordenacaoDecrescente = (w1, w2) ->
            -1 * w1.getFrontWord().compareToIgnoreCase(w2.getFrontWord());

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(index = true)
    private String frontWord;

    @NonNull
    @ColumnInfo(index = true)
    private String backWord;

    private boolean uppercaseWord;

    private boolean favorite;

    @NonNull
    private String category;

    @NonNull
    private LanguageLevel languageLevel;

    public Word(@NonNull String frontWord,
                @NonNull String backWord,
                boolean uppercaseWord,
                @NonNull String category,
                @NonNull LanguageLevel languageLevel) {

        if (frontWord.trim().isEmpty()) {
            throw new IllegalArgumentException("Front word não pode ser vazio.");
        }
        if (backWord.trim().isEmpty()) {
            throw new IllegalArgumentException("Back word não pode ser vazio.");
        }

        this.frontWord = frontWord;
        this.backWord = backWord;
        this.uppercaseWord = uppercaseWord;
        this.category = category;
        this.languageLevel = languageLevel;
        this.favorite = false; // padrão
    }

    public Word(@NonNull String frontWord,
                @NonNull String backWord,
                boolean uppercaseWord,
                @NonNull String category,
                int nivel) {
        this(frontWord, backWord, uppercaseWord, category, LanguageLevel.fromInt(nivel));
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFrontWord() { return frontWord; }
    public void setFrontWord(String frontWord) { this.frontWord = frontWord; }

    public String getBackWord() { return backWord; }
    public void setBackWord(String backWord) { this.backWord = backWord; }

    public boolean isUppercaseWord() { return uppercaseWord; }
    public void setUppercaseWord(boolean uppercaseWord) { this.uppercaseWord = uppercaseWord; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LanguageLevel getLanguageLevel() { return languageLevel; }
    public void setLanguageLevel(LanguageLevel languageLevel) { this.languageLevel = languageLevel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;
        Word other = (Word) o;
        return id == other.id &&
                uppercaseWord == other.uppercaseWord &&
                favorite == other.favorite &&
                frontWord.equals(other.frontWord) &&
                backWord.equals(other.backWord) &&
                category.equals(other.category) &&
                languageLevel == other.languageLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frontWord, backWord, category, languageLevel, uppercaseWord, favorite);
    }

    @Override
    public String toString() {
        return "Word{" +
                "frontWord='" + frontWord + '\'' +
                ", backWord='" + backWord + '\'' +
                ", uppercaseWord=" + uppercaseWord +
                ", favorite=" + favorite +
                ", category='" + category + '\'' +
                ", languageLevel=" + languageLevel +
                '}';
    }
}