package maki.millenairelod;

/** Bounded transfer into the current Millenaire main, lone-building and hunter queues. */
final class ReplayBudget {
    private ReplayBudget() {}
    static int limit(int main,int lone,int hunter){
        if(lone>=64)return 0;
        return Math.max(0,Math.min(8,72-main-hunter));
    }
}
