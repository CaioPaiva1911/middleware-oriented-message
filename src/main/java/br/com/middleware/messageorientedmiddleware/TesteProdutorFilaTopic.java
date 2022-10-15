package br.com.middleware.messageorientedmiddleware;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.List;
import java.util.Scanner;

public class TesteProdutorFilaTopic {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {

        InitialContext context = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        Connection connection = factory.createConnection("user","senha");
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination fila = (Destination) context.lookup("LOG");

        Destination topico = (Destination) context.lookup("pedidos");

        var listaErros = List.of("ERR", "WARN", "DEBUG", "");
        var listaPromocoes = List.of("BlackFriday", "DiaDosPais", "DiaDasMaes", "DiaDasCriancas");

		for (int i = 0; i < 1000; i++) {
            var indexListErros = (int)(Math.random() * listaErros.size());
            var indexListMessages = (int)(Math.random() * listaPromocoes.size());

            if(!listaErros.get(indexListErros).equals("") && i < 300){

                MessageProducer producer = session.createProducer(fila);

                Message erro = session.createTextMessage(listaErros.get(indexListErros) +
                        " | Apache ActiveMQ 5.12.0 (localhost, ID:Mac-mini-de-IFSP.local-49701-1443131721783-0:1) is starting");
                switch (listaErros.get(indexListErros)){
                    case "WARN":
                        producer.send(erro, DeliveryMode.NON_PERSISTENT, 1, 300000);
                        break;
                    case "DEBUG":
                        producer.send(erro, DeliveryMode.NON_PERSISTENT, 4, 300000);
                        break;
                    case "ERR":
                        producer.send(erro, DeliveryMode.NON_PERSISTENT, 9, 300000);
                        break;
                    default:
                        break;
                }
            } else{
                var promocao = listaPromocoes.get(indexListMessages);
                Message message = session.createTextMessage("<pedido><promocao>"
                        + promocao
                        + "</promocao><id>" + i + "</id></pedido>");
                MessageProducer producer = session.createProducer(topico);
                message.setBooleanProperty(promocao, true);
                producer.send(message);

            }
		}

        new Scanner(System.in).nextLine();

        session.close();
        connection.close();
        context.close();
    }
}