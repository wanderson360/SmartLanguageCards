package br.edu.utfpr.wandersonsousa.smartlanguagecards;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.Word;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDao;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDataBase;

public class RandomCardActivity extends AppCompatActivity {

    private TextView frenteTextView;
    private TextView versoTextView;
    private TextView categoriaTextView;
    private TextView nivelTextView;
    private Button btnMostrarVerso;
    private Button btnProximo;

    private WordDao wordDao;
    private Random random = new Random();
    private Word currentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_card);

        // Referências aos componentes do layout
        frenteTextView = findViewById(R.id.card_frente);
        versoTextView = findViewById(R.id.card_verso);
        categoriaTextView = findViewById(R.id.card_categoria);
        nivelTextView = findViewById(R.id.card_nivel);
        btnMostrarVerso = findViewById(R.id.btn_mostrar_verso);
        btnProximo = findViewById(R.id.btn_proximo);

        // Obter instância do banco e DAO
        WordDataBase db = WordDataBase.getInstance(getApplicationContext());
        wordDao = db.getWordDao();

        // Exibir primeiro card
        mostrarNovoCard();

        // Botão para revelar verso com animação de flip
        btnMostrarVerso.setOnClickListener(v -> {
            if (currentWord != null) {
                flipCard();
            }
        });

        // Botão para próximo card
        btnProximo.setOnClickListener(v -> mostrarNovoCard());
    }

    private void mostrarNovoCard() {
        // Buscar uma palavra aleatória direto do banco
        currentWord = wordDao.queryRandom();

        if (currentWord != null) {
            frenteTextView.setText(currentWord.getFrontWord());
            versoTextView.setText("");
            versoTextView.setVisibility(View.INVISIBLE);

            categoriaTextView.setText("Categoria: " + currentWord.getCategory());
            nivelTextView.setText("Nível: " + currentWord.getLanguageLevel().name());
        } else {
            frenteTextView.setText("Nenhum card encontrado!");
            versoTextView.setText("");
            categoriaTextView.setText("");
            nivelTextView.setText("");
        }
    }

    // Animação de flip para mostrar verso
    private void flipCard() {
        final View cardView = findViewById(R.id.card_container);

        cardView.animate()
                .rotationY(90f)
                .setDuration(200)
                .withEndAction(() -> {
                    // Troca o texto quando o card está "de lado"
                    versoTextView.setText(currentWord.getBackWord());
                    versoTextView.setVisibility(View.VISIBLE);

                    // Continua a rotação até 180°
                    cardView.setRotationY(-90f);
                    cardView.animate()
                            .rotationY(0f)
                            .setDuration(200)
                            .start();
                })
                .start();
    }
}