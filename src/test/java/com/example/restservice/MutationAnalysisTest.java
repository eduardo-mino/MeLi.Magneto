package com.example.restservice;

import static org.junit.jupiter.api.Assertions.*;

import MutantAnalysis.MutationAnalysisHelper;
import org.junit.jupiter.api.Test;

class MutationAnalysisTest {

    @Test
    void testValidMutantDNA() {
        String[] dna = new String[]{"TGACTG", "GCCATA", "TGGGGC", "CTAATC", "GGTCGA", "AGGTGT"};

        try {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(dna);
            boolean isMutant = helper.IsMutant();
            assertTrue(isMutant, "Return true for Mutant DNA");
            boolean isValidDna = helper.IsValidDna();
            assertTrue(isValidDna, "Return true for valid DNA");
        } catch (Exception e) {
        }
    }

    @Test
    void testIsValidHumanDNA() {
        String[] dna = new String[]{"TGACTG", "GCCATA", "GTGGCG", "CTAATC", "GGGCGA", "AGGTGT"};

        try {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(dna);
            boolean isMutant = helper.IsMutant();
            assertFalse(isMutant, "Return false for Human DNA");
            boolean isValidDna = helper.IsValidDna();
            assertTrue(isValidDna, "Return true for a valid DNA");
        } catch (Exception e) {
        }
    }


    @Test
    void testEmptyDNA(){
        String[] emptyDNA = new String[]{};

        try {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(emptyDNA);
            boolean isMutant = helper.IsMutant();
            assertFalse(isMutant, "Return false for invalid empty DNA");
            boolean isValidDNA = helper.IsValidDna();
            assertFalse(isValidDNA, "Return false for invalid empty DNA");
        } catch (Exception e) {
        }
    }

    @Test
    void testInvalidGene(){
        //one of the ADN chain contains the character X
        String[] invalidDNA = new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "RCCXTA", "TCACTG"};

        try {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(invalidDNA);
            boolean isMutant = helper.IsMutant();
            assertFalse(isMutant, "Return false for invalid DNA");
            boolean isValidDNA = helper.IsValidDna();
            assertFalse(isValidDNA, "Return false for invalid DNA Gene");
        } catch (Exception e) {
        }
    }

    @Test
    void testInvalidArrayLength() {
        String[] invalidDNA = new String[]{"CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};

        try {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(invalidDNA);
            boolean isMutant = helper.IsMutant();
            assertFalse(isMutant, "Return false for invalid DNA");
            boolean isValidDNA = helper.IsValidDna();
            assertFalse(isValidDNA, "Return false for invalid DNA");
        } catch (Exception e) {
        }
    }

    @Test
    void testInvalidLongerString(){
        //badDna[5].length =7 insted of 6
        String[] invalidDNA = new String[]{"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTGG"};

        try {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(invalidDNA);
            boolean isMutant = helper.IsMutant();
            assertFalse(isMutant, "Return false for invalid DNA");
            boolean isValidDNA = helper.IsValidDna();
            assertFalse(isValidDNA, "Return false for invalid DNA");
        } catch (Exception e) {
        }
    }

    @Test
    void testInvalidShorterString(){
        //badDna[5].length = 5 insted of 6
        String[] invalidDNA = new String[]{"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACT"};

        try {
            MutationAnalysisHelper helper = new MutationAnalysisHelper(invalidDNA);
            boolean isMutant = helper.IsMutant();
            assertFalse(isMutant, "Return false for invalid DNA");
            boolean isValidDNA = helper.IsValidDna();
            assertFalse(isValidDNA, "Return false for invalid DNA");
        } catch (Exception e) {
        }
    }
}