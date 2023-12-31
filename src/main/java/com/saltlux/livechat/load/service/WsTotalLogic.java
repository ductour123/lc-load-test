package com.saltlux.livechat.load.service;

import com.saltlux.livechat.load.config.LivechatConfig;
import com.saltlux.livechat.load.config.LivechatEndpoint;
import com.saltlux.livechat.load.config.TestConfig;
import com.saltlux.livechat.load.thread.WsCheckThreadAliveCount;
import com.saltlux.livechat.load.thread.WsWhileSingleThread;
import com.saltlux.livechat.load.util.SystemUtil;
import com.saltlux.livechat.load.util.WsSessionUtil;
import com.saltlux.livechat.load.vo.ResponseInfo;
import com.saltlux.livechat.load.vo.WsResultShare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
public class WsTotalLogic {

    private final WsSessionUtil wsRest;
    private final TestConfig testConfig;
    private final LivechatConfig livechatConfig;

    private WsTotalLogic(WsSessionUtil wsRest, TestConfig testConfig, LivechatConfig livechatConfig, LivechatEndpoint livechatEndpoint){
        this.wsRest = wsRest;
        this.wsRest.livechatEndpoint = livechatEndpoint;
        this.wsRest.testConfig = testConfig;
        this.testConfig = testConfig;
        this.livechatConfig = livechatConfig;
    }

    private ExecutorService singleExecutor = null;
    private ExecutorService singleExecutorForIncrease = null;
    private ExecutorService checkThreadAliveCount = null;
    private ExecutorService executor = null;

    private WsWhileSingleThread wst = null;
    private WsCheckThreadAliveCount chk = null;


    public void startPress() {

        System.out.println("------------");
        System.out.printf("CCU: %s\n", testConfig.getMaxCCU());
        System.out.printf("Duration: %s m\n", testConfig.getDuration());
        System.out.printf("Server: %s\n", livechatConfig.getUrlConnectWs());
        System.out.printf("livechatId: %s\n", testConfig.getLivechatId());
        System.out.printf("Dataset: %s\n", testConfig.getDataFile());

        StopWatch sw = StopWatch.createStarted();
        WsResultShare resultShare = new WsResultShare(testConfig.getMaxCCU(), sw);

        List<List<String>> dataSet = this.readDataSetRandom(2000, 20);
        List<Future<List<ResponseInfo>>> futureList = new ArrayList<Future<List<ResponseInfo>>>();

        this.singleExecutor = Executors.newSingleThreadExecutor();
        this.singleExecutorForIncrease = Executors.newSingleThreadExecutor();
        this.checkThreadAliveCount = Executors.newSingleThreadExecutor();
        this.executor = Executors.newScheduledThreadPool(testConfig.getMaxCCU());

        try {
            this.wsRest.createWsSession();
            this.readSessionData();
        } catch (Exception e) {
            System.out.print("--- exception when create Websocket session: " + e.getMessage());
        }

        wst = new WsWhileSingleThread(executor, futureList, wsRest, dataSet, testConfig.getMaxCCU(), resultShare);
        singleExecutor.submit(wst);

        chk = new WsCheckThreadAliveCount(this.executor, resultShare);
        checkThreadAliveCount.submit(chk);

        this.StopAllThread(testConfig.getDuration());

        // close websocket
        log.info("===========> close Ws Session");
        this.wsRest.closeWsSession();

        sw.stop();
        resultShare.printReport();
    }

