package br.edu.utfpr.wandersonsousa.smartlanguagecards.modelo;

public enum LanguageLevel {
    BASIC,
    INTERMEDIATE,
    ADVANCED;

    public static LanguageLevel fromInt(int nivel) {
        switch (nivel) {
            case 1: return BASIC;
            case 2: return INTERMEDIATE;
            case 3: return ADVANCED;
            default: return BASIC;
        }
    }
}