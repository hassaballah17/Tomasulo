import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

public class Tomasulo {
    int addsubcycles;
    int muldivcycles;
    int loadcycles;
    int storecycles;
    int addsubamounts;
    int muldivamounts;
    int loadamounts;
    int storeamounts;
    Cache cache;
    RegisterFile registerFile;
    ArrayList<String> InsQueue;
    int insSize;
    int addsubReserved;
    int muldivReserved;
    int cycles;
    AddSub [] addsubArray;
    ArrayList<InstructionCell> instructionsWaiting;
    ArrayList<AddSub> addsubWaiting;
    MulDiv [] muldivArray;
    ArrayList<MulDiv> muldivWaiting;
    LoadBuffer [] loadArray;
    StoreBuffer [] storeArray;
    ArrayList<StoreBuffer> storeWaiting;
    ArrayList<ReservationStation> aluStartsExecuting;
    ArrayList<ReservationStation> aluFStartsExecuting;
    ArrayList<ReservationStation> aluExecuting;
    ArrayList<ReservationStation> aluFExecuting;
    ArrayList<ReservationStation> aluWriteBack;
    ArrayList<ReservationStation> aluFWriteBack;
    ArrayList<Buffer> memExecuting;
    ArrayList<Buffer> memWriteBack;
    Boolean process;
    Boolean branch;
    int branchIns;
    InstructionsQueue instructionsQueue;
    int insc;
    int iteration;
    int proc;
    public Tomasulo(){
        cycles=1;
        insSize=0;
        addsubReserved=0;
        muldivReserved=0;
        addsubamounts=0;
        addsubcycles=0;
        muldivamounts=0;
        muldivcycles=0;
        registerFile = new RegisterFile(64);
        cache = new Cache();
        process=true;
        branch=false;
        branchIns=-1;
        insc=0;
        iteration=0;
        proc=0;
        InsQueue = new ArrayList<String>() ;
        addsubWaiting = new ArrayList<AddSub>();
        muldivWaiting = new ArrayList<MulDiv>();
        storeWaiting = new ArrayList<StoreBuffer>();
        memExecuting = new ArrayList<Buffer>();
        memWriteBack = new ArrayList<Buffer>();
        aluStartsExecuting = new ArrayList<ReservationStation>();
        aluFStartsExecuting = new ArrayList<ReservationStation>();
        aluExecuting = new ArrayList<ReservationStation>();
        aluFExecuting = new ArrayList<ReservationStation>();
        aluWriteBack = new ArrayList<ReservationStation>();
        aluFWriteBack = new ArrayList<ReservationStation>();
        instructionsWaiting = new ArrayList<InstructionCell>();
    }
    public void parseCode(String path) throws IOException {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String s ;
        while ((s = br.readLine()) != null) {
            System.out.println(s) ;
            InsQueue.add(s);
        }
        br.close();
        fr.close();
    }
    public void initializaStations(){
        for(int i=0;i<addsubamounts;i++){
            AddSub A = new AddSub();
            A.rsLabel="A"+i;
            A.endExecution=addsubamounts;
            addsubArray[i]=A;
        }
        for(int i=0;i<muldivamounts;i++){
            MulDiv M = new MulDiv();
            M.rsLabel="M"+i;
            M.endExecution=muldivamounts;
            muldivArray[i]=M;
        }
        for(int i=0;i<loadamounts;i++){
            LoadBuffer L = new LoadBuffer();
            L.label="l"+i;
            L.endExecution=loadamounts;
            loadArray[i]=L;
        }
        for(int i=0;i<storeamounts;i++){
            StoreBuffer S = new StoreBuffer();
            S.label="S"+i;
            S.endExecution=storeamounts;
            storeArray[i]=S;
        }
        System.out.println("Stations and Buffers initialized");
    }
    public void setInstructions(){
        insSize = InsQueue.size();
        instructionsQueue = new InstructionsQueue(insSize);
        int c = 0 ;
        for(int i=0;i<InsQueue.size();i++){
            String instruction1 = InsQueue.remove(i).toString();
            instructionsQueue.instructions[c].instruction=instruction1;
            i--;
            c++;
        }
        System.out.println("Instructions added to Queue");
    }

    public void executionStarted(){
        //Start execution for done methods
        if(aluFExecuting.size()>0){
            proc++;
            for(int i=0;i<aluFExecuting.size();i++){
                ReservationStation rs = aluFExecuting.remove(0);
                Execute(rs);
                i--;
                aluFWriteBack.add(rs);
            }
        }
        if(aluExecuting.size()>0){
            proc++;
            for(int i=0;i<aluExecuting.size();i++){
                ReservationStation rs = aluExecuting.remove(0);
                Execute(rs);
                i--;
                aluWriteBack.add(rs);
            }
        }
    }

