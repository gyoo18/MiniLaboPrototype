package com.MiniLabo.prototype;

public class Txt{
    // Source : shakram02, 2017 https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    /**Charactère qui efface le formatage du texte console.*/
    public static final String EFF = "\033[0m";  // Text Reset

    /**Charactère qui colore le texte console en noir*/
    public static final String NOIR = "\033[0;30m";   // BLACK
    /**Charactère qui colore le texte console en rouge.*/
    public static final String ROUGE = "\033[0;31m";     // RED
    /**Charactère qui colore le texte console en vert.*/
    public static final String VERT = "\033[0;32m";   // GREEN
    /**Charactère qui colore le texte console en jaune.*/
    public static final String JAUNE = "\033[0;33m";  // YELLOW
    /**Charactère qui colore le texte console en bleu.*/
    public static final String BLEU = "\033[0;34m";    // BLUE
    /**Charactère qui colore le texte console en mauve.*/
    public static final String MAUVE = "\033[0;35m";  // PURPLE
    /**Charactère qui colore le texte console en cyan.*/
    public static final String CYAN = "\033[0;36m";    // CYAN
    /**Charactère qui colore le texte console en blanc.*/
    public static final String BLANC = "\033[0;37m";   // WHITE

    /**Charactère qui colore le texte console en gras noir.*/
    public static final String NOIR_GRAS = "\033[1;30m";  // BLACK
    /**Charactère qui colore le texte console en gras rouge.*/
    public static final String ROUGE_GRAS = "\033[1;31m";    // RED
    /**Charactère qui colore le texte console en gras vert.*/
    public static final String VERT_GRAS = "\033[1;32m";  // GREEN
    /**Charactère qui colore le texte console en gras jaune.*/
    public static final String JAUNE_GRAS = "\033[1;33m"; // YELLOW
    /**Charactère qui colore le texte console en gras bleu.*/
    public static final String BLEU_GRAS = "\033[1;34m";   // BLUE
    /**Charactère qui colore le texte console en gras mauve.*/
    public static final String MAUVE_GRAS = "\033[1;35m"; // PURPLE
    /**Charactère qui colore le texte console en gras cyan.*/
    public static final String CYAN_GRAS = "\033[1;36m";   // CYAN
    /**Charactère qui colore le texte console en gras blanc.*/
    public static final String BLANC_GRAS = "\033[1;37m";  // WHITE

    /**Charactère qui colore le texte console en souligné noir.*/
    public static final String NOIR_SOUL = "\033[4;30m";  // BLACK
    /**Charactère qui colore le texte console en souligné rouge.*/
    public static final String ROUGE_SOUL = "\033[4;31m";    // RED
    /**Charactère qui colore le texte console en souligné vert.*/
    public static final String VERT_SOUL = "\033[4;32m";  // GREEN
    /**Charactère qui colore le texte console en souligné jaune.*/
    public static final String JAUNE_SOUL = "\033[4;33m"; // YELLOW
    /**Charactère qui colore le texte console en souligné bleu.*/
    public static final String BLEU_SOUL = "\033[4;34m";   // BLUE
    /**Charactère qui colore le texte console en souligné mauve.*/
    public static final String MAUVE_SOUL = "\033[4;35m"; // PURPLE
    /**Charactère qui colore le texte console en souligné cyan.*/
    public static final String CYAN_SOUL = "\033[4;36m";   // CYAN
    /**Charactère qui colore le texte console en souligné blanc.*/
    public static final String BLANC_SOUL = "\033[4;37m";  // WHITE

    /**Charactère qui colore le texte console en surligné noir.*/
    public static final String NOIR_SUR = "\033[40m";  // BLACK
    /**Charactère qui colore le texte console en surligné rouge.*/
    public static final String ROUGE_SUR = "\033[41m";    // RED
    /**Charactère qui colore le texte console en surligné vert.*/
    public static final String VERT_SUR = "\033[42m";  // GREEN
    /**Charactère qui colore le texte console en surligné jaune.*/
    public static final String JAUNE_SUR = "\033[43m"; // YELLOW
    /**Charactère qui colore le texte console en surligné bleu.*/
    public static final String BLEU_SUR = "\033[44m";   // BLUE
    /**Charactère qui colore le texte console en surligné mauve.*/
    public static final String MAUVE_SUR = "\033[45m"; // PURPLE
    /**Charactère qui colore le texte console en surligné cyan.*/
    public static final String CYAN_SUR = "\033[46m";   // CYAN
    /**Charactère qui colore le texte console en surligné blanc.*/
    public static final String BLAN_SUR = "\033[47m";  // WHITE

