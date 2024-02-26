import java.util.HashMap;
public class RegisterFile {

    HashMap<String, Register> registers;
    HashMap<String, FloatingRegister> floatingRegisters;
    public RegisterFile(int numRegisters) {
        this.registers = new HashMap<>(numRegisters);
        this.floatingRegisters = new HashMap<>(numRegisters);
        initializeRegisters(numRegisters);
    }

    public void initializeRegisters(int numRegisters) {
        for (int i = 0; i < numRegisters/2; i++) {
            String registerName = "R" + i;
            registers.put(registerName, new Register());
        }for (int i = 0; i < numRegisters/2; i++) {
            String registerName = "F" + i;
            floatingRegisters.put(registerName, new FloatingRegister());
        }
    }

    public Register getRegister(String registerName) {
        return registers.get(registerName);
    }



    public boolean isRegisterReady(String registerName) {
        return registers.get(registerName).isReady();
    }

}