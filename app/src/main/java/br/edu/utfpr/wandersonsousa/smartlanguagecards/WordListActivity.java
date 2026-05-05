package br.edu.utfpr.wandersonsousa.smartlanguagecards;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.Word;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDao;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDataBase;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.utils.UtilsAlert;

public class WordListActivity extends AppCompatActivity {

    private ListView listViewWordList;
    private List<Word> listWord;
    private WordAdapter adapter;

    private int posicaoSelecionada = -1;
    public static final String ARQUIVO_PREFERENCIAS = "br.edu.utfpr.wandersonsousa.smartlanguagecards.PREFERENCIAS";

    public static final String KEY_ORDENACAO_ASCENDENTE = "KEY_ORDENACAO_ASCENDENTE";
    public static final boolean PADRAO_INICIAL_ORDENACAO_ASCENDENTE = true;
    private boolean ordenacaoAscendente = PADRAO_INICIAL_ORDENACAO_ASCENDENTE;
    private MenuItem menuItemOrdenacao;
    private boolean mostrandoFavoritos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        setTitle(getString(R.string.flashcards_para_aprendizado_de_idiomas));

        listViewWordList = findViewById(R.id.listViewWordList);

        lerPreferencias();
        atualizarLista();

        listViewWordList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listViewWordList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int count = listViewWordList.getCheckedItemCount();
                mode.setTitle(count + getString(R.string.itens_selecionados));
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.menuItemExcluir) {
                    excluirSelecionados();
                    return true;
                } else if (id == R.id.menuItemEditar) {
                    editarPrimeiroSelecionado();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}
        });
    }

    private void atualizarLista() {
        WordDataBase database = WordDataBase.getInstance(this);

        if (mostrandoFavoritos) {
            if (ordenacaoAscendente) {
                listWord = database.getWordDao().queryFavoritesAscending();
            } else {
                listWord = database.getWordDao().queryFavoritesDescending();
            }
        } else {
            if (ordenacaoAscendente) {
                listWord = database.getWordDao().queryAllAscending();
            } else {
                listWord = database.getWordDao().queryAllDescending();
            }
        }

        if (listWord == null) {
            listWord = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new WordAdapter(this, listWord);
            listViewWordList.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(listWord);
            adapter.notifyDataSetChanged();
        }
    }

    public void openAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    // Launcher para adicionar/editar palavras
    ActivityResultLauncher<Intent> launcherAddWord = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) {
                    return;
                }

                Intent intent = result.getData();
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    long id = bundle.getLong(WordActivity.KEY_ID);

                    final WordDataBase database = WordDataBase.getInstance(WordListActivity.this);

                    int modo = bundle.getInt(WordActivity.KEY_MODO, WordActivity.MODO_NOVO);
                    if (modo == WordActivity.MODO_NOVO) {
                        UtilsAlert.showAlert(this, R.string.palavra_adicionada);
                    } else if (modo == WordActivity.MODO_EDITAR && posicaoSelecionada >= 0) {
                        UtilsAlert.showAlert(this, R.string.palavra_editada);
                        posicaoSelecionada = -1;
                    }
                    atualizarLista();
                }
            }
    );

    public void addWord() {
        Intent intentAbertura = new Intent(this, WordActivity.class);
        intentAbertura.putExtra(WordActivity.KEY_MODO, WordActivity.MODO_NOVO);
        launcherAddWord.launch(intentAbertura);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_options, menu);
        menuItemOrdenacao = menu.findItem(R.id.menuItemOrdenacao);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        atualizarIconeOrdenacao();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItemAdicionar) {
            addWord();
            return true;
        } else if (id == R.id.menuItemSobre) {
            openAbout();
            return true;
        } else if (id == R.id.menuItemOrdenacao) {
            salvarPreferenciaOrdenacaoAscendente(!ordenacaoAscendente);
            atualizarIconeOrdenacao();
            atualizarLista();
            return true;
        } else if (id == R.id.menuItemRestaurar) {
            restaurarPadroes();
            return true;
        } else if (id == R.id.menuItemRandomCard) {
            Intent intent = new Intent(this, RandomCardActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menuItemProgresso) {
            Intent intent = new Intent(this, ProgressoActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menuItemFavoritos) {
            mostrandoFavoritos = true;
            atualizarLista();
            return true;
        } else if (id == R.id.menuItemTodos) {
            mostrandoFavoritos = false;
            atualizarLista();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void editarPrimeiroSelecionado() {
        SparseBooleanArray checkedItems = listViewWordList.getCheckedItemPositions();
        for (int i = 0; i < checkedItems.size(); i++) {
            if (checkedItems.valueAt(i)) {
                int pos = checkedItems.keyAt(i);
                editarPalavra(pos);
                break;
            }
        }
    }

    private void excluirSelecionados() {
        SparseBooleanArray selected = listViewWordList.getCheckedItemPositions();
        WordDataBase database = WordDataBase.getInstance(this);

        for (int i = listWord.size() - 1; i >= 0; i--) {
            if (selected.get(i)) {
                Word word = listWord.get(i);
                String message = getString(R.string.confirmation_delete, word.getFrontWord());

                DialogInterface.OnClickListener listenerYes = (dialog, which) -> {
                    database.getWordDao().delete(word);
                    listWord.remove(word);
                    atualizarLista();
                    UtilsAlert.showAlert(this, R.string.palavra_excluida);
                };

                DialogInterface.OnClickListener listenerNo = (dialog, which) -> dialog.dismiss();

                UtilsAlert.confirmAction(this, message, listenerYes, listenerNo);
            }
        }
    }

    private void editarPalavra(int position) {
        posicaoSelecionada = position;
        Word word = listWord.get(position);

        Intent intentAbertura = new Intent(this, WordActivity.class);
        intentAbertura.putExtra(WordActivity.KEY_MODO, WordActivity.MODO_EDITAR);
        intentAbertura.putExtra(WordActivity.KEY_ID, word.getId());

        launcherAddWord.launch(intentAbertura);
    }

    private void lerPreferencias() {
        SharedPreferences preferences = getSharedPreferences(WordListActivity.ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
        ordenacaoAscendente = preferences.getBoolean(KEY_ORDENACAO_ASCENDENTE, ordenacaoAscendente);
    }

    private void salvarPreferenciaOrdenacaoAscendente(boolean novoValor) {
        SharedPreferences preferences = getSharedPreferences(WordListActivity.ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_ORDENACAO_ASCENDENTE, novoValor);
        editor.apply();
        ordenacaoAscendente = novoValor;
    }

    private void atualizarIconeOrdenacao() {
        if (ordenacaoAscendente) {
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ascending_order);
        } else {
            menuItemOrdenacao.setIcon(R.drawable.ic_action_descending_order);
        }
    }

    private void restaurarPadroes() {
        Executors.newSingleThreadExecutor().execute(() -> {
            WordDao wordDao = WordDataBase.getInstance(WordListActivity.this).getWordDao();

            wordDao.deleteAll();
            WordDataBase.inserirPalavrasExemplo(wordDao);

            SharedPreferences preferences = getSharedPreferences(
                    WordListActivity.ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            ordenacaoAscendente = PADRAO_INICIAL_ORDENACAO_ASCENDENTE;

            runOnUiThread(() -> {
                atualizarIconeOrdenacao();
                atualizarLista();
                Toast.makeText(WordListActivity.this,
                        R.string.padroes_restaurados,
                        Toast.LENGTH_SHORT).show();
            });
        });
    }
}