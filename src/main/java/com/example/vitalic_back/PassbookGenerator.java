package com.example.vitalic_back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.*;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
@Component
public class PassbookGenerator implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // SQL file path
    private static final String SQL_FILE_PATH = "src/main/resources/passbook_data.sql";

    // Function to generate random transaction amounts
    private int randomAmount(int minAmt, int maxAmt) {
        return (new Random().nextInt((maxAmt - minAmt) / 10 + 1) + minAmt / 10) * 10;
    }

    // 랜덤한 시간을 생성하는 메서드
    private LocalDateTime generateRandomTime(LocalDate date, boolean isFixed) {
        if (isFixed) {
            return date.atTime(15, 0); // 고정 지출 및 월급 입금 시각
        } else {
            Random random = new Random();
            int hour = random.nextInt(14) + 9; // 09:00 ~ 22:00 사이의 랜덤 시간
            int minute = random.nextInt(60); // 00 ~ 59 사이의 랜덤 분
            return date.atTime(hour, minute);
        }
    }

    // Function to insert generated SQL data into the database
    private void insertData() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/passbook_data.sql")))) {
            String line;
            StringBuilder sqlBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line);
                sqlBuilder.append("\n");
            }
            String sql = sqlBuilder.toString();
            String[] queries = sql.split(";");
            for (String query : queries) {
                if (!query.trim().isEmpty()) {
                    jdbcTemplate.execute(query);
                }
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        // Start date and end date for transactions
        LocalDate startDate = LocalDate.of(2022, 10, 2);
        LocalDate endDate = LocalDate.of(2024, 10, 2);
        LocalDate currentDate = startDate;

        // Initial account details
        int initialBalance = 4586400;
        int currentBalance = initialBalance;

        // Generate SQL data
        StringBuilder sqlQueries = new StringBuilder();
        sqlQueries.append("INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt)\n");
        sqlQueries.append("VALUES ('신한은행', '110490816690', ").append(initialBalance).append(", 0, '초기잔액', '내 신한은행 110490816690', NOW(), 0, 0, ").append(initialBalance).append(");\n");

        // Generate transactions for each day
        while (!currentDate.isAfter(endDate)) {
            // Monthly fixed transactions
            if (currentDate.getDayOfMonth() == 5) {
                int tranAmt = randomAmount(50000, 100000);
                LocalDateTime tranDateTime = generateRandomTime(currentDate, true);
                sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, "신한체크 교통", "내 신한은행 110490816690", tranDateTime, 0, tranAmt));
            }

            if (currentDate.getDayOfMonth() == 10) {
                LocalDateTime tranDateTime = generateRandomTime(currentDate, true);
                sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, "유튜브 프리미엄", "내 신한은행 110490816690", tranDateTime, 0, 20400));
            }

            if (currentDate.getDayOfMonth() == 15) {
                LocalDateTime tranDateTime = generateRandomTime(currentDate, true);
                sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 0, "대림대학교", "내 신한은행 110490816690", tranDateTime, 0, 2000000));
                sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, "넷플릭스", "내 신한은행 110490816690", tranDateTime, 0, 13500));
                sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, "KB손해보험", "내 신한은행 110490816690", tranDateTime, 0, 50000));
            }

            if (currentDate.getDayOfMonth() == 20) {
                LocalDateTime tranDateTime = generateRandomTime(currentDate, true);
                sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, "SKT통신", "내 신한은행 110490816690", tranDateTime, 0, 32000));
                sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, "신한 적금", "내 신한은행 110490816690", tranDateTime, 0, 300000));
            }

            // Generate random transactions (4 ~ 7개 사이)
            for (int i = 0; i < 4 + new Random().nextInt(4); i++) {
                // Random deposits
                if (Math.random() < 0.2) {
                    int tranAmt = randomAmount(10000, 80000);
                    String personName = "김" + getRandomName();
                    LocalDateTime randomTranDateTime = generateRandomTime(currentDate, false);
                    sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 0, personName + " 이체", "내 신한은행 110490816690", randomTranDateTime, 0, tranAmt));
                }

                // Random withdrawals
                if (Math.random() < 0.15) {
                    int tranAmt = randomAmount(10000, 70000);
                    String personName = "박" + getRandomName();
                    LocalDateTime randomTranDateTime = generateRandomTime(currentDate, false);
                    sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, "내 신한은행 110490816690", personName + " 이체", randomTranDateTime, 0, tranAmt));
                }

                // Consumer spending
                if (Math.random() < 0.25) {
                    int tranAmt = randomAmount(2000, 10000);
                    String store = getRandomStore();
                    LocalDateTime randomTranDateTime = generateRandomTime(currentDate, false);
                    sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, store, "내 신한은행 110490816690", randomTranDateTime, 1, tranAmt));
                }

                if (Math.random() < 0.25) {
                    int tranAmt = randomAmount(3000, 15000);
                    String cafe = getRandomCafe();
                    LocalDateTime randomTranDateTime = generateRandomTime(currentDate, false);
                    sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, cafe, "내 신한은행 110490816690", randomTranDateTime, 1, tranAmt));
                }

                // Additional consumer categories
                if (Math.random() < 0.1) {
                    int tranAmt = randomAmount(5000, 10000);
                    String fastfood = getRandomFastFood();
                    LocalDateTime randomTranDateTime = generateRandomTime(currentDate, false);
                    sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, fastfood, "내 신한은행 110490816690", randomTranDateTime, 1, tranAmt));
                }

                if (Math.random() < 0.1) {
                    int tranAmt = randomAmount(8000, 20000);
                    String restaurant = getRandomRestaurant();
                    LocalDateTime randomTranDateTime = generateRandomTime(currentDate, false);
                    sqlQueries.append(createTransaction("신한은행", "110490816690", currentBalance, 1, restaurant, "내 신한은행 110490816690", randomTranDateTime, 1, tranAmt));
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        // Write SQL queries to the file
        try (FileWriter fileWriter = new FileWriter(SQL_FILE_PATH)) {
            fileWriter.write(sqlQueries.toString());
        }

        // Insert data into the database
        insertData();
    }

    // Create formatted transaction entry
    private String createTransaction(String bankName, String accountNumber, int balanceAmt, int inoutType, String inDes, String outDes, LocalDateTime tranDateTime, int tranType, int tranAmt) {
        int afterBalanceAmt = inoutType == 0 ? balanceAmt + tranAmt : balanceAmt - tranAmt;
        return String.format("INSERT INTO Passbook (bank_name, account_number, balance_amt, inout_type, in_des, out_des, tran_date_time, tran_type, tran_amt, after_balance_amt) VALUES ('%s', '%s', %d, %d, '%s', '%s', '%s', %d, %d, %d);\n",
                bankName, accountNumber, balanceAmt, inoutType, inDes, outDes, tranDateTime, tranType, tranAmt, afterBalanceAmt);
    }

    // Generate random names
    private String getRandomName() {
        String[] names = {"수민", "서연", "민준", "지민", "예진"};
        return names[new Random().nextInt(names.length)];
    }

    // Get random store names
    private String getRandomStore() {
        String[] stores = {"CU", "GS25", "7-Eleven", "이마트", "홈플러스"};
        return stores[new Random().nextInt(stores.length)];
    }

    // Get random cafe names
    private String getRandomCafe() {
        String[] cafes = {"스타벅스", "이디야", "커피빈", "빽다방", "할리스", "컴포즈 커피", "메가커피"};
        return cafes[new Random().nextInt(cafes.length)];
    }

    // Get random fast food names
    private String getRandomFastFood() {
        String[] fastFoods = {"맥도날드", "버거킹", "롯데리아", "KFC", "파파이스"};
        return fastFoods[new Random().nextInt(fastFoods.length)];
    }

    // Get random restaurant names
    private String getRandomRestaurant() {
        String[] restaurants = {"김밥천국", "참치전문", "중화요리", "일식집", "이탈리안 레스토랑"};
        return restaurants[new Random().nextInt(restaurants.length)];
    }
}