package maki.millenairelod;
public final class ReplayBudgetSelfTest {
    public static void main(String[] args){
        check(ReplayBudget.limit(0,0,0)==8,"empty queues transfer a bounded burst");
        check(ReplayBudget.limit(71,0,0)==1,"main queue room limits replay");
        check(ReplayBudget.limit(0,0,71)==1,"native hunter work limits replay");
        check(ReplayBudget.limit(72,0,0)==0,"main backlog stops replay");
        check(ReplayBudget.limit(0,64,0)==0,"lone backlog stops replay");
        check(ReplayBudget.limit(0,0,72)==0,"hunter backlog stops replay");
        check(ReplayBudget.limit(40,0,32)==0,"main and hunter share the native backlog limit");
        check(ReplayBudget.limit(35,0,35)==2,"combined queue room limits transfer");
        check(ReplayBudget.limit(100,0,100)==0,"over-capacity queues never yield negative budget");
        System.out.println("ReplayBudgetSelfTest PASS (bounded main/lone/hunter transfer)");
    }
    private static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
}
