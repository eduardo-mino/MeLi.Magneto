package com.example.restservice;

import MutantAnalysis.DNA;
import DynamoDBAccess.DynamoDBEnhanced;
import MutantAnalysis.DnaStats;
import MutantAnalysis.MutationAnalysisHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MagnetoHiringController {

    @PostMapping("/mutant")
    public ResponseEntity<String> IsMutant(@RequestBody DNA dnaObject) {
        DynamoDBEnhanced db = new DynamoDBEnhanced();
        if(dnaObject!=null && dnaObject.dna!=null) {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(dnaObject.dna);
            if (helper.IsMutant()) {
                db.injectDynamoItem(dnaObject, "MutantDNA");
                return new ResponseEntity<String>(HttpStatus.OK);

            }
            else if(helper.IsValidDna()){
                db.injectDynamoItem(dnaObject, "HumanDNA");
                return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<String>("Invalid DNA", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/stats")
    public ResponseEntity<DnaStats> GetStats(){
        DynamoDBEnhanced db = new DynamoDBEnhanced();
        return new ResponseEntity<DnaStats>(db.GetStats("HumanDNA", "MutantDNA"), HttpStatus.OK);
    }
}