package com.virtual.app.sicbo.module.controllers;

import com.virtual.app.sicbo.module.common.concrete.UserConfig;
import com.virtual.app.sicbo.module.data.Config;
import com.virtual.app.sicbo.module.data.GamesArchive;
import com.virtual.app.sicbo.module.data.Journal;
import com.virtual.app.sicbo.module.data.ProfitAndLose;
import com.virtual.app.sicbo.module.data.response.GameResultResponse;
import com.virtual.app.sicbo.module.data.response.GameResultStatus;
import com.virtual.app.sicbo.module.model.UserPrincipal;
import com.virtual.app.sicbo.module.response.JournalResponse;
import com.virtual.app.sicbo.module.services.impl.GamesArchiveService;
import com.virtual.app.sicbo.module.services.impl.JournalServiceImpl;
import com.virtual.app.sicbo.module.services.impl.UserConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/journals")
public class JournalController {

    private static final Logger logger = LoggerFactory.getLogger(JournalController.class);
    private final JournalServiceImpl journalService;
    private final UserConfigService configService;
    private final GamesArchiveService gamesArchiveService;

    @Autowired
    public JournalController(JournalServiceImpl journalService, UserConfigService configService, GamesArchiveService gamesArchiveService) {
        this.journalService = journalService;
        this.configService = configService;
        this.gamesArchiveService = gamesArchiveService;
    }


    @GetMapping("/get-archive")
    public ResponseEntity<GameResultResponse> getArchive(@RequestParam("journalId") Long journalId) {
        // Check if journalId is null or less than or equal to zero
        if (journalId == null || journalId <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(new GameResultResponse("Invalid journalId provided", null, null, 0, 0, 0, null, false, 0));
        }

        Optional<GamesArchive> optionalGamesArchive = Optional.ofNullable(gamesArchiveService.findByJournalId(journalId));

        return optionalGamesArchive.map(gamesArchive -> {
            GameResultStatus gameResultStatus = new GameResultStatus(
                    gamesArchive.getHandCount(),
                    gamesArchive.getWins(),
                    gamesArchive.getLosses(),
                    gamesArchive.getProfit(),

                    gamesArchive.getPlayingUnits()
            );

            GameResultResponse response = new GameResultResponse(
                    gamesArchive.getMessage(),
                    gamesArchive.getSequence(),
                    gameResultStatus,
                    gamesArchive.getBaseBetUnit(),
                    gamesArchive.getSuggestedBetUnit(),
                    gamesArchive.getPlayingUnits(),
                    gamesArchive.getRecommendedBet(),

                    gamesArchive.getLossCounter()
            );
            response.setSkipState(gamesArchive.getSkipState());
            response.setRiskLevel(gamesArchive.getRiskLevel());
            response.setHandResult(gamesArchive.getHandResult());
            return ResponseEntity.ok(response);
        }).orElseGet(() -> ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GameResultResponse("Archive not found", null, null, 0, 0, 0, null, false, 0)));
    }


    @GetMapping("/dateCreated")
    public ResponseEntity<List<JournalResponse>> getJournalsByDateCreated(
            @RequestParam("dateCreated") LocalDate effectiveFrom) {


        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userUuid = userPrincipal.getUserUuid();


//        logger.info("User UUID: {}", userUuid);

        List<Journal> journals;
        journals = journalService.getJournalsByUserUuidAndDateCreated(userUuid, effectiveFrom);

        int dailyLimit = 100000;

        List<JournalResponse> journalResponses;


        // Create journal responses with a sequential shoe number, limited by dailyLimit
        journalResponses = IntStream.range(0, Math.min(journals.size(), dailyLimit))
                .mapToObj(i -> new JournalResponse(
                        journals.get(i).getJournalId(),
                        i + 1,
                        journals.get(i).getHand(),
                        journals.get(i).getProfit(),
                        journals.get(i).getWinLose(),
                        journals.get(i).getStrategy(),
                        journals.get(i).getDateCreated()))
                .collect(Collectors.toList());


        logger.info(userPrincipal.getUsername() + ": Journals fetched: {}", journals.size());
        return ResponseEntity.ok(journalResponses);
    }

    private int getDailyLimit() {

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userUuid = userPrincipal.getUserUuid();

        // Continue with your logic using userUuid
        return configService.getConfigsByUserUuid(userUuid).stream().filter(config -> config.getName().equals(UserConfig.DAILY_LIMIT.getValue())).map(Config::getValue).map(Integer::parseInt).findFirst().orElse(0);

    }

    @GetMapping("/profit-data")
    public ProfitAndLose getProfitData(@RequestParam("filterBy") String filterBy) {

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userUuid = userPrincipal.getUserUuid();


        // Get the list of total profit by date
        List<Object[]> totalProfitByDate  =  null;
        if(filterBy.equals("Day")){
            totalProfitByDate  = journalService.getTotalProfitByDate(userUuid);
        }else if(filterBy.equals("Week")){
            totalProfitByDate = journalService.getTotalProfitByWeek(userUuid);
        }

        // Initialize labels and data arrays
        String[] labels = new String[totalProfitByDate.size()];
        Integer[] data = new Integer[totalProfitByDate.size()];

        // Populate the arrays
        int index = 0;
        for (Object[] row : totalProfitByDate) {
            labels[index] = row[1].toString();           // Assuming row[1] is the date (created_date)
            data[index] = ((Number) row[0]).intValue();  // Assuming row[0] is the profit (total_profit)
            index++;
        }

        return new ProfitAndLose(Arrays.asList(labels), Arrays.asList(data));
    }

}
