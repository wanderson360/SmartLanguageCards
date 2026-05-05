package br.edu.utfpr.wandersonsousa.smartlanguagecards;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.LanguageLevel;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.Word;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDataBase;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.utils.UtilsAlert;

public class WordActivity extends AppCompatActivity {
    public static final String KEY_MODO = "KEY_MODO";
    public static final String KEY_ID = "ID";
    public static final String KEY_SUGERIR_TIPO = "KEY_SUGERIR_TIPO";
    public static final String KEY_ULTIMO_TIPO = "KEY_ULTIMO_TIPO";
    public static final int MODO_NOVO = 0;
    public static final int MODO_EDITAR = 1;

    private EditText editTextFrente, editTextVerso;
    private CheckBox checkBoxUpperCase;
    private RadioGroup radioGroupLevel;
    private Spinner spinnerTipo;
    private int modo = MODO_NOVO;

    private boolean sugerirTipo = false;
    private int ultimoTipo = 0;

    private Word wordOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_activity);
        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        editTextFrente = findViewById(R.id.editTextFrente);
        editTextVerso = findViewById(R.id.editTextVerso);
        checkBoxUpperCase = findViewById(R.id.checkBoxUpperCase);
        radioGroupLevel = findViewById(R.id.radioGroupLevel);
        spinnerTipo = findViewById(R.id.spinnerTipo);

        lerPreferencias();

        Intent intentAbertura = getIntent();
        Bundle bundle = intentAbertura.getExtras();

        if (bundle != null) {
            modo = bundle.getInt(KEY_MODO, MODO_NOVO);

            if (modo == MODO_NOVO) {
                setTitle(getString(R.string.cadastrar_nova_palavra_frase));
                editTextFrente.requestFocus();
                if (sugerirTipo) {
                    spinnerTipo.setSelection(ultimoTipo);
                }
            } else if (modo == MODO_EDITAR) {
                setTitle(getString(R.string.editar_nova_palavra_frase));

                WordDataBase database = WordDataBase.getInstance(this);
                long id = bundle.getLong(KEY_ID);
                wordOriginal = database.getWordDao().queryById(id);

                String categoria = wordOriginal.getCategory();
                String[] categorias = getResources().getStringArray(R.array.listaObjetivos);
                int categoriaIndex = 0;
                for (int i = 0; i < categorias.length; i++) {
                    if (categorias[i].equals(categoria)) {
                        categoriaIndex = i;
                        break;
                    }
                }

                LanguageLevel languageLevel = wordOriginal.getLanguageLevel();

                editTextFrente.setText(wordOriginal.getFrontWord());
                editTextVerso.setText(wordOriginal.getBackWord());
                checkBoxUpperCase.setChecked(wordOriginal.isUppercaseWord());
                atualizarTexto(editTextFrente, wordOriginal.isUppercaseWord());
                atualizarTexto(editTextVerso, wordOriginal.isUppercaseWord());
                spinnerTipo.setSelection(categoriaIndex);

                if (LanguageLevel.BASIC == languageLevel) radioGroupLevel.check(R.id.radioButtonBasico);
                else if (LanguageLevel.INTERMEDIATE == languageLevel) radioGroupLevel.check(R.id.radioButtonIntermediario);
                else if (LanguageLevel.ADVANCED == languageLevel) radioGroupLevel.check(R.id.radioButtonAvancado);
                else radioGroupLevel.clearCheck();

                editTextFrente.requestFocus();
                editTextFrente.setSelection(editTextFrente.getText().length());
            }
        }
    }

    private void configurarEventos() {
        aplicarUpperCaseWatcher(editTextFrente);
        aplicarUpperCaseWatcher(editTextVerso);

        checkBoxUpperCase.setOnCheckedChangeListener((buttonView, isChecked) -> {
            atualizarTexto(editTextFrente, isChecked);
            atualizarTexto(editTextVerso, isChecked);
        });
    }

    public void limparCampos() {
        // Guardar valores atuais para possível "Desfazer"
        final String textoFrente = editTextFrente.getText().toString();
        final String textoVerso = editTextVerso.getText().toString();
        final boolean upperCase = checkBoxUpperCase.isChecked();
        final int selectedLevelId = radioGroupLevel.getCheckedRadioButtonId();
        final int tipoCategoriaIndex = spinnerTipo.getSelectedItemPosition();

        final View rootView = findViewById(android.R.id.content);

        // 1. Limpar os campos imediatamente
        editTextFrente.setText("");
        editTextVerso.setText("");
        checkBoxUpperCase.setChecked(false);
        radioGroupLevel.clearCheck();
        if (spinnerTipo.getAdapter() != null && spinnerTipo.getAdapter().getCount() > 0) {
            spinnerTipo.setSelection(0);
        }

        // 2. Desabilitar campos temporariamente e aplicar cor acinzentada
        editTextFrente.setEnabled(false);
        editTextVerso.setEnabled(false);
        checkBoxUpperCase.setEnabled(false);
        spinnerTipo.setEnabled(false);
        for (int i = 0; i < radioGroupLevel.getChildCount(); i++) {
            radioGroupLevel.getChildAt(i).setEnabled(false);
        }

        editTextFrente.setTextColor(Color.GRAY);
        editTextVerso.setTextColor(Color.GRAY);

        // 3. Criar Snackbar "vazio"
        Snackbar snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_INDEFINITE);

        // 4. Inflar layout customizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.custom_snackbar, null);

        // Referências dos botões
        Button btnUndo = customView.findViewById(R.id.snackbar_undo);
        Button btnClose = customView.findViewById(R.id.snackbar_close);

        // Ação "Desfazer" → restaura valores antigos e reabilita campos
        btnUndo.setOnClickListener(v -> {
            editTextFrente.setText(textoFrente);
            editTextVerso.setText(textoVerso);
            checkBoxUpperCase.setChecked(upperCase);

            if (selectedLevelId != -1) {
                radioGroupLevel.check(selectedLevelId);
            }

            if (spinnerTipo.getAdapter() != null && tipoCategoriaIndex >= 0) {
                spinnerTipo.setSelection(tipoCategoriaIndex);
            }

            // Reabilitar campos e restaurar cor original
            editTextFrente.setEnabled(true);
            editTextVerso.setEnabled(true);
            checkBoxUpperCase.setEnabled(true);
            spinnerTipo.setEnabled(true);
            for (int i = 0; i < radioGroupLevel.getChildCount(); i++) {
                radioGroupLevel.getChildAt(i).setEnabled(true);
            }

            editTextFrente.setTextColor(Color.BLACK);
            editTextVerso.setTextColor(Color.BLACK);

            editTextFrente.requestFocus();
            snackbar.dismiss();
        });

        // Ação "Fechar" → confirma limpeza e reabilita campos
        btnClose.setOnClickListener(v -> {
            editTextFrente.setEnabled(true);
            editTextVerso.setEnabled(true);
            checkBoxUpperCase.setEnabled(true);
            spinnerTipo.setEnabled(true);
            for (int i = 0; i < radioGroupLevel.getChildCount(); i++) {
                radioGroupLevel.getChildAt(i).setEnabled(true);
            }

            editTextFrente.setTextColor(Color.BLACK);
            editTextVerso.setTextColor(Color.BLACK);

            editTextFrente.requestFocus();
            snackbar.dismiss();
        });

        // Substituir conteúdo padrão do Snackbar pelo customizado
        @SuppressLint("RestrictedApi")
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setBackgroundColor(Color.BLACK);
        snackbarLayout.removeAllViews();
        snackbarLayout.addView(customView);

        snackbar.show();
    }

    public void adicionarPalavra() {
        String frente = getTextoProcessado(editTextFrente);
        String verso = getTextoProcessado(editTextVerso);
        int tipoCategoriaIndex = spinnerTipo.getSelectedItemPosition();
        String tipoCategoria = (String) spinnerTipo.getSelectedItem();

        if (validarCampo(frente, editTextFrente)) return;
        if (validarCampo(verso, editTextVerso)) return;

        if (tipoCategoriaIndex == AdapterView.INVALID_POSITION) {
            UtilsAlert.showAlert(this, R.string.selecione_uma_categoria);
            return;
        }

        int selectedLevel = getSelectedLevel();
        if (selectedLevel == -1) {
            UtilsAlert.showAlert(this, R.string.selecione_uma_opcao_nivel);
            return;
        }

        Word word = new Word(frente, verso, checkBoxUpperCase.isChecked(), tipoCategoria, selectedLevel);

        if (word.equals(wordOriginal)) {
            setResult(WordActivity.RESULT_CANCELED);
            finish();
            return;
        }

        Intent intentResposta = new Intent();
        WordDataBase database = WordDataBase.getInstance(this);

        if (modo == MODO_NOVO) {
            long id = database.getWordDao().insert(word);
            word.setId(id);
            intentResposta.putExtra(KEY_ID, id);
        } else {
            word.setId(wordOriginal.getId());
            database.getWordDao().update(word);
            intentResposta.putExtra(KEY_ID, word.getId());
        }

        salvarUltimoTipoPreferencias();
        setResult(WordActivity.RESULT_OK, intentResposta);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registration_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menuItemSugerirTipo).setChecked(sugerirTipo);
        return true;
    }

    private void salvarUltimoTipoPreferencias() {
        SharedPreferences preferences = getSharedPreferences(WordListActivity.ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int tipoCategoriaIndex = spinnerTipo.getSelectedItemPosition();
        editor.putInt(KEY_ULTIMO_TIPO, tipoCategoriaIndex);
        editor.apply();

        ultimoTipo = tipoCategoriaIndex;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuItemSalvar) {
            adicionarPalavra();
            return true;
        } else if (id == R.id.menuItemLimpar) {
            limparCampos();
            return true;
        } else if (id == R.id.menuItemSugerirTipo) {
            sugerirTipo = !sugerirTipo;
            item.setChecked(sugerirTipo);
            salvarPreferencias(sugerirTipo);
            if (sugerirTipo) {
                spinnerTipo.setSelection(ultimoTipo);
            } else {
                spinnerTipo.setSelection(0);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void lerPreferencias() {
        SharedPreferences preferences = getSharedPreferences(WordListActivity.ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
        sugerirTipo = preferences.getBoolean(KEY_SUGERIR_TIPO, sugerirTipo);
        ultimoTipo = preferences.getInt(KEY_ULTIMO_TIPO, ultimoTipo);
    }

    private void salvarPreferencias(boolean novoValor) {
        SharedPreferences preferences = getSharedPreferences(WordListActivity.ARQUIVO_PREFERENCIAS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_SUGERIR_TIPO, novoValor);
        editor.putInt(KEY_ULTIMO_TIPO, ultimoTipo);
        editor.apply();
    }

    private boolean validarCampo(String texto, EditText campo) {
        if (texto.isBlank()) {
            campo.setError(getString(R.string.campo_obrigatorio));
            campo.requestFocus();
            return true;
        }
        return false;
    }

    private String getTextoProcessado(EditText campo) {
        String texto = campo.getText().toString().trim();
        return checkBoxUpperCase.isChecked() ? texto.toUpperCase() : texto;
    }

    private void aplicarUpperCaseWatcher(EditText campo) {
        campo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (checkBoxUpperCase.isChecked()) {
                    String textoAtual = s.toString();
                    String textoUpper = textoAtual.toUpperCase();
                    if (!textoAtual.equals(textoUpper)) {
                        campo.removeTextChangedListener(this);
                        campo.setText(textoUpper);
                        campo.setSelection(textoUpper.length());
                        campo.addTextChangedListener(this);
                    }
                }
            }
        });
    }

    private void atualizarTexto(EditText campo, boolean upperCase) {
        String texto = campo.getText().toString();
        campo.setText(upperCase ? texto.toUpperCase() : texto.toLowerCase());
        campo.setSelection(campo.getText().length());
    }

    private int getSelectedLevel() {
        int radioButtonId = radioGroupLevel.getCheckedRadioButtonId();
        if (radioButtonId == R.id.radioButtonBasico) return 1;
        if (radioButtonId == R.id.radioButtonIntermediario) return 2;
        if (radioButtonId == R.id.radioButtonAvancado) return 3;
        return -1;
    }
}