    public void startExecution(){
        //Start execution for ready instructions
        if(memExecuting.size()>0){
            proc++;
            for(int i=0;i<memExecuting.size();i++){
                Buffer B = memExecuting.remove(i);
                System.out.println("Instruction "+B.label+" starts execution");
                memWriteBack.add(B);
            }
        }
        if(aluStartsExecuting.size()>0){
            proc++;
            for(int i=0;i<aluStartsExecuting.size();i++){
                ReservationStation rs = aluStartsExecuting.remove(i);
                i--;
                System.out.println("Instruction "+rs.rsLabel+" starts execution");
                aluExecuting.add(rs);
            }
        }
        if(aluFStartsExecuting.size()>0){
            proc++;
            for(int i=0;i<aluFStartsExecuting.size();i++){
                ReservationStation rs = aluFStartsExecuting.remove(i);
                i--;
                System.out.println("Instruction "+rs.rsLabel+" starts execution");
                aluFExecuting.add(rs);            }
        }
    }

    public void checkIns(){
        //Check for instructions waiting for a place to issue
        if(instructionsWaiting.size()>0){
            proc++;
            int c=instructionsWaiting.size();
            for(int i=0;i<c;i++){
                InstructionCell ins = instructionsWaiting.remove(0);
                issueIns(ins);

            }
        }
    }
    public void checkWaitingIns(){
        //Check for the waiting instructions if they can execute
        if(addsubWaiting.size()>0) {
            proc++;
            for (int i = 0; i < addsubWaiting.size(); i++) {
                AddSub A = new AddSub();
                A = addsubWaiting.get(i);
                System.out.println(A.qj+" A.qj");
                if ((A.qj.equals("")) && (A.qk.equals(""))) {
                    System.out.println("Instruction "+A.rsLabel+" starts execution");
                    if(A.des.charAt(0)=='F'){
                        aluFStartsExecuting.add(A);
                    }
                    else{
                        aluStartsExecuting.add(A);
                    }
                    addsubWaiting.remove(i);
                    i--;
                }
            }
        }
        if(muldivWaiting.size()>0){
            proc++;
            for(int i=0;i<muldivWaiting.size();i++){
                MulDiv M=new MulDiv();
                M=muldivWaiting.get(i);
                if((M.qj.equals(""))&&(M.qk.equals(""))){
                    System.out.println("Instruction "+M.rsLabel+" starts execution");
                    aluStartsExecuting.add(M);
                    muldivWaiting.remove(i);
                    i--;
                }
            }
        }
        if(storeWaiting.size()>0){
            proc++;
            for (int i = 0; i < storeWaiting.size(); i++) {
                StoreBuffer S = storeWaiting.get(i);
                    if ((S.reg.charAt(0) == ('R'))||(S.reg.charAt(0)==('F'))) {
                        System.out.println("Instruction "+S.label+" starts execution");
                        memExecuting.add(S);
                        storeWaiting.remove(i);
                        i--;
                    }
            }
        }
    }

    public void checkWriteBack(){
        if(aluExecuting.size()>0) {
            proc++;
            for (int i = 0; i < aluExecuting.size(); i++) {
                ReservationStation rs = aluExecuting.get(i);
                if(rs.executionCycle==rs.endExecution){
                    System.out.println("Instruction " + rs.rsLabel + " finishes execution");
                    aluWriteBack.add(rs);
                    aluExecuting.remove(i);
                    i--;
                }
                else{
                    rs.executionCycle++;
                }
            }
        }
        if(aluFExecuting.size()>0){
            proc++;
            for(int i=0;i<aluFExecuting.size();i++){
                ReservationStation rs = aluFExecuting.get(i);
                if(rs.executionCycle==rs.endExecution){
                    System.out.println("Instruction " + rs.rsLabel + " finishes execution");
                    aluFWriteBack.add(rs);
                    aluFExecuting.remove(i);
                    i--;
                }
                else{
                    rs.executionCycle++;
                }
            }
        }
        if(memExecuting.size()>0){
            proc++;
            for(int i=0;i<memExecuting.size();i++){
                Buffer B = memExecuting.get(i);
                if(B.executionCycle==B.endExecution){
                    System.out.println("Instruction "+B.label + " finishes execution");
                    memWriteBack.add(B);
                    memExecuting.remove(i);
                    i--;
                }
                else{
                    B.executionCycle++;
                }
            }
        }
    }

