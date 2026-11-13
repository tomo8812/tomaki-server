package maki.millenairelod;
public final class ReplayBudgetSelfTest {
    public static void main(String[] args){
        long positions=0;
        for(int x=-256;x<=256;x++)for(int z=-256;z<=256;z++)if(x*x+z*z<=256*256)positions++;
        check(positions==205861,"current 4096-block circle's chunk-grid candidate count");
        check(ReplayBudget.QUEUE_LIMIT>positions,"one full pregen area fits in each native queue");
        check(ReplayBudget.mainCandidates(0,0)==64,"one native inspection batch per second");
        check(ReplayBudget.mainCandidates((int)positions,0)==64,"an entire pending pregen area does not stop other ready candidates");
        check(ReplayBudget.mainCandidates(ReplayBudget.QUEUE_LIMIT-1,0)==1,"village queue respects the upper bound");
        check(ReplayBudget.mainCandidates(ReplayBudget.QUEUE_LIMIT-32,32)==0,"main and hunter share only the village cap");
        check(ReplayBudget.lone(64)==64,"the old arbitrary lone ceiling is removed");
        check(ReplayBudget.lone(ReplayBudget.QUEUE_LIMIT)==0&&ReplayBudget.mainCandidates(0,0)==64,"a full lone queue cannot block villages");
        check(ReplayBudget.mainCandidates(ReplayBudget.QUEUE_LIMIT,0)==0&&ReplayBudget.lone(0)==64,"a full village queue cannot block lone retries");
        check(ReplayBudget.mainCandidates(Integer.MAX_VALUE,Integer.MAX_VALUE)==0,"native totals cannot overflow into a positive budget");
        System.out.println("ReplayBudgetSelfTest PASS (205861-chunk area, independent queues, native batch)");
    }
    private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
}
