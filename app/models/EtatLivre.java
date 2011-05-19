package models;


public enum EtatLivre {

    DISP0NIBLE("disponible", "models.EtatLivre.disponible") {
        @Override
        public EtatLivre getNextState() {
            return RESERVE;
        }
    },

    INSDIPONIBLE("indisponible", "models.EtatLivre.indisponible") {
        @Override
        public EtatLivre getNextState() {
            return DISP0NIBLE;
        }
    },

    RESERVE("reserve", "models.EtatLivre.reserve") {
        @Override
        public EtatLivre getNextState() {
            return INSDIPONIBLE;
        }
    };

    private String classeCss;
    private String localMessage;

    public abstract EtatLivre getNextState();

    private EtatLivre(String classeCss, String localMessage) {
        this.classeCss = classeCss;
        this.localMessage = localMessage;
    }

    public String getClasseCss() {
        return classeCss;
    }

    public String getLocalMessage() {
        return localMessage;
    }

    public static EtatLivre fromCss(String etat) {
        for (EtatLivre e : values()) {
            if (e.classeCss.equals(etat)) {
                return e;
            }
        }
        return null;
    }
}