    public void writeBack(){
        if(memWriteBack.size()>0){
            proc++;
            int memWriteSize=memWriteBack.size();
            for(int i=0;i<memWriteSize;i++) {
                Buffer B = memWriteBack.remove(i);
                if (B.addressLock.equals("")) {
                    if (B.operation.equals("L.D")) {
                        System.out.println("Instruction "+B.label+ " is writing the value from address "+
                                B.address+" into Register " + B.reg);
                    }
                   /* if (B.operation.equals("S.D")) {
                        System.out.println("Value of Register "+B.reg+ "is now Equal "+B.);
                    }*/
                    writeBackMem(B);
                    return;
                }
                else{
                    memWriteBack.add(B);
                }

            }
        }
        if(aluWriteBack.size()>0){
            proc++;
            ReservationStation rs = aluWriteBack.remove(0);
            if(rs.op.equals("BNEZ")){
                System.out.println("BNEZ INS "+rs.rsLabel+" is made");
            }
            else {
                System.out.println("Instruction "+rs.rsLabel+" is writing the result back into register " +
                        rs.des);
            }
            writeBackALU(rs);
            return;
        }
        if(aluFWriteBack.size()>0){
            proc++;
            ReservationStation rs = aluFWriteBack.remove(0);
            if(rs.op.equals("BNEZ")){
                System.out.println("BNEZ INS "+rs.rsLabel+" is made");
            }
            else {
                System.out.println("Instruction "+rs.rsLabel+" is writing the result back into register " +
                        rs.des);
            }
            writeBackALUF(rs);
        }
    }

