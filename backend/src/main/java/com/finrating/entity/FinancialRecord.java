package com.finrating.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "dataset_id")
    private Dataset dataset;
    
    private String issuerName;
    private String industry;
    private String country;
    
    private BigDecimal revenue;
    private BigDecimal ebitda;
    private BigDecimal totalDebt;
    private BigDecimal interestExpense;
    private BigDecimal currentAssets;
    private BigDecimal currentLiabilities;
    
    private BigDecimal debtToEbitda;
    private BigDecimal interestCoverageRatio;
    private BigDecimal liquidityCoverageRatio;
    private BigDecimal revenueStabilityScore;
    
    @Enumerated(EnumType.STRING)
    private CreditRating rating;
    
    @Enumerated(EnumType.STRING)
    private RatingCategory category;
    
    private LocalDateTime calculatedAt;
    
    public enum CreditRating {
        AAA, AA_PLUS, AA, AA_MINUS,
        A_PLUS, A, A_MINUS,
        BBB_PLUS, BBB, BBB_MINUS,
        BB_PLUS, BB, BB_MINUS,
        B_PLUS, B, B_MINUS,
        CCC_PLUS, CCC, CCC_MINUS,
        CC, C, D
    }
    
    public enum RatingCategory {
        INVESTMENT_GRADE, SPECULATIVE, DISTRESSED
    }
}
