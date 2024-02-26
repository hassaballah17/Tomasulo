

public class InstructionsQueue {
    InstructionCell[] instructions;

    public InstructionsQueue(int size) {
        this.instructions = new InstructionCell[size];
        for (int i = 0; i < size; i++) {
            this.instructions[i] = new InstructionCell();
        }
    }
}