    public void process() {
        initializaStations();
        setInstructions();
        while (process) {
            proc=0;
            System.out.println("Cycle:" + " " + cycles);
            //Perform WriteBacks
            writeBack();
            //Check for instructions finishing execution
            checkWriteBack();
            //Start execution for ready instructions
            startExecution();
            //Check for ALU Instructions to execute
            executionStarted();
            //Check for instructions waiting for a place to issue
            checkIns();
            //Check for the waiting instructions to execute
            checkWaitingIns();
            //issue new instruction
            if((insc!=insSize)&&(!branch)){
                proc++;
                issueIns(instructionsQueue.instructions[insc]);
                insc++;
            }
            System.out.println("AddSub/BNEZ Reservation Station: ");
            for(int i=0;i<addsubArray.length;i++){
                AddSub A = addsubArray[i];
                System.out.println(A.rsLabel+" : "+"A.busy= "+A.busy+" A.vj= "+A.vj+" A.vk= "+A.vk+
                        " A.qj= "+A.qj);
            }
            System.out.println("MulDiv Reservation Station: ");
            for(int i=0;i<muldivArray.length;i++){
                MulDiv M = muldivArray[i];
                System.out.println(M.rsLabel+" : "+"M.busy= "+M.busy+" M.vj= "+M.vj+" M.vk= "+M.vk+
                        " M.qj= "+M.qj);
            }
            System.out.println("Load Buffer: ");
            for(int i=0;i<loadArray.length;i++){
                LoadBuffer L = loadArray[i];
                System.out.println(L.label+" : "+"L.busy= "+L.busy+" L.destination= "+L.reg+" L.address= "+L.address);
            }
            System.out.println("Store Buffer: ");
            for(int i=0;i<storeArray.length;i++){
                StoreBuffer S = storeArray[i];
                System.out.println(S.label+" : "+"S.busy= "+S.busy+" S.register= "+S.reg+" S.address= "+S.address);
            }
            System.out.println("Cache Values");
            for(int i=0;i<20;i++){
                CacheCell cacheCell= cache.cacheCells[i];
                System.out.println("Cache Cell "+i+" = "+cacheCell.data);
            }
            System.out.println("Register File Values");
            for(int i=0;i<32;i++){
                Register reg=registerFile.registers.get("R"+i);
                System.out.println("Register R"+i+" = "+reg.content);
                FloatingRegister freg=registerFile.floatingRegisters.get("F"+i);
                System.out.println("FloatingRegister F"+i+" = "+freg.content);
            }
            cycles++;
            if(proc==0){
                process=false;
                return;
            }
            if(cycles==12){
                process=false;
            }

        }
    }
    public void issueIns(InstructionCell instructionCell){
        InstructionCell insCell=instructionCell;
        String s = instructionCell.instruction;
        String [] ins = s.split(" ");
        String [] sIns = ins[1].split(",");
        String operation = ins[0];
        String reg="";
        String mem="";
        String des="";
        String op1="";
        String op2="";
        Register reg1 = new Register();
        FloatingRegister freg1 = new FloatingRegister();
        Register r1=new Register();
        Register r2=new Register();
        Register r3=new Register();
        FloatingRegister fr1=new FloatingRegister();
        FloatingRegister fr2=new FloatingRegister();
        FloatingRegister fr3=new FloatingRegister();
        int cachelength=cache.cacheCells.length;
        CacheCell cCell = new CacheCell(0,0);
        if(operation.equals("S.D")||(operation.equals("L.D"))||(operation.equals("BNEZ"))) {
            reg = sIns[0];
            mem = sIns[1];
        }
        else{
            des = sIns[0];
            op1 = sIns[1];
            op2 = sIns[2];
        }

        switch (operation){
            case ("ADD"):
            case ("SUB"):
            case ("ADDI"):
            case ("SUBI"):
            case ("ADD.D"):
            case ("SUB.D"):
                int i;
                AddSub A;
                for(i=0;i<addsubamounts;i++){
                    A = addsubArray[i];
                    if(A.busy==0){
                        System.out.println("Instruction "+ instructionCell.instruction + " is issued");
                        A.busy=1;
                        A.des=des;
                        A.op=operation;
                        if ((operation.equals("ADDI")) ||(operation.equals("SUBI") )) {
                            r1=registerFile.registers.get(op1);
                            int r=Integer.parseInt(op2);
                            r3=registerFile.registers.get(des);
                            A.vk = r;
                            A.endExecution=1;
                            if (r1.isReady()) {
                                A.vj = r1.content;
                                aluStartsExecuting.add(A);
                            } else {
                                A.qj = r1.label;
                                addsubWaiting.add(A);
                            }
                            r3.setLabel(A.rsLabel);
                        }
                        else if((operation.equals("ADD") ) ||(operation.equals("SUB"))){
                            r1=registerFile.registers.get(op1);
                            r3=registerFile.registers.get(des);
                            r2=registerFile.registers.get(op2);
                            if(r1.isReady()){
                                if(r2.isReady()){
                                    A.vj= r1.content;
                                    A.vk= r2.content;
                                    aluStartsExecuting.add(A);
                                }
                                else{
                                    A.vj=r1.content;
                                    A.qk=r2.label;
                                    addsubWaiting.add(A);
                                }
                            }
                            else{
                                addsubWaiting.add(A);
                                if(r2.isReady()){
                                    A.qj=r1.label;
                                    A.vk=r2.content;
                                }
                                else{
                                    A.qj=r1.label;
                                    A.qk=r2.label;
                                }
                            }
                            r3.setLabel(A.rsLabel);
                        }
                        else{
                            fr1=registerFile.floatingRegisters.get(op1);
                            fr2=registerFile.floatingRegisters.get(op2);
                            fr3=registerFile.floatingRegisters.get(des);
                            if(fr1.isReady()){
                                if(fr2.isReady()){
                                    A.vj= fr1.content;
                                    A.vk= fr2.content;
                                    aluFStartsExecuting.add(A);
                                }
                                else{
                                    A.vj=fr1.content;
                                    A.qk=fr2.label;
                                    addsubWaiting.add(A);
                                }
                            }
                            else{
                                if(fr2.isReady()){
                                    A.qj=fr1.label;
                                    A.vk=fr2.content;
                                    addsubWaiting.add(A);
                                }
                                else{
                                    A.qj=fr1.label;
                                    A.qk=fr2.label;
                                    addsubWaiting.add(A);
                                }
                            }

                        }
                        fr3.setLabel(A.rsLabel);
                        return;
                    }
                }
                if(i==addsubamounts){
                    instructionsWaiting.add(instructionCell);
                    return;
                }break;

            case "MUL.D":
            case "DIV.D":
                MulDiv M= new MulDiv();
                int j;
                for(j=0;j<muldivamounts;j++){
                    M = muldivArray[j];
                    if(M.busy==0){
                        M.des=des;
                        System.out.println("Instruction "+ instructionCell.instruction + " is issued");
                        if(op1.charAt(0)=='F'){
                            fr1=registerFile.floatingRegisters.get(op1);
                            fr2=registerFile.floatingRegisters.get(op2);
                            fr3=registerFile.floatingRegisters.get(des);
                            M.busy=1;
                            M.des=des;
                            M.op=operation;
                            if(fr1.isReady()){
                                if(fr2.isReady()){
                                    M.vj=fr1.content;
                                    M.vk=fr2.content;
                                    aluStartsExecuting.add(M);
                                }
                                else{
                                    M.vj=fr1.content;
                                    M.qk=fr2.label;
                                    muldivWaiting.add(M);
                                }
                            }
                            else{
                                muldivWaiting.add(M);
                                if(r2.isReady()){
                                    M.qj=fr1.label;
                                    M.vk=fr2.content;
                                }
                                else{
                                    M.qj=fr1.label;
                                    M.qk=fr2.label;
                                }
                            }
                            fr3.setLabel(M.rsLabel);
                            return;
                        }
                        else{
                            r1=registerFile.registers.get(op1);
                            r2=registerFile.registers.get(op2);
                            r3=registerFile.registers.get(des);
                            M.busy=1;
                            M.des=des;
                            M.op=operation;
                            if(r1.isReady()){
                                if(r2.isReady()){
                                    M.vj=r1.content;
                                    M.vk=r2.content;
                                    aluStartsExecuting.add(M);
                                }
                                else{
                                    M.vj=r1.content;
                                    M.qk=r2.label;
                                    muldivWaiting.add(M);
                                }
                            }
                            else{
                                muldivWaiting.add(M);
                                if(r2.isReady()){
                                    M.qj=r1.label;
                                    M.vk=r2.content;
                                }
                                else{
                                    M.qj=r1.label;
                                    M.qk=r2.label;
                                }
                            }
                            r3.setLabel(M.rsLabel);
                            return;
                        }
                    }
                }
                if(j==muldivamounts){
                    instructionsWaiting.add(instructionCell);
                    return;
                }break;

            case "L.D":
                int k;
                LoadBuffer L;
                cachelength=cache.cacheCells.length;
                for(k=0;k<loadamounts;k++){
                    L=loadArray[k];
                    if(L.busy==0){
                        L.busy=1;
                        L.address = Integer.parseInt(mem);
                        L.reg=reg;
                        System.out.println("Instruction "+ instructionCell.instruction + " is issued");
                        int l=0;
                        if(reg.charAt(0)=='F'){
                            freg1 = registerFile.floatingRegisters.get(reg);
                            for(l=0;l<cachelength;l++){
                                cCell=cache.cacheCells[l];
                                if(cCell.address==Integer.parseInt(mem)){
                                    L.address = Integer.parseInt(mem);
                                    L.reg = reg;
                                    memExecuting.add(L);
                                    freg1.setLabel(L.label);
                                    if(!cCell.reserved) {
                                        cCell.storeReserved=true;
                                        cCell.addressLabel=L.label;
                                        return;
                                    }
                                    else{
                                        L.addressLock= cCell.addressLabel;
                                    }
                                }
                            }
                        }
                        else{
                            reg1 = registerFile.registers.get(reg);
                            for(l=0;l<cachelength;l++){
                                cCell=cache.cacheCells[l];
                                if(cCell.address==Integer.parseInt(mem)){
                                    L.reg = reg;
                                    memExecuting.add(L);
                                    reg1.setLabel(L.label);
                                    if(!cCell.reserved) {
                                        cCell.storeReserved=true;
                                        cCell.addressLabel=L.label;
                                        return;
                                    }
                                    else{
                                        L.addressLock= cCell.addressLabel;
                                    }
                                }
                            }
                        }
                        }
                    }

                if(k==loadamounts){
                    instructionsWaiting.add(instructionCell);
                }
                break;

            case "BNEZ":
               branch=true;
                int b=0;
                for(b=0;b<addsubArray.length;b++){
                    A=addsubArray[b];
                    if(A.busy==0){
                        System.out.println("Instruction "+ instructionCell.instruction + " is issued");
                        A.busy=1;
                        A.op=operation;
                        A.des=mem;
                        A.endExecution=1;
                        A.vk=0;
                        if(reg.charAt(0)=='R'){
                            Register r = registerFile.registers.get(reg);
                            if(r.isReady()){
                                A.vj=r.content;
                                aluStartsExecuting.add(A);
                            }
                            else{
                                A.qj=r.label;
                                addsubWaiting.add(A);
                            }return;
                        }
                        else {
                            FloatingRegister f= registerFile.floatingRegisters.get(reg);
                            if(f.isReady()){
                                A.vj=f.content;
                                aluFStartsExecuting.add(A);
                            }
                            else{
                                A.qj=f.label;
                                addsubWaiting.add(A);
                            }return;
                        }
                    }
                }
                if(b==addsubArray.length){
                    instructionsWaiting.add(instructionCell);
                }
                break;

            case "S.D":
                int x;
                StoreBuffer S;
                for(x=0;x<storeamounts;x++){
                    S=storeArray[x];
                    if(S.busy==0) {
                        S.busy = 1;
                        S.reg=reg;
                        S.address=Integer.parseInt(mem);
                        System.out.println("Instruction " + insCell.instruction + " is issued");
                        if(reg.charAt(0)=='F'){
                            freg1 = registerFile.floatingRegisters.get(reg);
                            if (freg1.isReady()) {
                                int l=0;
                                for (l = 0; l < cachelength; l++) {
                                    cCell=cache.cacheCells[l];
                                    if (cCell.address==Integer.parseInt(mem)) {
                                        S.address=Integer.parseInt(mem);
                                        S.reg=reg;
                                        memExecuting.add(S);
                                        if(!cCell.storeReserved){
                                            cCell.reserved=true;
                                            cCell.storeReserved=true;
                                            cCell.addressLabel=S.label;
                                        }
                                        else{
                                            S.addressLock= cCell.addressLabel;
                                        }
                                        return;
                                    }
                                }
                            }
                            else {
                                S.reg=freg1.label;
                                storeWaiting.add(S);
                            }

                        }
                        else{
                            reg1 = registerFile.registers.get(reg);
                            if (reg1.isReady()) {
                                int l=0;
                                for (l = 0; l < cachelength; l++) {
                                    cCell=cache.cacheCells[l];
                                    if (cCell.address==Integer.parseInt(mem)) {
                                        S.reg=reg;
                                        memExecuting.add(S);
                                        if(!cCell.storeReserved){
                                            cCell.reserved=true;
                                            cCell.storeReserved=true;
                                            cCell.addressLabel=S.label;
                                        }
                                        else{
                                            S.addressLock= cCell.addressLabel;
                                        }
                                        return;
                                    }
                                }
                            }
                            else {
                                S.reg=reg1.label;
                                storeWaiting.add(S);
                            }
                        }return;
                    }
                }
                if(x==storeamounts){
                    instructionsWaiting.add(instructionCell);
                }
                break;
        }

    }
    public void Execute(ReservationStation rs) {
        String operation=rs.op;
        switch(operation) {
            case "ADD":
            case "ADDI":
            case "ADD.D":
                for(AddSub A: addsubArray){
                    if(A.rsLabel.equals(rs.rsLabel)){
                        A.writeBack = A.vj + A.vk;
                        return;
                    }
                }
                break;
            case "SUB":
            case "SUBI":
            case "SUB.D":
                for(AddSub A: addsubArray){
                    if(A.rsLabel.equals(rs.rsLabel)){
                        A.writeBack = A.vj - A.vk;
                        return;
                    }
                }
                break;
            case "BNEZ":
                for(AddSub A: addsubArray){
                    if(A.rsLabel.equals(rs.rsLabel)){
                        System.out.println(A.vj);
                        if(A.vj==A.vk){
                            branch=true;
                            branchIns=Integer.parseInt(rs.des);
                            return;
                        }
                        else{
                            branch=false;
                            return;                        }
                    }
                }
                break;
            case "MUL.D":
                for(MulDiv M: muldivArray){
                    if(M.rsLabel.equals(rs.rsLabel)){
                        M.writeBack = M.vj * M.vk;
                        System.out.println(M.writeBack);
                        return;
                    }
                }
                break;
            case "DIV.D":
                for(MulDiv M: muldivArray){
                    if(M.rsLabel.equals(rs.rsLabel)){
                        M.writeBack = M.vj / M.vk;
                        return;
                    }
                }
                break;
        }
    }
    public void writeBackMem(Buffer B){
        String operation = B.operation;
        switch(operation) {
            case "L.D":
                for(int i=0;i<20;i++) {
                    if (cache.cacheCells[i].address == B.address) {
                        System.out.println("Value loaded = " + cache.cacheCells[i].data);
                        if (B.reg.charAt(0) == 'F') {
                            FloatingRegister freg1 = registerFile.floatingRegisters.get(B.reg);
                            if (freg1.label.equals(B.label)) {
                                freg1.content = (float) cache.cacheCells[i].data;
                                freg1.label = "";
                                freg1.lock = 0;
                            }
                            System.out.println(freg1.label);
                            if(cache.cacheCells[i].addressLabel.equals(B.label)){
                                cache.cacheCells[i].storeReserved=false;
                            }
                            for(AddSub A: addsubArray){
                                if(A.qj.equals(B.label)){
                                    A.vj=(float)cache.cacheCells[i].data;
                                    A.qj="";
                                }
                                if(A.qk.equals(B.label)){
                                    A.vk=(float)cache.cacheCells[i].data;
                                    A.qk="";
                                }
                            }
                            for(MulDiv M: muldivArray){
                                if(M.qj.equals(B.label)){
                                    M.vj=(float)cache.cacheCells[i].data;
                                    M.qj="";
                                }
                                if(M.qk.equals(B.label)){
                                    M.vk=(float)cache.cacheCells[i].data;
                                    M.qk="";
                                }
                            }
                            for(StoreBuffer S: storeArray){
                                if(S.addressLock.equals(B.label)){
                                    S.addressLock="";
                                }
                            }
                            for(LoadBuffer L: loadArray){
                                if(L.label.equals(B.label)){
                                    L.CleanB();
                                    return;
                                }
                            }
                            return;
                        } else {
                            Register reg1 = registerFile.registers.get(B.reg);
                            if (reg1.label.equals(B.label)) {
                                reg1.content = (int) cache.cacheCells[i].data;
                                reg1.label = "";
                                reg1.lock = 0;
                            }
                            if(cache.cacheCells[i].addressLabel.equals(B.label)){
                                cache.cacheCells[i].storeReserved=false;
                            }
                            for(AddSub A: addsubArray){
                                if(A.qj.equals(B.label)){
                                    A.vj=(int)cache.cacheCells[i].data;
                                    A.qj="";
                                }
                                if(A.qk.equals(B.label)){
                                    A.vk=(int)cache.cacheCells[i].data;
                                    A.qk="";
                                }
                            }
                            for(MulDiv M: muldivArray){
                                if(M.qj.equals(B.label)){
                                    M.vj=(int)cache.cacheCells[i].data;
                                    M.qj="";
                                }
                                if(M.qk.equals(B.label)){
                                    M.vk=(int)cache.cacheCells[i].data;
                                    M.qk="";
                                }
                            }
                            for(StoreBuffer S: storeArray){
                                if(S.addressLock.equals(B.label)){
                                    S.addressLock="";
                                }
                            }
                            for(LoadBuffer L: loadArray){
                                if(L.label.equals(B.label)){
                                    L.CleanB();
                                    return;
                                }
                            }
                            return;
                        }
                    }
                }
                break;
            case "S.D":
                for(int i=0;i<20;i++){
                    if(cache.cacheCells[i].address==B.address){
                        if (B.reg.charAt(0) == 'F') {
                            float f=registerFile.floatingRegisters.get(B.reg).content;
                            System.out.println("Value in Register "+ B.reg+"is now equal "+f);
                            cache.cacheCells[i].data=f;
                        }
                        else{
                            int r=registerFile.registers.get(B.reg).content;
                            System.out.println("Value in Register "+ B.reg+"is now equal "+r);
                            cache.cacheCells[i].data=r;
                        }
                        if(cache.cacheCells[i].addressLabel.equals(B.label)){
                            cache.cacheCells[i].reserved=false;
                            cache.cacheCells[i].storeReserved=false;
                        }
                        for(LoadBuffer L: loadArray){
                            if(L.addressLock.equals(B.label)){
                                L.addressLock="";
                            }
                        }
                        for(StoreBuffer S: storeArray){
                            if(S.addressLock.equals(B.label)){
                                S.addressLock="";
                            }
                        }
                        for(StoreBuffer S: storeArray){
                            if(S.label.equals(B.label)){
                                S.CleanB();
                                return;
                            }
                        }
                        return;
                    }
                }
                break;
        }
    }

