package br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.LanguageLevel;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.Word;

@Dao
public interface WordDao {

    // Inserir uma palavra e retornar o ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Word word);

    // Inserir várias palavras de uma vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Word> words);

    // Atualizar uma palavra existente
    @Update
    int update(Word word);

    // Deletar uma palavra
    @Delete
    int delete(Word word);

    // Buscar por ID
    @Query("SELECT * FROM Word WHERE id = :id")
    Word queryById(long id);

    // Buscar todas as palavras
    @Query("SELECT * FROM Word")
    List<Word> queryAll();

    // Buscar todas em ordem ascendente
    @Query("SELECT * FROM Word ORDER BY frontWord ASC")
    List<Word> queryAllAscending();

    // Buscar todas em ordem descendente
    @Query("SELECT * FROM Word ORDER BY frontWord DESC")
    List<Word> queryAllDescending();

    // Buscar uma palavra aleatória
    @Query("SELECT * FROM Word ORDER BY RANDOM() LIMIT 1")
    Word queryRandom();

    // Buscar por categoria
    @Query("SELECT * FROM Word WHERE category = :category")
    List<Word> queryByCategory(String category);

    // Buscar por nível (usando enum)
    @Query("SELECT * FROM Word WHERE languageLevel = :level")
    List<Word> queryByLevel(LanguageLevel level);

    // Contar quantas palavras existem
    @Query("SELECT COUNT(*) FROM Word")
    int countWords();

    // Apagar todas as palavras (útil para resetar exemplos)
    @Query("DELETE FROM Word")
    void deleteAll();

    // --- Estatísticas de progresso ---

    // Contar por nível (usando enum)
    @Query("SELECT COUNT(*) FROM Word WHERE languageLevel = :level")
    int countByLevel(LanguageLevel level);

    // Contar por categoria
    @Query("SELECT COUNT(*) FROM Word WHERE category = :category")
    int countByCategory(String category);

    // Contar favoritos
    @Query("SELECT COUNT(*) FROM Word WHERE favorite = 1")
    int countFavorites();

    // ✅ Buscar apenas favoritos
    @Query("SELECT * FROM Word WHERE favorite = 1 ORDER BY frontWord ASC")
    List<Word> queryFavorites();

    @Query("SELECT * FROM Word WHERE favorite = 1 ORDER BY frontWord ASC")
    List<Word> queryFavoritesAscending();

    @Query("SELECT * FROM Word WHERE favorite = 1 ORDER BY frontWord DESC")
    List<Word> queryFavoritesDescending();
}