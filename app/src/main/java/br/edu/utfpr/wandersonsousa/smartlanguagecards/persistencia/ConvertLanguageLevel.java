package br.edu.utfpr.wandersonsousa.smartlanguagecards.persistencia;

import androidx.room.TypeConverter;

import br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo.LanguageLevel;

public class ConvertLanguageLevel {

    public static LanguageLevel[] languageLevel = LanguageLevel.values();


    @TypeConverter
    public static int fromEnumToInt(LanguageLevel languageLevel) {
        if (languageLevel == null) {
            return -1;
        }
        return languageLevel.ordinal();
    }

    @TypeConverter
    public static LanguageLevel fromIntToEnum(int ordinal) {
        if (ordinal == -1) {
            return null;
        }
        return languageLevel[ordinal];
    }

}