    public void writeBackALU(ReservationStation rs){
        if(rs.op.equals("BNEZ")){
            if(branch){
                insc=branchIns;
                branch=false;
                return;
            }
            else{
                branch=false;
                return;
            }
        }
        if(rs.des.charAt(0)=='F'){
            FloatingRegister freg1 = registerFile.floatingRegisters.get(rs.des);
            float result =(float) rs.writeBack;
            if(freg1.label.equals(rs.rsLabel)){
                freg1.label="";
                freg1.lock=0;
                freg1.content=result;
                System.out.println("Value of register "+rs.des+" = "+result);
            }
            for(StoreBuffer S: storeArray){
                if(S.reg.equals(rs.rsLabel)){
                    S.reg=rs.des;
                }
            }
            for (AddSub A : addsubArray) {
                if (A.qj.equals(rs.rsLabel)) {
                    A.vj = result;
                    A.qj = "";
                    if (A.qk.equals(rs.rsLabel)) {
                        A.vk = result;
                        A.qk = "";
                    }

                } else {
                    if (A.qk.equals(rs.rsLabel)) {
                        A.vk = result;
                        A.qk = "";
                    }
                }
            }
            for (MulDiv M : muldivArray) {
                if (M.qj.equals(rs.rsLabel)) {
                    M.vj = result;
                    M.qj = "";
                    if (M.qk.equals(rs.rsLabel)) {
                        M.vk = result;
                        M.qk = "";
                    }
                } else {
                    if (M.qk.equals(rs.rsLabel)) {
                        M.vk = result;
                        M.qk = "";
                    }

                }
            }
        }
        else{
            Register reg1 = registerFile.registers.get(rs.des);
            int result =(int)rs.writeBack;
            if(reg1.label.equals(rs.rsLabel)){
                reg1.label="";
                reg1.lock=0;
                reg1.content=(int)rs.writeBack;
                System.out.println("Value of register "+rs.des+" = "+reg1.content);

            }
            for (AddSub A : addsubArray) {
                if (A.qj.equals(rs.rsLabel)) {
                    A.vj = result;
                    A.qj = "";
                    if (A.qk.equals(rs.rsLabel)) {
                        A.vk = result;
                        A.qk = "";
                    }
                } else {
                    if (A.qk.equals(rs.rsLabel)) {
                        A.vk = result;
                        A.qk = "";
                    }
                }
            }
            for (MulDiv M : muldivArray) {
                if (M.qj.equals(rs.rsLabel)) {
                    M.vj = result;
                    M.qj = "";
                    if (M.qk.equals(rs.rsLabel)) {
                        M.vk = result;
                        M.qk = "";
                    }
                } else {
                    if (M.qk.equals(rs.rsLabel)) {
                        M.vk = result;
                        M.qk = "";
                    }

                }
            }
        }
        for(StoreBuffer S: storeArray){
            if(S.reg.equals(rs.rsLabel)){
                S.reg=rs.des;
            }
        }
        for(AddSub A: addsubArray){
            if(A.rsLabel.equals(rs.rsLabel)){
                A.CleanRS();
                return;
            }
        }
        for(MulDiv M: muldivArray){
            if(M.rsLabel.equals(rs.rsLabel)){
                M.CleanRS();
                return;
            }
        }
    }

