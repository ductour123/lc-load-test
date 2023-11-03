package com.saltlux.livechat.load.service;

import com.saltlux.livechat.load.config.LivechatConfig;
import com.saltlux.livechat.load.config.TestConfig;
import com.saltlux.livechat.load.thread.CheckThreadAliveCount;
import com.saltlux.livechat.load.thread.WhileSingleThread;
import com.saltlux.livechat.load.util.RestTemplateUtil;
import com.saltlux.livechat.load.util.SystemUtil;
import com.saltlux.livechat.load.vo.ResponseInfo;
import com.saltlux.livechat.load.vo.ResultShare;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TotalLogic {

	private final RestTemplateUtil rest;
	private final TestConfig testConfig;
	private final LivechatConfig livechatConfig;

	private TotalLogic(RestTemplateUtil rest, TestConfig testConfig, LivechatConfig livechatConfig) {
		this.rest = rest;
		this.testConfig = testConfig;
		this.livechatConfig = livechatConfig;
	}
	
	private ExecutorService singleExecutor = null;
	private ExecutorService singleExecutorForIncrease = null;
	private ExecutorService checkThreadAliveCount = null;
	private ExecutorService executor = null;

	private WhileSingleThread wst = null;
	private CheckThreadAliveCount chk = null;


	public void startPress() {

		System.out.println("------------");
		System.out.printf("CCU: %s\n", testConfig.getMaxCCU());
		System.out.printf("Duration: %s m\n", testConfig.getDuration());
		System.out.printf("Server: %s\n", livechatConfig.getChatApi());
		System.out.printf("Bot: %s\n", livechatConfig.getBotId());
		System.out.printf("Dataset: %s\n", testConfig.getDataFile());

		StopWatch sw = StopWatch.createStarted();
		ResultShare resultShare = new ResultShare(testConfig.getMaxCCU(), sw);

		List<List<String>> dataSet = this.readDataSetRandom(2000, 20);
		List<Future<List<ResponseInfo>>> futureList = new ArrayList<Future<List<ResponseInfo>>>();

		this.singleExecutor = Executors.newSingleThreadExecutor();
		this.singleExecutorForIncrease = Executors.newSingleThreadExecutor();
		this.checkThreadAliveCount = Executors.newSingleThreadExecutor();
		this.executor = Executors.newScheduledThreadPool(testConfig.getMaxCCU());

		wst = new WhileSingleThread(executor, futureList, rest, dataSet, testConfig.getMaxCCU(), resultShare);
		singleExecutor.submit(wst);
		
		chk = new CheckThreadAliveCount(this.executor,resultShare);
		checkThreadAliveCount.submit(chk);
		
		this.StopAllThread(testConfig.getDuration());

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

			this.singleExecutor.shutdownNow();
			this.singleExecutorForIncrease.shutdownNow();
			this.checkThreadAliveCount.shutdownNow();
			this.executor.shutdownNow();


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

			System.out.println("============> All task completed");
		} catch (InterruptedException e) {
			e.printStackTrace();
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
				int numTurn = SystemUtil.getRandomNumber(5, maxTurn);
				for(int j=0;j<numTurn;j++) {
					int rdSentIdx = SystemUtil.getRandomNumber(0, totalSentence);
					sentences.add(sentenceLst.get(rdSentIdx));
				}
				result.add(sentences);
			}

		}catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String []args) {

	}
	
}
