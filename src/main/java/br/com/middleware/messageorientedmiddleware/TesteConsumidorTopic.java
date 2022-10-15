package br.com.middleware.messageorientedmiddleware;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.Scanner;

public class TesteConsumidorTopic {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {

        InitialContext context = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        Connection connection = factory.createConnection("user", "senha");
        connection.setClientID("estoque");
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = (Topic) context.lookup("pedidos");

        MessageConsumer consumer = session.createDurableSubscriber(topic, "promocao", "DiaDasCriancas=true",true);

        consumer.setMessageListener(message -> {
            TextMessage textMessage = (TextMessage)message;
            try {
                //message.acknowledge();
                System.out.println(textMessage.getText());

            } catch (JMSException e) {
                e.printStackTrace();
            }
        });


        new Scanner(System.in).nextLine();

        session.close();
        connection.close();
        context.close();
    }
}