    public void writeBackALUF(ReservationStation rs){
        if(rs.op.equals("BNEZ")){
            if(branch){
                insc=branchIns;
                branch=false;
                return;
            }
            else{
                branch=false;
                return;
            }
        }
        FloatingRegister freg1 = registerFile.floatingRegisters.get(rs.des);
        float result = (float)rs.writeBack;
        if(freg1.label.equals(rs.rsLabel)){
            freg1.label="";
            freg1.lock=0;
            freg1.content=result;
        }
        for (AddSub A : addsubArray) {
            if (A.qj.equals(rs.rsLabel)) {
                A.vj = result;
                A.qj = "";
                if (A.qk.equals(rs.rsLabel)) {
                    A.vk = result;
                    A.qk = "";
                }
            } else {
                if (A.qk.equals(rs.rsLabel)) {
                    A.vk = result;
                    A.qk = "";
                }
            }
        }
        for(StoreBuffer S: storeArray){
            if(S.reg.equals(rs.rsLabel)){
                S.reg=rs.des;
            }
        }

        for (MulDiv M : muldivArray) {
            if (M.qj.equals(rs.rsLabel)) {
                M.vj = result;
                M.qj = "";
                if (M.qk.equals(rs.rsLabel)) {
                    M.vk = result;
                    M.qk = "";
                }
            } else {
                if (M.qk.equals(rs.rsLabel)) {
                    M.vk = result;
                    M.qk = "";
                }
            }
        }
        for(AddSub A: addsubArray){
            if(A.rsLabel.equals(rs.rsLabel)){
                A.CleanRS();
                return;
            }
        }
        for(MulDiv M: muldivArray){
            if(M.rsLabel.equals(rs.rsLabel)){
                M.CleanRS();
                return;
            }
        }

    }

