package random.client;

public class ClientReceiver extends Thread{

    Client owner;

    public ClientReceiver(Client owner) {
        this.owner = owner;
    }

    @Override
    public void run() {
        owner.readCommands();
    }




}
