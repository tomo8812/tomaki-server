package maki.millenairelod;
public final class SnapshotBudgetSelfTest {
    public static void main(String[] args) {
        SnapshotBudget budget=new SnapshotBudget(2,100);
        var first=budget.tryAcquire(40); var second=budget.tryAcquire(40);
        require(first!=null && second!=null && budget.tryAcquire(1)==null,"count admission before snapshot");
        require(!first.resize(70) && budget.bytes()==80,"oversize resize must not leak accounting");
        require(first.resize(20) && budget.bytes()==60,"coalesced replacement releases capacity");
        second.close(); second.close();
        require(budget.count()==1 && budget.bytes()==20,"idempotent release");
        var replacement=budget.tryAcquire(80);
        require(replacement!=null && budget.pressured(),"capacity recovers and pressure is visible");
        first.close(); replacement.close();
        require(budget.count()==0 && budget.bytes()==0 && !budget.pressured(),"drain ends pressure");
        System.out.println("SnapshotBudgetSelfTest PASS");
    }
    private static void require(boolean ok,String reason) { if (!ok) throw new AssertionError(reason); }
}
