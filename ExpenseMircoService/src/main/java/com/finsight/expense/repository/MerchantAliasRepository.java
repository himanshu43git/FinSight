package com.finsight.expense.repository;

import com.finsight.expense.model.MerchantAlias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantAliasRepository extends JpaRepository<MerchantAlias, UUID> {
    Optional<MerchantAlias> findByNormalizedName(String normalized);
}
