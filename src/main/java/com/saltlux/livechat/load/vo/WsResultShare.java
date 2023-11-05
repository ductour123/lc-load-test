package com.saltlux.livechat.load.vo;

import com.saltlux.livechat.load.util.SystemUtil;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WsResultShare {
    private final int ccu;
    private final AtomicInteger rpCnt;
    private final AtomicInteger rqCnt;
    private final AtomicInteger beforeReqAgentRqCnt;
    private final AtomicInteger errCnt;

    @Getter
    private final WsResultShare.OpenedConversation openedConversation = new WsResultShare.OpenedConversation();

    @Getter
    public final List<Long> converationTimeLst = Collections.synchronizedList(new ArrayList<>());

    @Getter
    public final List<Integer> conversationMsgTotalLst = Collections.synchronizedList(new ArrayList<>());

    private final ConcurrentHashMap<String, Integer> errorMap = new ConcurrentHashMap<>();

    private final StopWatch sw;
    private final Date startTime;

    public WsResultShare(int ccu, StopWatch sw) {
        this.startTime = new Date();
        this.rpCnt = new AtomicInteger(0);
        this.rqCnt = new AtomicInteger(0);
        this.beforeReqAgentRqCnt = new AtomicInteger(0);
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
    public int addBeforeReqAgentRqCnt(int number) { return beforeReqAgentRqCnt.addAndGet(number); }
    public int addErr(int number){
        return errCnt.addAndGet(number);
    }

    public void print(){

        System.out.printf("Run time: %s\n", SystemUtil.toHumanReadableTimeFormat(this.sw.getTime()));
        this.openedConversation.print();
        System.out.printf("Total request : %s\n", rqCnt);
        System.out.printf("Total responses : %s\n", rpCnt);
//        System.out.printf("\tSuccess: %s (%s%%)\n", rpCnt, SystemUtil.calculatePercentage(rpCnt.get(), rqCnt.get()));
//        System.out.printf("\tFailed: %s (%s%%)\n", errCnt, SystemUtil.calculatePercentage(errCnt.get(), rqCnt.get()));


        if(!errorMap.isEmpty()) {
            errorMap.forEach((eN, count) -> {
                System.out.printf("\t\t%s: %s\n", count, eN);
            });
        }
    }

    public void printReport() {

        // Định dạng theo log CPU RAM trên server "2023/05/16-20:02:18"
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Đặt múi giờ Hàn Quốc
//        String timezone = "Asia/Seoul";
        String timezone = "Asia/Ho_Chi_Minh";
        TimeZone koreaTimeZone = TimeZone.getTimeZone(timezone);
        formatter.setTimeZone(koreaTimeZone);

        String startTimeKR = formatter.format(startTime);
        String endtTimeKR = formatter.format(new Date());


        System.out.printf("CCU: %s\n", this.ccu);
        System.out.printf("Start time: %s\n", startTimeKR);
        System.out.printf("End time: %s\n", endtTimeKR);
        System.out.printf("Run time: %s\n", SystemUtil.toHumanReadableTimeFormat(this.sw.getTime()));
        this.openedConversation.print();
        System.out.println("Time to run a conversation:");
        System.out.printf(" - Min: %s\n", SystemUtil.toHumanReadableTimeFormat(Collections.min(converationTimeLst)));
        System.out.printf(" - Max: %s\n", SystemUtil.toHumanReadableTimeFormat(Collections.max(converationTimeLst)));
        System.out.printf(" - Avg: %s\n", SystemUtil.toHumanReadableTimeFormat((long)converationTimeLst.stream().mapToLong(l -> l).average().orElse(0.0)));
        System.out.println("Messages Send:");
        System.out.println((" - Total messages: " + rqCnt));
        System.out.println((" - Total messages before Request Agent: " + beforeReqAgentRqCnt));
        System.out.println(" - Total responses : " + rpCnt);
        System.out.printf(" - Min: %s\n", Collections.min(conversationMsgTotalLst));
        System.out.printf(" - Max: %s\n", Collections.max(conversationMsgTotalLst));
        System.out.printf(" - Avg: %s\n", (int)conversationMsgTotalLst.stream().mapToInt(i -> i).average().orElse(0.0));

    }

    public void increaseOpenConversationTotal() {
        this.openedConversation.total.incrementAndGet();
    }

    public void increaseOpenConversation(boolean isSuccess) {
        this.openedConversation.total.incrementAndGet();
        if(isSuccess) this.openedConversation.success.incrementAndGet();
        else this.openedConversation.failed.incrementAndGet();
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
            System.out.printf("Opened Conversation: %s\n", total);
            System.out.printf(" - Success: %s ~ %s%%\n", success, SystemUtil.calculatePercentage(success.get(), total.get()));
//            System.out.printf("phep tru: %s - %s = %s\n", total.get(), success.get(), total.get() - success.get());
            System.out.printf(" - Failed: %s ~ %s%%\n", failed, SystemUtil.calculatePercentage(failed.get(), total.get()));
        }
    }
}
