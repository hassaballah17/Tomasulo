public class FloatingRegister {
    int lock;
    String label;
    float content;


    public FloatingRegister() {
        this.lock = 0;
        this.label="";
        this.content = 0;
    }

    public void setLabel(String label){
        this.label=label;
        this.lock=1;
    }
    public void clearLabel(){
        this.label="";
        this.lock=0;
    }
    public boolean isReady() {
        if((lock == 0) || (label=="")) {
            return true;
        }
        else{
            return false;
        }
    }


}
