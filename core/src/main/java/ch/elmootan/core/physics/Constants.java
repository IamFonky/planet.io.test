package ch.elmootan.core.physics;

public enum Constants {
    GRAVITATION(6.66E-15, "N⋅m2⋅kg-2");

    Constants(Double valeur, String unite) {
        this.valeur = valeur;
        this.unite = unite;
    }

    public Double valeur;
    public String unite;
}
