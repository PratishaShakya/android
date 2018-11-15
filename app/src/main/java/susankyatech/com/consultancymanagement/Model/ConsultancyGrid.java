package susankyatech.com.consultancymanagement.Model;

import java.util.List;

import susankyatech.com.consultancymanagement.Interfaces.HomeItems;

public class ConsultancyGrid implements HomeItems {
    public List<Client> clientList;

    public ConsultancyGrid(List<Client> clientList) {
        this.clientList = clientList;
    }
}
