package models;


public enum EtatLivre {

    DISP0NIBLE("disponible") {
        @Override
        public EtatLivre getNextState() {
            return RESERVE;
        }
    }, INSDIPONIBLE("indisponible") {
        @Override
        public EtatLivre getNextState() {
            return DISP0NIBLE;
        }
    }, RESERVE("reserve") {
        @Override
        public EtatLivre getNextState() {
            return INSDIPONIBLE;
        }
    };

    private String classeCss;

    public abstract EtatLivre getNextState();

    private EtatLivre(String classeCss){
       this.classeCss = classeCss;
    }

    public String getClasseCss(){
        return classeCss;
    }
}
