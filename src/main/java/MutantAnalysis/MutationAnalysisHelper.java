package MutantAnalysis;

import DynamoDBAccess.DynamoDBEnhanced;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class MutationAnalysisHelper {

    private int _mutations;
    private String[] _dna;
    private boolean _isValidDna;
    private boolean _isMutant;
    private int _n;
    final static Logger logger =
            LogManager.getLogger(DynamoDBEnhanced.class.getName());

    private boolean IsMutantDNA(int mutationsCount) {
        return mutationsCount > 1;
    }

    private boolean IsValidKey(char c){
        return c == 'T' || c == 'G' || c == 'C' || c == 'A';
    }

    private boolean checkForward(int x, int y, String[] dna){
        if(dna[y].charAt(x) == dna[y].charAt(x+1)
            && dna[y].charAt(x)== dna[y].charAt(x+2)
            && dna[y].charAt(x)== dna[y].charAt(x+3)) {

            return IsMutantDNA(++this._mutations);
        }
        return false;
    }

    private boolean checkDownward(int x, int y, String[] dna){
        if(dna[y].charAt(x) == dna[y+1].charAt(x)
            && dna[y].charAt(x)== dna[y+2].charAt(x)
            && dna[y].charAt(x)== dna[y+3].charAt(x)) {

            return IsMutantDNA(++this._mutations);
        }
        return false;
    }

    private boolean checkDiagFrontDown(int x, int y, String[] dna){
        if(dna[y].charAt(x) == dna[y+1].charAt(x+1)
            && dna[y].charAt(x)== dna[y+2].charAt(x+2)
            && dna[y].charAt(x)== dna[y+3].charAt(x+3)) {

            return IsMutantDNA(++this._mutations);
        }
        return false;
    }

    private boolean checkDiagFrontUp(int x, int y, String[] dna){
        if(dna[y].charAt(x) == dna[y-1].charAt(x+1)
            && dna[y].charAt(x)== dna[y-2].charAt(x+2)
            && dna[y].charAt(x)== dna[y-3].charAt(x+3)) {

            return IsMutantDNA(++this._mutations);
        }
        return false;
    }
    private void processADN() {
        int x = 0;
        this._mutations = 0;

        while(!_isMutant && x  < _n){
            int y = 0;

            while(y < _n) {
                if(_dna[y].length() != _n || !IsValidKey(_dna[y].charAt(x))){
                    this._isValidDna = false;
                    _isMutant = false;
                    return;
                }
                if(!_isMutant){
                    try{
                        //se realizan chequeos en base al caracter en la posición dna[y, x]
                        //chequeo si se puede validar "hacia adelante"
                        if(x < _n -3) {
                            _isMutant = checkForward(x, y, _dna);
                            //chequeo si se puede validar "hacia abajo"
                            if(!_isMutant && y < _n -3) {
                                _isMutant = checkDiagFrontDown(x,y,_dna);
                            }

                            //chequeo si se puede validar hacia arriba
                            if(!_isMutant && y > 3) {
                                _isMutant = checkDiagFrontUp(x,y,_dna);
                            }
                        }

                        //chequeo si puedo validar verticalmente hacia abajo
                        if(!_isMutant && y < _n -3) {
                            _isMutant = checkDownward(x,y,_dna);
                        }
                    }
                    catch(ArrayIndexOutOfBoundsException exception){
                        this._isValidDna = false;
                        _isMutant = false;
                        return;
                    }
                    catch(Exception ex) {
                        logger.fatal("An exception occurred: " + ex);
                        throw ex;
                    }
                }
                y++;
            }
            x++;
        }
    }

    public MutationAnalysisHelper(String[] dna){
        this._dna = dna;
        _isValidDna = dna.length>0;
        _n = dna.length;

        processADN();
    }

    public boolean IsMutant() { return _isMutant;}

    public boolean IsValidDna() {
        return _isValidDna;
    }


}
