

public class InstructionCellALU extends InstructionCell{
    String regDes;
    String regSrc1;
    String regSrc2;
    int writeResult;

    public InstructionCellALU(String instruction, String regDes, String regSrc1,
                              String regSrc2, int writeResult) {
        super();
        this.regDes = regDes;
        this.regSrc1 = regSrc1;
        this.regSrc2 = regSrc2;
        this.writeResult = writeResult;
    }


}
