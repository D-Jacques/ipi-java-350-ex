package com.ipiecoles.java.java350.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
public class Employe {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    private String prenom;

    private String matricule;

    private LocalDate dateEmbauche;

    private Double salaire = Entreprise.SALAIRE_BASE;

    private Integer performance = Entreprise.PERFORMANCE_BASE;

    private Double tempsPartiel = 1.0;

    public Employe() {
    }

    public Employe(String nom, String prenom, String matricule, LocalDate dateEmbauche, Double salaire, Integer performance, Double tempsPartiel) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.performance = performance;
        this.tempsPartiel = tempsPartiel;
    }

    /**
     * Méthode calculant le nombre d'années d'ancienneté à partir de la date d'embauche
     * @return
     */
    public Integer getNombreAnneeAnciennete() {
        if(this.dateEmbauche == null || dateEmbauche.isAfter(LocalDate.now())){
            return null;
        }
        return LocalDate.now().getYear() - dateEmbauche.getYear();
    }

    public Integer getNbConges() {
        return Entreprise.NB_CONGES_BASE + this.getNombreAnneeAnciennete();
    }


    public Integer getNbRtt(){
        return getNbRtt(LocalDate.now());
    }

    /**
     * Méthode permettant de calculer le nombre de jour de RTT dans l'année (au pro-rata du taux d'activité de l'employé)
     * selon la formule :
     * Nb jours RTT =
     * Nombre de jours dans l'année
     * - Nombre de jours travaillés dans l'année en plein temps
     * - Nombre de samedi et dimanche dans l'année
     * - Nombre de jours fériés ne tombant pas le week-end
     * - Nombre de congés payés
     *
     * @param dateReference la date à laquelle on va calculer le nombre de RTT pour l'année
     * @return Nombre de jours de RTT pour l'employé l'année de la date de référence
     * au prorata du temps d'activité
     */
    public Integer getNbRtt(LocalDate dateReference) {
        int nbJoursAnnee = dateReference.isLeapYear() ? 366 : 365;
        int nbSamediDimanche = 104;
        switch (LocalDate.of(dateReference.getYear(), 1, 1).getDayOfWeek()) {
            case THURSDAY:
                if (dateReference.isLeapYear()) {
                    nbSamediDimanche = nbSamediDimanche + 1;
                }
                break;
            case FRIDAY:
                if (dateReference.isLeapYear()) {
                    nbSamediDimanche = nbSamediDimanche + 2;
                }
                else {
                nbSamediDimanche = nbSamediDimanche + 1;
                }
            break;
            case SATURDAY:
                nbSamediDimanche = nbSamediDimanche + 1;
                break;
            default:
                break;
        }
        //Entreprise.joursFeries => ressorts tout les jours fériés d'une année
        //Calcul du nombre de jours férié de l'année ne tombant pas un week-end
        int nbJoursFeriesSemaine = (int) Entreprise.joursFeries(dateReference).stream().filter(localDate ->
                localDate.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()).count();
        //Lambda

        return (int) Math.ceil((
                nbJoursAnnee
                        - Entreprise.NB_JOURS_MAX_FORFAIT
                        - nbSamediDimanche
                        - Entreprise.NB_CONGES_BASE
                        - nbJoursFeriesSemaine
        ) * tempsPartiel);
    }

    /**
     * Calcul de la prime annuelle selon la règle :
     * Pour les managers : Prime annuelle de base bonnifiée par l'indice prime manager
     * Pour les autres employés, la prime de base plus éventuellement la prime de performance calculée si l'employé
     * n'a pas la performance de base, en multipliant la prime de base par un l'indice de performance
     * (égal à la performance à laquelle on ajoute l'indice de prime de base)
     *
     * Pour tous les employés, une prime supplémentaire d'ancienneté est ajoutée en multipliant le nombre d'année
     * d'ancienneté avec la prime d'ancienneté. La prime est calculée au pro rata du temps de travail de l'employé
     *
     * @return la prime annuelle de l'employé en Euros et cents
     */
    //Matricule, performance, date d'embauche, temps partiel, prime
    public Double getPrimeAnnuelle(){
        //Calcule de la prime d'ancienneté
        Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
        Double prime;
        //Prime du manager (matricule commençant par M) : Prime annuelle de base multipliée par l'indice prime manager
        //plus la prime d'anciennté.
        if(matricule != null && matricule.startsWith("M")) {
            prime = Entreprise.primeAnnuelleBase() * Entreprise.INDICE_PRIME_MANAGER + primeAnciennete;
        }
        //Pour les autres employés en performance de base, uniquement la prime annuelle plus la prime d'ancienneté.
        else if (this.performance == null || Entreprise.PERFORMANCE_BASE.equals(this.performance)){
            prime = Entreprise.primeAnnuelleBase() + primeAnciennete;
        }
        //Pour les employés plus performance, on bonnifie la prime de base en multipliant par la performance de l'employé
        // et l'indice de prime de base.
        else {
            prime = Entreprise.primeAnnuelleBase() * (this.performance + Entreprise.INDICE_PRIME_BASE) + primeAnciennete;
        }

        if(this.tempsPartiel < 0){
            return null;
        }

        //Au pro rata du temps partiel.
        return prime * this.tempsPartiel;
    }

    //Augmenter salaire
    /**
     * Calcul de l'augmentation du salaire d'un employé.
     *
     * Cette méthode augmente tout simplement le salaire de l'employé selon un pourcentage passé en paramètres.
     *
     * @param pourcentage le pourcentage d'augmentation du salaire de l'employe
     *
     */
    public double augmenterSalaire(double pourcentage){
        if(pourcentage <= 0){
            return this.getSalaire();
        } else if (this.getSalaire() == null || this.getSalaire() <= Entreprise.SALAIRE_BASE){
            this.setSalaire(Entreprise.SALAIRE_BASE);
        }
        this.salaire = this.getSalaire()+(this.getSalaire()*(pourcentage/100d));
        this.salaire = (double) Math.round(this.salaire*100)/100;
        return this.salaire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public Employe setNom(String nom) {
        this.nom = nom;
        return this;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return the matricule
     */
    public String getMatricule() {
        return matricule;
    }

    /**
     * @param matricule the matricule to set
     */
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    /**
     * @return the dateEmbauche
     */
    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    /**
     * @param dateEmbauche the dateEmbauche to set
     */
    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    /**
     * @return the salaire
     */
    public Double getSalaire() {
        return salaire;
    }

    /**
     * @param salaire the salaire to set
     */
    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Double getTempsPartiel() {
        return tempsPartiel;
    }

    public void setTempsPartiel(Double tempsPartiel) {
        this.tempsPartiel = tempsPartiel;
    }

    @Override
    public String toString() {
        return "Employe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", matricule='" + matricule + '\'' +
                ", dateEmbauche=" + dateEmbauche +
                ", salaire=" + salaire +
                ", performance=" + performance +
                ", tempsPartiel=" + tempsPartiel +
                '}';
    }

}
