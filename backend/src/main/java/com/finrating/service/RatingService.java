package com.finrating.service;

import com.finrating.entity.FinancialRecord;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RatingService {
    
    public void calculateRating(FinancialRecord record) {
        // Calculate financial ratios
        if (record.getEbitda() != null && record.getTotalDebt() != null && 
            record.getEbitda().compareTo(BigDecimal.ZERO) > 0) {
            record.setDebtToEbitda(
                record.getTotalDebt().divide(record.getEbitda(), 2, RoundingMode.HALF_UP)
            );
        }
        
        if (record.getInterestExpense() != null && record.getEbitda() != null && 
            record.getInterestExpense().compareTo(BigDecimal.ZERO) > 0) {
            record.setInterestCoverageRatio(
                record.getEbitda().divide(record.getInterestExpense(), 2, RoundingMode.HALF_UP)
            );
        }
        
        if (record.getCurrentAssets() != null && record.getCurrentLiabilities() != null && 
            record.getCurrentLiabilities().compareTo(BigDecimal.ZERO) > 0) {
            record.setLiquidityCoverageRatio(
                record.getCurrentAssets().divide(record.getCurrentLiabilities(), 2, RoundingMode.HALF_UP)
            );
        }
        
        // Simple rating algorithm based on key metrics
        int score = 0;
        
        // Debt to EBITDA (lower is better)
        if (record.getDebtToEbitda() != null) {
            if (record.getDebtToEbitda().compareTo(BigDecimal.valueOf(2)) < 0) score += 40;
            else if (record.getDebtToEbitda().compareTo(BigDecimal.valueOf(4)) < 0) score += 30;
            else if (record.getDebtToEbitda().compareTo(BigDecimal.valueOf(6)) < 0) score += 20;
            else score += 10;
        }
        
        // Interest Coverage (higher is better)
        if (record.getInterestCoverageRatio() != null) {
            if (record.getInterestCoverageRatio().compareTo(BigDecimal.valueOf(8)) > 0) score += 40;
            else if (record.getInterestCoverageRatio().compareTo(BigDecimal.valueOf(4)) > 0) score += 30;
            else if (record.getInterestCoverageRatio().compareTo(BigDecimal.valueOf(2)) > 0) score += 20;
            else score += 10;
        }
        
        // Liquidity Coverage (higher is better)
        if (record.getLiquidityCoverageRatio() != null) {
            if (record.getLiquidityCoverageRatio().compareTo(BigDecimal.valueOf(1.5)) > 0) score += 20;
            else if (record.getLiquidityCoverageRatio().compareTo(BigDecimal.ONE) > 0) score += 10;
        }
        
        // Assign rating based on score
        FinancialRecord.CreditRating rating;
        FinancialRecord.RatingCategory category;
        
        if (score >= 90) {
            rating = FinancialRecord.CreditRating.AAA;
            category = FinancialRecord.RatingCategory.INVESTMENT_GRADE;
        } else if (score >= 85) {
            rating = FinancialRecord.CreditRating.AA_PLUS;
            category = FinancialRecord.RatingCategory.INVESTMENT_GRADE;
        } else if (score >= 80) {
            rating = FinancialRecord.CreditRating.AA;
            category = FinancialRecord.RatingCategory.INVESTMENT_GRADE;
        } else if (score >= 75) {
            rating = FinancialRecord.CreditRating.A_PLUS;
            category = FinancialRecord.RatingCategory.INVESTMENT_GRADE;
        } else if (score >= 70) {
            rating = FinancialRecord.CreditRating.A;
            category = FinancialRecord.RatingCategory.INVESTMENT_GRADE;
        } else if (score >= 65) {
            rating = FinancialRecord.CreditRating.BBB_PLUS;
            category = FinancialRecord.RatingCategory.INVESTMENT_GRADE;
        } else if (score >= 60) {
            rating = FinancialRecord.CreditRating.BBB;
            category = FinancialRecord.RatingCategory.INVESTMENT_GRADE;
        } else if (score >= 55) {
            rating = FinancialRecord.CreditRating.BB_PLUS;
            category = FinancialRecord.RatingCategory.SPECULATIVE;
        } else if (score >= 50) {
            rating = FinancialRecord.CreditRating.BB;
            category = FinancialRecord.RatingCategory.SPECULATIVE;
        } else if (score >= 45) {
            rating = FinancialRecord.CreditRating.B_PLUS;
            category = FinancialRecord.RatingCategory.SPECULATIVE;
        } else if (score >= 40) {
            rating = FinancialRecord.CreditRating.B;
            category = FinancialRecord.RatingCategory.SPECULATIVE;
        } else if (score >= 30) {
            rating = FinancialRecord.CreditRating.CCC;
            category = FinancialRecord.RatingCategory.DISTRESSED;
        } else {
            rating = FinancialRecord.CreditRating.D;
            category = FinancialRecord.RatingCategory.DISTRESSED;
        }
        
        record.setRating(rating);
        record.setCategory(category);
    }
}
