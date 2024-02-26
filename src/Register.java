public class Register {
    int lock;
    String label;
    int content;

    public Register() {
        this.lock = 0;
        this.label="";
        this.content = 0;
    }
    public void clearLabel(){
        this.label="";
        this.lock=0;
    }

    public void setLabel(String label){
        this.label=label;
        this.lock=1;
    }

    public boolean isReady() {
        if((lock == 0) || (label.equals(""))) {
            return true;
        }
        else{
            return false;
        }
    }

}