    public void readfromuser () {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the latency for Add/Sub instructions: ");
        String addsubcycle = scanner.nextLine();
        System.out.println("You entered: " + addsubcycle);
        addsubcycles = Integer.parseInt(addsubcycle) ;

        System.out.println("Enter the latency for Mul/Div instructions: ");
        String muldivcycle = scanner.nextLine();
        System.out.println("You entered: " + muldivcycle);
        muldivcycles = Integer.parseInt(muldivcycle) ;

        System.out.println("Enter the latency for load instructions: ");
        String loadcycle = scanner.nextLine();
        System.out.println("You entered: " + loadcycle);
        loadcycles = Integer.parseInt(loadcycle) ;

        System.out.println("Enter the latency for store instructions: ");
        String storecycle = scanner.nextLine();
        System.out.println("You entered: " + storecycle);
        storecycles = Integer.parseInt(storecycle) ;

        System.out.println("Enter the Number of the Add/Sub Reservation stations: ");
        String addsubamount = scanner.nextLine();
        System.out.println("You entered: " + addsubamount);
        addsubamounts = Integer.parseInt(addsubamount) ;

        System.out.println("Enter the Number of the Mul/Div Reservation stations: ");
        String muldivamount = scanner.nextLine();
        System.out.println("You entered: " + muldivamount);
        muldivamounts = Integer.parseInt(muldivamount) ;

        System.out.println("Enter the Number of the Load Buffers: ");
        String loadamount = scanner.nextLine();
        System.out.println("You entered: " + loadamount);
        loadamounts = Integer.parseInt(loadamount) ;

        System.out.println("Enter the Number of the Store Buffers: ");
        String storeamount = scanner.nextLine();
        System.out.println("You entered: " + storeamount);
        storeamounts = Integer.parseInt(storeamount) ;

        scanner.close();
    }

    public static void main(String [] args) throws IOException {
        Tomasulo TA = new Tomasulo();
        TA.registerFile.registers.get("R1").content=2;
        TA.readfromuser();
        String File = "E://Micro ProjectF//test1.txt";
        TA.parseCode(File);
        TA.addsubArray=new AddSub[TA.addsubamounts];
        TA.muldivArray=new MulDiv[TA.muldivamounts];
        TA.loadArray=new LoadBuffer[TA.loadamounts];
        TA.storeArray=new StoreBuffer[TA.storeamounts];
        TA.process();
    }

}
