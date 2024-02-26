public class Buffer {
    int busy;
    int address;
    String reg;
    String operation;
    int executionCycle;
    String label;
    int endExecution;
    String addressLock;

    public Buffer() {
        this.busy = 0;
        this.address = -1;
        this.reg = "";
        this.executionCycle = 1;
        this.label="";
        this.endExecution=0;
        this.addressLock="";
    }

    public void CleanB() {
        this.busy = 0;
        this.address = -1;
        this.reg = "";
        this.executionCycle = 1;
        this.addressLock="";
    }
}
