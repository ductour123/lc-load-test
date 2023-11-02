package com.saltlux.livechat.load.vo;

import com.saltlux.livechat.load.util.RestTemplateUtil;
import com.saltlux.livechat.load.util.SystemUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ResultShare {
    private final int ccu;
    private final AtomicInteger rpCnt;
    private final AtomicInteger rqCnt;
    private final AtomicInteger errCnt;

    @Getter
    private final OpenedConversation openedConversation = new OpenedConversation();

    @Getter
    private final List<Long> resTimeLst = Collections.synchronizedList(new ArrayList<>());

    private final ConcurrentHashMap<String, Integer> errorMap = new ConcurrentHashMap<>();

    private final StopWatch sw;
    private final Date startTime;

    public ResultShare(int ccu, StopWatch sw) {
        this.startTime = new Date();
        this.rpCnt = new AtomicInteger(0);
        this.rqCnt = new AtomicInteger(0);
        this.errCnt = new AtomicInteger(0);
        this.ccu = ccu;
        this.sw = sw;
    }

    public int addRp(int number){
        return rpCnt.addAndGet(number);
    }
    public int addRq(int number){
        return rqCnt.addAndGet(number);
    }
    public int addErr(int number){
        return errCnt.addAndGet(number);
    }

    public void print(){

        System.out.printf("Run time: %s\n", SystemUtil.toHumanReadableTimeFormat(this.sw.getTime()));
        this.openedConversation.print();
        System.out.printf("Total request : %s\n", rqCnt);
        System.out.printf("\tSuccess: %s (%s%%)\n", rpCnt, SystemUtil.calculatePercentage(rpCnt.get(), rqCnt.get()));
        System.out.printf("\tFailed: %s (%s%%)\n", errCnt, SystemUtil.calculatePercentage(errCnt.get(), rqCnt.get()));


        if(!errorMap.isEmpty()) {
            errorMap.forEach((eN, count) -> {
                System.out.printf("\t\t%s: %s\n", count, eN);
            });
        }
    }

    public void printReport() {

        // Định dạng theo log CPU RAM trên server "2023/05/16-20:02:18"
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // Đặt múi giờ Hàn Quốc
//        String timezone = "Asia/Seoul";
        String timezone = "Asia/Ho_Chi_Minh";
        TimeZone koreaTimeZone = TimeZone.getTimeZone(timezone);
        formatter.setTimeZone(koreaTimeZone);

        String startTimeKR = formatter.format(startTime);
        String endtTimeKR = formatter.format(new Date());
        

        System.out.printf("CCU: %s\n", this.ccu);
        System.out.printf("Start time (TimeZone : %s): %s\n", timezone, startTimeKR);
        System.out.printf("End time (TimeZone : %s): %s\n", timezone, endtTimeKR);
        System.out.printf("Run time: %s\n", SystemUtil.toHumanReadableTimeFormat(this.sw.getTime()));
        this.openedConversation.print();
        System.out.println(("Total request : " + rqCnt));
        System.out.printf("\tSuccess: %s (%s%%)\n", rpCnt, SystemUtil.calculatePercentage(rpCnt.get(), rqCnt.get()));
        System.out.printf("\tFailed: %s (%s%%)\n", errCnt, SystemUtil.calculatePercentage(errCnt.get(), rqCnt.get()));

        if(!errorMap.isEmpty()) {
            errorMap.forEach((eN, count) -> {
                System.out.printf("\t\t%s: %s\n", count, eN);
            });
        }

        System.out.printf("Response time: %s response\n", resTimeLst.size());
        System.out.printf("\tMin: %s\n", SystemUtil.toHumanReadableTimeFormat(Collections.min(resTimeLst)));
        System.out.printf("\tMax: %s\n",  SystemUtil.toHumanReadableTimeFormat(Collections.max(resTimeLst)));
        System.out.printf("\tAvg: %s\n", SystemUtil.toHumanReadableTimeFormat((long)resTimeLst.stream().mapToLong(l -> l).average().orElse(0.0)));
        System.out.printf("\t0-4s: %s\n", resTimeLst.stream().filter(c -> c <= 4000).count());
        System.out.printf("\t4-7s: %s\n", resTimeLst.stream().filter(c -> c > 4000 && c <= 7000).count());
        System.out.printf("\t7-10s: %s\n", resTimeLst.stream().filter(c -> c > 7000 && c <= 10000).count());
        System.out.printf("\t10-15s: %s\n", resTimeLst.stream().filter(c -> c > 10000 && c <= 15000).count());
        System.out.printf("\t>15s: %s\n", resTimeLst.stream().filter(c -> c > 15000).count());

    }

    public void increaseOpenConversation(boolean isSuccess) {
        this.openedConversation.total.incrementAndGet();
        if(isSuccess) this.openedConversation.success.incrementAndGet();
        else this.openedConversation.failed.incrementAndGet();
    }

    public void addCallApiResult(RestTemplateUtil.CallResult e) {
        if(e.getStatus() == RestTemplateUtil.CallStatus.Success) {
            this.resTimeLst.add(e.getTook());
            // Tăng số lần thành công
            this.addRp(1);
        }

        if(e.getStatus() == RestTemplateUtil.CallStatus.Error) {
            // Tăng số lần thất bại
            this.addErr(1);

            if(null != e.getException()) {

                String exName = e.getException().getClass().getSimpleName();

                if(e.getException() instanceof HttpClientErrorException) {
                    exName += ": " +  ((HttpClientErrorException)e.getException()).getStatusCode().toString();
                } else if(e.getException() instanceof RestClientException) {
                    if(e.getException().getCause() != null)
                        exName += ": " + e.getException().getCause().getLocalizedMessage();
                }

                if(this.errorMap.containsKey(exName)) {
                    this.errorMap.put(exName, this.errorMap.get(exName) + 1);
                } else  {
                    this.errorMap.put(exName, 1);
                }
            }
        }
    }

    private String getErrorStr() {
        StringBuilder builder = new StringBuilder();

        if(!errorMap.isEmpty()) {
            errorMap.forEach((eN, count) -> {
                builder.append(eN).append(": ").append(count).append(", ");
            });
        }
        return builder.toString();
    }

    public void showErrorList() {
        errorMap.forEach((eN, count) -> {
            System.out.println(eN + ": " + count);
        });
    }

    @Data
    @NoArgsConstructor
    public static class OpenedConversation {
        private AtomicInteger total = new AtomicInteger(0);
        private AtomicInteger success = new AtomicInteger(0);
        private AtomicInteger failed = new AtomicInteger(0);

        public void print() {
            System.out.printf("Opened Conversation: %s|%s|%s\n", total, success, failed);
        }
    }
}
