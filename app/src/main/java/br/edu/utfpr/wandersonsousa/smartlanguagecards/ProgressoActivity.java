package br.edu.utfpr.wandersonsousa.smartlanguagecards;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.LanguageLevel;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDao;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDataBase;

public class ProgressoActivity extends AppCompatActivity {

    private WordDao wordDao;
    private TextView txtResumo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progresso);

        wordDao = WordDataBase.getInstance(this).getWordDao();
        txtResumo = findViewById(R.id.txtResumo);

        atualizarEstatisticas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarEstatisticas();
    }

    private void atualizarEstatisticas() {
        int total = wordDao.countWords();
        int basico = wordDao.countByLevel(LanguageLevel.BASIC);
        int intermediario = wordDao.countByLevel(LanguageLevel.INTERMEDIATE);
        int avancado = wordDao.countByLevel(LanguageLevel.ADVANCED);
        int favoritos = wordDao.countFavorites();

        txtResumo.setText(
                "Total de palavras: " + total +
                        "\nBásico: " + basico +
                        "\nIntermediário: " + intermediario +
                        "\nAvançado: " + avancado +
                        "\nFavoritos: " + favoritos
        );

        ProgressBar progressBasico = findViewById(R.id.progressBasico);
        ProgressBar progressIntermediario = findViewById(R.id.progressIntermediario);
        ProgressBar progressAvancado = findViewById(R.id.progressAvancado);
        ProgressBar progressFavoritos = findViewById(R.id.progressFavoritos);

        TextView txtPercentBasico = findViewById(R.id.txtPercentBasico);
        TextView txtPercentIntermediario = findViewById(R.id.txtPercentIntermediario);
        TextView txtPercentAvancado = findViewById(R.id.txtPercentAvancado);
        TextView txtPercentFavoritos = findViewById(R.id.txtPercentFavoritos);

        if (total > 0) {
            int percentBasico = (basico * 100) / total;
            int percentIntermediario = (intermediario * 100) / total;
            int percentAvancado = (avancado * 100) / total;
            int percentFavoritos = (favoritos * 100) / total;

            animateProgress(progressBasico, percentBasico);
            animateProgress(progressIntermediario, percentIntermediario);
            animateProgress(progressAvancado, percentAvancado);
            animateProgress(progressFavoritos, percentFavoritos);

            txtPercentBasico.setText(percentBasico + "%");
            txtPercentIntermediario.setText(percentIntermediario + "%");
            txtPercentAvancado.setText(percentAvancado + "%");
            txtPercentFavoritos.setText(percentFavoritos + "%");
        } else {
            animateProgress(progressBasico, 0);
            animateProgress(progressIntermediario, 0);
            animateProgress(progressAvancado, 0);
            animateProgress(progressFavoritos, 0);

            txtPercentBasico.setText("0%");
            txtPercentIntermediario.setText("0%");
            txtPercentAvancado.setText("0%");
            txtPercentFavoritos.setText("0%");
        }
    }

    private void animateProgress(ProgressBar progressBar, int toValue) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, toValue);
        animation.setDuration(1500); // 1,5 segundos
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}