    /**Charactère qui colore le texte console en noir lumineux.*/
    public static final String NOIR_LUM = "\033[0;90m";  // BLACK
    /**Charactère qui colore le texte console en rouge lumineux.*/
    public static final String ROUGE_LUM = "\033[0;91m";    // RED
    /**Charactère qui colore le texte console en vert lumineux.*/
    public static final String VERT_LUM = "\033[0;92m";  // GREEN
    /**Charactère qui colore le texte console en jaune lumineux.*/
    public static final String JAUNE_LUM = "\033[0;93m"; // YELLOW
    /**Charactère qui colore le texte console en bleu lumineux.*/
    public static final String BLEU_LUM = "\033[0;94m";   // BLUE
    /**Charactère qui colore le texte console en mauve lumineux.*/
    public static final String MAUVE_LUM = "\033[0;95m"; // PURPLE
    /**Charactère qui colore le texte console en cyan lumineux.*/
    public static final String CYAN_LUM = "\033[0;96m";   // CYAN
    /**Charactère qui colore le texte console en blanc lumineux.*/
    public static final String BLANC_LUM = "\033[0;97m";  // WHITE

    /**Charactère qui colore le texte console en gras noir lumineux.*/
    public static final String NOIR_GRAS_LUM = "\033[1;90m"; // BLACK
    /**Charactère qui colore le texte console en gras rouge lumineux.*/
    public static final String ROUGE_GRAS_LUM = "\033[1;91m";   // RED
    /**Charactère qui colore le texte console en gras vert lumineux.*/
    public static final String VERT_GRAS_LUM = "\033[1;92m"; // GREEN
    /**Charactère qui colore le texte console en gras jaune lumineux.*/
    public static final String JAUNE_GRAS_LUM = "\033[1;93m";// YELLOW
    /**Charactère qui colore le texte console en gras bleu lumineux.*/
    public static final String BLEU_GRAS_LUM = "\033[1;94m";  // BLUE
    /**Charactère qui colore le texte console en gras mauve lumineux.*/
    public static final String MAUVE_GRAS_LUM = "\033[1;95m";// PURPLE
    /**Charactère qui colore le texte console en gras cyan lumineux.*/
    public static final String CYAN_GRAS_LUM = "\033[1;96m";  // CYAN
    /**Charactère qui colore le texte console en gras blanc lumineux.*/
    public static final String BLANC_GRAS_LUM = "\033[1;97m"; // WHITE

    /**Charactère qui colore le texte console en surligné noir lumineux.*/
    public static final String NOIR_SUR_LUM = "\033[0;100m";// BLACK
    /**Charactère qui colore le texte console en surligné rouge lumineux.*/
    public static final String ROUGE_SUR_LUM = "\033[0;101m";// RED
    /**Charactère qui colore le texte console en surligné vert lumineux.*/
    public static final String VERT_SUR_LUM = "\033[0;102m";// GREEN
    /**Charactère qui colore le texte console en surligné jaune lumineux.*/
    public static final String JAUNE_SUR_LUM = "\033[0;103m";// YELLOW
    /**Charactère qui colore le texte console en surligné bleu lumineux.*/
    public static final String BLEU_SUR_LUM = "\033[0;104m";// BLUE
    /**Charactère qui colore le texte console en surligné mauve lumineux.*/
    public static final String MAUVE_SUR_LUM = "\033[0;105m"; // PURPLE
    /**Charactère qui colore le texte console en surligné cyan lumineux.*/
    public static final String CYAN_SUR_LUM = "\033[0;106m";  // CYAN
    /**Charactère qui colore le texte console en surligné blanc lumineux.*/
    public static final String BLANC_SUR_LUM = "\033[0;107m";   // WHITE
}