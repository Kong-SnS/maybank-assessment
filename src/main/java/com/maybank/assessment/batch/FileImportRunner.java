package com.maybank.assessment.batch;

import com.maybank.assessment.entity.TransactionRecordEntity;
import com.maybank.assessment.repository.TransactionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileImportRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FileImportRunner.class);
    private final TransactionRecordRepository repository;

    @Value("${file.import.location:}")
    private String externalLocation;

    @Override
    public void run(String... args) throws Exception {
        Path sourcePath = null;

        if (externalLocation != null && !externalLocation.isBlank()) {
            sourcePath = Paths.get(externalLocation);
            if (Files.notExists(sourcePath)) {
                log.warn("Configured import file {} does not exist; will fall back to classpath", externalLocation);
                sourcePath = null;
            }
        }

        if (sourcePath == null) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("dataSource.txt")) {
                if (is == null) {
                    log.warn("No classpath dataSource.txt found; skipping import entirely.");
                    return;
                }
                List<TransactionRecordEntity> list = new BufferedReader(new InputStreamReader(is))
                        .lines().skip(1)
                        .map(line -> line.split("\\|"))
                        .map(p -> TransactionRecordEntity.builder()
                                .accountNumber(p[0])
                                .trxAmount(new BigDecimal(p[1]))
                                .description(p[2])
                                .trxDate(LocalDate.parse(p[3]))
                                .trxTime(LocalTime.parse(p[4]))
                                .customerId(p[5])
                                .build())
                        .collect(Collectors.toList());
                repository.saveAll(list);
                log.info("Imported {} records from embedded dataSource.txt", list.size());
            }
            return;
        }

        List<TransactionRecordEntity> records;
        try (BufferedReader reader = Files.newBufferedReader(sourcePath)) {
            records = reader.lines()
                    .skip(1)
                    .map(line -> line.split("\\|"))
                    .map(p -> TransactionRecordEntity.builder()
                            .accountNumber(p[0])
                            .trxAmount(new BigDecimal(p[1]))
                            .description(p[2])
                            .trxDate(LocalDate.parse(p[3]))
                            .trxTime(LocalTime.parse(p[4]))
                            .customerId(p[5])
                            .build())
                    .collect(Collectors.toList());
            repository.saveAll(records);
            log.info("Imported {} records from {}", records.size(), sourcePath);
        }

        Path parentDir = sourcePath.getParent();
        if (parentDir == null) {
            parentDir = Paths.get("").toAbsolutePath();
        }
        Path processedDir = parentDir.resolve("processed");
        if (Files.notExists(processedDir)) {
            Files.createDirectories(processedDir);
        }
        Path target = processedDir.resolve(sourcePath.getFileName());
        Files.move(sourcePath, target, StandardCopyOption.REPLACE_EXISTING);
        log.info("Moved {} → {}", sourcePath, target);
    }
}