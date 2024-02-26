public class ReservationStation {
    int busy;
    String op;
    double vj;
    double vk;
    String qj;
    String qk;
    int executionCycle;
    String rsLabel;
    double writeBack;
    String des;
    int endExecution;

    public ReservationStation() {
        this.busy = 0;
        this.op = "";
        this.vj = 0;
        this.vk = 0;
        this.qj = "";
        this.qk = "";
        this.executionCycle = 1;
        this.rsLabel="";
        this.writeBack=0;
        this.des="";
        this.endExecution=0;
    }

    public void CleanRS() {
        this.busy = 0;
        this.op = "";
        this.vj = 0;
        this.vk = 0;
        this.qj = "";
        this.qk = "";
        this.executionCycle = 1;
        this.writeBack=0;
        this.des="";
    }
}
