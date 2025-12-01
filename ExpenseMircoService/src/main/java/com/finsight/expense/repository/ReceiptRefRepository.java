package com.finsight.expense.repository;

import com.finsight.expense.model.ReceiptRef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceiptRefRepository extends JpaRepository<ReceiptRef, UUID> {

}
