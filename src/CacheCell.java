

public class CacheCell {
    int address;
    double data;
    Boolean reserved;
    Boolean storeReserved;
    String addressLabel;
    public CacheCell(int address, double data) {
        this.address = address;
        this.data = data;
        this.reserved=false;
        this.storeReserved=false;
        this.addressLabel="";
    }
}
