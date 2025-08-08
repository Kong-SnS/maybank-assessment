package com.maybank.assessment.repository;

import com.maybank.assessment.entity.TransactionRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRecordRepository extends JpaRepository<TransactionRecordEntity, Long>, JpaSpecificationExecutor<TransactionRecordEntity> {

}
