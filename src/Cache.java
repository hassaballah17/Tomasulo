

public class Cache {
    CacheCell[] cacheCells;

    public Cache() {
        this.cacheCells = new CacheCell[20];
        for (int i = 0; i < 20; i++) {
            this.cacheCells[i] = new CacheCell(i,i*10);
            //so with address 0, data is 0, address 1 data is 10, address 2 data is 20...
        }
    }

    public void storeInCache(RegisterFile rf, String regName, int address) {
        Register reg = rf.getRegister(regName);
        for(int i=0;i<cacheCells.length;i++) {
            if(cacheCells[i].address == address) {
                cacheCells[i].data = reg.content;
            }
        }
    }

    public void loadFromCache(RegisterFile rf, String regName, int address) {
        Register reg = rf.getRegister(regName);
        for(int i=0;i<cacheCells.length;i++) {
            if(cacheCells[i].address == address) {
                reg.content = (int)cacheCells[i].data;
            }
        }
    }
}
