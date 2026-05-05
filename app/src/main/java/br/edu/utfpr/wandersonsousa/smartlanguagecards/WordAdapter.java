package br.edu.utfpr.wandersonsousa.smartlanguagecards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.Word;
import br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia.WordDataBase;

public class WordAdapter extends ArrayAdapter<Word> {

    public WordAdapter(Context context, List<Word> words) {
        super(context, 0, words);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_word, parent, false);
        }

        Word word = getItem(position);

        TextView textFrontWord = convertView.findViewById(R.id.textFrontWord);
        TextView textBackWord = convertView.findViewById(R.id.textBackWord);
        TextView textLevel = convertView.findViewById(R.id.textLevel);
        TextView textCategoria = convertView.findViewById(R.id.textCategoria);
        ImageView imgFavorite = convertView.findViewById(R.id.imgFavorite);

        if (word != null) {
            textFrontWord.setText(word.getFrontWord());
            textBackWord.setText(word.getBackWord());
            textLevel.setText("Nível: " + word.getLanguageLevel().name());
            textCategoria.setText("Categoria: " + word.getCategory());

            if (word.isFavorite()) {
                imgFavorite.setImageResource(R.drawable.ic_star);
            } else {
                imgFavorite.setImageResource(R.drawable.ic_star_border);
            }

            // Clique para alternar favorito
            imgFavorite.setOnClickListener(v -> {
                word.setFavorite(!word.isFavorite());
                WordDataBase db = WordDataBase.getInstance(getContext());
                db.getWordDao().update(word);

                if (word.isFavorite()) {
                    imgFavorite.setImageResource(R.drawable.ic_star);
                } else {
                    imgFavorite.setImageResource(R.drawable.ic_star_border);
                }
            });
        }

        return convertView;
    }
}