package random.client;

public class ClientReceiver extends Thread{

    Client owner;

    public ClientReceiver(Client owner) {
        this.owner = owner;
    }

    @Override
    public void run() {
        System.out.println("Receiver opened");
        owner.readCommands();
        System.out.println("Receiver closed");
    }




}