    private void StopAllThread(int stopMinute) {
        // 모든 쓰레드 동작 종료시까지 대기
        try {
            SystemUtil.sleep(TimeUnit.MINUTES, stopMinute);

            System.out.println("============> Stop all thread and wait completed");
            this.wst.stop();
            this.chk.stop();

            this.singleExecutor.shutdown();
            this.singleExecutorForIncrease.shutdown();
            this.checkThreadAliveCount.shutdown();
            this.executor.shutdown();
//            try {
//                if (!this.singleExecutor.awaitTermination(5, TimeUnit.MINUTES)) {
//                    this.singleExecutor.shutdownNow();
//                }
//                if (!this.singleExecutorForIncrease.awaitTermination(10, TimeUnit.SECONDS)) {
//                    this.singleExecutorForIncrease.shutdownNow();
//                }
//                if (!this.checkThreadAliveCount.awaitTermination(10, TimeUnit.SECONDS)) {
//                    this.checkThreadAliveCount.shutdownNow();
//                }
//                if (!this.executor.awaitTermination(5, TimeUnit.MINUTES)) {
//                    this.executor.shutdownNow();
//                }
//
//            } catch (InterruptedException ex) {
//                singleExecutor.shutdownNow();
//                singleExecutorForIncrease.shutdownNow();
//                checkThreadAliveCount.shutdownNow();
//                executor.shutdownNow();
//                Thread.currentThread().interrupt();
//            }


            while (!this.singleExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                SystemUtil.sleep(TimeUnit.MILLISECONDS, 100);
            }

            while (!this.singleExecutorForIncrease.awaitTermination(10, TimeUnit.SECONDS)) {
                SystemUtil.sleep(TimeUnit.MILLISECONDS, 100);
            }

            while (!this.checkThreadAliveCount.awaitTermination(10, TimeUnit.SECONDS)) {
                SystemUtil.sleep(TimeUnit.MILLISECONDS, 100);
            }

            while (!this.executor.awaitTermination(10, TimeUnit.SECONDS)) {
                SystemUtil.sleep(TimeUnit.MILLISECONDS, 100);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("============> All task completed");
        }
    }

    @SuppressWarnings("resource")
    private List<List<String>> readDataSet() {
        List<List<String>> result = new ArrayList<List<String>>();
        try {
            InputStream file = this.getClass().getClassLoader().getResourceAsStream("dataset_vi_3.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            int rowindex=0;
            int columnindex=0;
            XSSFSheet sheet=workbook.getSheetAt(0);
            int rows=sheet.getPhysicalNumberOfRows();
            for(rowindex=0;rowindex<rows;rowindex++){
                XSSFRow row=sheet.getRow(rowindex);
                if(row !=null){
                    List<String> tempArray = new ArrayList<String>();
                    int cells=row.getPhysicalNumberOfCells();
                    for(columnindex=0; columnindex<=cells; columnindex++){
                        XSSFCell cell=row.getCell(columnindex);
                        if(cell == null){
                            continue;
                        }else if(cell.getStringCellValue().length() > 0){
                            tempArray.add(cell.getStringCellValue());
                        }
                    }
                    if (!tempArray.isEmpty()) {
                        result.add(tempArray);
                    }
                }
            }

        }catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<List<String>> readDataSetRandom(int nConversation, int maxTurn) {
        List<List<String>> result = new ArrayList<List<String>>();
        try {
            File file = new File(testConfig.getDataFile());
            List<String> sentenceLst = FileUtils.readLines(file, StandardCharsets.UTF_8);
            int totalSentence = sentenceLst.size() - 1;

            for(int i=0; i<nConversation; i++) {
                List<String> sentences = new ArrayList<>();
                int numTurn = SystemUtil.getRandomNumber(10, maxTurn);
                for(int j=0;j<numTurn;j++) {
                    int rdSentIdx = SystemUtil.getRandomNumber(0, totalSentence);
                    sentences.add(sentenceLst.get(rdSentIdx).trim());
                }
                result.add(sentences);
            }

        }catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void readSessionData() {

        try {
            // read user-agents
            File file = new File(testConfig.getUserAgentsDataFile());
            this.wsRest.userAgentsData = FileUtils.readLines(file, StandardCharsets.UTF_8);

            // read os
            File file1 = new File(testConfig.getOsDataFile());
            this.wsRest.osData = FileUtils.readLines(file1, StandardCharsets.UTF_8);

            // read divices
            File file2 = new File(testConfig.getDivicesDataFile());
            this.wsRest.devicesData =  FileUtils.readLines(file2, StandardCharsets.UTF_8);

            // read cities
            File file3 = new File(testConfig.getCitiesDataFile());
            this.wsRest.citiesData =  FileUtils.readLines(file3, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    public static void main(String []args) {

    }

}
