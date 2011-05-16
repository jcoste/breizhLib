package models;


public enum EtatLivre {
    DISP0NIBLE() {
        @Override
        public EtatLivre getNextState() {
            return RESERVE;
        }
    }, INSDIPONIBLE() {
        @Override
        public EtatLivre getNextState() {
            return DISP0NIBLE;
        }
    }, RESERVE() {
        @Override
        public EtatLivre getNextState() {
            return INSDIPONIBLE;
        }
    };

    public abstract EtatLivre getNextState();
}
