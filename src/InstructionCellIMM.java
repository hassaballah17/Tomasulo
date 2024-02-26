

public class InstructionCellIMM extends InstructionCell{
    String regDes;
    String regSrc;
    double immediate;
    int writeResult;

    public InstructionCellIMM(String regDes, String regSrc,
                              double immediate, int writeResult) {
        super();
        this.regDes = regDes;
        this.regSrc = regSrc;
        this.immediate = immediate;
        this.writeResult = writeResult;
    }





}
