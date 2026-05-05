package br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.LanguageLevel;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.Word;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDao; // ✅ Import necessário

@Database(entities = {Word.class}, version = 1, exportSchema = false)
@TypeConverters({ConvertLanguageLevel.class})
public abstract class WordDataBase extends RoomDatabase {

    public abstract WordDao getWordDao();

    private static volatile WordDataBase INSTANCE;

    public static WordDataBase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    WordDataBase.class,
                                    "word_database.db"
                            )
                            .allowMainThreadQueries() // apenas para testes
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Inserir palavras de exemplo apenas na criação do banco
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        WordDao dao = INSTANCE.getWordDao();
                                        inserirPalavrasExemplo(dao);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // ✅ Método estático para inserir exemplos
    public static void inserirPalavrasExemplo(WordDao dao) {
        List<Word> exemplos = Arrays.asList(
                new Word("Book", "Livro", false, "Substantivo", LanguageLevel.BASIC),
                new Word("Run", "Correr", false, "Verbo", LanguageLevel.BASIC),
                new Word("Beautiful", "Bonito", false, "Adjetivo", LanguageLevel.INTERMEDIATE),
                new Word("Good morning", "Bom dia", false, "Expressão", LanguageLevel.BASIC),
                new Word("Dog", "Cachorro", false, "Substantivo", LanguageLevel.BASIC)
        );

        // Marcar alguns como favoritos para testar estatísticas
        exemplos.get(0).setFavorite(true); // Book
        exemplos.get(4).setFavorite(true); // Dog

        dao.insertAll(exemplos);
    }
}